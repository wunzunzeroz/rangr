package com.rangr

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.extension.style.atmosphere.generated.atmosphere
import com.mapbox.maps.extension.style.layers.generated.skyLayer
import com.mapbox.maps.extension.style.layers.properties.generated.ProjectionName
import com.mapbox.maps.extension.style.layers.properties.generated.SkyType
import com.mapbox.maps.extension.style.projection.generated.projection
import com.mapbox.maps.extension.style.sources.generated.rasterDemSource
import com.mapbox.maps.extension.style.style
import com.mapbox.maps.extension.style.terrain.generated.terrain

class SecondActivity : ComponentActivity() {
    private lateinit var mapView: MapView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mapView = MapView(this)
        mapView.mapboxMap.setCamera(
            CameraOptions.Builder()
                .center(Point.fromLngLat(168.0, -44.7))
                .pitch(0.0)
                .zoom(10.0)
                .bearing(0.0)
                .build()
        )

        mapView.mapboxMap.loadStyle(
            styleExtension = style(Style.OUTDOORS) {
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
            }
        )

        setContentView(mapView)
    }

    companion object {
        private const val SOURCE = "TERRAIN_SOURCE"
        private const val SKY_LAYER = "sky"
        private const val TERRAIN_URL_TILE_RESOURCE = "mapbox://mapbox.mapbox-terrain-dem-v1"
    }
}
