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
import kotlinx.coroutines.launch

class MapViewModel(application: Application) : AndroidViewModel(application) {
    private val _db: AppDatabase = Room.databaseBuilder(application, AppDatabase::class.java, "rangr-database").build()

    private val _waypointsRepository = WaypointsRepository(_db.waypointDao())
    val waypoints: LiveData<List<Waypoint?>> = _waypointsRepository.getWaypoints().asLiveData()

    private val _routeRepository = RouteRepository()
    private lateinit var _mapboxService: MapboxService

    private lateinit var _tapIcon: Bitmap
    private lateinit var _routeIcon: Bitmap
    private lateinit var _waypointIcon: Bitmap

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

    private var _selectedWaypoint = MutableLiveData<Waypoint?>(null)
    val selectedWaypoint = _selectedWaypoint

    fun initialise(mapboxService: MapboxService) {
        _mapboxService = mapboxService
        _mapboxService.initialise()

        _mapboxService.onWaypointTap = this::onWaypointTap

        waypoints.observeForever { waypointList ->
            waypointList.filterNotNull().forEach { waypoint ->
                mapboxService.renderWaypoint(waypoint, _waypointIcon)
            }
        }
    }

    private fun onWaypointTap(point: Point) {
        println("POINT TAPPED - ${point.latitude()}, ${point.longitude()}")

        viewModelScope.launch {
            val waypoint = _waypointsRepository.getWaypointByLatLng(point.latitude(), point.longitude())

            println("GOT WAYPOINT: ${waypoint?.name}")

            _selectedWaypoint.value = waypoint
            setBottomSheetType(SheetType.WaypointDetail)
            setBottomSheetVisible(true)
        }
    }

    fun setTapIcon(icon: Bitmap) {
        _tapIcon = icon
    }

    fun setRouteIcon(icon: Bitmap) {
        _routeIcon = icon
    }

    fun setWaypointIcon(icon: Bitmap) {
        _waypointIcon = icon
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

    suspend fun createWaypoint(lat: Double, lng: Double, name: String, desc: String) {
        val wpt = Waypoint(name = name, latitude = lat, longitude = lng, description = desc)

        _waypointsRepository.saveWaypoint(wpt)
        _mapboxService.renderWaypoint(wpt, _waypointIcon)
    }

    fun setMapType(type: MapType) {
        _mapboxService.setMapType(type)
    }

    fun setMapState(state: MapState) {
        _mapState.value = state
    }

    fun onBottomSheetDismissed() {
        setBottomSheetVisible(false)
        deleteTapPoint()
    }

}