package com.rangr.map

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mapbox.common.location.*
import com.mapbox.geojson.Point
import com.rangr.BuildConfig
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class MapViewModel : ViewModel() {
    private var _tappedLocation = MutableLiveData<Point>()

    private var _route = MutableLiveData<List<Point>>()

    val tappedLocation = _tappedLocation

    val route = _route

    init {
        _route.value = listOf(
            Point.fromLngLat(168.711982, -45.019794),
            Point.fromLngLat(168.730094, -45.020903),
            Point.fromLngLat(168.720684, -45.028618)
        )
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
                val url = "https://data.linz.govt.nz/services/query/v1/raster.json?layer=51768&y=$lat&x=$lon&key=$linzApiKey"

                val response: HttpResponse = client.get(url)
                val jsonObject = JSONObject(response.bodyAsText())


                val bands = jsonObject
                    .getJSONObject("rasterQuery")
                    .getJSONObject("layers")
                    .getJSONObject("51768")
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
}