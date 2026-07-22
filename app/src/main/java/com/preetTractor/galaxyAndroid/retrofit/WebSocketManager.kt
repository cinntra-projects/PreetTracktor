package com.preetTractor.galaxyAndroid.retrofit

import android.os.Handler
import android.os.Looper
import android.util.Log
import com.pixplicity.easyprefs.library.Prefs
import com.preetTractor.galaxyAndroid.BuildConfig
import com.preetTractor.galaxyAndroid.helper.Globals
import com.preetTractor.galaxyAndroid.helper.PrefsByShubh
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import org.json.JSONObject

object WebSocketManager {

    private var webSocket: WebSocket? = null
    @Volatile
    var isConnected = false
    private val client = OkHttpClient()

    private val _events = MutableSharedFlow<String>(
        replay = 1,
        extraBufferCapacity = 1
    )
    val events = _events.asSharedFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    fun clearEvents() {
        _events.resetReplayCache()
    }

    fun connect(mobile: String) {

        val request = Request.Builder()
            .url(BuildConfig.SOCKET_URL+"/galaxy/signout/$mobile/")
            .build()

        webSocket = client.newWebSocket(
            request,
            object : WebSocketListener() {

                override fun onOpen(
                    webSocket: WebSocket,
                    response: Response
                ) {
                    isConnected = true
                    Log.d("Socket", "Connected")
                }

                override fun onMessage(
                    webSocket: WebSocket,
                    text: String
                ) {
                    Log.d("Socket", "Received: $text")

                    try {
                        val jsonObject = JSONObject(text)

                        val serverDeviceId = jsonObject.optString("device_id")
                        val currentDeviceId = Prefs.getString(Globals.DEVICE_ID)
                        Log.d("Socket", "Server Device Id = $serverDeviceId")
                        Log.d("Socket", "Current Device Id = $currentDeviceId")
                        if (serverDeviceId != currentDeviceId) {

                            CoroutineScope(Dispatchers.IO).launch {
                                _events.emit(text) // Emit original JSON
                            }
                        }

                    } catch (e: Exception) {
                        Log.e("Socket", "Parse Error", e)
                    }
                }

                override fun onClosing(
                    webSocket: WebSocket,
                    code: Int,
                    reason: String
                ) {
                    isConnected = false
                    Log.d("Socket", "Closing: $code $reason")
                }

                override fun onClosed(
                    webSocket: WebSocket,
                    code: Int,
                    reason: String
                ) {
                    isConnected = false
                    Log.d("Socket", "Closed: $code $reason")
                }

                override fun onFailure(
                    webSocket: WebSocket,
                    t: Throwable,
                    response: Response?
                ) {
                    isConnected = false
                    Log.e("Socket", "Failure: ${t.message}")
                    Log.e("Socket", "Response: ${response?.code}")
                    Log.e("Socket", "Error = ${t.message}")
                    Log.e("Socket", "Body = ${response?.body?.string()}")
                    Handler(Looper.getMainLooper()).postDelayed({
                        connect(PrefsByShubh.getMobileNO().toString())
                    }, 5000)

                }
            }
        )
    }

    fun disconnect() {
        Log.e("Socket", "Disconnected")
        webSocket?.close(1000, null)
    }
}