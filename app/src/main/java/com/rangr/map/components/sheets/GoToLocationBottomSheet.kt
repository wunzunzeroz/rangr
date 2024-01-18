package com.rangr.map.components.sheets

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
import com.rangr.map.models.CardinalDirection
import com.rangr.map.models.GeoPosition
import com.rangr.map.models.GridRef
import com.rangr.map.models.SheetType
import com.rangr.ui.theme.RangrDark
import com.rangr.ui.theme.RangrOrange

@Composable
fun GoToLocationBottomSheet(model: MapViewModel) {

    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("GRID REF", "LAT/LNG DD", "LAT/LNG DDM")

    // Point state to remember the coordinates
    var position by remember { mutableStateOf(GeoPosition(0.0, 0.0)) }

    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(modifier = Modifier.height(5.dp))
        Text("GO TO LOCATION", fontSize = 5.em)
        Spacer(modifier = Modifier.height(5.dp))
        Divider()
        Spacer(modifier = Modifier.height(20.dp))

        TabRow(selectedTabIndex = selectedTab, contentColor = RangrOrange, backgroundColor = RangrDark) {
            tabs.forEachIndexed { index, title ->
                Tab(selected = index == selectedTab, onClick = { selectedTab = index }, text = { Text(title) })
            }
        }

        when (selectedTab) {
            0 -> GridReferenceInput(onCoordinateChange = { gp -> position = gp })
            1 -> DecimalInput(onCoordinateChange = { gp -> position = gp })
            2 -> DMSInput(onCoordinateChange = { gp -> position = gp })
        }

        com.rangr.map.components.TextButton(
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
fun DecimalInput(onCoordinateChange: (GeoPosition) -> Unit) {
    var lat by remember { mutableStateOf("") }
    var lng by remember { mutableStateOf("") }

    Column {
        Spacer(Modifier.height(10.dp))
        OutlinedTextField(
            value = lat,
            onValueChange = { value ->
                lat = value
                if (lat.length > 3 && lng.length > 3) {

                    val position = GeoPosition(lat.toDouble(), lng.toDouble())
                    println("DBG: LAT: $lat, LNG: $lng")
                    println("CREATE POINT: ${position.latLngDegreesMinutes}")

                    onCoordinateChange(position)
                }
            },
            label = { Text("LATITUDE") },
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
            value = lng,
            onValueChange = { value ->
                lng = value
                if (value.length > 3 && lat.length > 3) {

                    val position = GeoPosition(lat.toDouble(), lng.toDouble())
                    println("DBG: LAT: $lat, LNG: $lng")
                    println("CREATE POINT: ${position.latLngDegreesMinutes}")

                    onCoordinateChange(position)
                }
            },
            label = { Text("LONGITUDE") },
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
fun DMSInput(onCoordinateChange: (GeoPosition) -> Unit) {
    var latDeg by remember { mutableStateOf("") }
    var latMin by remember { mutableStateOf("") }
    var latDir by remember { mutableStateOf("S") }

    var lngDeg by remember { mutableStateOf("") }
    var lngMin by remember { mutableStateOf("") }
    var lngDir by remember { mutableStateOf("E") }

    Column {
        Spacer(Modifier.height(10.dp))
        Row() {
            OutlinedTextField(
                modifier = Modifier.weight(0.8f),
                value = latDeg,
                onValueChange = { value ->
                    latDeg = value
                    if (latDeg.length > 1 && lngDeg.length > 1 && latMin.length > 1 && lngMin.length > 1) {

                        val position = GeoPosition.fromDegreesMinutes(
                            latDeg.toInt(),
                            latMin.toDouble(),
                            CardinalDirection.parse(latDir.first().toString()),
                            lngDeg.toInt(),
                            lngMin.toDouble(),
                            CardinalDirection.parse(lngDir.first().toString())
                        )
                        println("DBG: LAT: $latDeg, LNG: $lngDeg")
                        println("CREATE POINT: ${position.latLngDegreesMinutes}")

                        onCoordinateChange(position)
                    }
                },
                label = { Text("LAT DEGREES") },
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
            Spacer(Modifier.width(10.dp))
            OutlinedTextField(
                value = latMin,
                modifier = Modifier.weight(1f),
                onValueChange = { value ->
                    latMin = value
                    if (latDeg.length > 1 && lngDeg.length > 1 && latMin.length > 1 && lngMin.length > 1) {


                        val position = GeoPosition.fromDegreesMinutes(
                            latDeg.toInt(),
                            latMin.toDouble(),
                            CardinalDirection.parse(latDir.first().toString()),
                            lngDeg.toInt(),
                            lngMin.toDouble(),
                            CardinalDirection.parse(lngDir.first().toString())
                        )
                        println("DBG: LAT: $latDeg, LNG: $lngDeg")
                        println("CREATE POINT: ${position.latLngDegreesMinutes}")

                        onCoordinateChange(position)
                    }
                },
                label = { Text("LAT MINUTES") },
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
            Spacer(Modifier.width(10.dp))
            OutlinedTextField(
                value = latDir,
                modifier = Modifier.weight(0.3f),
                onValueChange = { value ->
                    latDir = value.uppercase()
                    if (latDeg.length > 1 && lngDeg.length > 1) {
                        val position = GeoPosition.fromDegreesMinutes(
                            latDeg.toInt(),
                            latMin.toDouble(),
                            CardinalDirection.parse(latDir.first().toString()),
                            lngDeg.toInt(),
                            lngMin.toDouble(),
                            CardinalDirection.parse(lngDir.first().toString())
                        )
                        println("DBG: LAT: $latDeg, LNG: $lngDeg")
                        println("CREATE POINT: ${position.latLngDegreesMinutes}")

                        onCoordinateChange(position)
                    }
                },
                label = { Text("DIR") },
                colors = TextFieldDefaults.textFieldColors(
                    textColor = RangrOrange, // Text color
                    cursorColor = RangrOrange, // Cursor color
                    focusedIndicatorColor = RangrOrange, // Underline color when focused
                    unfocusedIndicatorColor = RangrOrange, // Underline color when unfocused
                    focusedLabelColor = RangrOrange, // Label color when focused
                    unfocusedLabelColor = RangrOrange, // Label co
                ),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Ascii, imeAction = ImeAction.Next
                ),
            )
        }
        Spacer(modifier = Modifier.height(10.dp))

        Row() {
            OutlinedTextField(
                modifier = Modifier.weight(0.8f),
                value = lngDeg,
                onValueChange = { value ->
                    lngDeg = value
                    if (latDeg.length > 1 && lngDeg.length > 1 && latMin.length > 1 && lngMin.length > 1) {

                        val position = GeoPosition.fromDegreesMinutes(
                            latDeg.toInt(),
                            latMin.toDouble(),
                            CardinalDirection.parse(latDir.first().toString()),
                            lngDeg.toInt(),
                            lngMin.toDouble(),
                            CardinalDirection.parse(lngDir.first().toString())
                        )
                        println("DBG: LAT: $latDeg, LNG: $lngDeg")
                        println("CREATE POINT: ${position.latLngDegreesMinutes}")

                        onCoordinateChange(position)
                    }
                },
                label = { Text("LNG DEGREES") },
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
            Spacer(Modifier.width(10.dp))
            OutlinedTextField(
                value = lngMin,
                modifier = Modifier.weight(1f),
                onValueChange = { value ->
                    lngMin = value
                    if (latDeg.length > 1 && lngDeg.length > 1 && latMin.length > 1 && lngMin.length > 1) {

                        val position = GeoPosition.fromDegreesMinutes(
                            latDeg.toInt(),
                            latMin.toDouble(),
                            CardinalDirection.parse(latDir.first().toString()),
                            lngDeg.toInt(),
                            lngMin.toDouble(),
                            CardinalDirection.parse(lngDir.first().toString())
                        )
                        println("DBG: LAT: $latDeg, LNG: $lngDeg")
                        println("CREATE POINT: ${position.latLngDegreesMinutes}")

                        onCoordinateChange(position)
                    }
                },
                label = { Text("LNG MINUTES") },
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
            Spacer(Modifier.width(10.dp))
            OutlinedTextField(
                value = lngDir,
                modifier = Modifier.weight(0.3f),
                onValueChange = { value ->
                    lngDir = value.uppercase()
                    if (latDeg.length > 1 && lngDeg.length > 1) {
                        val position = GeoPosition.fromDegreesMinutes(
                            latDeg.toInt(),
                            latMin.toDouble(),
                            CardinalDirection.parse(latDir.first().toString()),
                            lngDeg.toInt(),
                            lngMin.toDouble(),
                            CardinalDirection.parse(lngDir.first().toString())
                        )
                        println("DBG: LAT: $latDeg, LNG: $lngDeg")
                        println("CREATE POINT: ${position.latLngDegreesMinutes}")

                        onCoordinateChange(position)
                    }
                },
                label = { Text("DIR") },
                colors = TextFieldDefaults.textFieldColors(
                    textColor = RangrOrange, // Text color
                    cursorColor = RangrOrange, // Cursor color
                    focusedIndicatorColor = RangrOrange, // Underline color when focused
                    unfocusedIndicatorColor = RangrOrange, // Underline color when unfocused
                    focusedLabelColor = RangrOrange, // Label color when focused
                    unfocusedLabelColor = RangrOrange, // Label co
                ),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Ascii, imeAction = ImeAction.Next
                ),
            )
        }
        Spacer(Modifier.height(20.dp))

        // You might want to convert eastings and northings to lat, lon and call onCoordinateChange here
        // This depends on your specific conversion logic
    }
}
