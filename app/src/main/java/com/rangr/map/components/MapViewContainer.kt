package com.rangr.map.components

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.viewinterop.AndroidView
import com.mapbox.geojson.Point
import com.mapbox.maps.MapView
import com.mapbox.maps.plugin.gestures.addOnMapClickListener
import com.rangr.map.MapViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MapViewContainer(model: MapViewModel) {
    val sheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.HalfExpanded)
    var tappedPoint by remember { mutableStateOf<Point?>(null) }
    val coroutineScope = rememberCoroutineScope()
    val mapView = model.getMapView()

    LaunchedEffect(sheetState) {
        snapshotFlow { sheetState.isVisible }.collect { isVisible ->
            if (!isVisible) {
                model.deleteTapPoint()
            }
        }
    }

    ModalBottomSheetLayout(
        sheetState = sheetState, sheetContent = { LocationDetailsBottomSheet(tappedPoint = tappedPoint, mapViewModel = model)},
        sheetBackgroundColor = Color.Black,
        sheetContentColor = Color(0xFFFF4F00),
    ) {
        AndroidView({ mapView }) { mapView ->
            mapView.mapboxMap.addOnMapClickListener {
                println("MAP TAPPED")
                tappedPoint = it

                model.addTapPoint(it)

                coroutineScope.launch { sheetState.show() }

                true
            }
        }
    }
}
