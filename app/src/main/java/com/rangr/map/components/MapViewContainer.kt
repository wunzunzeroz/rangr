package com.rangr.map.components

import androidx.compose.runtime.*
import androidx.compose.ui.viewinterop.AndroidView
import com.mapbox.geojson.Point
import com.mapbox.maps.plugin.gestures.addOnMapClickListener
import com.rangr.map.MapViewModel
import com.rangr.map.models.SheetType

@Composable
fun MapViewContainer(model: MapViewModel) {
    var tappedPoint by remember { mutableStateOf<Point?>(null) }
    val mapView = model.getMapView()

    AndroidView({ mapView }) { mapView ->
        mapView.mapboxMap.addOnMapClickListener {
            println("MAP TAPPED")

            tappedPoint = it
            model.addTapPoint(it)

            model.setBottomSheetType(SheetType.LocationDetail)
            model.setBottomSheetVisible(true)

            true
        }
    }
}
