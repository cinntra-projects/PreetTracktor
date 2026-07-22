package com.preetTractor.galaxyAndroid.ui.mediaUi.activity

import android.Manifest
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.preetTractor.galaxyAndroid.BuildConfig
import com.preetTractor.galaxyAndroid.R
import com.preetTractor.galaxyAndroid.apiHelper.AppConstants
import com.preetTractor.galaxyAndroid.databinding.ActivityPdfViewBinding
import java.io.File

class PdfViewActivity : AppCompatActivity() {
    lateinit var binding: ActivityPdfViewBinding
    var url = ""

    lateinit var builder: AlertDialog.Builder
    lateinit var alertDialog: AlertDialog

    // TODO: Rename and change types and number of parameters
    private fun SetUPDialog(context: Context) {
        builder = AlertDialog.Builder(context)

        builder.setView(R.layout.progress_dialog)

            .setCancelable(false)
        alertDialog = builder.create()
    }

    val listPermissionsNeeded = mutableListOf<String>()

    private fun checkAndRequestPermissions(): Boolean {

        /* val write =
             ContextCompat.checkSelfPermission(
                 this,
                 Manifest.permission.MANAGE_EXTERNAL_STORAGE
             )*/


        val writeXcoe = ContextCompat.checkSelfPermission(
            this, Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        val read = ContextCompat.checkSelfPermission(
            this, Manifest.permission.READ_EXTERNAL_STORAGE
        )


        /*  if (write != PackageManager.PERMISSION_GRANTED) {
              listPermissionsNeeded.add(Manifest.permission.MANAGE_EXTERNAL_STORAGE)
          }*/

        if (read != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }

        if (writeXcoe != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }

        try {
            if (listPermissionsNeeded.isNotEmpty()) {
                ActivityCompat.requestPermissions(
                    this,
                    listPermissionsNeeded.toTypedArray(),
                    AppConstants.REQUEST_ID_MULTIPLE_PERMISSIONS
                )
                return false
            }
        } catch (e: Exception) {
        }

        return true
    }


    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            AppConstants.REQUEST_ID_MULTIPLE_PERMISSIONS -> {
                val perms = hashMapOf<String, Int>()
                // Initialize the map with both permissions
                perms[Manifest.permission.MANAGE_EXTERNAL_STORAGE] =
                    PackageManager.PERMISSION_GRANTED

                perms[Manifest.permission.READ_EXTERNAL_STORAGE] = PackageManager.PERMISSION_GRANTED

                // Fill with actual results from user
                if (grantResults.isNotEmpty()) {
                    for (i in permissions.indices) {
                        perms[permissions[i]] = grantResults[i]
                    }
                    // Check for both permissions
                    if (perms[Manifest.permission.WRITE_EXTERNAL_STORAGE] == PackageManager.PERMISSION_GRANTED && perms[Manifest.permission.READ_EXTERNAL_STORAGE] == PackageManager.PERMISSION_GRANTED) {

                    } else {
                        // Permissions are denied
                        /*  Toast.makeText(
                              this,
                              "Some permissions are not granted. You cannot proceed.",
                              Toast.LENGTH_LONG
                          ).show()*/

                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPdfViewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        SetUPDialog(this)
        url = intent.getStringExtra("url").toString()
        checkAndRequestPermissions()

        binding.ivBackPress.setOnClickListener {
            finish()
        }


        // Configure the WebView settings
        val webSettings: WebSettings = binding.webview.settings
        webSettings.javaScriptEnabled = true
        webSettings.builtInZoomControls = true
        webSettings.displayZoomControls = false

        // Ensure links open in the WebView
        binding.webview.webViewClient = WebViewClient()

        // Enable ChromeClient for better support
        binding.webview.webChromeClient = WebChromeClient()

        // Load the PDF using Google Docs viewer
        val pdfUrl = "${BuildConfig.IMAGE_URL}${url}" // Replace with your PDF URL

        //  downloadFile(pdfUrl)
        val googleDocsUrl = "https://docs.google.com/gview?embedded=true&url=$pdfUrl"
        binding.webview.loadUrl(googleDocsUrl)

        binding.btnShare.setOnClickListener {
            downloadFile(pdfUrl)
        }

    }

    private var downloadID: Long = 0

    private fun downloadFile(url: String) {
        alertDialog.show()
        val request = DownloadManager.Request(Uri.parse(url)).setTitle("Downloading File")
            .setDescription("Downloading image")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalPublicDir(
                Environment.DIRECTORY_DOWNLOADS, Uri.parse(url).lastPathSegment
            )

        try {
            val downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            downloadID = downloadManager.enqueue(request)
        } catch (e: Exception) {
            alertDialog.dismiss()
            Toast.makeText(this, "permission not granted", Toast.LENGTH_SHORT).show()
        }

        val filter = IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        registerReceiver(receiver, filter, Context.RECEIVER_EXPORTED)

    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            if (downloadID == id) {
                handleDownloadCompletion(id)
            }
        }
    }


    private fun handleDownloadCompletion(downloadId: Long) {
        val downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val query = DownloadManager.Query().setFilterById(downloadId)
        val cursor = downloadManager.query(query)

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                val columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI)
                if (columnIndex >= 0) {
                    val fileUri = cursor.getString(columnIndex)
                    if (fileUri != null) {
                        alertDialog.dismiss()/*      val file = File(Uri.parse(fileUri).path!!)
                              val uri = FileProvider.getUriForFile(this, "${packageName}.FileProvider", file)
                              binding.webview.loadUrl(uri.toString())*/
                        shareFile(fileUri)
                    } else {
                        alertDialog.dismiss()
                        Log.e("DownloadReceiver", "File URI is null")
                    }
                } else {
                    alertDialog.dismiss()
                    Log.e("DownloadReceiver", "Column index is invalid")
                }
            } else {
                alertDialog.dismiss()
                Log.e("DownloadReceiver", "Cursor move to first failed")
            }
            cursor.close()
        } else {
            alertDialog.dismiss()
            Log.e("DownloadReceiver", "Cursor is null")
        }
    }


    private fun shareFile(fileUri: String) {
        alertDialog.dismiss()
        val file = File(Uri.parse(fileUri).path!!)
        val uri = FileProvider.getUriForFile(this, "${packageName}.FileProvider", file)

        //  val uri = Uri.parse(fileUri)
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_STREAM, uri)
            type = contentResolver.getType(uri)
        }
        startActivity(Intent.createChooser(shareIntent, "Share File"))
    }
}