package com.rangr.map.models

data class LatLngDegreesMinutes(val latitude: DegreesMinutes, val longitude: DegreesMinutes)

data class DegreesMinutes(val degrees: Int, val minutes: Double, val cardinalDirection: CardinalDirection) {
    override fun toString(): String {
        return "$degrees° $minutes' $cardinalDirection"
    }
}

enum class CardinalDirection {
    N, S, E, W;

    companion object {
        fun parse(str: String): CardinalDirection {
            val result = entries.firstOrNull { it.name == str }

            return result ?: throw IllegalStateException("Unable to parse string in CardinalDirection: $str")
        }
    }
}
