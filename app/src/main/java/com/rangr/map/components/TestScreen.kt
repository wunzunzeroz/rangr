package com.rangr.map.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import com.rangr.map.MapViewModel

@Composable
fun TestScreen(model: MapViewModel) {
    val waypoints = model.waypoints.observeAsState()

    if (waypoints.value == null) {
        return Text("No Waypoints")
    }

    Column {
        Text("WAYPOINTS")
        waypoints.value!!.map {
            Text("Name: ${it?.name}")
            Text("Lat: ${it?.position?.latLngDecimal?.latitude}")
            Text("LNG: ${it?.position?.latLngDecimal?.longitude}")
            Text("Description: ${it?.description}")
        }
    }
}