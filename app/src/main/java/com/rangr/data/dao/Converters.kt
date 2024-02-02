package com.rangr.data.dao

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
}