package com.preetTractor.galaxyAndroid.ui.activity.customermodule

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import com.preetTractor.galaxyAndroid.BaseApplication

class ConnectivityReceiver : BroadcastReceiver() {

    companion object {
        var connectivityReceiverListener: ConnectivityReceiverListener? = null

        fun isConnected(): Boolean {
            val cm = BaseApplication.getInstance().applicationContext
                .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            val activeNetwork = cm.activeNetworkInfo
            return activeNetwork?.isConnectedOrConnecting == true
        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        val cm = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo
        val isConnected = activeNetwork?.isConnectedOrConnecting == true

        connectivityReceiverListener?.onNetworkConnectionChanged(isConnected)
    }

    interface ConnectivityReceiverListener {
        fun onNetworkConnectionChanged(isConnected: Boolean)
    }
}
