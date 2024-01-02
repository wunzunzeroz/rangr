package com.rangr.map

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mapbox.geojson.Point
import com.mapbox.maps.MapView
import com.rangr.map.models.MapType
import com.rangr.nav.MapState

class MapViewModel : ViewModel() {
    private lateinit var _mapboxService: MapboxService

    private var _mapState = MutableLiveData(MapState.Viewing)
    val mapState = _mapState

    private var _mapRotationEnabled = MutableLiveData(false)
    val mapRotationEnabled = _mapRotationEnabled
    
    fun initialise(mapboxService: MapboxService) {
        _mapboxService = mapboxService
        _mapboxService.initialise()
    }

    fun setTapIcon(icon: Bitmap) {
        _mapboxService.setTapIcon(icon)
    }

    fun getMapView(): MapView {
        return _mapboxService.getMapView()
    }

    fun onDestroy() {
//        TODO("Not yet implemented")
    }

    suspend fun scrollToUserLocation() {
        _mapboxService.panToUserLocation()
    }

    fun toggleMapRotation() {
        val isRotationEnabled = _mapRotationEnabled.value ?: false

        if (isRotationEnabled) {
            _mapboxService.enableRotation()
            _mapRotationEnabled.value = false

        } else {
            _mapboxService.disableRotation()
            _mapRotationEnabled.value = true

        }
    }

    fun addTapPoint(tappedPoint: Point) {
    }

    fun deleteTapPoint() {
    }

    fun createWaypoint(tappedPoint: Point) {
    }

    fun addToRoute(tappedPoint: Point) {
    }

    fun setMapType(type: MapType) {
        _mapboxService.setMapType(type)
    }
}