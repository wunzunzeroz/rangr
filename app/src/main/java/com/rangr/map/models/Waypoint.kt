package com.rangr.map.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Waypoint(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val markerType: WaypointIconType,
    val position: GeoPosition,
    val description: String
)

