package com.rangr.map

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.*
import androidx.room.Room
import com.mapbox.geojson.Point
import com.mapbox.maps.MapView
import com.mapbox.maps.plugin.annotation.generated.PointAnnotation
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.entryOf
import com.rangr.data.AppDatabase
import com.rangr.map.models.*
import com.rangr.map.repositories.WaypointsRepository
import kotlinx.coroutines.flow.Flow

class MapViewModel(application: Application) : AndroidViewModel(application) {
    private val _db: AppDatabase = Room.databaseBuilder(application, AppDatabase::class.java, "rangr-database").build()

    private val _waypointsRepository = WaypointsRepository(_db.waypointDao())
    val waypoints: LiveData<List<Waypoint?>> = _waypointsRepository.getWaypoints().asLiveData()

    private val _routeRepository = RouteRepository()
    private lateinit var _mapboxService: MapboxService

    private lateinit var _tapIcon: Bitmap
    private lateinit var _routeIcon: Bitmap

    private var _mapState = MutableLiveData(MapState.Viewing)
    val mapState = _mapState

    private var _isBottomSheetVisible = MutableLiveData(false)
    val isBottomSheetVisible = _isBottomSheetVisible

    private var _sheetType = MutableLiveData(SheetType.LocationDetail)
    val sheetType = _sheetType

    private var _mapRotationEnabled = MutableLiveData(false)
    val mapRotationEnabled = _mapRotationEnabled

    private var _tappedPoint = MutableLiveData<Point?>(null)
    val tappedPoint = _tappedPoint

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

    fun setRouteIcon(icon: Bitmap) {
        _routeIcon = icon
    }

    fun setBottomSheetVisible(visible: Boolean) {
        _isBottomSheetVisible.value = visible
    }

    fun setBottomSheetType(type: SheetType) {
        _sheetType.value = type
    }

    fun getMapView(): MapView {
        return _mapboxService.getMapView()
    }

    fun onDestroy() {
//        TODO("Not yet implemented")
    }

    suspend fun addToRoute(waypoint: Point) {
        println("ADD TO ROUTE")
        _mapState.value = MapState.Routing

        _routeRepository.updateRoute(waypoint)
        val newRoute = _routeRepository.getRoute()
        _route.value = newRoute

        var idx = 1
        val profileEntries = newRoute.elevationProfile.map { entryOf(++idx, it.altitude()) }

        routeProfile.setEntries(profileEntries)

        _mapboxService.renderRoute(newRoute, _routeIcon)

        deleteTapPoint()
    }

    fun clearRoute() {
        _routeRepository.clearRoute()
        _mapboxService.clearRoute()

        _mapState.value = MapState.Viewing
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
        _tappedPoint.value = tappedPoint

        val point = _mapboxService.renderPoint(tappedPoint, _tapIcon)

        _tapPointRef = point
    }

    fun deleteTapPoint() {
        if (_tapPointRef == null) {
            return
        }

        _mapboxService.deletePoint(_tapPointRef!!)
    }

    fun createWaypoint(lat: Double, lng: Double, name: String, desc: String) {
    }

    fun setMapType(type: MapType) {
        _mapboxService.setMapType(type)
    }

    fun onBottomSheetDismissed() {
        setBottomSheetVisible(false)
        deleteTapPoint()
    }

}