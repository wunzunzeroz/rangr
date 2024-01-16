package com.rangr.map.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import com.rangr.map.CoordinateConversion
import com.rangr.map.MapViewModel
import com.rangr.map.models.GeoPosition
import com.rangr.ui.theme.RangrDark
import com.rangr.ui.theme.RangrOrange

@Composable
fun WaypointDetailBottomSheet(model: MapViewModel) {
    val waypoint = model.selectedWaypoint.observeAsState()
    val wpt = waypoint.value

    if (wpt == null) {
        return Text("Waypoint is null")
    }

    val gp = GeoPosition(wpt.latitude, wpt.longitude)

    var showDialog by remember { mutableStateOf(false) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Text(wpt.name, fontSize = 5.em)
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

        Button(
            modifier = Modifier.fillMaxWidth(), onClick = { showDialog = true },
            colors = ButtonDefaults.buttonColors(
                contentColor = RangrDark, backgroundColor = RangrOrange
            ),
        ) {
            Text(text = "DELETE")
        }
    }

    if (showDialog) {
        AlertDialog(onDismissRequest = { showDialog = false },
            title = { Text("DELETE WAYPOINT") },
            text = { Text("Are you sure you want to delete waypoint?") },
            confirmButton = {
                Button(
                    onClick = {
                        showDialog = false
                        model.deleteWaypoint(wpt)
                    }, colors = ButtonDefaults.buttonColors(
                        contentColor = RangrDark, backgroundColor = RangrOrange
                    )
                ) {
                    Text("Yes")
                }
            },
            dismissButton = {
                Button(
                    onClick = { showDialog = false },
                    colors = ButtonDefaults.buttonColors(
                        contentColor = RangrDark, backgroundColor = Color.Gray
                    ),
                ) {
                    Text("No")
                }
            })
    }
}

