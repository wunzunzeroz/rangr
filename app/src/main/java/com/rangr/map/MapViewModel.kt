package com.rangr.map

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mapbox.common.location.*
import com.mapbox.geojson.Point
import com.mapbox.maps.MapView
import com.mapbox.turf.TurfConstants
import com.mapbox.turf.TurfMeasurement
import com.rangr.BuildConfig
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.Dispatchers
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

    init {
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

    private fun addPointToRoute(point: Point) {
        val route = _route.value ?: emptyList()
        val newRoute = route + point

        val routeDistance = calculateRouteDistance(newRoute)
        val dist = BigDecimal(routeDistance).setScale(1, RoundingMode.HALF_EVEN).toDouble()

        _route.value = newRoute
        _routeDistance.value = dist
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