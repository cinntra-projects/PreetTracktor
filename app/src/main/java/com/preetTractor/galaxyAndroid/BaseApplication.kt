package com.preetTractor.galaxyAndroid

import android.app.Application
import android.util.Log
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.preetTractor.galaxyAndroid.helper.PrefsByShubh
import com.google.firebase.FirebaseApp
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.messaging.FirebaseMessaging
import com.preetTractor.galaxyAndroid.helper.AppGraph
import com.preetTractor.galaxyAndroid.helper.Globals
import com.preetTractor.galaxyAndroid.helper.LocationSyncWorker
import com.preetTractor.galaxyAndroid.retrofit.WebSocketManager
import java.io.File
import java.util.concurrent.TimeUnit



class BaseApplication : Application() {

    companion object {
        @Volatile
        private var mInstance: BaseApplication? = null

        fun getInstance(): BaseApplication {
            return mInstance ?: throw IllegalStateException("Application instance is not initialized yet.")
        }
    }

    override fun onCreate() {
        super.onCreate()
        mInstance = this
        // Initialize Firebase
        FirebaseApp.initializeApp(this)

        // Enable Crashlytics
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)

        getFCMToken()
        AppGraph.init(this)
        PrefsByShubh.Builder()
            .setContext(this)
            .setMode(MODE_PRIVATE)
            .setPrefsName(packageName)
            .setUseDefaultSharedPreference(true)
            .build()

        PrefsByShubh.initPrefs(this)
        val dexOutputDir: File = codeCacheDir
        dexOutputDir.setReadOnly()

        initWorkManager()
    }


    private fun getFCMToken() {
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.e("FCM", "Fetching FCM token failed", task.exception)
                    return@addOnCompleteListener
                }
                val token = task.result
                token?.let {
                    Log.d("FCM", "FCM Token: $token")
                    saveTokenToSharedPreferences(token)
                }
            }
    }

    private fun saveTokenToSharedPreferences(token: String) {

        PrefsByShubh.setFirebaseFCMToken(token)
    }



    private fun initWorkManager() {
        // Must be connected to the internet
        val workerConstraints = Constraints.Builder()
            .build()

        // If work request retry the Worker, it will give 1 minutes backoff delay
        val request = PeriodicWorkRequestBuilder<LocationSyncWorker>(
            15, TimeUnit.MINUTES
        ).build()

        WorkManager.getInstance(this)
            .enqueueUniquePeriodicWork(
                "location_sync",
                ExistingPeriodicWorkPolicy.KEEP,
                request
            )

    }

}