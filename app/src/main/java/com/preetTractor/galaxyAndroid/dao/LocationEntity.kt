package com.preetTractor.galaxyAndroid.dao

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "location_queue")
data class LocationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val latitude: Double,
    val longitude: Double,
    val address: String,
    val timestamp: Long,
    val isSynced: Boolean = false
)