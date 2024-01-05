package com.rangr.map.components

import android.view.ViewGroup
import androidx.compose.runtime.*
import androidx.compose.ui.viewinterop.AndroidView
import com.mapbox.geojson.Point
import com.mapbox.maps.MapView
import com.mapbox.maps.plugin.gestures.addOnMapClickListener
import com.rangr.map.MapViewModel
import com.rangr.map.models.SheetType

@Composable
fun MapViewContainer(model: MapViewModel) {
    var tappedPoint by remember { mutableStateOf<Point?>(null) }
    val mapView = model.getMapView()

    AndroidView(factory = { _ ->
        if (mapView.parent is ViewGroup) {
            (mapView.parent as ViewGroup).removeView(mapView)
        }
        mapView
    }, update = { _ ->
        mapView.mapboxMap.addOnMapClickListener {
            println("MAP TAPPED")

            tappedPoint = it
            model.addTapPoint(it)

            model.setBottomSheetType(SheetType.LocationDetail)
            model.setBottomSheetVisible(true)

            true
        }
    })
}
