package com.rangr.map

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mapbox.geojson.Point
import com.mapbox.maps.MapView
import com.rangr.map.models.MapType
import com.rangr.nav.MapState

class MapViewModel : ViewModel() {
    private var _mapState = MutableLiveData(MapState.Viewing)
    val mapState = _mapState
    
    fun initialise(mapView: MapView) {
        // Check permissions
        // Call onMapReady
        // Set style
        // Scroll to user location
        TODO("Not yet implemented")
    }

    fun setTapIcon(icon: Bitmap) {

    }

    fun onDestroy() {
        TODO("Not yet implemented")
    }

    fun scrollToUserLocation() {
        TODO("Not yet implemented")
    }

    fun toggleMapRotation() {
        TODO("Not yet implemented")
    }

    fun deleteTapPoint() {
        TODO("Not yet implemented")
    }

    fun createWaypoint(tappedPoint: Point) {

    }

    fun addToRoute(tappedPoint: Point) {

    }

    fun addTapPoint(tappedPoint: Point) {

    }

    fun SetMapType(type: MapType) {
        TODO("Not yet implemented")
    }

}