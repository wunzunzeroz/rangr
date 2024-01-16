package com.rangr.map.models

data class LatLngDegreesMinutes(val latitude: DegreesMinutes, val longitude: DegreesMinutes)

data class DegreesMinutes(val degrees: Int, val minutes: Double, val cardinalDirection: CardinalDirection) {
    override fun toString(): String {
        return "$degrees° $minutes' $cardinalDirection"
    }
}

enum class CardinalDirection {
    N, S, E, W
}
