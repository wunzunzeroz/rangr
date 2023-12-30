package com.rangr.map

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
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
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import com.mapbox.maps.plugin.gestures.addOnMapClickListener
import com.rangr.R
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference

class MapActivity : ComponentActivity() {
    private val model: MapViewModel by viewModels()

    private lateinit var locationPermissionHelper: LocationPermissionHelper

    private lateinit var mapView: MapView // TODO - Move to view model
    private lateinit var mapController: MapboxController // TODO - Remove
    private lateinit var pointAnnotationManager: PointAnnotationManager // TODO - Remove

    private var hasRotationEnabled: Boolean = false // TODO - Move to view model

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mapView = MapView(this)
        mapController = MapboxController(mapView)

        model.setMapView(mapView)

        mapController.ScrollToLocation(168.0, -44.7)
        mapController.SetMapStyle(Style.OUTDOORS)
        mapController.SetMapRotation(hasRotationEnabled)

        val annotationApi = mapView.annotations // TODO - Move to model/controller
        pointAnnotationManager = annotationApi.createPointAnnotationManager() // TODO - Move to model/controller

        setContent { MainScreen(mapView = mapView) }

        locationPermissionHelper = LocationPermissionHelper(WeakReference(this))
        locationPermissionHelper.checkPermissions {
            mapController.onMapReady()
        }

        model.route.observe(this) { mapController.renderRoute(it, getBitmap()) }
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

    fun getBitmap(): Bitmap {
        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.tap_marker)
        return Bitmap.createScaledBitmap(bitmap, 50, 50, false)
    }

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    fun MainScreen(mapView: MapView) {
        Box(modifier = Modifier.fillMaxSize()) {
            MapViewContainer(mapView, onMapTap = {})
            GetUiOverlayForMapState()
        }
    }

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    fun MapViewContainer(mapView: MapView, onMapTap: (Point) -> Unit) {

        val sheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
        var tappedPoint by remember { mutableStateOf<Point?>(null) }
        val coroutineScope = rememberCoroutineScope()

        LaunchedEffect(sheetState) {
            snapshotFlow { sheetState.isVisible }.collect { isVisible ->
                if (!isVisible) {
                    pointAnnotationManager.deleteAll() // Clear the symbols when the bottom sheet is hidden
                }
            }
        }

        ModalBottomSheetLayout(
            sheetState = sheetState, sheetContent = { LocationDetailsBottomSheet(tappedPoint, model) },
            sheetBackgroundColor = Color.Black,
            sheetContentColor = Color(0xFFFF4F00),
        ) {
            AndroidView({ mapView }) { mapView ->
                mapView.mapboxMap.addOnMapClickListener {
                    tappedPoint = it

                    val bitmap = BitmapFactory.decodeResource(resources, R.drawable.tap_marker)
                    val resized = Bitmap.createScaledBitmap(bitmap, 50, 50, false)
                    val pointAnnotationOptions: PointAnnotationOptions =
                        PointAnnotationOptions().withPoint(it).withIconImage(resized)
                    pointAnnotationManager.create(pointAnnotationOptions)

                    coroutineScope.launch { sheetState.show() }

                    true
                }
            }
        }
    }

    @Composable
    private fun GetUiOverlayForMapState() {
        val state by model.mapState.observeAsState()

        when (state) {
            MapState.Routing -> RoutingScreen()
            MapState.Viewing -> ViewingScreen()
            null -> ViewingScreen()
        }
    }

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    private fun ViewingScreen() {
        val sheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
        val coroutineScope = rememberCoroutineScope()

        ModalBottomSheetLayout(
            sheetState = sheetState, sheetContent = { MapStyleBottomSheet() },
            sheetBackgroundColor = Color.Black,
            sheetContentColor = Color(0xFFFF4F00),
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                Spacer(modifier = Modifier.height(32.dp))
                MapActionButton(
                    icon = Icons.Filled.Layers,
                    onClick = {
                        coroutineScope.launch { sheetState.show() }
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

    @Composable
    private fun RoutingScreen() {
        val route by model.route.observeAsState(emptyList())
        val distance by model.routeDistance.observeAsState(0.0)

        val legs = route.count() - 1

        Box(
            modifier = Modifier
                .background(color = Color.Black)
                .fillMaxWidth()
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(8.dp)
                ) {
                    Text("ACTIVE ROUTE", color = Color.White)
                    Text("LEGS: $legs", color = Color(0xFFFF4F00))
                    Text("DISTANCE: $distance m", color = Color(0xFFFF4F00))
                }
                Spacer(modifier = Modifier.weight(1f))
                Button(onClick = {
                    model.clearRoute()
                    mapController.clearRoute()
                }) {
                    Text("Clear Route")
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
                        println("SET TOPO")
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
                    CameraOptions.Builder().center(userLocation.value).zoom(14.0) // Adjust the zoom level as needed
                        .build()
                )
            }, contentDescription = "My location"
        )
    }

    @Composable
    private fun ToggleRotateButton() {
        val text = if (hasRotationEnabled) "Map rotation enabled" else "Map rotation disabled"
        MapActionButton(
            icon = Icons.Filled.Refresh,
            onClick = {
                hasRotationEnabled = !hasRotationEnabled
                mapController.SetMapRotation(hasRotationEnabled)
                Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
            },
            contentDescription = "Toggle map rotation",
        )
    }

}
