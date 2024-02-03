package com.rangr.map.models

import android.content.res.Resources
import android.graphics.Bitmap
import com.rangr.R

class WaypointMarkerFactory(private val markers: Map<WaypointIconType, Bitmap>) {
    fun getMarkerForType(type: WaypointIconType): Bitmap {
        return markers[type] ?: throw IllegalStateException("No marker defined for type: $type")
    }
}