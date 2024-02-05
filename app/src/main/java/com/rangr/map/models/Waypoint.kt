package com.rangr.map.models

import androidx.compose.ui.graphics.Color
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Waypoint(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val markerType: WaypointIconType,
    val markerColor: Long,
    val position: GeoPosition,
    val description: String
) {
    fun getColor(): Color {
        return Color(markerColor.toInt()) // Convert Long back to Color
    }
}

