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
import com.rangr.map.models.SheetType
import java.math.BigDecimal
import java.math.RoundingMode

@Composable
fun LocationDetailBottomSheet(mapViewModel: MapViewModel) {
    val tappedPoint = mapViewModel.tappedPoint.observeAsState(null)
    val tp = tappedPoint.value

    var buttonClicked by remember { mutableStateOf(false) }

    Column(
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .height(150.dp)
            .fillMaxWidth()
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("POINT", fontSize = 4.em)
        Spacer(modifier = Modifier.height(20.dp))
        tp.let {
            if (tp != null) {
                val lat = BigDecimal(tp.latitude()).setScale(6, RoundingMode.HALF_EVEN).toDouble()
                val lng = BigDecimal(tp.longitude()).setScale(6, RoundingMode.HALF_EVEN).toDouble()

                Text("LATITUDE: $lat")
                Text("LONGITUDE: $lng")

                Spacer(modifier = Modifier.height(10.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    TextButton(text = "CREATE WAYPOINT", onClick = {
                        mapViewModel.setBottomSheetType(SheetType.WaypointCreation)
                    })
                    TextButton(text = "ADD TO ROUTE", onClick = { buttonClicked = true })
                }
            }
        }
    }

    if (buttonClicked) {
        LaunchedEffect(Unit) {
            if (tp != null) {
                println("TAPPED ADD TO ROUTE")
                mapViewModel.addToRoute(tp)
                buttonClicked = false
            }
        }
    }
}
