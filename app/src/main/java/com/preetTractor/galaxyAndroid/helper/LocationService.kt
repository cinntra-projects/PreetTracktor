package com.preetTractor.galaxyAndroid.helper

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.os.SystemClock
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationAvailability
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.preetTractor.galaxyAndroid.R
import com.preetTractor.galaxyAndroid.data.ResponseGlobal
import com.preetTractor.galaxyAndroid.data.localdata.LocalGalaxyTracking
import com.preetTractor.galaxyAndroid.repos.LocationRepository
import com.preetTractor.galaxyAndroid.retrofit.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class LocationService : Service() {

    companion object {
        private const val TAG = "LocationService"
        const val ACTION_LOCATION = "action_location"
        const val ACTION_START = "action_start"
        const val ACTION_STOP = "action_stop"
        const val ARG_LOCATION = "arg_location"

        private const val NOTIFICATION_ID = 1001
        private const val CHANNEL_ID = "location_sharing_channel"
        private const val CHANNEL_NAME = "Location sharing"

    }
    private val serviceScope =
        CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val locationRequest: LocationRequest by lazy {
        LocationRequest.create().also {
            it.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            it.interval = 2 * 60 * 1000L // 10 seconds
            it.fastestInterval = 60 * 1000L // 5 seconds
        }
    }

    private val locationCallback: LocationCallback by lazy {
        object : LocationCallback() {
            override fun onLocationAvailability(p0: LocationAvailability) {
            }

            override fun onLocationResult(p0: LocationResult) {
                p0.lastLocation?.let {
                    val intent = Intent(ACTION_LOCATION)
                    intent.putExtra(ARG_LOCATION, it)
                    LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)
                    handleLocationUpdate(it)
                }
            }
        }
    }
    private var isTrackingLocation = false

    var latitude = 0.0
    var longitude = 0.0
    private lateinit var repo: LocationRepository
    //todo new method to get location offline

    private lateinit var locationManager: LocationManager

    private val locationListener = object : android.location.LocationListener {
        override fun onLocationChanged(location: Location) {
            val latitude = location.latitude
            val longitude = location.longitude
            println("Latitude: $latitude, Longitude: $longitude")
            Log.e(
                TAG, "onLocationChangedOFFLINESERVICE: Latitude: $latitude, Longitude: $longitude"
            )
        }

        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
            // Deprecated in API 29, but still required for lower APIs
        }

        override fun onProviderEnabled(provider: String) {
            // Called when GPS is enabled
        }

        override fun onProviderDisabled(provider: String) {
            // Called when GPS is disabled
        }
    }

    private var lastApiCallTime = 0L
    private val MIN_INTERVAL = 2 * 60 * 1000L
    private fun handleLocationUpdate(location: Location) {

        val now = System.currentTimeMillis()

        if (now - lastApiCallTime < MIN_INTERVAL) return

        lastApiCallTime = now

        latitude = location.latitude
        longitude = location.longitude

        updateForegroundNotification()
        getAddressFromLocation(latitude, longitude)
    }

    private fun getAddressFromLocation(latitude: Double, longitude: Double) {

        if (!PrefsByShubh.getBoolean(Globals.isCheckingStart, false)) {
            return
        }

        CoroutineScope(Dispatchers.IO).launch {

            try {

                if (!Geocoder.isPresent()) {
                    Log.e(TAG, "Geocoder service unavailable")
                    return@launch
                }

                if (latitude !in -90.0..90.0 ||
                    longitude !in -180.0..180.0
                ) {
                    Log.e(TAG, "Invalid coordinates")
                    return@launch
                }

                val geocoder = Geocoder(this@LocationService, Locale.getDefault())

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

                    geocoder.getFromLocation(
                        latitude,
                        longitude,
                        1,
                        object : Geocoder.GeocodeListener {

                            override fun onGeocode(addresses: MutableList<android.location.Address>) {

                                val addressLine =
                                    addresses.firstOrNull()?.getAddressLine(0)
                                        ?: "Address unavailable"

                                createGalaxyTracking(
                                    latitude,
                                    longitude,
                                    addressLine
                                )
                            }

                            override fun onError(errorMessage: String?) {

                                Log.e(
                                    TAG,
                                    "Geocoder Error: $errorMessage"
                                )

                                createGalaxyTracking(
                                    latitude,
                                    longitude,
                                    "Address unavailable"
                                )
                            }
                        }
                    )

                } else {

                    try {

                        val addresses =
                            geocoder.getFromLocation(
                                latitude,
                                longitude,
                                1
                            )

                        val addressLine =
                            addresses?.firstOrNull()?.getAddressLine(0)
                                ?: "Address unavailable"

                        createGalaxyTracking(
                            latitude,
                            longitude,
                            addressLine
                        )

                    } catch (e: Exception) {

                        Log.e(TAG, "Geocoder Exception", e)

                        createGalaxyTracking(
                            latitude,
                            longitude,
                            "Address unavailable"
                        )
                    }
                }

            } catch (e: IOException) {

                Log.e(TAG, "Geocoder IO Exception", e)

            } catch (e: IllegalArgumentException) {

                Log.e(TAG, "Invalid LatLng", e)

            } catch (e: Exception) {

                Log.e(TAG, "Geocoder Exception", e)
            }
        }
    }

    private fun startLocationUpdates() {
        if (!PrefsByShubh.getBoolean(Globals.isCheckingStart, false)) {
            return
        }
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, 2 * 60 * 1000L, 0f, locationListener
            )

        }


    }

    private fun createGalaxyTracking(latitude: Double, longitude: Double, address: String) {

        val androidId = Settings.Secure.getString(
            contentResolver, Settings.Secure.ANDROID_ID
        )

        val tracking = LocalGalaxyTracking(
            Address = address,
            Create_Date = Globals.getTodaysDatervrsfrmt()!!,
            Create_Time = Globals.getTCurrentTime()!!,
            device_id = androidId,
            Latitude = latitude,
            Longitude = longitude,
            SalesEmployeeCode = PrefsByShubh.getEmpCode().toString()
        )

        Globals.offlineLatLong.add(tracking)
        Globals.globalLatitude = latitude
        Globals.globalLongitude = longitude
        Globals.globalAddress = address
        val jsonArray = JsonArray().apply {
            val gson = Gson()
            Globals.offlineLatLong.forEach {
                add(gson.toJsonTree(it).asJsonObject)
            }
        }

        sendTrackingToServer(jsonArray)

    }

    private val fusedLocationClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(this)
    }

    override fun onCreate() {
        super.onCreate()
        repo = AppGraph.locationRepository
        displayForegroundNotification()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_STOP) {
            stopLocation()
            stopSelf()
            return START_NOT_STICKY
        }
        startLocation()
        return START_STICKY
    }

    override fun onDestroy() {
        serviceScope.cancel()
        stopLocation()
        super.onDestroy()
    }

    override fun onBind(p0: Intent?): IBinder? = null

    override fun onTaskRemoved(rootIntent: Intent?) {
        val restartIntent = Intent(applicationContext, LocationService::class.java)
        restartIntent.setPackage(packageName)

        val pendingIntent = PendingIntent.getService(
            this, 1, restartIntent, PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager

        alarmManager.set(
            AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 2000, pendingIntent
        )

        super.onTaskRemoved(rootIntent)
    }

    @SuppressLint("MissingPermission")
    private fun startLocation() {
        if (isTrackingLocation) {
            return
        }
        if (!LocationPermissionHelper.hasLocationPermission(this)) {
            Log.w(TAG, "Location sharing service started without required location permissions")
            Toast.makeText(
                this, "Location permission required to share location", Toast.LENGTH_SHORT
            ).show()
            stopSelf()
            return
        }
        isTrackingLocation = true
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, mainLooper)
    }

    private fun stopLocation() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
        if (::locationManager.isInitialized) {
            locationManager.removeUpdates(locationListener)
        }
        isTrackingLocation = false
    }

    private fun buildNotification(contentText: String) =
        NotificationCompat.Builder(this, CHANNEL_ID).setContentTitle(getString(R.string.app_name))
            .setContentText(contentText).setSmallIcon(R.mipmap.ic_launcher).setOngoing(true)
            .setOnlyAlertOnce(true).setPriority(NotificationCompat.PRIORITY_LOW)

    private fun displayForegroundNotification() {
        createNotificationChannel()
        val notification =
            buildNotification(getString(R.string.notification_update_location)).build()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(
                NOTIFICATION_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION
            )
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }
    }

    private fun updateForegroundNotification() {
        val lastUpdate = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
        val notification = buildNotification(
            getString(R.string.notification_location_shared_at, lastUpdate)
        ).build()
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager?.notify(NOTIFICATION_ID, notification)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return
        }
        val channel = NotificationChannel(
            CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_LOW
        )
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager?.createNotificationChannel(channel)
    }

    private fun sendTrackingToServer(data: JsonArray) {
        RetrofitClient.apiService.galaxyTrackingCreate(data)
            .enqueue(object : Callback<ResponseGlobal> {

                override fun onResponse(
                    call: Call<ResponseGlobal>, response: Response<ResponseGlobal>
                ) {
                    if (response.isSuccessful && response.body()?.status == 200) {
                        Globals.offlineLatLong.clear()
                        Log.d(TAG, "Upload success")
                    }/*else{
                        Globals.loginIntoAnotherDevice = true
                        serviceScope.cancel()
                        stopLocation()
                        Toast.makeText(
                            applicationContext,
                            "Your account logged in on another device",
                            Toast.LENGTH_LONG
                        ).show()
                    }*/
                }

                override fun onFailure(call: Call<ResponseGlobal>, t: Throwable) {
                    Log.e(TAG, "Upload failed: ${t.message}")
                    serviceScope.launch {


                        val address = try {

                            if (Geocoder.isPresent()) {

                                Geocoder(
                                    this@LocationService,
                                    Locale.getDefault()
                                )
                                    .getFromLocation(latitude, longitude, 1)
                                    ?.firstOrNull()
                                    ?.getAddressLine(0)
                                    ?: "Unknown"

                            } else {
                                "Unknown"
                            }

                        } catch (e: Exception) {

                            Log.e(TAG, "Geocoder Error", e)
                            "Unknown"
                        }

                        repo.saveLocation(latitude, longitude, address)
                    }
                }
            })
    }

}
