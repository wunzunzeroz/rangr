package com.rangr.map.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mapbox.geojson.Point
import com.rangr.map.MapViewModel
import java.math.BigDecimal
import java.math.RoundingMode

@Composable
fun LocationDetailsBottomSheet(tappedPoint: Point?, mapViewModel: MapViewModel) {
    if (tappedPoint == null ) {
        return
    }
    var buttonClicked by remember { mutableStateOf(false) }



    val lat = BigDecimal(tappedPoint.latitude()).setScale(6, RoundingMode.HALF_EVEN).toDouble()
    val lng = BigDecimal(tappedPoint.longitude()).setScale(6, RoundingMode.HALF_EVEN).toDouble()

    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("TAPPED POINT")
            Text("LAT: $lat, LNG: $lng")
            Row {
                Button(onClick = { mapViewModel.createWaypoint(tappedPoint) }, modifier = Modifier.padding(8.dp)) {
                    Text("Create Waypoint")
                }
                Button(onClick = {buttonClicked = true}, modifier = Modifier.padding(8.dp)) {
                    Text("Add to Route")
                }
            }
        }
    }

    if (buttonClicked) {
        LaunchedEffect(Unit) {
            mapViewModel.addToRoute(tappedPoint)
            buttonClicked = false
        }
    }
}
