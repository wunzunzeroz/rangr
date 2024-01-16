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
import com.rangr.ui.theme.RangrDark
import com.rangr.ui.theme.RangrOrange

@Composable
fun WaypointDetailBottomSheet(model: MapViewModel) {
    val waypoint = model.selectedWaypoint.observeAsState()
    val wpt = waypoint.value

    if (wpt == null) {
        return Text("Waypoint is null")
    }

    var showDialog by remember { mutableStateOf(false) }

    val gr = CoordinateConversion.LatLngToGridRef(wpt.latitude, wpt.longitude)
    val degreesMinutes = CoordinateConversion.LatLngDecimalToMinutes(wpt.latitude, wpt.longitude)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Text(wpt.name, fontSize = 5.em)
        Spacer(modifier = Modifier.height(20.dp))

        Text("GRID REFERENCE", fontSize = 3.em)
        Text("E ${gr?.eastings}")
        Text("N ${gr?.northings}")
        Spacer(modifier = Modifier.height(20.dp))

        Text("LAT / LNG (DECIMAL)", fontSize = 3.em)
        Text("LAT: ${wpt.latitude}")
        Text("LNG: ${wpt.longitude}")
        Spacer(modifier = Modifier.height(20.dp))

        Text("LAT / LNG (MINUTES)", fontSize = 3.em)
        Text("LAT: ${degreesMinutes.x}")
        Text("LNG: ${degreesMinutes.y}")
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

