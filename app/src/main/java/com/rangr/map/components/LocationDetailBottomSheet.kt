package com.rangr.map.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import com.rangr.map.MapViewModel
import com.rangr.map.models.GeoPosition
import com.rangr.map.models.SheetType

@Composable
fun LocationDetailBottomSheet(mapViewModel: MapViewModel) {
    val tappedPoint = mapViewModel.tappedPoint.observeAsState(null)

    val tp = tappedPoint.value ?: return Text("Tapped point is null")

    val gp = GeoPosition(tp.latitude(), tp.longitude())

    var buttonClicked by remember { mutableStateOf(false) }

    Column(
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("TAPPED POINT", fontSize = 5.em)
        Spacer(modifier = Modifier.height(20.dp))

        Text("GRID REFERENCE", fontSize = 3.em)
        Text("E ${gp.gridReference.eastings}")
        Text("N ${gp.gridReference.northings}")
        Spacer(modifier = Modifier.height(20.dp))

        Text("LAT / LNG (DECIMAL)", fontSize = 3.em)
        Text("LAT: ${gp.latLngDecimal.latitude}")
        Text("LNG: ${gp.latLngDecimal.longitude}")
        Spacer(modifier = Modifier.height(20.dp))

        Text("LAT / LNG (MINUTES)", fontSize = 3.em)
        Text("LAT: ${gp.latLngDegreesMinutes.latitude}")
        Text("LNG: ${gp.latLngDegreesMinutes.longitude}")
        Spacer(modifier = Modifier.height(20.dp))

        TextButton(text = "CREATE WAYPOINT", onClick = {
            mapViewModel.setBottomSheetType(SheetType.WaypointCreation)
        }, modifier = Modifier.fillMaxWidth())
        TextButton(text = "ADD TO ROUTE", onClick = { buttonClicked = true }, modifier = Modifier.fillMaxWidth())
    }

    if (buttonClicked) {
        LaunchedEffect(Unit) {
            println("TAPPED ADD TO ROUTE")
            mapViewModel.addToRoute(tp)
            buttonClicked = false
        }
    }
}
