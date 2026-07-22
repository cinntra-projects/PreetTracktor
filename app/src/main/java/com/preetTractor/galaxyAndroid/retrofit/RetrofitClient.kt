package com.preetTractor.galaxyAndroid.retrofit

import com.preetTractor.galaxyAndroid.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    //  private const val BASE_URL = "https://api.example.com/"
    val loggingInterceptor = HttpLoggingInterceptor().apply {
        level =
            HttpLoggingInterceptor.Level.BODY // You can change this to HEADERS or BASIC depending on your needs
    }

    val okHttpClient =
        OkHttpClient.Builder().addInterceptor(loggingInterceptor) // Add the logging interceptor
            .connectTimeout(120, TimeUnit.SECONDS) // Set connection timeout
            .readTimeout(120, TimeUnit.SECONDS)    // Set read timeout
            .writeTimeout(120, TimeUnit.SECONDS).
            retryOnConnectionFailure(true).build()


    private val retrofit: Retrofit by lazy {
        Retrofit.Builder().baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()).client(okHttpClient).build()
    }


    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }

    private val retrofit1: Retrofit by lazy {
        Retrofit.Builder().baseUrl(BuildConfig.SUPER_ADMIN_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()).client(okHttpClient).build()
    }


    val apiService1: ApiService by lazy {
        retrofit1.create(ApiService::class.java)
    }
}
