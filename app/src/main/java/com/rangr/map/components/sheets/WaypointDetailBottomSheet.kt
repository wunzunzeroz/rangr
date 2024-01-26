package com.rangr.map.components.sheets

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import com.rangr.map.MapViewModel
import com.rangr.map.models.GeoPosition
import com.rangr.ui.theme.RangrDark
import com.rangr.ui.theme.RangrOrange
import com.rangr.util.ClipboardUtils

@Composable
fun WaypointDetailBottomSheet(model: MapViewModel) {
    val waypoint = model.selectedWaypoint.observeAsState()
    val wpt = waypoint.value
    val ctx = LocalContext.current

    if (wpt == null) {
        return Text("Waypoint is null")
    }

    val gp = GeoPosition(wpt.latitude, wpt.longitude)

    var showDialog by remember { mutableStateOf(false) }
    val name = wpt.name.ifBlank { "WAYPOINT" }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Spacer(modifier = Modifier.height(5.dp))
        Text(name, fontSize = 5.em)
        Spacer(modifier = Modifier.height(5.dp))
        Divider()
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
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                ClipboardUtils.copyToClipboard(ctx, "Location Data", gp.toShareableString())
                Toast.makeText(ctx, "Copied location to clipboard", Toast.LENGTH_SHORT).show()
                      },
            colors = ButtonDefaults.buttonColors(
                contentColor = RangrDark, backgroundColor = Color.Gray
            ),
        ) {
            Text(text = "COPY TO CLIPBOARD")
        }

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

