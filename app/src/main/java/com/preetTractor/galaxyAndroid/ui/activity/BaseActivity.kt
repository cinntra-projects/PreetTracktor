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

    companion object {
        private const val KEY_CURRENT_PHOTO_PATH = "base_activity_current_photo_path"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("Socket", "Status = ${WebSocketManager.isConnected}")
        // On low-memory devices, launching the system camera app can get this activity's
        // process killed in the background. Android then recreates it from savedInstanceState
        // before delivering onActivityResult, so currentPhotoPath (a plain in-memory field)
        // resets to "" unless we restore it here — the captured file on disk survives, only
        // this path variable does not.
        savedInstanceState?.getString(KEY_CURRENT_PHOTO_PATH)?.let { currentPhotoPath = it }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_CURRENT_PHOTO_PATH, currentPhotoPath)
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
        // Using the public Pictures directory (Environment.getExternalStoragePublicDirectory)
        // is unreliable on scoped-storage devices (Android 10+) and on several OEM builds the
        // directory/file silently fails to be created, so the camera later writes nothing at
        // that path and the upload fails with "open failed: ENOENT". The app-specific external
        // files dir doesn't need any storage permission and is guaranteed to exist.
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES) ?: cacheDir
        if (!storageDir.exists()) {
            storageDir.mkdirs()
        }
        val image = File.createTempFile(imageFileName, ".png", storageDir)

        currentPhotoPath = image.absolutePath


        return image
    }


    open fun dispatchMakeModelPictureIntent() {

        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        if (storageDir == null) {
            AlertDialog.Builder(this)
                .setTitle("Storage Error")
                .setMessage("Unable to access device storage. Please check available storage and try again.")
                .setPositiveButton("OK", null)
                .show()
            return
        }

        if (!storageDir.exists()) {
            storageDir.mkdirs()
        }

        if (!storageDir.canWrite()) {
            AlertDialog.Builder(this)
                .setTitle("Storage Permission")
                .setMessage("Unable to write image to storage.")
                .setPositiveButton("OK", null)
                .show()
            return
        }

        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {

                val photoFile = try {
                    createImageFile()
                } catch (e: Exception) {
                    AlertDialog.Builder(this)
                        .setTitle("Camera Error")
                        .setMessage("Unable to create image file.")
                        .setPositiveButton("OK", null)
                        .show()
                    null
                }

                photoFile?.let { file ->

                    val photoUri = FileProvider.getUriForFile(
                        this,
                        "${BuildConfig.APPLICATION_ID}.FileProvider",
                        file
                    )

                    takePictureIntent.putExtra(
                        MediaStore.EXTRA_OUTPUT,
                        photoUri
                    )

                    takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    takePictureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)

                    startActivityForResult(
                        takePictureIntent,
                        REQUEST_IMAGE_MAKE_MODEL_PHOTO
                    )
                }
            }
        }
    }

    // Some OEM camera apps (MIUI in particular) don't reliably honor EXTRA_OUTPUT and instead
    // hand the captured image back through the result Intent (as a content Uri, or occasionally
    // just a thumbnail Bitmap in the "data" extra) while leaving our target file at 0 bytes.
    // This pulls the image from whichever of those the camera actually gave us.
    fun recoverPhotoFromResultIntent(data: Intent?, destFile: File): Boolean {
        val uri = data?.data
        if (uri != null) {
            try {
                contentResolver.openInputStream(uri)?.use { input ->
                    FileOutputStream(destFile).use { output ->
                        input.copyTo(output)
                    }
                }
                if (destFile.exists() && destFile.length() > 0L) return true
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        @Suppress("DEPRECATION")
        val thumbnail = data?.extras?.get("data") as? Bitmap
        if (thumbnail != null) {
            try {
                FileOutputStream(destFile).use { output ->
                    thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, output)
                }
                if (destFile.exists() && destFile.length() > 0L) return true
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        return false
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