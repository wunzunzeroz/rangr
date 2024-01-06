package com.rangr.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.rangr.map.models.Waypoint
import kotlinx.coroutines.flow.Flow

@Dao
interface WaypointDao {
    @Query("SELECT * FROM waypoint")
    fun getAll(): Flow<List<Waypoint>>

    @Query("SELECT * FROM waypoint WHERE id = :id")
    fun getById(id: Int): Waypoint?

    @Insert
    suspend fun insert(waypoint: Waypoint)

    @Delete()
    suspend fun delete(waypoint: Waypoint)
}
