package com.rangr

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
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

class MapActivity : ComponentActivity() {
    private lateinit var mapView: MapView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
// Create a map programmatically and set the initial camera
        mapView = MapView(this)
        mapView.mapboxMap.setCamera(
            CameraOptions.Builder()
                .center(Point.fromLngLat(168.0, -44.7))
                .pitch(0.0)
                .zoom(10.0)
                .bearing(0.0)
                .build()
        )

        setMapStyle(Style.OUTDOORS)

        setContent { MainScreen(mapView = mapView) }
    }

    private fun setMapStyle(style: String) {
        mapView.mapboxMap.loadStyle(styleExtension = style(style.toString()) {
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

    @Composable
    fun MainScreen(mapView: MapView) {
        Box(modifier = Modifier.fillMaxSize()) {
            MapViewContainer(mapView)
            Column(modifier = Modifier.padding(16.dp)) {
                Spacer(modifier = Modifier.height(16.dp))
                MapStyleButton()
                Spacer(modifier = Modifier.height(8.dp))
                Toggle3dButton(mapView)
                Spacer(modifier = Modifier.height(8.dp))
                LocateUserButton(mapView)
            }
            // Positioning the BottomAppBar at the bottom of the screen
//            BottomAppBar {
//                // Map button
//                IconButton(onClick = {
//                    // Code to stay on current activity or refresh the view
//                }) {
//                    Icon(Icons.Filled.Home, contentDescription = "Map")
//                }
//                // Second Activity button
//                IconButton(onClick = {
//                    startActivity(Intent(this@MainActivity, SecondActivity::class.java))
//                }) {
//                    Icon(Icons.Filled.Settings, contentDescription = "Settings")
//                }
//            }
        }
    }

    @Composable
    fun MapStyleButton() {
        // Style Toggle Button
        Box() {
            var isSatelliteStyle by remember { mutableStateOf(false) }
            FloatingActionButton(
                onClick = {
                    isSatelliteStyle = !isSatelliteStyle
                    setMapStyle(
                        if (isSatelliteStyle) Style.SATELLITE else Style.OUTDOORS
                    )
                },
                modifier = Modifier.align(Alignment.TopEnd),
                content = { Icon(Icons.Filled.Menu, contentDescription = "Change Style") }
            )
        }
    }

    @Composable
    fun Toggle3dButton(mapView: MapView) {
        FloatingActionButton(onClick = {}) {
            Icon(Icons.Filled.Star, contentDescription = "Toggle 3D viewing")
        }
    }

    @Composable
    fun LocateUserButton(mapView: MapView) {
        FloatingActionButton(onClick = {}) {
            Icon(Icons.Filled.AccountCircle, contentDescription = "Locate User")
        }
    }

    @Composable
    fun MapViewContainer(mapView: MapView) {
        AndroidView({ mapView }) { mapView ->
            mapView.onStart()
        }
    }

}
