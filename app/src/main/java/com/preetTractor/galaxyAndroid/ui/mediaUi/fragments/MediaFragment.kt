package com.preetTractor.galaxyAndroid.ui.mediaUi.fragments

import android.Manifest
import android.animation.Animator
import android.animation.ObjectAnimator
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.Spannable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.style.StyleSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.recyclerview.widget.LinearLayoutManager
import com.preetTractor.galaxyAndroid.R
import com.preetTractor.galaxyAndroid.databinding.FragmentMediaBinding
import com.preetTractor.galaxyAndroid.helper.APiPayloadKeys
import com.preetTractor.galaxyAndroid.helper.Globals
import com.preetTractor.galaxyAndroid.helper.PrefsByShubh
import com.preetTractor.galaxyAndroid.ui.activity.MainActivity
import com.preetTractor.galaxyAndroid.ui.activity.SchemeOneActivity
import com.google.gson.JsonObject
import com.preetTractor.galaxyAndroid.BuildConfig
import com.preetTractor.galaxyAndroid.apiHelper.AppConstants
import com.preetTractor.galaxyAndroid.apiHelper.AppConstants.REQUEST_ID_MULTIPLE_PERMISSIONS
import com.preetTractor.galaxyAndroid.ui.mediaUi.activity.PdfViewActivity
import com.preetTractor.galaxyAndroid.mediaUi.adapter.HeadingMediaDynamicAdapter
import com.preetTractor.galaxyAndroid.ui.mediaUi.adapter.MediaAdapter
import com.preetTractor.galaxyAndroid.moreUi.model.documentsModel.DocumentListModel
import com.preetTractor.galaxyAndroid.mvvmSetUp.MainViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.File


class MediaFragment : Fragment() {
    lateinit var binding: FragmentMediaBinding
    lateinit var viewModel: MainViewModel
    //lateinit var sessionManagement: SessionManagement


    private var downloadID: Long = 0

    lateinit var builder: AlertDialog.Builder
    lateinit var alertDialog: AlertDialog

    // TODO: Rename and change types and number of parameters
    private fun SetUPDialog(context: Context) {
        builder = AlertDialog.Builder(context)

        builder!!.setView(R.layout.progress_dialog)

            .setCancelable(false)
        alertDialog = builder!!.create()
    }

    private val baseText = "search for \"aloo\""
    private val newWords = listOf("Video", "Document", "Posters")
    private var wordIndex = 0

    // Declare a global variable to hold the ObjectAnimator
    private var textAnimation: ObjectAnimator? = null


    private var currentTextIndex = 0
    private val textList =
        listOf("Text 1", "Text 2", "Text 3", "Text 4") // Your list of text values

    /*   private val textWatcher = object : TextWatcher {
           override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

           override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
               if (s.isNullOrEmpty()) {
                   // If the entered text is empty, resume the animation
                   startAutoUpdate()
               } else {
                   // If the entered text is not empty, cancel the animation
                   cancelAnimation()
               }
           }

           override fun afterTextChanged(s: Editable?) {}
       }*/

    private fun cancelAnimation() {
        textAnimation?.cancel()
    }


    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            CoroutineScope(Dispatchers.Main).launch {
                delay(1000)
                if (Globals.checkForInternet(requireContext())) {


                    var jsonObject = JsonObject()
                    jsonObject.addProperty(APiPayloadKeys.document_id, selectedId)
                    jsonObject.addProperty(APiPayloadKeys.search, s.toString())
                    viewModel.getDocumentAllItemListApi(jsonObject, requireActivity())

                    bindVideoDocListObserver()


                }
            }
        }

        override fun afterTextChanged(s: Editable?) {}
    }


    private fun bindEditText() {
        binding.edtSearchActual.addTextChangedListener(textWatcher)
    }

    override fun onDestroy() {
        super.onDestroy()
        // Remove the TextWatcher to avoid memory leaks
        binding.edtSearchActual.removeTextChangedListener(textWatcher)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMediaBinding.inflate(layoutInflater)
        return binding.root
    }

    companion object {
        private const val TAG = "MediaFragment"
    }

    var selectedId = ""
    var searchTextValue = ""
    var REQUEST_EXTERNAL_STORAGE = 8888


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as MainActivity).viewModel

        try {
            SetUPDialog(requireActivity())
        } catch (e: Exception) {
        }
        // binding.edtSearch.setHint(baseText)
        binding.tvSearchDoc.text = newWords[wordIndex]
        // Initial text setup
        updateText(newWords[wordIndex])

        // startAutoUpdateText()

        // Start the automatic update coroutine
        startAutoUpdate()
        bindEditText()

        // isPermissionGiven()


        /*  // Check if the permission is already granted
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
              if (ActivityCompat.checkSelfPermission(
                      requireActivity(),
                      Manifest.permission.WRITE_EXTERNAL_STORAGE
                  ) != PackageManager.PERMISSION_GRANTED
              ) {
                  // Permission is not granted
                  // Requesting the permission
                  ActivityCompat.requestPermissions(
                      requireActivity(),
                      arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                      REQUEST_EXTERNAL_STORAGE
                  )
              } else {
                  // Permission has already been granted
                  // You can proceed with your file operations
                  // writeToFile()
              }
          } else {
              // Runtime permissions are not required before Android 6.0
              // You can proceed with your file operations
              // writeToFile()
          }*/

        //todo check self permissions
        checkAndRequestPermissions()



        binding.linearSearch.setOnClickListener {
//            Intent(requireActivity(), SearchActivity::class.java).also {
//                it.putExtra("where", "media")
//                it.putExtra("docId", selectedId)
//                startActivity(it)
//            }
        }

        binding.ibSearch.setOnClickListener {
//            Intent(requireActivity(), SearchActivity::class.java).also {
//                it.putExtra("where", "media")
//                it.putExtra("docId", selectedId)
//                startActivity(it)
//            }
        }

        var title = requireActivity().findViewById<TextView>(R.id.tvTitle)
//        var image = requireActivity().findViewById<ImageView>(R.id.profile_icon)
        //image.visibility = View.VISIBLE
//        title.text = PrefsByShubh.getCardName()?:""

        if (Globals.checkForInternet(requireActivity())) {
            viewModel.getDocumentAllApi(requireActivity())
            bindListObserver()

            var jsonObject = JsonObject()
            jsonObject.addProperty(APiPayloadKeys.document_id, selectedId)
            jsonObject.addProperty(APiPayloadKeys.search, searchTextValue)
            viewModel.getDocumentAllItemListApi(jsonObject, requireActivity())

            bindVideoDocListObserver()


        }

    }


    /*  private fun isPermissionGiven(): Boolean {
          if (ContextCompat.checkSelfPermission(
                  context!!,
                  Manifest.permission.MANAGE_EXTERNAL_STORAGE
              ) == PackageManager.PERMISSION_GRANTED
          ) {
              ActivityCompat.requestPermissions(
                  requireActivity(),
                  arrayOf<String>(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                  REQUEST_ID_MULTIPLE_PERMISSIONS
              )
          } else {
              return true
          }
          return false
      }*/


    private fun startAutoUpdateText() {
        val textView = binding.tvSearchDoc

        val job = CoroutineScope(Dispatchers.Main).launch {
            while (isActive) {
                animateTextChangeText(textView, textList[currentTextIndex])
                delay(3000) // Update interval: 3 seconds
                currentTextIndex = (currentTextIndex + 1) % textList.size
            }
        }

        // Cancel the job when the activity is destroyed
        lifecycle.addObserver(LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_DESTROY) {
                job.cancel()
            }
        })
    }

    private fun animateTextChangeText(textView: TextView, newText: String) {
        textView.text = newText
        textView.translationY = 100f // Initial position below its original position

        val translateY = ObjectAnimator.ofFloat(textView, "translationY", 100f, 0f)
        translateY.duration = 1000 // Duration for rise-from-bottom effect
        translateY.interpolator = AccelerateInterpolator()

        translateY.start()
    }


    private fun updateText(word: String) {
        var newWord = "\"$word\""
        // val newText = baseText.replace("aloo", word)
        val newText = baseText.replace("search for \"aloo\"", word)
        val spannableString = SpannableString(newText)

        // Find the start and end indices of the word to be bolded
        val startIndex = newText.indexOf(word)
        val endIndex = startIndex + word.length
        // var subString=baseText.substring(startIndex-1,endIndex)


        // Apply bold style to the word
        spannableString.setSpan(
            StyleSpan(Typeface.BOLD),
            startIndex,
            endIndex,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        // Apply text change with animation
        animateTextChange(spannableString)
    }

    private fun animateTextChange(newText: SpannableString) {
        // Cancel any ongoing animation
        cancelAnimation()
        // Fade out and translate up the old hint
        val fadeOut = ObjectAnimator.ofFloat(binding.tvSearchDoc, "alpha", 1f, 0f)
        val translateUp = ObjectAnimator.ofFloat(binding.tvSearchDoc, "translationY", 0f, -50f)
        fadeOut.duration = 300
        translateUp.duration = 300

        fadeOut.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {}
            override fun onAnimationEnd(animation: Animator) {
                // Set the new hint
                binding.tvSearchDoc.text = newText

                // Reset the translation and fade in the new hint
                binding.tvSearchDoc.translationY = 100f
                val fadeIn = ObjectAnimator.ofFloat(binding.tvSearchDoc, "alpha", 0f, 1f)
                val translateDown =
                    ObjectAnimator.ofFloat(binding.tvSearchDoc, "translationY", 100f, 0f)
                fadeIn.duration = 1000
                translateDown.duration = 1000

                fadeIn.start()
                translateDown.start()
            }

            override fun onAnimationCancel(animation: Animator) {}
            override fun onAnimationRepeat(animation: Animator) {}
        })

        fadeOut.start()
        translateUp.start()
    }


    private fun animateTextChange(newText: String) {
        // Cancel any ongoing animation
        cancelAnimation()
        // Fade out and translate up the old hint
        val fadeOut = ObjectAnimator.ofFloat(binding.tvSearchDoc, "alpha", 1f, 0f)
        val translateUp = ObjectAnimator.ofFloat(binding.tvSearchDoc, "translationY", 0f, -100f)
        fadeOut.duration = 300
        translateUp.duration = 300

        fadeOut.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {}
            override fun onAnimationEnd(animation: Animator) {
                // Set the new hint
                binding.tvSearchDoc.hint = newText

                // Reset the translation and fade in the new hint
                binding.tvSearchDoc.translationY = 100f
                val fadeIn = ObjectAnimator.ofFloat(binding.tvSearchDoc, "alpha", 0f, 1f)
                val translateDown =
                    ObjectAnimator.ofFloat(binding.tvSearchDoc, "translationY", 100f, 0f)
                fadeIn.duration = 1000
                translateDown.duration = 1000

                fadeIn.start()
                translateDown.start()
            }

            override fun onAnimationCancel(animation: Animator) {}
            override fun onAnimationRepeat(animation: Animator) {}
        })

        fadeOut.start()
        translateUp.start()
    }


    /* private fun bindEditText() {
         binding.edtSearch.addTextChangedListener(textWatcher)
     }

     override fun onDestroy() {
         super.onDestroy()
         // Remove the TextWatcher to avoid memory leaks
         binding.edtSearch.removeTextChangedListener(textWatcher)
 //       requireActivity().unregisterReceiver(receiver)
     }*/

    private fun startAutoUpdate() {
        val job = CoroutineScope(Dispatchers.Main).launch {
            while (isActive) {
                delay(2500) // Delay for 3 seconds
                wordIndex = (wordIndex + 1) % newWords.size
                updateText(newWords[wordIndex])
            }
        }

        // Cancel the job when the activity is destroyed
        lifecycle.addObserver(LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_DESTROY) {
                job.cancel()
            }
        })
    }


    /*    private fun updateText(word: String) {
            val newText = baseText.replace("aloo", word)
            val spannableString = SpannableString(newText)

            // Find the start and end indices of the word to be bolded
            val startIndex = newText.indexOf(word)
            val endIndex = startIndex + word.length

            // Apply bold style to the word
            spannableString.setSpan(
                StyleSpan(Typeface.BOLD),
                startIndex,
                endIndex,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            // Set the new hint
            binding.edtSearch.setHint(spannableString)

            // Apply text change with animation
            animateTextChange(spannableString, startIndex, endIndex)
        }

        private fun animateTextChange(spannableString: SpannableString, startIndex: Int, endIndex: Int) {
            val animator = ValueAnimator.ofFloat(0f, 1f)
            animator.duration = 500

            animator.addUpdateListener { animation ->
                val value = animation.animatedValue as Float
                spannableString.setSpan(
                    ScaleXSpan(value),
                    startIndex,
                    endIndex,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                binding.edtSearch.setHint(spannableString)
            }

            animator.start()
        }

        private fun startAutoUpdate() {
            val job = CoroutineScope(Dispatchers.Main).launch {
                while (isActive) {
                    delay(3000) // Delay for 3 seconds
                    wordIndex = (wordIndex + 1) % newWords.size
                    updateText(newWords[wordIndex])
                }
            }

            // Cancel the job when the activity is destroyed
            lifecycle.addObserver(LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_DESTROY) {
                    job.cancel()
                }
            })
        }*/


    private fun bindVideoDocListObserver() {
        viewModel.documentItemAllList.observe(requireActivity(), Event.EventObserver(
            onError = {
                //  binding.spinKitLoader.visibility = View.GONE
                Log.e(TAG, "bindRemarkObserver: $it")
            },
            onLoading = {
                //  binding.spinKitLoader.visibility = View.VISIBLE
            },
            onSuccess = { response ->
                if (response.status == 200) {
                    // binding.spinKitLoader.visibility = View.GONE

                    if (response.data.size > 0) {
                        binding.ivNoDataFound.ivNoDataFound.visibility = View.GONE
                    } else {
                        binding.ivNoDataFound.ivNoDataFound.visibility = View.VISIBLE
                    }

                    try {
                        var adapter = MediaAdapter(requireActivity(), response.data)
                        var layoutManager =
                            LinearLayoutManager(
                                requireActivity(),
                                LinearLayoutManager.VERTICAL,
                                false
                            )
                        binding.rvVideoDoc.layoutManager = layoutManager
                        binding.rvVideoDoc.adapter = adapter

                        adapter.setOnItemClickListener { data, i ->
                            if (data.file_type.equals("Video")) {
                                shareVideoUrl(data.file)
                            } else {
                                downloadFile(BuildConfig.IMAGE_URL + data.file)
                            }

                        }

                        adapter.setOnItemPdfClickListener { data, i ->
                            //downloadFile(BuildConfig.IMAGE_URL + data.file)
                            Intent(requireActivity(), PdfViewActivity::class.java).also {
                                it.putExtra("url", data.file)
                                startActivity(it)
                            }
                        }


                        adapter.setOnItemImageClickListener { data, i ->
                            //downloadFile(BuildConfig.IMAGE_URL + data.file)
                            Intent(requireActivity(), SchemeOneActivity::class.java).also {
                                it.putExtra("user_data", data)
                                startActivity(it)
                            }
                        }

                        adapter.notifyDataSetChanged()
                    } catch (e: Exception) {
                    }


                } else if (response.status == 401) {
              //      PrefsByShubh.ClearSession()
                    Globals.logoutScreen(requireActivity())

                } else {
                    //binding.spinKitLoader.visibility = View.GONE
                    Globals.warningMessage(requireActivity(), response.message)
                }
            }
        ))
    }

    private fun bindListObserver() {
        viewModel.documentALlList.observe(viewLifecycleOwner, Event.EventObserver(
            onError = {

                Log.e(TAG, "bindRemarkObserver: $it")
            },
            onLoading = {

            },
            onSuccess = { response ->
                if (response.status == 200) {


                    if (response.data.size > 0) {
                        response.data.add(0, DocumentListModel.Data(name = "Latest", id = 987))
                    } else {

                    }

                    var linearLayoutManager =
                        LinearLayoutManager(
                            requireActivity(),
                            LinearLayoutManager.HORIZONTAL,
                            false
                        )
                    var adapter = HeadingMediaDynamicAdapter(requireActivity(), response.data)
                    binding.rvHeading.layoutManager = linearLayoutManager
                    binding.rvHeading.adapter = adapter

                    adapter.setOnItemClickListener { data, i ->
                        Log.e(TAG, "bindListObserver: ${data.name}")
                        if (data.id.equals(987)) {
                            selectedId = ""
                        } else {
                            selectedId = data.id.toString()
                        }

                        var jsonObject = JsonObject()
                        jsonObject.addProperty(APiPayloadKeys.document_id, selectedId)
                        jsonObject.addProperty(APiPayloadKeys.search, searchTextValue)
                        viewModel.getDocumentAllItemListApi(jsonObject, requireActivity())

                        bindVideoDocListObserver()

                    }

                } else if (response.status == 401) {
               //     PrefsByShubh.ClearSession()
                    Globals.logoutScreen(requireActivity())

                } else {

                    Globals.warningMessage(requireActivity(), response.message)
                }
            }
        ))
    }


    private fun downloadFile(url: String) {
        alertDialog.show()
        val request = DownloadManager.Request(Uri.parse(url))
            .setTitle("Downloading File")
            .setDescription("Downloading image")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalPublicDir(
                Environment.DIRECTORY_DOWNLOADS,
                Uri.parse(url).lastPathSegment
            )

        try {
            val downloadManager =
                requireActivity().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            downloadID = downloadManager.enqueue(request)
        } catch (e: Exception) {
            alertDialog.dismiss()
            Toast.makeText(requireActivity(), "permission not granted", Toast.LENGTH_SHORT).show()
        }

        val filter = IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        requireActivity().registerReceiver(receiver, filter, Context.RECEIVER_EXPORTED)
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            if (downloadID == id) {
                handleDownloadCompletion(id)
            }
        }
    }


 /*   private fun handleDownloadCompletion(downloadId: Long) {
        val downloadManager =
            requireActivity().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val query = DownloadManager.Query().setFilterById(downloadId)
        val cursor = downloadManager.query(query)

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                val columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI)
                if (columnIndex >= 0) {
                    val fileUri = cursor.getString(columnIndex)
                    if (fileUri != null) {
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
    }*/


    private fun handleDownloadCompletion(downloadId: Long) {
        val downloadManager = requireActivity().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val query = DownloadManager.Query().setFilterById(downloadId)
        val cursor = downloadManager.query(query)

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                val columnIndexUri = cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI)

                if (columnIndexUri >= 0) {
                    val fileUri = cursor.getString(columnIndexUri)
                    if (fileUri != null) {
                        val uri = Uri.parse(fileUri)
                        val contentResolver = requireActivity().contentResolver
                        val inputStream = contentResolver.openInputStream(uri)

                        if (inputStream != null) {
                            // Process the file or share it
                            shareFile(uri.toString())
                        } else {
                            alertDialog.dismiss()
                            Log.e("DownloadReceiver", "Failed to open InputStream")
                        }
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
        val uri = FileProvider.getUriForFile(
            requireActivity(),
            "${requireActivity().packageName}.FileProvider",
            file
        )

        //  val uri = Uri.parse(fileUri)
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_STREAM, uri)
            type = requireActivity().contentResolver.getType(uri)
        }
        startActivity(Intent.createChooser(shareIntent, "Share File"))
    }

    private fun shareVideoUrl(fileUri: String) {
        /*  // alertDialog.dismiss()
           val file = File(Uri.parse(fileUri).path!!)
           val uri = FileProvider.getUriForFile(
               requireActivity(),
               "${requireActivity().packageName}.FileProvider",
               file
           )*/

        //  val uri = Uri.parse(fileUri)
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, fileUri)
            type = "text/plain"
        }
        startActivity(Intent.createChooser(shareIntent, "Share  via"))
    }


    val listPermissionsNeeded = mutableListOf<String>()


    private fun checkAndRequestPermissions(): Boolean {

        /* val write =
             ContextCompat.checkSelfPermission(
                 requireActivity(),
                 Manifest.permission.MANAGE_EXTERNAL_STORAGE
             )*/
        val read =
            ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            )


        /*  if (write != PackageManager.PERMISSION_GRANTED) {
              listPermissionsNeeded.add(Manifest.permission.MANAGE_EXTERNAL_STORAGE)
          }*/

        if (read != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }

        try {
            if (listPermissionsNeeded.isNotEmpty()) {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    listPermissionsNeeded.toTypedArray(),
                    REQUEST_ID_MULTIPLE_PERMISSIONS
                )
                return false
            }
        } catch (e: Exception) {
        }

        return true
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_ID_MULTIPLE_PERMISSIONS -> {
                val perms = hashMapOf<String, Int>()
                // Initialize the map with both permissions
                /*  perms[Manifest.permission.MANAGE_EXTERNAL_STORAGE] =
                      PackageManager.PERMISSION_GRANTED*/

                perms[Manifest.permission.READ_EXTERNAL_STORAGE] = PackageManager.PERMISSION_GRANTED

                // Fill with actual results from user
                if (grantResults.isNotEmpty()) {
                    for (i in permissions.indices) {
                        perms[permissions[i]] = grantResults[i]
                    }
                    // Check for both permissions
                    if (perms[Manifest.permission.WRITE_EXTERNAL_STORAGE] == PackageManager.PERMISSION_GRANTED &&
                        perms[Manifest.permission.READ_EXTERNAL_STORAGE] == PackageManager.PERMISSION_GRANTED
                    ) {

                    } else {
                        // Permissions are denied
                        Toast.makeText(
                            requireActivity(),
                            "Some permissions are not granted. You cannot proceed.",
                            Toast.LENGTH_LONG
                        ).show()

                    }
                }
            }
        }
    }


    /*   // Handle the permission request response
       override fun onRequestPermissionsResult(
           requestCode: Int,
           permissions: Array<out String>,
           grantResults: IntArray
       ) {
           super.onRequestPermissionsResult(requestCode, permissions, grantResults)

           when (requestCode) {
               REQUEST_EXTERNAL_STORAGE -> {
                   if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                       // Permission granted, proceed with your file operations
                       // writeToFile()
                   } else {
                       // Permission denied, handle this as needed
                       // For example, show a message to the user
                   }
                   return
               }
           }
       }*/


}