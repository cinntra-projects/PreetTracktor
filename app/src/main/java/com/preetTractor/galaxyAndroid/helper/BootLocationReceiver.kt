package com.preetTractor.galaxyAndroid.helper

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat

class BootLocationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action != Intent.ACTION_BOOT_COMPLETED) {
            return
        }

        if (!PrefsByShubh.getBoolean(Globals.isCheckingStart, false)) {
            Log.d(TAG, "Location sharing was not active before reboot")
            return
        }

        if (!LocationPermissionHelper.hasLocationPermission(context)) {
            Log.w(TAG, "Cannot restart location sharing after reboot: permissions missing")
            return
        }

        val serviceIntent = Intent(context, LocationService::class.java).apply {
            action = LocationService.ACTION_START
        }

        try {
            ContextCompat.startForegroundService(context, serviceIntent)
            Log.d(TAG, "Location sharing restarted after reboot")
        } catch (exception: Exception) {
            Log.e(TAG, "Unable to restart location sharing after reboot", exception)
        }
    }

    companion object {
        private const val TAG = "BootLocationReceiver"
    }
}
