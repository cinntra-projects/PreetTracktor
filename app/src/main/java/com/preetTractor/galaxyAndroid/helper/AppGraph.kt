package com.preetTractor.galaxyAndroid.helper

import android.content.Context
import androidx.room.Room
import com.preetTractor.galaxyAndroid.repos.LocationRepository
import com.preetTractor.galaxyAndroid.retrofit.ApiService
import com.preetTractor.galaxyAndroid.retrofit.RetrofitClient

object AppGraph {

    lateinit var db: AppDatabase
        private set

    lateinit var api: ApiService
        private set

    lateinit var locationRepository: LocationRepository
        private set

    fun init(context: Context) {

        val appContext = context.applicationContext

        db = Room.databaseBuilder(
            appContext,
            AppDatabase::class.java,
            "galaxy_db"
        )
            .fallbackToDestructiveMigration()
            .build()

        api = RetrofitClient.apiService

        locationRepository = LocationRepository(
            db.locationDao(),
            api
        )
    }
}