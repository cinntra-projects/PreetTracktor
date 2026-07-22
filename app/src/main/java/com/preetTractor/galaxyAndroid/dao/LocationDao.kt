package com.preetTractor.galaxyAndroid.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.preetTractor.galaxyAndroid.dao.LocationEntity

@Dao
interface LocationDao {

    @Insert
    suspend fun insert(location: LocationEntity)

    @Query("SELECT * FROM location_queue WHERE isSynced = 0")
    suspend fun getPending(): List<LocationEntity>

    @Query("DELETE FROM location_queue WHERE isSynced = 1")
    suspend fun deleteSynced()
}