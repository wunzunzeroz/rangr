package com.rangr.map

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mapbox.geojson.Point
import com.mapbox.maps.MapView
import com.mapbox.maps.plugin.annotation.generated.PointAnnotation
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.entryOf
import com.rangr.map.models.MapType
import com.rangr.map.models.Route
import com.rangr.nav.MapState

class MapViewModel : ViewModel() {
    private val _routeRepository = RouteRepository()
    private lateinit var _mapboxService: MapboxService

    private lateinit var _tapIcon: Bitmap

    private var _mapState = MutableLiveData(MapState.Viewing)
    val mapState = _mapState

    private var _mapRotationEnabled = MutableLiveData(false)
    val mapRotationEnabled = _mapRotationEnabled

    private var _tapPointRef: PointAnnotation? = null

    private var _route = MutableLiveData(Route.empty())
    val route = _route
    val routeProfile = ChartEntryModelProducer()


    fun initialise(mapboxService: MapboxService) {
        _mapboxService = mapboxService
        _mapboxService.initialise()
    }

    fun setTapIcon(icon: Bitmap) {
        _tapIcon = icon
    }

    fun getMapView(): MapView {
        return _mapboxService.getMapView()
    }

    fun onDestroy() {
//        TODO("Not yet implemented")
    }

    suspend fun addToRoute(waypoint: Point) {
        _mapState.value = MapState.Routing

        _routeRepository.updateRoute(waypoint)
        val newRoute = _routeRepository.getRoute()
        _route.value = newRoute

        var idx = 1
        val profileEntries = newRoute.elevationProfile.map { entryOf(++idx, it.altitude()) }

        routeProfile.setEntries(profileEntries)

        _mapboxService.renderRoute(newRoute, _tapIcon)
    }

    fun clearRoute() {
        _routeRepository.clearRoute()
        _mapboxService.clearRoute()
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
        val point = _mapboxService.renderPoint(tappedPoint, _tapIcon)

        _tapPointRef = point
    }

    fun deleteTapPoint() {
        if (_tapPointRef == null) {
            return
        }

        _mapboxService.deletePoint(_tapPointRef!!)
    }

    fun createWaypoint(tappedPoint: Point) {
    }

    fun setMapType(type: MapType) {
        _mapboxService.setMapType(type)
    }

}