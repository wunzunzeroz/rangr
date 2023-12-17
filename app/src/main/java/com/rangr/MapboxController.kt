package com.rangr

import android.widget.Toast
import com.mapbox.android.gestures.MoveGestureDetector
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.extension.style.atmosphere.generated.atmosphere
import com.mapbox.maps.extension.style.layers.addLayer
import com.mapbox.maps.extension.style.layers.generated.rasterLayer
import com.mapbox.maps.extension.style.layers.generated.skyLayer
import com.mapbox.maps.extension.style.layers.properties.generated.ProjectionName
import com.mapbox.maps.extension.style.layers.properties.generated.SkyType
import com.mapbox.maps.extension.style.projection.generated.projection
import com.mapbox.maps.extension.style.sources.addSource
import com.mapbox.maps.extension.style.sources.generated.rasterDemSource
import com.mapbox.maps.extension.style.sources.generated.rasterSource
import com.mapbox.maps.extension.style.style
import com.mapbox.maps.extension.style.terrain.generated.setTerrain
import com.mapbox.maps.extension.style.terrain.generated.terrain
import com.mapbox.maps.plugin.PuckBearing
import com.mapbox.maps.plugin.gestures.OnMoveListener
import com.mapbox.maps.plugin.gestures.gestures
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorBearingChangedListener
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorPositionChangedListener
import com.mapbox.maps.plugin.locationcomponent.createDefault2DPuck
import com.mapbox.maps.plugin.locationcomponent.location

class MapboxController(private val mapView: MapView) {
    private val onIndicatorBearingChangedListener = OnIndicatorBearingChangedListener {
        mapView.mapboxMap.setCamera(CameraOptions.Builder().bearing(it).build())
    }

    private val onIndicatorPositionChangedListener = OnIndicatorPositionChangedListener {
        mapView.mapboxMap.setCamera(CameraOptions.Builder().center(it).build())
        mapView.gestures.focalPoint = mapView.mapboxMap.pixelForCoordinate(it)
    }

    private val onMoveListener = object : OnMoveListener {
        override fun onMoveBegin(detector: MoveGestureDetector) {
            onCameraTrackingDismissed()
        }

        override fun onMove(detector: MoveGestureDetector): Boolean {
            return false
        }

        override fun onMoveEnd(detector: MoveGestureDetector) {}
    }

    companion object {
        private const val SOURCE = "TERRAIN_SOURCE"
        private const val SKY_LAYER = "sky"
        private const val TERRAIN_URL_TILE_RESOURCE = "mapbox://mapbox.mapbox-terrain-dem-v1"
    }

    fun SetTopographicStyle() {
        val apiKey = BuildConfig.LINZ_API_KEY

        val topo50Url = "https://tiles-cdn.koordinates.com/services;key=${apiKey}/tiles/v4/layer=52343/EPSG:3857/{z}/{x}/{y}.png"
        val topo250Url = "https://tiles-cdn.koordinates.com/services;key=${apiKey}/tiles/v4/layer=52324/EPSG:3857/{z}/{x}/{y}.png"

        mapView.mapboxMap.loadStyle(Style.OUTDOORS) {
            it.addSource(
                rasterDemSource("TERRAIN_SOURCE") {
                    url(TERRAIN_URL_TILE_RESOURCE)
                }
            )
            it.addSource(
                rasterSource("LINZ_TOPO_50") {
                    tiles(listOf(topo50Url))
                    tileSize(128)
                }
            )
            it.addSource(
                rasterSource("LINZ_TOPO_250") {
                    tiles(listOf(topo250Url))
                    tileSize(128)
                }
            )
            it.setTerrain(
                terrain("TERRAIN_SOURCE")
            )
            it.addLayer(
                rasterLayer("LINZ_TOPO_250_LAYER", "LINZ_TOPO_250") {
                    sourceLayer("LINZ_TOPO_250")
                    minZoom(0.0)
                    maxZoom(12.0)
                },
            )
            it.addLayer(
                rasterLayer("LINZ_TOPO_50_LAYER", "LINZ_TOPO_50") {
                    sourceLayer("LINZ_TOPO_50")
                    minZoom(12.0)
                },
            )
        }
    }
    fun SetNauticalStyle() {

        var apiKey = BuildConfig.LINZ_API_KEY

        mapView.mapboxMap.loadStyle(Style.DARK) {
            it.addSource(
                rasterDemSource("TERRAIN_SOURCE") {
                    url(TERRAIN_URL_TILE_RESOURCE)
                }
            )
            it.addSource(
                rasterSource("LINZ_MARINE") {
                    tiles(listOf("https://tiles-cdn.koordinates.com/services;key=${apiKey}/tiles/v4/set=4758/EPSG:3857/{z}/{x}/{y}.png"))
                    tileSize(128)
                    minzoom(2)
                    maxzoom(18)
                }
            )
            it.setTerrain(
                terrain("TERRAIN_SOURCE")
            )
            it.addLayer(
                rasterLayer("LINZ_MARINE_LAYER", "LINZ_MARINE") {
                    sourceLayer("LINZ_MARINE")
                },
            )
        }
    }
    fun SetMapStyle(style: String) {
        mapView.mapboxMap.loadStyle(styleExtension = style(style) {
            +rasterDemSource(SOURCE) {
                url(TERRAIN_URL_TILE_RESOURCE)
                // 514 specifies padded DEM tile and provides better performance than 512 tiles.
                tileSize(514)
            }
            +terrain(SOURCE)
            +skyLayer(SKY_LAYER) {
                skyType(SkyType.ATMOSPHERE)
                skyAtmosphereSun(listOf(-50.0, 90.2))
            }
            +atmosphere { }
            +projection(ProjectionName.GLOBE)
        })
    }

    fun OnMapReady() {
        mapView.mapboxMap.setCamera(
            CameraOptions.Builder().zoom(14.0).build()
        )

        mapView.mapboxMap.loadStyle(
            Style.STANDARD
        ) {
            initLocationComponent()
            setupGesturesListener()
        }
    }

    fun SetMapRotation(enabled: Boolean) {
        mapView.gestures.rotateEnabled = enabled
    }

    fun onDestroy() {
        mapView.location.removeOnIndicatorBearingChangedListener(onIndicatorBearingChangedListener)
        mapView.location.removeOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener)
        mapView.gestures.removeOnMoveListener(onMoveListener)
    }

    fun SetCameraPitch(pitch: Double) {
        mapView.mapboxMap.setCamera(CameraOptions.Builder().pitch(pitch).build())
    }

    fun Toggle3dView() {}

    fun ScrollToLocation(lng: Double, lat: Double) {
        val cameraOptions = CameraOptions.Builder().center(Point.fromLngLat(lng, lat)).build()

        mapView.mapboxMap.setCamera(cameraOptions)
    }

    private fun setupGesturesListener() {
        mapView.gestures.addOnMoveListener(onMoveListener)
    }

    private fun initLocationComponent() {
        val locationComponentPlugin = mapView.location
        locationComponentPlugin.updateSettings {
            puckBearing = PuckBearing.COURSE
            puckBearingEnabled = true
            enabled = true
            locationPuck = createDefault2DPuck(withBearing = true)
        }
        locationComponentPlugin.addOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener)
        locationComponentPlugin.addOnIndicatorBearingChangedListener(onIndicatorBearingChangedListener)
    }

    private fun onCameraTrackingDismissed() {
        mapView.location.removeOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener)
        mapView.location.removeOnIndicatorBearingChangedListener(onIndicatorBearingChangedListener)
        mapView.gestures.removeOnMoveListener(onMoveListener)
    }

}