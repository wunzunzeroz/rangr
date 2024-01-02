package com.rangr.map

import android.graphics.Bitmap
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
import com.rangr.BuildConfig
import com.rangr.map.models.MapType

class MapboxService(mapView: MapView) {
    private val _mapView: MapView

    init {
        _mapView = mapView
    }

    fun initialise() {
        setMapType(MapType.Outdoor)
        panToUserLocation()
    }

    fun getMapView(): MapView {
        return _mapView
    }

    fun panToUserLocation() {}

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

    companion object {
        private const val SOURCE = "TERRAIN_SOURCE"
        private const val SKY_LAYER = "sky"
        private const val TERRAIN_URL_TILE_RESOURCE = "mapbox://mapbox.mapbox-terrain-dem-v1"
    }
}