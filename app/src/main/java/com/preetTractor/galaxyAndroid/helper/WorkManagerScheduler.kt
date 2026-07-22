package com.preetTractor.galaxyAndroid.helper

import android.content.Context
import android.util.Log
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.preetTractor.galaxyAndroid.helper.LocationSyncWorker
import java.util.concurrent.TimeUnit

object WorkManagerScheduler {

    private const val WORK_NAME = "myWorkManager"

    fun refreshPeriodicWork(context: Context) {

        val constraints =
            Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()

        val request = PeriodicWorkRequestBuilder<LocationSyncWorker>(
            15, TimeUnit.MINUTES
        ).build()

        WorkManager.getInstance(context)
            .enqueueUniquePeriodicWork(
                "location_sync",
                ExistingPeriodicWorkPolicy.KEEP,
                request
            )
        Log.e("WorkManager", "Periodic Work Scheduled")
    }

    fun cancelWork(context: Context) {

        WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)

        Log.e("WorkManager", "Periodic Work Cancelled")
    }
}