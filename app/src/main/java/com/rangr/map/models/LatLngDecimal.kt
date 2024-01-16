package com.rangr.map.models

import com.rangr.util.Utils

data class LatLngDecimal(var latitude: Double, var longitude: Double) {
    init {
        latitude = Utils.RoundNumberToDp(latitude, 6)
        longitude = Utils.RoundNumberToDp(longitude, 6)
    }
}
