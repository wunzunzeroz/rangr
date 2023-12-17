package com.rangr

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.mapbox.common.location.*
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import java.lang.ref.WeakReference

class MapActivity : ComponentActivity() {
    private lateinit var locationPermissionHelper: LocationPermissionHelper
    private lateinit var mapView: MapView
    private lateinit var mapController: MapboxController

    private var hasRotationEnabled: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mapView = MapView(this)
        mapController = MapboxController(mapView)

        mapController.ScrollToLocation(168.0, -44.7)
        mapController.SetMapStyle(Style.OUTDOORS)
        mapController.SetMapRotation(hasRotationEnabled)

        setContent { MainScreen(mapView = mapView) }

        locationPermissionHelper = LocationPermissionHelper(WeakReference(this))
        locationPermissionHelper.checkPermissions {
            mapController.OnMapReady()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        locationPermissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    @Composable
    fun MainScreen(mapView: MapView) {
        Box(modifier = Modifier.fillMaxSize()) {
            MapViewContainer(mapView)
            Column(modifier = Modifier.padding(16.dp)) {
                Spacer(modifier = Modifier.height(16.dp))
                MapStyleButton()
//                Spacer(modifier = Modifier.height(8.dp))
//                Toggle3dButton(mapView)
                Spacer(modifier = Modifier.height(8.dp))
                LocateUserButton(mapView)
                Spacer(modifier = Modifier.height(8.dp))
                ToggleRotateButton()
                Spacer(modifier = Modifier.height(8.dp))
                EnableMarineMode()
                Spacer(modifier = Modifier.height(8.dp))
                EnableTopoMode()
            }
        }
    }

    @Composable
    private fun ToggleRotateButton() {
        Box {
            FloatingActionButton(onClick = {
                hasRotationEnabled = !hasRotationEnabled
                mapController.SetMapRotation(hasRotationEnabled)
                ShowToast()
            },
                modifier = Modifier.align(Alignment.TopEnd),
                content = { Icon(Icons.Filled.Refresh, contentDescription = "Toggle rotation") })
        }
    }

    @Composable
    private fun EnableTopoMode() {
        Box {
            FloatingActionButton(onClick = {
                mapController.SetTopographicStyle()
            },
                modifier = Modifier.align(Alignment.TopEnd),
                content = { Icon(Icons.Filled.Terrain, contentDescription = "Toggle rotation") })
        }
    }

    @Composable
    private fun EnableMarineMode() {
        Box {
            FloatingActionButton(onClick = {
                mapController.SetNauticalStyle()
            },
                modifier = Modifier.align(Alignment.TopEnd),
                content = { Icon(Icons.Filled.DirectionsBoat, contentDescription = "Toggle rotation") })
        }
    }

    private fun ShowToast() {
        val text = if (hasRotationEnabled) "Map rotation enabled" else "Map rotation disabled"
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }

    @Composable
    fun MapStyleButton() {
        // Style Toggle Button
        Box() {
            var isSatelliteStyle by remember { mutableStateOf(false) }
            FloatingActionButton(onClick = {
                isSatelliteStyle = !isSatelliteStyle
                mapController.SetMapStyle(
                    if (isSatelliteStyle) Style.SATELLITE else Style.OUTDOORS
                )
            },
                modifier = Modifier.align(Alignment.TopEnd),
                content = { Icon(Icons.Filled.Layers, contentDescription = "Change Style") })
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mapController.onDestroy()
    }

    @Composable
    fun Toggle3dButton(mapView: MapView) {
        var is3d by remember { mutableStateOf(false) }

        FloatingActionButton(onClick = {
            is3d = !is3d

            mapController.SetCameraPitch(if (is3d) 30.0 else 0.0)
        }) {
            Icon(Icons.Filled.Star, contentDescription = "Toggle 3D viewing")
        }
    }

    @Composable
    fun LocateUserButton(mapView: MapView) {
        FloatingActionButton(onClick = {


            val locationService: LocationService = LocationServiceFactory.getOrCreate()
            var locationProvider: DeviceLocationProvider? = null

            val request = LocationProviderRequest.Builder()
                .interval(IntervalSettings.Builder().interval(0L).minimumInterval(0L).maximumInterval(0L).build())
                .displacement(0F).accuracy(AccuracyLevel.HIGHEST).build();

            val result = locationService.getDeviceLocationProvider(request)
            if (result.isValue) {
                locationProvider = result.value!!
            } else {
            }
            locationProvider?.getLastLocation { lastLocation ->
                lastLocation?.let {
                    // Scroll the map to the user's location
                    mapView.mapboxMap.setCamera(
                        CameraOptions.Builder().center(Point.fromLngLat(it.longitude, it.latitude))
                            .zoom(14.0) // Adjust the zoom level as needed
                            .build()
                    )
                }
            }
        }) {
            Icon(Icons.Filled.MyLocation, contentDescription = "Locate User")
        }
    }

    @Composable
    fun MapViewContainer(mapView: MapView) {
        AndroidView({ mapView }) { mapView ->
            mapView.onStart()
        }
    }

}
