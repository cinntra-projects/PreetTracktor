package com.preetTractor.galaxyAndroid.helper

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.gson.JsonArray
import com.google.gson.JsonObject


class LocationSyncWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {

        val dao = AppGraph.db.locationDao()
        val api = AppGraph.api

        val pending = dao.getPending()
        if (pending.isEmpty()) return Result.success()

        val jsonArray = JsonArray()

        pending.forEach {
            val obj = JsonObject().apply {
                addProperty("Latitude", it.latitude)
                addProperty("Longitude", it.longitude)
                addProperty("Address", it.address)
                addProperty("Create_Time", it.timestamp.toString())
            }
            jsonArray.add(obj)
        }

        return try {

            val response = api.galaxyTrackingCreate(jsonArray).execute()

            if (response.isSuccessful) {
                dao.deleteSynced()
                Result.success()
            } else {
                Result.retry()
            }

        } catch (e: Exception) {
            Result.retry()
        }
    }
}