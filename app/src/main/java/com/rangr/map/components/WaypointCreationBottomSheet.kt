package com.rangr.map.components

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import com.rangr.map.MapViewModel
import com.rangr.ui.theme.RangrOrange
import com.rangr.util.Utils

@Composable
fun WaypointCreationBottomSheet(model: MapViewModel) {
    val point = model.tappedPoint.observeAsState()

    val ctx = LocalContext.current

    if (point.value == null) return // TODO - Fix

    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    val lat = Utils.RoundNumberToDp(point.value!!.latitude(), 6)
    val lon = Utils.RoundNumberToDp(point.value!!.longitude(), 6)

    var latitude by remember { mutableDoubleStateOf(lat) }
    var longitude by remember { mutableDoubleStateOf(lon) }

    var buttonClicked by remember { mutableStateOf(false) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Text("CREATE WAYPOINT", fontSize = 4.em)
        Spacer(modifier = Modifier.height(20.dp))

        TextField(
            value = latitude.toString(),
            onValueChange = { latitude = it.toDouble() },
            label = { Text("LAT") },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number, imeAction = ImeAction.Next
            ),
            colors = TextFieldDefaults.textFieldColors(
                textColor = RangrOrange, // Text color
                cursorColor = RangrOrange, // Cursor color
                focusedIndicatorColor = RangrOrange, // Underline color when focused
                unfocusedIndicatorColor = RangrOrange, // Underline color when unfocused
                focusedLabelColor = RangrOrange, // Label color when focused
                unfocusedLabelColor = RangrOrange, // Label co
            )
        )

        TextField(
            value = longitude.toString(),
            onValueChange = { latitude = it.toDouble() },
            label = { Text("LNG") },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number, imeAction = ImeAction.Next
            ),
            colors = TextFieldDefaults.textFieldColors(
                textColor = RangrOrange, // Text color
                cursorColor = RangrOrange, // Cursor color
                focusedIndicatorColor = RangrOrange, // Underline color when focused
                unfocusedIndicatorColor = RangrOrange, // Underline color when unfocused
                focusedLabelColor = RangrOrange, // Label color when focused
                unfocusedLabelColor = RangrOrange, // Label co
            )
        )

        TextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("NAME") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            colors = TextFieldDefaults.textFieldColors(
                textColor = RangrOrange, // Text color
                cursorColor = RangrOrange, // Cursor color
                focusedIndicatorColor = RangrOrange, // Underline color when focused
                unfocusedIndicatorColor = RangrOrange, // Underline color when unfocused
                focusedLabelColor = RangrOrange, // Label color when focused
                unfocusedLabelColor = RangrOrange, // Label co
            )
        )

        // Description TextField
        TextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("DESCRIPTION") },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            colors = TextFieldDefaults.textFieldColors(
                textColor = RangrOrange, // Text color
                cursorColor = RangrOrange, // Cursor color
                focusedIndicatorColor = RangrOrange, // Underline color when focused
                unfocusedIndicatorColor = RangrOrange, // Underline color when unfocused
                focusedLabelColor = RangrOrange, // Label color when focused
                unfocusedLabelColor = RangrOrange, // Label co
            )
        )

        Spacer(modifier = Modifier.height(10.dp))

        TextButton(text = "CREATE WAYPOINT", onClick = {
            buttonClicked = true
        })
    }

    if (buttonClicked) {
        LaunchedEffect(Unit) {
            model.createWaypoint(latitude, longitude, name, description)
            model.setBottomSheetVisible(false)
            Toast.makeText(ctx, "Created waypoint successfully", Toast.LENGTH_SHORT).show()

            buttonClicked = false
        }
    }
}