package com.rangr.map.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.rangr.map.MapViewModel
import com.rangr.map.models.MapType

@Composable
fun MapTypeSheet(model: MapViewModel) {
    val mapStyles = listOf("OUTDOORS", "SATELLITE", "MARINE", "TOPOGRAPHIC")

    LazyColumn {
        items(mapStyles) { style ->
            ListItem(style, model)
        }
    }
}

@Composable
fun ListItem(style: String, model: MapViewModel) {
    TextButton(
        colors = ButtonDefaults.buttonColors(contentColor = Color(0xFFFF4F00), backgroundColor = Color.Black),
        onClick = {
            when (style) {
                "OUTDOORS" -> {
                    model.setMapType(MapType.Outdoor)
                }

                "SATELLITE" -> {
                    model.setMapType(MapType.Satellite)
                }

                "MARINE" -> {
                    model.setMapType(MapType.Marine)
                }

                "TOPOGRAPHIC" -> {
                    model.setMapType(MapType.Topographic)
                }
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Text(style, modifier = Modifier.padding(8.dp))
    }
}

