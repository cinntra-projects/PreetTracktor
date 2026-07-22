package com.preetTractor.galaxyAndroid.ui.activity


import Event
import android.Manifest
import android.annotation.SuppressLint
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
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.gson.JsonObject
import com.pixplicity.easyprefs.library.Prefs
import com.preetTractor.galaxyAndroid.BuildConfig
import com.preetTractor.galaxyAndroid.R
import com.preetTractor.galaxyAndroid.apiHelper.ApiClient
import com.preetTractor.galaxyAndroid.apiHelper.ApisInterface
import com.preetTractor.galaxyAndroid.apiHelper.AppConstants
import com.preetTractor.galaxyAndroid.databinding.ActivitySchemeOneBinding
import com.preetTractor.galaxyAndroid.helper.APiPayloadKeys
import com.preetTractor.galaxyAndroid.helper.Globals
import com.preetTractor.galaxyAndroid.helper.PrefsByShubh
import com.preetTractor.galaxyAndroid.helper.PrefsByShubh.businessPartnerDetails
import com.preetTractor.galaxyAndroid.moreUi.model.documentsModel.DocumentItemListModel
import com.preetTractor.galaxyAndroid.mvvmSetUp.DefaultMainRepositories
import com.preetTractor.galaxyAndroid.mvvmSetUp.MainRepos
import com.preetTractor.galaxyAndroid.mvvmSetUp.MainViewModel
import com.preetTractor.galaxyAndroid.mvvmSetUp.MainViewModelProvider
import com.preetTractor.galaxyAndroid.ui.orderUi.activity.CartActivity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import java.io.File

class SchemeOneActivity : BaseActivity() {
    lateinit var binding: ActivitySchemeOneBinding

    lateinit var scheme: DocumentItemListModel.Data

    private var downloadID: Long = 0

    lateinit var builder: AlertDialog.Builder
    lateinit var alertDialog: AlertDialog
    //lateinit var sessionManagement: SessionManagement

    lateinit var viewModel: MainViewModel


    // TODO: Rename and change types and number of parameters
    private fun SetUPDialog(context: Context) {
        builder = AlertDialog.Builder(context)

        builder.setView(R.layout.progress_dialog)

            .setCancelable(false)
        alertDialog = builder.create()
    }

    private fun setUpViewModel() {
        val dispatchers: CoroutineDispatcher = Dispatchers.Main
        val mainRepos = DefaultMainRepositories() as MainRepos
        val fanxApi: ApisInterface = ApiClient().service(this)
        val viewModelProviderfactory =
            MainViewModelProvider(application, mainRepos, dispatchers, fanxApi)
        viewModel = ViewModelProvider(this, viewModelProviderfactory)[MainViewModel::class.java]

    }

    override fun onResume() {
        super.onResume()
        //sessionManagement = SessionManagement(applicationContext)
        //todo bind default data---
        /*  var hashmap = HashMap<String, String>()
          hashmap["card_code"] = PrefsByShubh.getCardCode()!! //sessionManagement.getCardCode()!!
          hashmap["id"] = PrefsByShubh.getDistributorID()!! //sessionManagement.getDistributorID()!!
          viewModel.distributorProfile(hashmap, this)
          bindObserver()*/

        viewModel.bPOneApi(JsonObject().apply {
            addProperty(APiPayloadKeys.CardCode, PrefsByShubh.getCardCode())
        }, this)

        bindBpOneObserver()

        binding.tvTitle.text = PrefsByShubh.getCardName() //sessionManagement.getCardName()


        AppConstants.cartListForOrderRequest = AppConstants.getCartListFromPreferences(this)
        binding.tvCartCounter.text = AppConstants.cartListForOrderRequest.size.toString()

        if (AppConstants.cartListForOrderRequest.isNotEmpty()) {
            binding.tvCartCounter.visibility = View.VISIBLE
        } else {
            binding.tvCartCounter.visibility = View.INVISIBLE
        }

        binding.ivCollapseCart.setOnClickListener {

            if (AppConstants.cartListForOrderRequest.isNotEmpty()) {
                try {
                    binding.tvCartCounter.text =
                        AppConstants.getCartListFromPreferences(this).size.toString()
                } catch (e: Exception) {
                }
                Intent(this, CartActivity::class.java).also {

                    startActivity(it)
                }
            } else {

                Globals.warningMessage(this, "Cart is Empty")
            }

        }

    }

    var getId = ""

    private fun bindBpOneObserver() {
        viewModel.bPOneDetailData.observe(this, Event.EventObserver(onError = {
            alertDialog.dismiss()
            Globals.warningMessage(this, it)
        }, onLoading = {
            alertDialog.show()
        }, { response ->
            alertDialog.dismiss()
            if (response.status == 200) {
                //todo set dealer, special and additional discount
                response.data[0].apply {

                    businessPartnerDetails = response
                    Log.i("BP_DETAILS", "$businessPartnerDetails")

                    if (SalesPersonCode.isNotEmpty()) {
                        Prefs.putString(
                            Globals.SALES_EMPLOYEE_CODE, SalesPersonCode[0].SalesEmployeeCode
                        )
                    }



                    Prefs.putString(
                        Globals.CURRENCY, Currency
                    )

                    Prefs.putString(
                        Globals.DEALER_DISC, U_UTL_DLRD
                    )
                    Prefs.putString(
                        Globals.SPECIAL_DISC, U_UTL_SPCL
                    )
                    Prefs.putString(
                        Globals.ADDITIONAL_DISC, U_CIS_AD
                    )

                    Prefs.putString(
                        Globals.DISCOUNT_PERCENT, DiscountPercent
                    )
                    if (BPAddresses.isNotEmpty()) {
                        Prefs.putString(
                            Globals.BLOCK, BPAddresses[0].Block
                        )
                        Prefs.putString(
                            Globals.CITY, BPAddresses[0].City
                        )
                        Prefs.putString(
                            Globals.STATE, BPAddresses[0].State
                        )
                    }

                    if (ContactEmployees.isNotEmpty()) {
                        Prefs.putString(
                            Globals.CONTACT_PERSON_CODE, ContactEmployees[0].InternalCode
                        )

                    }

                    if (PayTermsGrpCode.isNotEmpty()) {
                        Prefs.putString(
                            Globals.PAYMENT_GROUP_CODE, PayTermsGrpCode[0].GroupNumber
                        )

                    }

                }

            } else if (response.status == 201) {
                Globals.warningMessage(this, response.message)
            } else if (response.status == 401) {
                //sessionManagement.ClearSession()
                PrefsByShubh.ClearSession()
                Globals.logoutScreen(this)

            } else {
                Globals.warningMessage(this, response.message)
            }


        }))
    }

    private fun bindObserver() {
        viewModel.distributorProfileData.observe(this, Event.EventObserver(onError = {
            //  Log.e(TAG, "bindRemarkObserver: $it")
        }, onLoading = {}, onSuccess = { response ->
            if (response.status == 200) {
                if (response.data.isNotEmpty()) {

                    //todo set dealer, special and additional discount
                    if (response.data[0].bp_detail.isNotEmpty()) {
                        if (response.data[0].bp_detail[0].SalesPersonCode.isNotEmpty()) {
                            Prefs.putString(
                                Globals.SALES_EMPLOYEE_CODE,
                                response.data[0].bp_detail[0].SalesPersonCode[0].SalesEmployeeCode
                            )
                        }



                        Prefs.putString(
                            Globals.CURRENCY, response.data[0].bp_detail[0].Currency
                        )

                        Prefs.putString(
                            Globals.DEALER_DISC, response.data[0].bp_detail[0].U_UTL_DLRD
                        )
                        Prefs.putString(
                            Globals.SPECIAL_DISC, response.data[0].bp_detail[0].U_UTL_SPCL
                        )
                        Prefs.putString(
                            Globals.ADDITIONAL_DISC, response.data[0].bp_detail[0].U_CIS_AD
                        )

                        Prefs.putString(
                            Globals.DISCOUNT_PERCENT,
                            response.data[0].bp_detail[0].DiscountPercent
                        )
                        if (response.data[0].bp_detail[0].BPAddresses.isNotEmpty()) {
                            Prefs.putString(
                                Globals.BLOCK,
                                response.data[0].bp_detail[0].BPAddresses[0].Block
                            )
                            Prefs.putString(
                                Globals.CITY,
                                response.data[0].bp_detail[0].BPAddresses[0].City
                            )
                            Prefs.putString(
                                Globals.STATE,
                                response.data[0].bp_detail[0].BPAddresses[0].State
                            )
                        }

                        if (response.data[0].bp_detail[0].ContactEmployees.isNotEmpty()) {
                            Prefs.putString(
                                Globals.CONTACT_PERSON_CODE,
                                response.data[0].bp_detail[0].ContactEmployees[0].InternalCode
                            )

                        }

                        if (response.data[0].bp_detail[0].PayTermsGrpCode.isNotEmpty()) {
                            Prefs.putString(
                                Globals.PAYMENT_GROUP_CODE,
                                response.data[0].bp_detail[0].PayTermsGrpCode[0].GroupNumber
                            )

                        }

                    }



                    getId = response.data[0].id.toString()

                    if (response.data[0].profileImage.isNotEmpty()) {
                        val filePath: String = BuildConfig.IMAGE_URL + response.data[0].profileImage
                        Glide.with(this@SchemeOneActivity).load(filePath).into(binding.profileIcon)


                    } else {
                        binding.profileIcon.setImageResource(R.drawable.deafult_image)

                    }


                } else {
                    binding.profileIcon.setImageResource(R.drawable.deafult_image)

                }


                /*   if (filePath != null) {



                   } else {

                   }*/


            } else if (response.status == 401) {
                //sessionManagement.ClearSession()
                PrefsByShubh.ClearSession()
                Globals.logoutScreen(this)

            } else {
                Globals.warningMessage(this, response.message)
            }
        }))
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySchemeOneBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpViewModel()
        // supportActionBar!!.hide()
        SetUPDialog(this)

        scheme = intent.getParcelableExtra<DocumentItemListModel.Data>("user_data")!!
        // Use the user data
        Glide.with(this).load(BuildConfig.IMAGE_URL + scheme.file).centerCrop()
            .into(binding.ivImage)

        binding.tvTitle.text = scheme.title
        binding.tvDesc.text = scheme.description
        binding.ivBackPress.setOnClickListener {
            finish()
        }

        binding.btnShare.setOnClickListener {
            downloadFile(BuildConfig.IMAGE_URL + scheme.file)
        }

        checkAndRequestPermissions()
    }


    @SuppressLint("NewApi")
    private fun downloadFile(url: String) {
        alertDialog.show()
        val request = DownloadManager.Request(Uri.parse(url)).setTitle("Downloading File")
            .setDescription("Downloading image")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalPublicDir(
                Environment.DIRECTORY_DOWNLOADS,
                Uri.parse(url).lastPathSegment
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