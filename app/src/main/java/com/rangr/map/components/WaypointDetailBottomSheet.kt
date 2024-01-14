package com.rangr.map.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import com.rangr.map.MapViewModel

@Composable
fun WaypointDetailBottomSheet(model: MapViewModel) {
    val waypoint = model.selectedWaypoint.observeAsState()
    val wpt = waypoint.value

    Column {
        Text("Waypoint:")
        Text("Name: ${wpt?.name}")
    }
}