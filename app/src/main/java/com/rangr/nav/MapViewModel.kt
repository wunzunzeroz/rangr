package com.rangr.nav

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mapbox.common.location.*
import com.mapbox.geojson.LineString
import com.mapbox.geojson.Point
import com.mapbox.maps.MapView
import com.mapbox.maps.plugin.annotation.generated.PointAnnotation
import com.mapbox.turf.TurfConstants
import com.mapbox.turf.TurfMeasurement
import com.mapbox.turf.TurfMisc
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.entryOf
import com.rangr.BuildConfig
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class MapViewModel : ViewModel() {
    private lateinit var _mapController: MapboxController
    private lateinit var _mapView: MapView

    private var _tappedLocation = MutableLiveData<Point>()
    val tappedLocation = _tappedLocation

    private var _mapState = MutableLiveData<MapState>(MapState.Viewing)
    val mapState = _mapState

    private var _mapStyle = MutableLiveData<MapStyle>(MapStyle.Outdoors)
    val mapStyle = _mapStyle

    private var _route = MutableLiveData<List<Point>>(emptyList())
    val route = _route

    private var _routeDistance = MutableLiveData<Double>(0.0)
    val routeDistance = _routeDistance

    private var _routeElevationPoints = MutableLiveData<List<Double>>(emptyList())
    val routeElevationPoints = _routeElevationPoints

    var routeElevationProducer = ChartEntryModelProducer()

    lateinit var tapMarker: Bitmap
    private var _tappedPoint: PointAnnotation? = null

    fun setTapIcon(bitmap: Bitmap) {
        tapMarker = bitmap
    }

    fun setMapView(mapView: MapView) {
        _mapView = mapView
        _mapController = MapboxController(mapView)
    }

    suspend fun getUserLocation(): Point? = suspendCoroutine { continuation ->
        val locationService: LocationService = LocationServiceFactory.getOrCreate()
        var locationProvider: DeviceLocationProvider? = null

        val request = LocationProviderRequest.Builder()
            .interval(IntervalSettings.Builder().interval(0L).minimumInterval(0L).maximumInterval(0L).build())
            .displacement(0F).accuracy(AccuracyLevel.HIGHEST).build()

        val result = locationService.getDeviceLocationProvider(request)
        if (!result.isValue) {
            continuation.resumeWithException(RuntimeException("Unable to get device location provider"))
            return@suspendCoroutine
        }
        locationProvider = result.value

        locationProvider?.getLastLocation { lastLocation ->
            if (lastLocation == null) {
                continuation.resume(null)
            }
            continuation.resume(Point.fromLngLat(lastLocation!!.longitude, lastLocation.latitude))
        }
    }

    suspend fun getElevation(lat: Double, lon: Double): Double? {
        val client = HttpClient(CIO)
        val linzApiKey = BuildConfig.LINZ_API_KEY

        return withContext(Dispatchers.IO) {
            try {
                val url =
                    "https://data.linz.govt.nz/services/query/v1/raster.json?layer=51768&y=$lat&x=$lon&key=$linzApiKey"

                val response: HttpResponse = client.get(url)
                val jsonObject = JSONObject(response.bodyAsText())


                val bands = jsonObject.getJSONObject("rasterQuery").getJSONObject("layers").getJSONObject("51768")
                    .getJSONArray("bands")

                val elevation = bands.getJSONObject(0).getDouble("value")
                return@withContext elevation
            } catch (e: Exception) {
                e.printStackTrace()
                null
            } finally {
                client.close()
            }
        }
    }

    fun createWaypoint(tappedPoint: Point) {
        _mapState.value = MapState.Viewing
        // TODO
    }

    fun renderTapPoint(point: Point) {
        val pointAnnotation = _mapController.renderPoint(point, tapMarker)
        _tappedPoint = pointAnnotation
    }

    fun deleteTapPoint() {
        if (_tappedPoint == null) {
            return
        }
        _mapController.deletePoint(_tappedPoint!!)
    }


    fun addToRoute(tappedPoint: Point) {
        println("CREATE ROUTE")
        _mapState.value = MapState.Routing

        addPointToRoute(tappedPoint)
    }

    fun clearRoute() {
        _route.value = emptyList()
        _routeDistance.value = 0.0
        _mapState.value = MapState.Viewing
    }

//    fun getPointsAlongRoute(waypoints: List<Point>, intervalMeters: Double): List<Point> {
//        val lineString = LineString.fromLngLats(waypoints)
//
//        val intervalKilometers = intervalMeters / 1000.0
//
//        val slicedLine = TurfMisc.lineSliceAlong(lineString, 0.0, intervalKilometers, "kilometers")
//
//        return slicedLine.coordinates()
//    }
private fun getPointsAlongRoute(waypoints: List<Point>, intervalMeters: Double): List<Point> {
        if (waypoints.size < 2) {
            return waypoints
        }

        val lineString = LineString.fromLngLats(waypoints)
        val totalLength = TurfMeasurement.length(lineString, "kilometers")
        val intervalKilometers = intervalMeters / 1000.0

        val detailedPoints = mutableListOf<Point>()
        var traveledDistance = 0.0

        while (traveledDistance <= totalLength) {
            val segment = TurfMisc.lineSliceAlong(lineString, traveledDistance, traveledDistance + intervalKilometers, "kilometers")
            detailedPoints.add(segment.coordinates()[0]) // Add the start point of each segment
            traveledDistance += intervalKilometers
        }

        // Check if the last point is added, if not, add it
        if (detailedPoints.last() != waypoints.last()) {
            detailedPoints.add(waypoints.last())
        }

        return detailedPoints
    }


    private fun updateRouteElevation() {
        viewModelScope.launch {
            println("UPDATE ELE")

            if (route.value.isNullOrEmpty()) {
                return@launch
            }

            if (route.value!!.size < 2) {
                return@launch // Need at least 2 coords to slice route
            }

//            val explodedRoute = explodeRoute(route.value!!)
            val explodedRoute = getPointsAlongRoute(route.value!!, 250.0)

            if (explodedRoute.isNullOrEmpty()) {
                return@launch
            }

            println("EXPLODED COUNT: ${explodedRoute.size}")

            val elevations = explodedRoute.mapNotNull {
                _mapController.getElevation(it.latitude(), it.longitude())
            }

            _routeElevationPoints.value = elevations


            val entries = elevations.map {
                entryOf(elevations.indexOf(it), it)
            }
            routeElevationProducer.setEntries(entries)
        }


    }

    private fun addPointToRoute(point: Point) {
        viewModelScope.launch {
            val elevation = _mapController.getElevation(point.latitude(), point.longitude())
            println("ELEVATION: $elevation")

            if (elevation != null) {
                val ele = _routeElevationPoints.value ?: emptyList()
                val newEle = ele + elevation

                _routeElevationPoints.value = newEle
            }

            val route = _route.value ?: emptyList()
            val newRoute = route + point

            val routeDistance = calculateRouteDistance(newRoute)
            val dist = BigDecimal(routeDistance).setScale(1, RoundingMode.HALF_EVEN).toDouble()

            _route.value = newRoute
            _routeDistance.value = dist
            updateRouteElevation()
        }
//        val route = _route.value ?: emptyList()
//        val newRoute = route + point
//
//
//        val routeDistance = calculateRouteDistance(newRoute)
//        val dist = BigDecimal(routeDistance).setScale(1, RoundingMode.HALF_EVEN).toDouble()
//
//        _route.value = newRoute
//        _routeDistance.value = dist
    }

    private fun calculateRouteDistance(route: List<Point>): Double {
        var totalDistance = 0.0

        for (i in 0 until route.size - 1) {
            val a = route[i]
            val b = route[i + 1]

            val legDistance = TurfMeasurement.distance(a, b, TurfConstants.UNIT_METRES)

            totalDistance += legDistance
        }

        return totalDistance
    }
}

enum class MapState {
    Viewing, Routing
}

enum class MapStyle {
    Outdoors, Satellite, Topographic, Marine
}