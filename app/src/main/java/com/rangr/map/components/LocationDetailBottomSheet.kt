package com.rangr.map.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rangr.map.MapViewModel
import java.math.BigDecimal
import java.math.RoundingMode

@Composable
fun LocationDetailBottomSheet(mapViewModel: MapViewModel) {
    println("HELLO SIR!!")
    val tappedPoint = mapViewModel.tappedPoint.observeAsState(null)
//
//    if (tappedPoint.value == null) {
//        return
//    }
//
    val tp = tappedPoint.value
//
    var buttonClicked by remember { mutableStateOf(false) }


    Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("TAPPED POINT")
        tp.let {
            if (tp != null) {
                val lat = BigDecimal(tp.latitude()).setScale(6, RoundingMode.HALF_EVEN).toDouble()
                val lng = BigDecimal(tp.longitude()).setScale(6, RoundingMode.HALF_EVEN).toDouble()

                Text("LAT: $lat, LNG: $lng")

                Row {
                    Button(onClick = { mapViewModel.createWaypoint(tp) }, modifier = Modifier.padding(8.dp)) {
                        Text("Create Waypoint")
                    }
                    Button(onClick = { buttonClicked = true }, modifier = Modifier.padding(8.dp)) {
                        Text("Add to Route")
                    }
                }
            }
        }
    }

    if (buttonClicked) {
        LaunchedEffect(Unit) {
            if (tp != null) {
                mapViewModel.addToRoute(tp)
                buttonClicked = false
            }
        }
    }
}
