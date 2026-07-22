package com.preetTractor.galaxyAndroid.repos

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.preetTractor.galaxyAndroid.dao.LocationDao
import com.preetTractor.galaxyAndroid.dao.LocationEntity
import com.preetTractor.galaxyAndroid.data.ResponseGlobal
import com.preetTractor.galaxyAndroid.retrofit.ApiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LocationRepository(
    private val dao: LocationDao,
    private val api: ApiService
) {

    suspend fun saveLocation(lat: Double, lng: Double, address: String) {
        dao.insert(
            LocationEntity(
                latitude = lat,
                longitude = lng,
                address = address,
                timestamp = System.currentTimeMillis()
            )
        )
        syncToServer()
    }

    fun syncToServer() {
        CoroutineScope(Dispatchers.IO).launch {

            val pending = dao.getPending()
            if (pending.isEmpty()) return@launch

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

            api.galaxyTrackingCreate(jsonArray)
                .enqueue(object : Callback<ResponseGlobal> {

                    override fun onResponse(
                        call: Call<ResponseGlobal>,
                        response: Response<ResponseGlobal>
                    ) {
                        if (response.isSuccessful) {
                            CoroutineScope(Dispatchers.IO).launch {
                                dao.deleteSynced()
                            }
                        }
                    }

                    override fun onFailure(call: Call<ResponseGlobal>, t: Throwable) {
                        // keep data for retry
                    }
                })
        }
    }
}