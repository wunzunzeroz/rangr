package com.rangr.map.repositories

import androidx.annotation.WorkerThread
import com.rangr.data.dao.WaypointDao
import com.rangr.map.models.Waypoint
import kotlinx.coroutines.flow.Flow

class WaypointsRepository(private val dao: WaypointDao) {
    @WorkerThread
    suspend fun saveWaypoint(waypoint: Waypoint) {
        dao.insert(waypoint)
    }

    fun getWaypoint(id: Int): Waypoint? {
        return dao.getById(id)
    }

    val allWaypoints = dao.getAll()

    fun getWaypoints(): Flow<List<Waypoint?>> {
        return dao.getAll()
    }

    @WorkerThread
    suspend fun deleteWaypoint(waypoint: Waypoint) {
        dao.delete(waypoint)
    }

    fun updateWaypoint(id: Int, newWaypoint: Waypoint) {
        TODO()
    }
}