package com.preetTractor.galaxyAndroid.retrofit

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response


class AuthInterceptor(private val token: String?) : Interceptor {


    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val requestBuilder = request.newBuilder()

        // API endpoints that should NOT have the Authorization header
        val excludedEndpoints = listOf(
            "/employee/galaxy_profile",
            "/api/user/login"
        )

        Log.d("sjkdkjsdbckb", "intercept: $token")
        // Only add the token if it's not null, not empty, and the request URL does NOT contain any excluded endpoint
        if (!token.isNullOrEmpty() && excludedEndpoints.none { request.url.encodedPath.contains(it) }) {
            requestBuilder.addHeader("Authorization", "Token $token")
        }

        return chain.proceed(requestBuilder.build())
    }
}



