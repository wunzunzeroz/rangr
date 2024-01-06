package com.rangr.map.repositories

import com.rangr.data.dao.WaypointDao
import com.rangr.map.models.Waypoint

class WaypointsRepository(private val dao: WaypointDao) {
    fun saveWaypoint(waypoint: Waypoint) {
        dao.insert(waypoint)
    }

    fun getWaypoint(id: Int): Waypoint? {
        return dao.getById(id)
    }

    fun getWaypoints(): List<Waypoint?> {
        return dao.getAll()
    }

    fun deleteWaypoint(waypoint: Waypoint) {
        dao.delete(waypoint)
    }

    fun updateWaypoint(id: Int, newWaypoint: Waypoint) {
        TODO()
    }
}