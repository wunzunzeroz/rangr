package com.rangr.map.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import com.rangr.map.MapViewModel
import com.rangr.map.models.GeoPosition
import com.rangr.map.models.GridRef
import com.rangr.map.models.SheetType
import com.rangr.ui.theme.RangrDark
import com.rangr.ui.theme.RangrOrange

@Composable
fun GoToLocationBottomSheet(model: MapViewModel) {

    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("GRID REF", "LAT/LNG DEC", "LAT/LNG DMS")

    // Point state to remember the coordinates
    var position by remember { mutableStateOf(GeoPosition(0.0, 0.0)) }

    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("GO TO LOCATION", fontSize = 5.em)
        TabRow(selectedTabIndex = selectedTab, contentColor = RangrOrange, backgroundColor = RangrDark) {
            tabs.forEachIndexed { index, title ->
                Tab(selected = index == selectedTab, onClick = { selectedTab = index }, text = { Text(title) })
            }
        }

        when (selectedTab) {
            0 -> GridReferenceInput(onCoordinateChange = { gp -> position = gp })
            1 -> DecimalInput(onCoordinateChange = { lat, lon -> position = GeoPosition(lat, lon) })
            2 -> DMSInput(onCoordinateChange = { lat, lon -> position = GeoPosition(lat, lon) })
        }

        TextButton(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .fillMaxWidth(), onClick = {
                println("POINT: ${position.latLngDegreesMinutes}")
                val pt = position.toPoint()

                model.addTapPoint(pt)
                model.scrollToLocation(pt)
                model.setBottomSheetType(SheetType.LocationDetail)
            }, text = "GO TO"
        )

        Spacer(Modifier.height(10.dp))
    }
}

// Dummy composable functions for different inputs. Replace with actual input handling logic.
@Composable
fun GridReferenceInput(onCoordinateChange: (GeoPosition) -> Unit) {
    var eastings by remember { mutableStateOf("") }
    var northings by remember { mutableStateOf("") }

    var eastValid by remember { mutableStateOf(false) }
    var northValid by remember { mutableStateOf(false) }

    Column {
        Spacer(Modifier.height(10.dp))
        OutlinedTextField(
            value = eastings,
            onValueChange = { value ->
                if (value.length <= 7 && value.all { it.isDigit() }) {
                    eastings = value
                    eastValid = true
                    // Optionally, call onCoordinateChange when both fields are valid

                    if (northValid) {
                        val position = GeoPosition.fromGridRef(GridRef(eastings.toInt(), northings.toInt()))

                        onCoordinateChange(position)
                    }
                }
            },
            label = { Text("Eastings") },
            colors = TextFieldDefaults.textFieldColors(
                textColor = RangrOrange, // Text color
                cursorColor = RangrOrange, // Cursor color
                focusedIndicatorColor = RangrOrange, // Underline color when focused
                unfocusedIndicatorColor = RangrOrange, // Underline color when unfocused
                focusedLabelColor = RangrOrange, // Label color when focused
                unfocusedLabelColor = RangrOrange, // Label co
            ),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number, imeAction = ImeAction.Next
            ),
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = northings,
            onValueChange = { value ->
                if (value.length <= 7 && value.all { it.isDigit() }) {
                    northings = value
                    northValid = true
                    // Optionally, call onCoordinateChange when both fields are valid
                    if (eastValid) {
                        val position = GeoPosition.fromGridRef(GridRef(eastings.toInt(), northings.toInt()))

                        onCoordinateChange(position)
                    }
                }
            },
            label = { Text("Northings") },
            colors = TextFieldDefaults.textFieldColors(
                textColor = RangrOrange, // Text color
                cursorColor = RangrOrange, // Cursor color
                focusedIndicatorColor = RangrOrange, // Underline color when focused
                unfocusedIndicatorColor = RangrOrange, // Underline color when unfocused
                focusedLabelColor = RangrOrange, // Label color when focused
                unfocusedLabelColor = RangrOrange, // Label co
            ),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number, imeAction = ImeAction.Next
            ),
        )
        Spacer(Modifier.height(20.dp))

        // You might want to convert eastings and northings to lat, lon and call onCoordinateChange here
        // This depends on your specific conversion logic
    }
}

@Composable
fun DecimalInput(onCoordinateChange: (Double, Double) -> Unit) {
    // Implement decimal lat/lon input
    // Call onCoordinateChange with the lat, lon
}

@Composable
fun DMSInput(onCoordinateChange: (Double, Double) -> Unit) {
    // Implement degrees/minutes/seconds input and conversion logic
    // Call onCoordinateChange with the converted lat, lon
}
