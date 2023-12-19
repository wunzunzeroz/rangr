package com.rangr

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.mapbox.common.location.*
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import com.mapbox.maps.plugin.gestures.addOnMapClickListener
import com.mapbox.turf.TurfMeasurement
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.roundToInt

class MapActivity : ComponentActivity() {
    private lateinit var locationPermissionHelper: LocationPermissionHelper
    private lateinit var mapView: MapView
    private lateinit var mapController: MapboxController
    private lateinit var pointAnnotationManager: PointAnnotationManager

    private var hasRotationEnabled: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mapView = MapView(this)
        mapController = MapboxController(mapView)

        mapController.ScrollToLocation(168.0, -44.7)
        mapController.SetMapStyle(Style.OUTDOORS)
        mapController.SetMapRotation(hasRotationEnabled)

        val annotationApi = mapView.annotations
        pointAnnotationManager = annotationApi.createPointAnnotationManager()

        setContent { MainScreen(mapView = mapView) }

        locationPermissionHelper = LocationPermissionHelper(WeakReference(this))
        locationPermissionHelper.checkPermissions {
            mapController.OnMapReady()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mapController.onDestroy()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        locationPermissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    enum class BottomSheetType {
        MAP_STYLE_SELECTION, LOCATION_DETAILS
    }

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    fun MainScreen(mapView: MapView) {
        val sheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
        val coroutineScope = rememberCoroutineScope()

        var bottomSheetType by remember { mutableStateOf<BottomSheetType?>(null) }
        var tappedPoint by remember { mutableStateOf<Point?>(null) }

        LaunchedEffect(sheetState) {
            snapshotFlow { sheetState.isVisible }.collect { isVisible ->
                if (!isVisible) {
                    pointAnnotationManager.deleteAll() // Clear the symbols when the bottom sheet is hidden
                }
            }
        }

        ModalBottomSheetLayout(sheetState = sheetState,
            sheetBackgroundColor = Color.Black,
            sheetContentColor = Color(0xFFFF4F00),
            sheetContent = {
                when (bottomSheetType) {
                    BottomSheetType.MAP_STYLE_SELECTION -> MapStyleBottomSheet()
                    BottomSheetType.LOCATION_DETAILS -> LocationDetailsBottomSheet(tappedPoint)
                    null -> {}
                }
            }) {
            Box(modifier = Modifier.fillMaxSize()) {
                MapViewContainer(mapView, onMapTap = {
                    tappedPoint = it

                    val bitmap = BitmapFactory.decodeResource(resources, R.drawable.tap_marker)
                    val resized = Bitmap.createScaledBitmap(bitmap, 50, 50, false)
                    val pointAnnotationOptions: PointAnnotationOptions =
                        PointAnnotationOptions().withPoint(it).withIconImage(resized)
                    pointAnnotationManager.create(pointAnnotationOptions)

                    bottomSheetType = BottomSheetType.LOCATION_DETAILS
                    coroutineScope.launch { sheetState.show() }
                })
                Column(modifier = Modifier.padding(8.dp)) {
                    Spacer(modifier = Modifier.height(32.dp))
                    MapActionButton(
                        icon = Icons.Filled.Layers,
                        onClick = {
                            bottomSheetType = BottomSheetType.MAP_STYLE_SELECTION
                            coroutineScope.launch {
                                if (sheetState.isVisible) {
                                    sheetState.hide()
                                } else {
                                    sheetState.show()
                                }
                            }

                        },
                        contentDescription = "Select map style",
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LocateUserButton()
                    Spacer(modifier = Modifier.height(8.dp))
                    ToggleRotateButton()
                }
            }
        }
    }

    @Composable
    private fun LocationDetailsBottomSheet(tappedPoint: Point?) {
        val userLocation = remember { mutableStateOf<Point?>(null) }
        val pointElevation = remember { mutableStateOf<Double?>(null) }

        if (tappedPoint == null) return

        LaunchedEffect(tappedPoint) {
            userLocation.value = mapController.GetUserLocation()
            pointElevation.value = mapController.getElevation(tappedPoint.latitude(), tappedPoint.longitude())
        }

        val latitude = tappedPoint.latitude()
        val longitude = tappedPoint.longitude()

        val distance = userLocation.value?.let { loc ->
            TurfMeasurement.distance(loc, tappedPoint, "kilometers")
        }

        val bearing = userLocation.value?.let { loc ->
            TurfMeasurement.bearing(loc, tappedPoint)
        }

        val lat = BigDecimal(latitude).setScale(6, RoundingMode.HALF_EVEN).toDouble()
        val lng = BigDecimal(longitude).setScale(6, RoundingMode.HALF_EVEN).toDouble()

        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Tapped Point:")
                Text("LAT: $lat")
                Text("LNG: $lng")
                pointElevation.value?.let { 
                    Text("Elevation: ${it.roundToInt()}m AMSL")
                }
                distance?.let {
                    var dist = BigDecimal(it).setScale(1, RoundingMode.HALF_EVEN).toDouble()
                    Text("Distance from you: $dist km")
                }
                bearing?.let {
                    val normalizedBearing = if (bearing >= 0) bearing else 360 + bearing
                    val brg = normalizedBearing.roundToInt()

                    Text("Bearing from you: $brg deg T")
                }
            }
        }
    }

    @Composable
    fun MapStyleBottomSheet() {
        val mapStyles = listOf("STREETS", "OUTDOORS", "SATELLITE", "MARINE", "TOPOGRAPHIC")

        LazyColumn {
            items(mapStyles) { style ->
                ListItem(style)
            }
        }
    }

    @Composable
    fun ListItem(style: String) {
        TextButton(
            colors = ButtonDefaults.buttonColors(contentColor = Color(0xFFFF4F00), backgroundColor = Color.Black),
            onClick = {
                when (style) {
                    "STREETS" -> {
                        mapController.SetMapStyle(Style.STANDARD)
                    }

                    "OUTDOORS" -> {
                        mapController.SetMapStyle(Style.OUTDOORS)
                    }

                    "SATELLITE" -> {
                        mapController.SetMapStyle(Style.SATELLITE_STREETS)
                    }

                    "MARINE" -> {
                        mapController.SetNauticalStyle()

                    }

                    "TOPOGRAPHIC" -> {
                        mapController.SetTopographicStyle()
                    }
                }

            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text(style, modifier = Modifier.padding(8.dp))
        }
    }

    @Composable
    fun MapActionButton(icon: ImageVector, onClick: () -> Unit, contentDescription: String) {
        Button(
            onClick = onClick,
            shape = CircleShape,
            contentPadding = PaddingValues(0.dp),
            modifier = Modifier.size(45.dp),
            colors = ButtonDefaults.buttonColors(
                contentColor = Color(0xFFFF4F00), backgroundColor = Color.Black
            ),
        ) {
            Icon(
                icon, modifier = Modifier.size(25.dp), tint = Color(0xFFFF4F00), contentDescription = contentDescription
            )
        }
    }

    @Composable
    fun LocateUserButton() {
        val userLocation = remember { mutableStateOf<Point?>(null) }

        LaunchedEffect(Unit) {
            userLocation.value = mapController.GetUserLocation()
        }

        MapActionButton(
            icon = Icons.Filled.MyLocation, onClick = {
                mapView.mapboxMap.setCamera(
                    CameraOptions.Builder().center(userLocation.value)
                        .zoom(14.0) // Adjust the zoom level as needed
                        .build()
                )
            }, contentDescription = "My location"
        )
    }

    @Composable
    private fun ToggleRotateButton() {
        MapActionButton(
            icon = Icons.Filled.Refresh,
            onClick = {
                hasRotationEnabled = !hasRotationEnabled
                mapController.SetMapRotation(hasRotationEnabled)
                ShowToast()
            },
            contentDescription = "Toggle map rotation",
        )
    }

    private fun ShowToast() {
        val text = if (hasRotationEnabled) "Map rotation enabled" else "Map rotation disabled"
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }

    @Composable
    fun MapViewContainer(mapView: MapView, onMapTap: (Point) -> Unit) {
        AndroidView({ mapView }) { mapView ->
            mapView.mapboxMap.addOnMapClickListener {
                onMapTap(it)
                true
            }
        }
    }

}
