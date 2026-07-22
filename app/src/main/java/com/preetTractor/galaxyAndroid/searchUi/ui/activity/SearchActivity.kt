package com.preetTractor.galaxyAndroid.searchUi.ui.activity

import Event
import android.Manifest
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.JsonObject
import com.preetTractor.galaxyAndroid.BuildConfig
import com.preetTractor.galaxyAndroid.R
import com.preetTractor.galaxyAndroid.apiHelper.ApiClient
import com.preetTractor.galaxyAndroid.apiHelper.ApisInterface
import com.preetTractor.galaxyAndroid.apiHelper.AppConstants
import com.preetTractor.galaxyAndroid.databinding.ActivitySearchBinding
import com.preetTractor.galaxyAndroid.helper.APiPayloadKeys
import com.preetTractor.galaxyAndroid.helper.Globals
import com.preetTractor.galaxyAndroid.helper.PrefsByShubh
import com.preetTractor.galaxyAndroid.mediaUi.adapter.SchemeAdapter
import com.preetTractor.galaxyAndroid.mvvmSetUp.DefaultMainRepositories
import com.preetTractor.galaxyAndroid.mvvmSetUp.MainRepos
import com.preetTractor.galaxyAndroid.mvvmSetUp.MainViewModel
import com.preetTractor.galaxyAndroid.mvvmSetUp.MainViewModelProvider
import com.preetTractor.galaxyAndroid.searchUi.adapter.SearchItemInListAdapter
import com.preetTractor.galaxyAndroid.searchUi.model.DataSearchItemDmsSuggestion
import com.preetTractor.galaxyAndroid.ui.activity.SchemeOneActivity
import com.preetTractor.galaxyAndroid.ui.mediaUi.activity.PdfViewActivity
import com.preetTractor.galaxyAndroid.ui.mediaUi.adapter.MediaAdapter
import com.preetTractor.galaxyAndroid.ui.orderUi.activity.ItemListFromSubCategoryOrderRequestActivity
import com.preetTractor.galaxyAndroid.ui.orderUi.activity.ItemOneActivity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

class SearchActivity : AppCompatActivity() {
    lateinit var binding: ActivitySearchBinding
    lateinit var viewModel: MainViewModel
    var searchTextValue = ""
    //lateinit var sessionManagement: SessionManagement

    var itemSearchAdapter: SearchItemInListAdapter? = null

    var where = ""
    var docId = ""
    private var downloadID: Long = 0

    companion object {
        // private const val TAG = "SearchActivity"
    }

    private fun setUpViewModel() {
        val dispatchers: CoroutineDispatcher = Dispatchers.Main
        val mainRepos = DefaultMainRepositories() as MainRepos
        val fanxApi: ApisInterface = ApiClient().service(this)
        val viewModelProviderfactory =
            MainViewModelProvider(application, mainRepos, dispatchers, fanxApi)
        viewModel = ViewModelProvider(this, viewModelProviderfactory)[MainViewModel::class.java]

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //sessionManagement = SessionManagement(this)
        binding.tvTitle.text = PrefsByShubh.getCardName() //sessionManagement.getCardName()
        setUpViewModel()
        bindEditText()
        SetUPDialog(this)
        checkAndRequestPermissions()
        // supportActionBar!!.hide()
        binding.ibBack.setOnClickListener {
            finish()
        }



        where = intent.getStringExtra("where").toString()
        docId = intent.getStringExtra("docId").toString()


        binding.ibSearch.setOnClickListener {
            if (Globals.checkForInternet(this@SearchActivity)) {

                if (where.equals("item")) {
                    var jsonObject = JsonObject().apply {
                        addProperty(APiPayloadKeys.Search, "")
                    }
                    viewModel.searchItemInDMS(jsonObject, this@SearchActivity)
                    subscribeToItemSearchObserver()
                } else if (where.equals("scheme")) {
                    var jsonObject = JsonObject()
                    jsonObject.addProperty(APiPayloadKeys.PageNo, 1)
                    jsonObject.addProperty(APiPayloadKeys.Search, "")
                    jsonObject.addProperty(APiPayloadKeys.MaxSize, "All")
                    viewModel.getSchemeDocumentAllItemListApi(jsonObject, this@SearchActivity)
                    bindSchemeListObserver()
                } else {
                    var jsonObject = JsonObject()
                    jsonObject.addProperty(APiPayloadKeys.document_id, docId)
                    jsonObject.addProperty(APiPayloadKeys.search, "")
                    viewModel.getDocumentAllItemListApi(jsonObject, this@SearchActivity)

                    bindVideoDocListObserver()
                }


            }
        }


    }

    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        @RequiresApi(Build.VERSION_CODES.O)
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            CoroutineScope(Dispatchers.Main).launch {
                delay(1000)
                if (Globals.checkForInternet(this@SearchActivity)) {

                    if (where.equals("item")) {
                        var jsonObject = JsonObject().apply {
                            addProperty(APiPayloadKeys.Search, s.toString())
                        }
                        viewModel.searchItemInDMS(jsonObject, this@SearchActivity)
                        subscribeToItemSearchObserver()
                    } else if (where.equals("scheme")) {
                        var jsonObject = JsonObject()
                        jsonObject.addProperty(APiPayloadKeys.PageNo, 1)
                        jsonObject.addProperty(APiPayloadKeys.Search, s.toString())
                        jsonObject.addProperty(APiPayloadKeys.MaxSize, "All")
                        viewModel.getSchemeDocumentAllItemListApi(jsonObject, this@SearchActivity)
                        bindSchemeListObserver()
                    } else {
                        var jsonObject = JsonObject()
                        jsonObject.addProperty(APiPayloadKeys.document_id, docId)
                        jsonObject.addProperty(APiPayloadKeys.search, s.toString())
                        viewModel.getDocumentAllItemListApi(jsonObject, this@SearchActivity)

                        bindVideoDocListObserver()
                    }


                }
            }
        }

        override fun afterTextChanged(s: Editable?) {}
    }


    private fun bindEditText() {
        binding.edtSearch.addTextChangedListener(textWatcher)
    }

    override fun onDestroy() {
        super.onDestroy()
        // Remove the TextWatcher to avoid memory leaks
        binding.edtSearch.removeTextChangedListener(textWatcher)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun bindVideoDocListObserver() {
        viewModel.documentItemAllList.observe(this, Event.EventObserver(onError = {
            binding.spinKitLoader.visibility = View.GONE
            //  Log.e(TAG, "bindRemarkObserver: $it")
        }, onLoading = {
            binding.spinKitLoader.visibility = View.VISIBLE
        }, onSuccess = { response ->
            if (response.status == 200) {
                binding.spinKitLoader.visibility = View.GONE

                if (response.data.size > 0) {
                    binding.ivNoDataFound.ivNoDataFound.visibility = View.GONE
                } else {
                    binding.ivNoDataFound.ivNoDataFound.visibility = View.VISIBLE
                }

                var adapter = MediaAdapter(this, response.data)
//                    var layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                var layoutManager = GridLayoutManager(this, 2)

                binding.rvVideoDoc.layoutManager = layoutManager
                binding.rvVideoDoc.adapter = adapter

                adapter.notifyDataSetChanged()

                adapter.setOnItemClickListener { data, i ->
                    if (data.file_type.equals("Video")) {
                        shareVideoUrl(data.file)
                    } else {
                        downloadFile(BuildConfig.IMAGE_URL + data.file)
                    }
                }

                adapter.setOnItemPdfClickListener { data, i ->
                    //downloadFile(BuildConfig.IMAGE_URL + data.file)
                    Intent(this, PdfViewActivity::class.java).also {
                        it.putExtra("url", data.file)
                        startActivity(it)
                    }
                }


                adapter.setOnItemImageClickListener { data, i ->
                    //downloadFile(BuildConfig.IMAGE_URL + data.file)
                    Intent(this, SchemeOneActivity::class.java).also {
                        it.putExtra("user_data", data)
                        startActivity(it)
                    }
                }


            } else if (response.status == 401) {
                //sessionManagement.ClearSession()
                PrefsByShubh.ClearSession()
                Globals.logoutScreen(this)

            } else {
                //binding.spinKitLoader.visibility = View.GONE
                Globals.warningMessage(this, response.message)
            }
        }))
    }

    private fun shareVideoUrl(fileUri: String) {/*  // alertDialog.dismiss()
           val file = File(Uri.parse(fileUri).path!!)
           val uri = FileProvider.getUriForFile(
               requireActivity(),
               "${activity!!.packageName}.FileProvider",
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


    private fun bindSchemeListObserver() {
        viewModel.documentSchemeItemAllList.observe(this, Event.EventObserver(onError = {
            //  binding.spinKitLoader.visibility = View.GONE
            // Log.e(TAG, "bindRemarkObserver: $it")
        }, onLoading = {
            //  binding.spinKitLoader.visibility = View.VISIBLE
        }, onSuccess = { response ->
            if (response.status == 200) {
                // binding.spinKitLoader.visibility = View.GONE

                if (response.data.size > 0) {
                    binding.ivNoDataFound.ivNoDataFound.visibility = View.GONE
                } else {
                    binding.ivNoDataFound.ivNoDataFound.visibility = View.VISIBLE
                }

                try {
                    var adapter = SchemeAdapter(this, response.data)
                    var layoutManager =
                        LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                    binding.rvVideoDoc.layoutManager = layoutManager
                    binding.rvVideoDoc.adapter = adapter

                    adapter.notifyDataSetChanged()

                    adapter.notifyDataSetChanged()

                    adapter.setOnItemClickListener { data, i ->
                        val intent = Intent(this, SchemeOneActivity::class.java).apply {
                            putExtra("user_data", data)
                        }
                        startActivity(intent)

                    }
                } catch (e: Exception) {
                }


            } else if (response.status == 401) {
                //sessionManagement.ClearSession()
                PrefsByShubh.ClearSession()
                Globals.logoutScreen(this)

            } else {
                //binding.spinKitLoader.visibility = View.GONE
                Globals.warningMessage(this, response.message)
            }
        }))
    }


    private fun subscribeToItemSearchObserver() {
        viewModel.itemSearcSugggestion.observe(this, Event.EventObserver(onError = {
            Globals.warningMessage(this, it)
            binding.spinKitLoader.visibility = View.GONE
            binding.apply {
                // shimmerLayout.stopShimmer()
            }

        }, onLoading = {
            binding.spinKitLoader.visibility = View.VISIBLE
            binding.apply {
                //shimmerLayout.startShimmer()
            }

        }, { response ->
            binding.apply {
                //shimmerLayout.stopShimmer()
            }
            binding.spinKitLoader.visibility = View.GONE

            if (response.status.equals(200)) {
                if (response.data.isNotEmpty()) {
                    setupRecyclerviewItemSearch(response.data)
                }


            } else if (response.status.equals(201)) {
                Globals.warningMessage(
                    this, response.message
                )
            } else if (response.status == 401) {
                //sessionManagement.ClearSession()
                PrefsByShubh.ClearSession()
                Globals.logoutScreen(this)

            } else {
                Globals.warningMessage(
                    this, response.message
                )
            }

        }))
    }


    private fun setupRecyclerviewItemSearch(data: List<DataSearchItemDmsSuggestion>) {

        /*  if (!data[0].U_UTL_ITMCT.equals("All")){
              data.add(0, CategoryItemResponseModel.Data("All"))
          }*/
        itemSearchAdapter = SearchItemInListAdapter(this)
        itemSearchAdapter!!.submitList(data)

        binding.rvVideoDoc.adapter = itemSearchAdapter
        binding.rvVideoDoc.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        itemSearchAdapter!!.notifyDataSetChanged()
        //todo calling subcategory
        itemSearchAdapter!!.setOnItemClickListener { data, pos ->
            AppConstants.addItemToDataSearchList(this, data)


            when (data.Type) {
                "Category" -> {
                    val i = Intent(this, ItemListFromSubCategoryOrderRequestActivity::class.java)

                    i.putExtra("id", "" + data.id)


                    startActivity(i)
                    finish()
                }

                "SubCategory" -> {
                    val i = Intent(this, ItemListFromSubCategoryOrderRequestActivity::class.java)

                    i.putExtra("id", "" + data.Name)


                    startActivity(i)
                    finish()
                }

                "Item" -> {
                    val i = Intent(this, ItemOneActivity::class.java)

                    i.putExtra("id", "" + data.id)


                    startActivity(i)
                    finish()
                }

                else -> {

                }


            }


        }


    }

    lateinit var builder: AlertDialog.Builder
    lateinit var alertDialog: AlertDialog

    // TODO: Rename and change types and number of parameters
    private fun SetUPDialog(context: Context) {
        builder = AlertDialog.Builder(context)

        builder.setView(R.layout.progress_dialog)

            .setCancelable(false)
        alertDialog = builder.create()
    }


    @RequiresApi(Build.VERSION_CODES.O)
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

    val listPermissionsNeeded = mutableListOf<String>()


    private fun checkAndRequestPermissions(): Boolean {

        val write = ContextCompat.checkSelfPermission(
            this, Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        val read = ContextCompat.checkSelfPermission(
            this, Manifest.permission.READ_EXTERNAL_STORAGE
        )



        if (write != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }

        if (read != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE)
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
                perms[Manifest.permission.WRITE_EXTERNAL_STORAGE] =
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
}