package com.preetTractor.galaxyAndroid.ui.activity

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.preference.PreferenceManager
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.pixplicity.easyprefs.library.Prefs
import com.preetTractor.galaxyAndroid.BuildConfig
import com.preetTractor.galaxyAndroid.helper.Globals
import com.preetTractor.galaxyAndroid.helper.LocationService
import com.preetTractor.galaxyAndroid.helper.PrefsByShubh
import com.preetTractor.galaxyAndroid.retrofit.WebSocketManager
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date

abstract class BaseActivity : AppCompatActivity() {
    private var fusedLocationClient: FusedLocationProviderClient? = null
    private val REQUEST_NOTIFICATION_PERMISSION = 333
    private val TAG = "BaseActivity"
    open var currentPhotoPath = ""
    open val REQUEST_IMAGE_MAKE_MODEL_PHOTO = 12444
    private val MY_PERMISSIONS_REQUEST_LOCATION = 99
    private val MY_PERMISSIONS_REQUEST_BACKGROUND_LOCATION = 66
    private val REQUIRED_PERMISSIONS = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("Socket", "Status = ${WebSocketManager.isConnected}")
        if(Globals.loginIntoAnotherDevice){
            showSessionExpiredDialog()
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {

                WebSocketManager.events.collect { message ->

                    Log.d("Socket", "Collector Received: $message")

                    val json = JSONObject(message)

                    val type = json.optString("type")

                    if (type == "signout_notification") {
                        showSessionExpiredDialog()
                    }
                }
            }
        }
    }

    private fun showSessionExpiredDialog() {

        AlertDialog.Builder(this)
            .setTitle("Session Expired")
            .setMessage("Your account has been logged in from another device.")
            .setCancelable(false)
            .setPositiveButton("OK") { _, _ ->

                Prefs.clear()
                Globals.loginIntoAnotherDevice = false
                PrefsByShubh.clear()
                WebSocketManager.clearEvents()
                WebSocketManager.disconnect()
                startActivity(
                    Intent(
                        this,
                        ActivitySignIn::class.java
                    )
                )

                finishAffinity()
            }
            .show()
    }

    fun checkAndRequestNotificationPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Permission is not granted, request it
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                REQUEST_NOTIFICATION_PERMISSION
            )
        } else {
            // Permission is already granted
//            sendNotification()
        }
    }

    fun requestPermissions() {
        val permissionsToRequest = mutableListOf<String>()
        for (permission in REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(
                    this, permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                permissionsToRequest.add(permission)
            }
        }

        if (permissionsToRequest.isNotEmpty()) {
            val permissionsArray = permissionsToRequest.toTypedArray()
            requestPermissionLauncher.launch(permissionsArray)
        } else {
            //  dispatchTakePictureIntent()
        }


    }

    val requestPermissionLauncher: ActivityResultLauncher<Array<String>> =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val allGranted = permissions.all { it.value }
            if (allGranted) {
                //  dispatchTakePictureIntent()
            } else {
                //Toast.makeText(this, "Permission(s) denied", Toast.LENGTH_SHORT).show()
            }
        }
    val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val location = intent.getParcelableExtra<Location>(LocationService.ARG_LOCATION)
            location?.let {
                Log.e(TAG, "onReceive: obserbing Broadcast")

            }
        }
    }

    fun startService() {
        val intent = Intent(this, LocationService::class.java)
        ContextCompat.startForegroundService(this, intent)
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(broadcastReceiver, IntentFilter(LocationService.ACTION_LOCATION))
    }

    fun stopService() {
        val intent = Intent(this, LocationService::class.java)
        stopService(intent)
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver)
    }
    open var makeModelPhoto = ""
    open lateinit var fileMakeModelPhotoUri: Uri
    open fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_${timeStamp}_"
        val storageDir = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                .toString() + "/AnGService"
        )
        if (!storageDir.exists()) {
            storageDir.mkdir()
        }
        val image = File.createTempFile(imageFileName, ".png", storageDir)

        currentPhotoPath = image.absolutePath


        return image
    }



    open fun dispatchMakeModelPictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                var photoFile: File? = try {
                    createImageFile()
                } catch (ex: java.io.IOException) {
                    Toast.makeText(
                        this, "Error occurred while creating the file", Toast.LENGTH_SHORT
                    ).show()
                    null
                }
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        this, "${BuildConfig.APPLICATION_ID}.FileProvider", it
                    )
//                    val photoURI: Uri = Uri.fromFile(it) //todo ==> using Uri.fromFile to create the URI for the photo file, which leads to a FileUriExposedException on Android 7.0 and above
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_MAKE_MODEL_PHOTO)
                }
            }
        }
    }

    fun compressImageFile(context: Context, imageFile: File): File {

        val bitmap = BitmapFactory.decodeFile(imageFile.absolutePath)
            ?: throw IOException("Unable to decode image")

        val compressedFile = File(
            context.cacheDir,
            "compressed_${System.currentTimeMillis()}.jpg"
        )

        FileOutputStream(compressedFile).use {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 40, it)
        }

        return compressedFile
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_IMAGE_MAKE_MODEL_PHOTO && resultCode == RESULT_OK) {
            //  customerPhotoPath = getCustomerPhotoPath(data!!.data)
        //    val compressedImageFile = compressImageFile(File(currentPhotoPath))

            val imgFile = File(makeModelPhoto)
            if (imgFile.exists()) {
                fileMakeModelPhotoUri = Uri.fromFile(imgFile)
                currentPhotoPath = fileMakeModelPhotoUri.path!!
                Log.e("fileUri---", fileMakeModelPhotoUri.toString())
            }

            Log.e(TAG, "onActivityResultCUSTOMERPHOTO>>>>>>>>>: $currentPhotoPath")


            //TODO chanchal---

        }
    }

}