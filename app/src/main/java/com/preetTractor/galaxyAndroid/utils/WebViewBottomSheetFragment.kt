package com.preetTractor.galaxyAndroid.utils

import android.app.ProgressDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.content.FileProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.preetTractor.galaxyAndroid.databinding.BottomSheetDialogShareReportBinding
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class WebViewBottomSheetFragment(dialogWeb: WebView, url: String, title: String) : BottomSheetDialogFragment() {

    var _binding : BottomSheetDialogShareReportBinding? = null
    private val binding get() = _binding!!

    private lateinit var dialogWeb: WebView
    private lateinit var url: String
    private lateinit var title: String


    companion object {
        fun newInstance(dialogWeb: WebView, url: String, title: String): WebViewBottomSheetFragment {
            return WebViewBottomSheetFragment(dialogWeb, url, title)
        }
    }

    init {
        this.dialogWeb = dialogWeb
        this.url = url
        this.title = title
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = BottomSheetDialogShareReportBinding.inflate(layoutInflater)

        binding.headingBottomSheetShareReport.text = title
        setUpWebViewDialog(binding.webViewBottomSheetDialog, url, false, binding.loader, binding.linearWhatsappShare, binding.linearGmailShare, binding.linearOtherShare)

        binding.ivForword.setOnClickListener {
            val uri = Uri.parse(url)
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }

        binding.ivCloseBottomSheet.setOnClickListener {
            dismiss()
        }

        binding.linearWhatsappShare.setOnClickListener {
            val f_name = String.format(
                "%s.pdf", SimpleDateFormat("dd_MM_yyyyHH_mm_ss", Locale.US).format(
                    Date()
                )
            )

            labPdf(dialogWeb, f_name)

        }

        binding.linearOtherShare.setOnClickListener {
            val f_name = String.format(
                "%s.pdf", SimpleDateFormat("dd_MM_yyyyHH_mm_ss", Locale.US).format(
                    Date()
                )
            )
            labOtherPdf(dialogWeb, f_name)
        }

        binding.linearGmailShare.setOnClickListener {
            val f_name = String.format(
                "%s.pdf", SimpleDateFormat("dd_MM_yyyyHH_mm_ss", Locale.US).format(
                    Date()
                )
            )
            labGmailPdf(dialogWeb, f_name)
        }


        return binding.root
    }

    private fun labGmailPdf(webView: WebView, fName: String) {
        val path = "${Environment.getExternalStorageDirectory()}/${Environment.DIRECTORY_DOWNLOADS}/hana/"
        val f = File(path)
        val fileName = fName

        val progressDialog = ProgressDialog(requireContext())
        progressDialog.setMessage("Please wait")
        progressDialog.show()

        WebViewToPdfConverter.createWebPrintJob(
            requireActivity(),
            webView,
            f,
            fileName,
            object : WebViewToPdfConverter.Callback {
                override fun success(path: String) {
                    progressDialog.dismiss()
                    gmailShare(fileName)
                }

                override fun failure() {
                    progressDialog.dismiss()
                }
            })
    }

    private fun gmailShare(fName: String) {
        val stringFile = "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)}/hana/$fName"
        val file = File(stringFile)
        val apkURI = FileProvider.getUriForFile(requireContext(), "${requireActivity().packageName}.FileProvider", file)

        if (!file.exists()) {
            Toast.makeText(requireContext(), "File Not exist", Toast.LENGTH_SHORT).show()
        }

        val share = Intent().apply {
            action = Intent.ACTION_SEND
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, apkURI)
            setPackage("com.google.android.gm")
        }

        startActivity(share)
    }

    private fun labOtherPdf(webView: WebView, fName: String) {
        val path = "${Environment.getExternalStorageDirectory()}/${Environment.DIRECTORY_DOWNLOADS}/hana/"
        val f = File(path)
        val fileName = fName

        val progressDialog = ProgressDialog(requireContext())
        progressDialog.setMessage("Please wait")
        progressDialog.show()

        WebViewToPdfConverter.createWebPrintJob(
            requireActivity(),
            webView,
            f,
            fileName,
            object : WebViewToPdfConverter.Callback {
                override fun success(path: String) {
                    progressDialog.dismiss()
                    otherShare(fileName)
                }

                override fun failure() {
                    progressDialog.dismiss()
                }
            })
    }

    private fun whatsappShare(fName: String) {
        val stringFile = "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)}/hana/$fName"
        val file = File(stringFile)
        val apkURI: Uri? = try {
            FileProvider.getUriForFile(requireContext(), requireActivity().packageName + ".FileProvider", file)
        } catch (e: Exception) {
            Log.e("whatsapp", "showBottomSheetDialog: ", e)
            null
        }

        if (!file.exists()) {
            Toast.makeText(requireContext(), "File Not exist", Toast.LENGTH_SHORT).show()
        }

        try {
            val share = Intent().apply {
                action = Intent.ACTION_SEND
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, apkURI)
                `package` = when {
                    isAppInstalled("com.whatsapp") -> "com.whatsapp"
                    isAppInstalled("com.whatsapp.w4b") -> "com.whatsapp.w4b"
                    else-> "Whatsapp not found"

                }
            }
            startActivity(share)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(requireContext(), "WhatsApp is not currently installed on your phone.", Toast.LENGTH_LONG).show()
        }
    }

    private fun otherShare(fName: String) {
        val stringFile = "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)}/hana/$fName"
        val file = File(stringFile)
        val apkURI = FileProvider.getUriForFile(requireContext(), "${requireActivity().packageName}.FileProvider", file)

        if (!file.exists()) {
            Toast.makeText(requireContext(), "File Not exist", Toast.LENGTH_SHORT).show()
        }

        val intent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, apkURI)
        }

        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            startActivity(Intent.createChooser(intent, "Share PDF using"))
        }
    }

    private fun labPdf(webView: WebView, fName: String) {
        val path = "${Environment.getExternalStorageDirectory()}/${Environment.DIRECTORY_DOWNLOADS}/hana/"
        val f = File(path)
        val fileName = fName

        val progressDialog = ProgressDialog(requireContext())
        progressDialog.setMessage("Please wait")
        progressDialog.show()

        WebViewToPdfConverter.createWebPrintJob(
            requireActivity(),
            webView,
            f,
            fileName,
            object : WebViewToPdfConverter.Callback {
                override fun success(path: String) {
                    progressDialog.dismiss()
                    whatsappShare(fileName)
                }

                override fun failure() {
                    progressDialog.dismiss()
                }
            })
    }

    private fun setUpWebViewDialog(webView: WebView, url: String, isZoomAvailable: Boolean, dialog: ProgressBar, whatsapp: LinearLayout, gmail: LinearLayout, other: LinearLayout) {
        webView.settings.apply {
            builtInZoomControls = isZoomAvailable
            loadsImagesAutomatically = true
            javaScriptEnabled = true
            domStorageEnabled = true
            useWideViewPort = true
            loadWithOverviewMode = true
        }
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY)

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            // For API level 21 and above
            webView.settings.cacheMode = WebSettings.LOAD_NO_CACHE
        } else {
            // For API level 20 and below
            webView.clearCache(true)
        }

        webView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, null)
                dialog.visibility = View.VISIBLE
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                dialog.visibility = View.GONE
                dialogWeb = webView
                whatsapp.isEnabled = true
                gmail.isEnabled = true
                other.isEnabled = true
            }
        }

        webView.loadUrl(url)
    }

    private fun isAppInstalled(packageName: String): Boolean {
        return try {
            requireActivity().packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
            true
        } catch (ignored: PackageManager.NameNotFoundException) {
            false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}