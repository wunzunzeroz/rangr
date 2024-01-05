package com.rangr.map.components

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.maps.MapView
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.rangr.map.MapViewModel
import com.rangr.map.models.Route
import java.lang.ref.WeakReference


@Composable
fun RoutingScreen(model: MapViewModel) {
    val route by model.route.observeAsState(Route.empty())

    val chartProducer = model.routeProfile

    Box {
        MapViewContainer(model)
        Column(modifier = Modifier.height(50.dp)) {
            Text(text = "ROUTING")
            Row(modifier = Modifier.fillMaxWidth()) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(8.dp)
                ) {
//                    Text("ACTIVE ROUTE", color = Color.White)
                    Text("DISTANCE: ${route.distance} m", color = Color(0xFFFF4F00))
                }
                Spacer(modifier = Modifier.weight(1f))
                Button(onClick = {
                    model.clearRoute()
                }) {
                    Text("Clear Route")
                }
            }
            Chart(
                chart = lineChart(spacing = 1.dp),
                chartModelProducer = chartProducer,
                startAxis = rememberStartAxis(),
                bottomAxis = rememberBottomAxis(),
            )
        }
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