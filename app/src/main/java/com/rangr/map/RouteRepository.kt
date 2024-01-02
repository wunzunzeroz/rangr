package com.rangr.map

import com.mapbox.geojson.LineString
import com.mapbox.geojson.Point
import com.mapbox.turf.TurfConstants
import com.mapbox.turf.TurfMeasurement
import com.mapbox.turf.TurfMisc
import com.rangr.map.models.Route

class RouteRepository {
    private val _elevationService = ElevationService()

    private var _route: Route = Route.empty()

    fun getRoute(): Route {
        return _route
    }

    suspend fun updateRoute(newPoint: Point) {
       val newWaypoints = _route.waypoints + newPoint
        val newDistance = computeRouteDistance(newWaypoints)
        val elevationProfile = computeElevationProfile(newWaypoints)

        val newRoute = Route(newWaypoints, newDistance, elevationProfile)

        _route = newRoute
    }

    fun clearRoute() {
        _route = Route.empty()
    }

    private fun computeRouteDistance(waypoints: List<Point>): Double {
        var totalDistance = 0.0

        var waypoints = _route.waypoints

        for (i in 0 until waypoints.size - 1) {
            val a = waypoints[i]
            val b = waypoints[i + 1]

            val legDistance = TurfMeasurement.distance(a, b, TurfConstants.UNIT_METRES)

            totalDistance += legDistance
        }

        return totalDistance
    }

    private suspend fun computeElevationProfile(waypoints: List<Point>): List<Point> {
        val routePoints = getPointsAlongRoute(100.0) ?: return emptyList()

        return getElevationPointsAlongRoute(routePoints)
    }

    private fun getPointsAlongRoute(intervalMeters: Double): List<Point>? {
        val waypoints = _route.waypoints

        if (waypoints.size < 2) {
            return null
        }

        val lineString = LineString.fromLngLats(waypoints)
        val totalLength = TurfMeasurement.length(lineString, "kilometers")
        val intervalKilometers = intervalMeters / 1000.0

        val detailedPoints = mutableListOf<Point>()
        var traveledDistance = 0.0

        while (traveledDistance <= totalLength) {
            val segment = TurfMisc.lineSliceAlong(
                lineString,
                traveledDistance,
                traveledDistance + intervalKilometers,
                "kilometers"
            )
            detailedPoints.add(segment.coordinates()[0]) // Add the start point of each segment
            traveledDistance += intervalKilometers
        }

        // Check if the last point is added, if not, add it
        if (detailedPoints.last() != waypoints.last()) {
            detailedPoints.add(waypoints.last())
        }

        return detailedPoints
    }

    private suspend fun getElevationPointsAlongRoute(points: List<Point>): List<Point> {
       return _elevationService.addElevationToPoints(points)
    }

}