package com.rangr.map.models

import com.mapbox.geojson.Point

data class Route(val waypoints: List<Point>, val distance: Double, val elevationProfile: List<Point>) {
    companion object {
        fun empty(): Route {
            return Route(emptyList(), 0.0, emptyList())
        }
    }
}
