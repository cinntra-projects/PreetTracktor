package com.preetTractor.galaxyAndroid.helper

import androidx.room.Database
import androidx.room.RoomDatabase
import com.preetTractor.galaxyAndroid.dao.LocationDao
import com.preetTractor.galaxyAndroid.dao.LocationEntity

@Database(
    entities = [LocationEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun locationDao(): LocationDao
}