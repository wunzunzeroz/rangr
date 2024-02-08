package com.rangr.data.dao

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.room.TypeConverter
import com.rangr.map.models.GeoPosition

class Converters {
    @TypeConverter
    fun positionToString(pos: GeoPosition): String {
        return "${pos.latLngDecimal.latitude},${pos.latLngDecimal.longitude}"
    }

    @TypeConverter
    fun stringToPosition(str: String): GeoPosition {
        val latLng = str.split(",")
        val lat = latLng.first()
        val lng = latLng.last()

        return GeoPosition(lat.toDouble(), lng.toDouble())
    }
    @TypeConverter
    fun fromColor(color: Color): Long {
        return color.toArgb().toLong() // Convert Color to Long for storage
    }

    @TypeConverter
    fun toColor(value: Long): Color {
        return Color(value.toInt()) // Convert Long back to Color
    }

}