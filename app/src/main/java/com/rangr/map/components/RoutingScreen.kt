package com.rangr.map.components

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import com.rangr.map.MapViewModel
import com.rangr.map.models.Route
import com.rangr.ui.theme.RangrDark
import com.rangr.ui.theme.RangrOrange
import com.rangr.util.Utils
import java.lang.ref.WeakReference


@Composable
fun RoutingScreen(model: MapViewModel) {
    val route by model.route.observeAsState(Route.empty())

    val dist = Utils.RoundNumberToDp(route.distance, 1)

    val distance = getDistance(route.distance)
    val chartProducer = model.routeProfile

    Box {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(RangrDark)
                    .padding(8.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(8.dp),
                ) {
                    Text("ROUTE: $distance", fontSize = 5.em, color = RangrOrange)
                }
                Spacer(modifier = Modifier.weight(1f))
                TextButton(
                    text = "CLEAR",
                    onClick = {
                        model.clearRoute()
                    },
                )
            }
            MapViewContainer(model)
//            Chart(
//                chart = lineChart(spacing = 1.dp),
//                chartModelProducer = chartProducer,
//                startAxis = rememberStartAxis(),
//                bottomAxis = rememberBottomAxis(),
//            )
        }
    }
}

private fun getDistance(rawDistance: Double): String {
    val dist = Utils.RoundNumberToDp(rawDistance, 1)

    return if (dist > 1000) {
        "${Utils.RoundNumberToDp(dist / 1000, 1)} km"
    } else {
        "$dist m"
    }
}

class LocationPermissionHelper(val activityRef: WeakReference<Activity>) {
    private lateinit var permissionsManager: PermissionsManager

    fun checkPermissions(onMapReady: () -> Unit) {
        activityRef.get()?.let { activity: Activity ->
            if (PermissionsManager.areLocationPermissionsGranted(activity)) {
                onMapReady()
            } else {
                permissionsManager = PermissionsManager(object : PermissionsListener {

                    override fun onExplanationNeeded(permissionsToExplain: List<String>) {
                        activityRef.get()?.let {
                            Toast.makeText(
                                it, "You need to accept location permissions.", Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onPermissionResult(granted: Boolean) {
                        activityRef.get()?.let {
                            if (granted) {
                                onMapReady()
                            } else {
                                it.finish()
                            }
                        }
                    }
                })
                permissionsManager.requestLocationPermissions(activity)
            }
        }
    }

    fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}