package com.preetTractor.galaxyAndroid.helper

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

object NetworkQualityChecker {

    suspend fun isInternetFast(): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val startTime = System.currentTimeMillis()

                val url = URL("http://103.197.76.50:8090")
                val connection = url.openConnection() as HttpURLConnection
                connection.connectTimeout = 3000
                connection.readTimeout = 3000
                connection.requestMethod = "HEAD"

                connection.connect()

                val responseTime = System.currentTimeMillis() - startTime

                connection.disconnect()

                // Consider fast if response is under 500 ms
                responseTime < 500

            } catch (e: Exception) {
                false
            }
        }
    }
}