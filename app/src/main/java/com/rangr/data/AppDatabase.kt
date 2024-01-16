package com.rangr.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.rangr.data.dao.WaypointDao
import com.rangr.map.models.Waypoint

@Database(entities = [Waypoint::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun waypointDao(): WaypointDao
}