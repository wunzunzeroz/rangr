package com.rangr.map.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import com.rangr.map.MapViewModel
import com.rangr.map.models.MapType
import com.rangr.ui.theme.RangrDark
import com.rangr.ui.theme.RangrOrange

@Composable
fun MapTypeBottomSheet(model: MapViewModel) {
    val mapStyles = listOf("OUTDOORS", "SATELLITE", "MARINE", "TOPOGRAPHIC")

    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(8.dp)) {
        Text("MAP TYPE")
        LazyColumn {
            items(mapStyles) { style ->
                ListItem(style, model)
            }
        }
    }
}

@Composable
fun ListItem(style: String, model: MapViewModel) {
    TextButton(
        colors = ButtonDefaults.buttonColors(contentColor = RangrDark, backgroundColor = RangrOrange),
        shape = RoundedCornerShape(10.dp),
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
        Text(style, fontSize = 3.em, fontWeight = FontWeight.Bold, modifier = Modifier.padding(8.dp))
    }
}

