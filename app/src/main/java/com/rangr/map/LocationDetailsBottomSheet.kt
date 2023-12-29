package com.rangr.map

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mapbox.geojson.Point
import com.mapbox.turf.TurfMeasurement
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.roundToInt

@Composable
fun LocationDetailsBottomSheet(tappedPoint: Point, viewModel: MapViewModel) {
    var userLocation = remember { mutableStateOf<Point?>(null) }

    val pointElevation = remember { mutableStateOf<Double?>(null) }
    val userElevation = remember { mutableStateOf<Double?>(null) }

    LaunchedEffect(tappedPoint) {
        userLocation.value = viewModel.getUserLocation()

        coroutineScope {
            launch {
                pointElevation.value = viewModel.getElevation(tappedPoint.latitude(), tappedPoint.longitude())
            }
            launch {
                userLocation.value.let {
                    if (it != null) {
                        userElevation.value = viewModel.getElevation(it.latitude(), it.longitude())
                    }
                }
            }
        }
    }

    val latitude = tappedPoint.latitude()
    val longitude = tappedPoint.longitude()

    val distance = userLocation.value?.let { loc ->
        TurfMeasurement.distance(loc, tappedPoint, "kilometers")
    }

    val bearing = userLocation.value?.let { loc ->
        TurfMeasurement.bearing(loc, tappedPoint)
    }

    val lat = BigDecimal(latitude).setScale(6, RoundingMode.HALF_EVEN).toDouble()
    val lng = BigDecimal(longitude).setScale(6, RoundingMode.HALF_EVEN).toDouble()

    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Tapped Point:")
            Text("LAT: $lat")
            Text("LNG: $lng")
            pointElevation.value?.let {
                Text("Elevation: ${it.roundToInt()}m AMSL")
            }
            distance?.let {
                var dist = BigDecimal(it).setScale(1, RoundingMode.HALF_EVEN).toDouble()
                Text("Distance from you: $dist km")
            }
            bearing?.let {
                val normalizedBearing = if (bearing >= 0) bearing else 360 + bearing
                val brg = normalizedBearing.roundToInt()

                Text("Bearing from you: $brg deg T")
            }
            userElevation.let {
                val relative = if (userElevation.value != null) pointElevation.value!! - userElevation.value!! else 0.0;
                var rel = BigDecimal(relative).setScale(1, RoundingMode.HALF_EVEN).toDouble()

                Text("Relative elevation: $rel m")
            }
        }
    }
}
