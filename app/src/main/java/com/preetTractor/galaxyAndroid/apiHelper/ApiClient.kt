package com.preetTractor.galaxyAndroid.apiHelper


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import com.preetTractor.galaxyAndroid.BuildConfig
import com.preetTractor.galaxyAndroid.helper.Globals
import com.preetTractor.galaxyAndroid.helper.PrefsByShubh
import com.preetTractor.galaxyAndroid.retrofit.AuthInterceptor
import com.preetTractor.galaxyAndroid.ui.activity.ActivitySignIn
import okhttp3.Cache
import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit


class ApiClient {

    var okHttpClient = OkHttpClient.Builder()
         .addInterceptor(HttpLoggingInterceptor().apply {
             level = HttpLoggingInterceptor.Level.BODY // Todo for Playstore
         })
          .addNetworkInterceptor(provideCacheInterceptor()!!)
        .cache(provideCache())
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .build()


    fun service(context: Context): ApisInterface {
        if (PrefsByShubh.getFromWhere() == "login") {
            val retrofit = Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            return retrofit.create(ApisInterface::class.java)
        } else {
            val retrofit = Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(initCallOkHttp(context))
                .build()
            return retrofit.create(ApisInterface::class.java)
        }

    }


    /*  private val retrofit = Retrofit.Builder()
                        .baseUrl(AppConstants.BASE_URL)
          .client(okHttpClient)
          .addConverterFactory(GsonConverterFactory.create())
          .build()

        val service = retrofit.create(ApisInterface::class.java)*/


    fun initCallOkHttp(context: Context): OkHttpClient {

        val httpClient = OkHttpClient().newBuilder()
            .connectTimeout(30, TimeUnit.MINUTES)
            .readTimeout(30, TimeUnit.MINUTES)
            .writeTimeout(30, TimeUnit.MINUTES);
        //  Log.e("Token==>", " " + sessionManagement.getToken())

           httpClient.addInterceptor(AuthInterceptor(Globals.GalaxyVistaToken)).addInterceptor(HttpLoggingInterceptor().apply {
                   level = HttpLoggingInterceptor.Level.BODY // todo for playstore
               })

        httpClient.addInterceptor(Interceptor { chain ->
            val original = chain.request()
            val requestBuilder = original.newBuilder()
              //  .addHeader("Authorization", "Bearer " + PrefsByShubh.getToken())
                .header("content-type", "application/json").cacheControl(CacheControl.FORCE_NETWORK)
            val request = requestBuilder.build()
            //   Log.e("URL=>", request.toString())
            val response = chain.proceed(request)

            if (response.code == 301 || response.code == 401) {

               // PrefsByShubh.ClearSession()
                logoutScreen(context)
                return@Interceptor response
            }

            response
        })
        return httpClient.build()
    }

    private fun logoutScreen(context: Context) {
        val mainIntent = Intent(context, ActivitySignIn::class.java)
        mainIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        context.applicationContext.startActivity(mainIntent)
        (context as Activity).finish()
    }


    private val mContext: Context? = null
    private fun provideCache(): Cache? {
        var cache: Cache? = null
        try {
            cache = Cache(
                File(mContext!!.cacheDir, "http-cache"),
                10 * 1024 * 1024
            ) // 10 MB
        } catch (e: java.lang.Exception) {
            // Log.e(ContentValues.TAG, "Could not create Cache!")
        }
        return cache
    }


    /************* Offline Work Manager  */
    val HEADER_CACHE_CONTROL = "Cache-Control"
    val HEADER_PRAGMA = "PreetTractor"


    private fun provideCacheInterceptor(): Interceptor? {
        return Interceptor { chain: Interceptor.Chain ->
            val response = chain.proceed(chain.request())
            val cacheControl: CacheControl = if (isConnected()) {
                CacheControl.Builder()
                    .maxAge(0, TimeUnit.SECONDS)
                    .build()
            } else {
                CacheControl.Builder()
                    .maxStale(7, TimeUnit.DAYS)
                    .build()
            }
            response.newBuilder()
                .removeHeader(HEADER_PRAGMA)
                .removeHeader(HEADER_CACHE_CONTROL)
                .header(
                    HEADER_CACHE_CONTROL,
                    cacheControl.toString()
                )
                .build()
        }
    }

    private fun provideOfflineCacheInterceptor(): Interceptor? {
        return Interceptor { chain: Interceptor.Chain ->
            var request = chain.request()
            if (!isConnected()) {
                val cacheControl = CacheControl.Builder()
                    .maxStale(7, TimeUnit.DAYS)
                    .build()
                request = request.newBuilder()
                    .removeHeader(HEADER_PRAGMA)
                    .removeHeader(HEADER_CACHE_CONTROL)
                    .cacheControl(cacheControl)
                    .build()
            }
            chain.proceed(request)
        }
    }

    fun isConnected(): Boolean {
        try {
            val e = mContext!!.getSystemService(
                Context.CONNECTIVITY_SERVICE
            ) as ConnectivityManager
            val activeNetwork = e.activeNetworkInfo
            return activeNetwork != null && activeNetwork.isConnectedOrConnecting
        } catch (e: Exception) {
            // Log.w(ContentValues.TAG, e.toString())
        }
        return false
    }


}
