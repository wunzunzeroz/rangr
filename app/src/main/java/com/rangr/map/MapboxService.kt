package com.rangr.map

import android.graphics.Bitmap
import com.mapbox.common.location.*
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
import com.mapbox.maps.plugin.gestures.gestures
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorPositionChangedListener
import com.mapbox.maps.plugin.locationcomponent.createDefault2DPuck
import com.mapbox.maps.plugin.locationcomponent.location
import com.rangr.BuildConfig
import com.rangr.map.models.MapType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class MapboxService(mapView: MapView) {
    private val _mapView: MapView
    private val _serviceScope = CoroutineScope(Dispatchers.IO)

    init {
        _mapView = mapView
    }

    fun initialise() {
        setMapType(MapType.Outdoor)
        initLocationTracking()
//        panToUserLocation()
    }

    fun onDestroy() {
        _mapView.location.removeOnIndicatorPositionChangedListener(_onIndicatorPositionChangedListener)
    }

    private fun initLocationTracking() {
        val locationComponent = _mapView.location
        locationComponent.updateSettings {
            puckBearing = PuckBearing.COURSE
            puckBearingEnabled = true
            enabled = true
            locationPuck = createDefault2DPuck(withBearing = true)
        }
        locationComponent.addOnIndicatorPositionChangedListener(_onIndicatorPositionChangedListener)

    }

    suspend fun getUserLocation(): Point? = suspendCoroutine { continuation ->
        val locationService: LocationService = LocationServiceFactory.getOrCreate()
        var locationProvider: DeviceLocationProvider? = null

        val request = LocationProviderRequest.Builder()
            .interval(IntervalSettings.Builder().interval(0L).minimumInterval(0L).maximumInterval(0L).build())
            .displacement(0F).accuracy(AccuracyLevel.HIGHEST).build()

        val result = locationService.getDeviceLocationProvider(request)
        if (!result.isValue) {
            continuation.resumeWithException(RuntimeException("Unable to get device location provider"))
            return@suspendCoroutine
        }
        locationProvider = result.value

        locationProvider?.getLastLocation { lastLocation ->
            if (lastLocation == null) {
                continuation.resume(null)
            }
            continuation.resume(Point.fromLngLat(lastLocation!!.longitude, lastLocation.latitude))
        }
    }

    suspend fun panToUserLocation() {
            val userLocation = getUserLocation()

            userLocation?.let {
                _mapView.mapboxMap.setCamera(CameraOptions.Builder().center(it).build())
            }

    }


    fun getMapView(): MapView {
        return _mapView
    }

    fun setTapIcon(icon: Bitmap) {

    }

    fun setMapType(type: MapType) {
        when (type) {
            MapType.Outdoor -> SetMapStyle(Style.OUTDOORS)
            MapType.Satellite -> SetMapStyle(Style.SATELLITE_STREETS)
            MapType.Topographic -> SetTopographicStyle()
            MapType.Marine -> SetNauticalStyle()
        }
    }

    fun SetTopographicStyle() {
        val apiKey = BuildConfig.LINZ_API_KEY

        val topo50Url =
            "https://tiles-cdn.koordinates.com/services;key=${apiKey}/tiles/v4/layer=52343/EPSG:3857/{z}/{x}/{y}.png"
        val topo250Url =
            "https://tiles-cdn.koordinates.com/services;key=${apiKey}/tiles/v4/layer=52324/EPSG:3857/{z}/{x}/{y}.png"

        _mapView.mapboxMap.loadStyle(Style.OUTDOORS) {
            it.addSource(rasterDemSource("TERRAIN_SOURCE") {
                url(TERRAIN_URL_TILE_RESOURCE)
            })
            it.addSource(rasterSource("LINZ_TOPO_50") {
                tiles(listOf(topo50Url))
                tileSize(128)
            })
            it.addSource(rasterSource("LINZ_TOPO_250") {
                tiles(listOf(topo250Url))
                tileSize(128)
            })
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

        _mapView.mapboxMap.loadStyle(Style.OUTDOORS) {
            it.addSource(rasterDemSource("TERRAIN_SOURCE") {
                url(TERRAIN_URL_TILE_RESOURCE)
            })
            it.addSource(rasterSource("LINZ_MARINE") {
                tiles(listOf("https://tiles-cdn.koordinates.com/services;key=${apiKey}/tiles/v4/set=4758/EPSG:3857/{z}/{x}/{y}.png"))
                tileSize(128)
                minzoom(2)
                maxzoom(18)
            })
            it.addSource(rasterSource("LINZ_MARINE_SOUTH") {
                tiles(listOf("https://tiles-cdn.koordinates.com/services;key=${apiKey}/tiles/v4/set=4759/EPSG:3857/{z}/{x}/{y}.png"))
                tileSize(128)
                minzoom(2)
                maxzoom(18)
            })
            it.setTerrain(
                terrain("TERRAIN_SOURCE")
            )
            it.addLayer(
                rasterLayer("LINZ_MARINE_LAYER", "LINZ_MARINE") {
                    sourceLayer("LINZ_MARINE")
                },
            )
            it.addLayer(
                rasterLayer("LINZ_MARINE_LAYER_SOUTH", "LINZ_MARINE_SOUTH") {
                    sourceLayer("LINZ_MARINE_SOUTH")
                },
            )
        }
    }

    fun SetMapStyle(style: String) {
        _mapView.mapboxMap.loadStyle(styleExtension = style(style) {
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

    private val _onIndicatorPositionChangedListener = OnIndicatorPositionChangedListener {
        _mapView.mapboxMap.setCamera(CameraOptions.Builder().center(it).build())
        _mapView.gestures.focalPoint = _mapView.mapboxMap.pixelForCoordinate(it)
    }

    companion object {
        private const val SOURCE = "TERRAIN_SOURCE"
        private const val SKY_LAYER = "sky"
        private const val TERRAIN_URL_TILE_RESOURCE = "mapbox://mapbox.mapbox-terrain-dem-v1"
    }
}