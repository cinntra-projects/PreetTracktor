package com.preetTractor.galaxyAndroid.utils

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.print.PdfPrint
import android.print.PrintAttributes
import android.webkit.WebView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.preetTractor.galaxyAndroid.R
import java.io.File

object WebViewToPdfConverter {

    const val REQUEST_CODE = 101

    fun createWebPrintJob(activity: Activity, webView: WebView, directory: File, fileName: String, callback: Callback) {
        //todo commented because its not working in android 14
        // Check for permissions
     /*   if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE),
                    REQUEST_CODE
                )
                callback.failure()
                return
            }
        }*/

        val jobName = activity.getString(R.string.app_name) + " Document"
        val attributes = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            PrintAttributes.Builder()
                .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
                .setResolution(PrintAttributes.Resolution("pdf", "pdf", 600, 600))
                .setMinMargins(PrintAttributes.Margins.NO_MARGINS)
                .build()
        } else null

        val pdfPrint = PdfPrint(attributes)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // Create the file path as a String
            val filePath = File(directory, fileName).path

            pdfPrint.print(webView.createPrintDocumentAdapter(jobName), filePath, object : PdfPrint.CallbackPrint {
                override fun success(path: String) {
                    callback.success(path)
                }

                override fun onFailure() {
                    callback.failure()
                }
            })
        }

        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val filePath = File(directory, fileName).path

            pdfPrint.print(webView.createPrintDocumentAdapter(), filePath, object : PdfPrint.CallbackPrint {
                override fun success(path: String) {
                    callback.success(path)
                }

                override fun onFailure() {
                    callback.failure()
                }
            })
        }

    }

    fun openPdfFile(activity: Activity, title: String, message: String, path: String) {
        // Check for permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE),
                    REQUEST_CODE
                )
                return
            }
        }

        val builder = AlertDialog.Builder(activity)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton("Open") { dialog, _ ->
            dialog.dismiss()
            fileChooser(activity, path)
        }
        builder.setNegativeButton("Dismiss") { dialog, _ ->
            dialog.dismiss()
        }
        val alert = builder.create()
        alert.show()
    }

    private fun fileChooser(activity: Activity, path: String) {
        val file = File(path)
        val target = Intent(Intent.ACTION_VIEW)
        val uri = FileProvider.getUriForFile(
            activity,
            "${activity.applicationContext.packageName}.FileProvider",
            file
        )
        target.setDataAndType(uri, "application/pdf")
        target.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        val intent = Intent.createChooser(target, "Open File")
        try {
            activity.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            e.printStackTrace()
        }
    }

    interface Callback {
        fun success(path: String)
        fun failure()
    }
}
