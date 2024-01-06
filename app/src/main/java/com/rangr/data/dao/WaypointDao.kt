package com.rangr.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.rangr.map.models.Waypoint

@Dao
interface WaypointDao {
    @Query("SELECT * FROM waypoint")
    fun getAll(): List<Waypoint>

    @Query("SELECT * FROM waypoint WHERE id = :id")
    fun getById(id: Int): Waypoint?

    @Insert
    fun insert(waypoint: Waypoint)

    @Delete()
    fun delete(waypoint: Waypoint)
}
