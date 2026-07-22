package com.preetTractor.galaxyAndroid.ui.activity.customer

import android.content.Intent
import android.net.Uri

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import androidx.core.view.isVisible

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController

import androidx.navigation.fragment.NavHostFragment

import com.google.gson.JsonObject

import com.preetTractor.galaxyAndroid.BuildConfig
import com.preetTractor.galaxyAndroid.R

import com.preetTractor.galaxyAndroid.apiHelper.ApiClient
import com.preetTractor.galaxyAndroid.data.ResponseGlobal
import com.preetTractor.galaxyAndroid.data.beatplan.ResponseBeatPlan

import com.preetTractor.galaxyAndroid.apiHelper.ApisInterface
import com.preetTractor.galaxyAndroid.data.model.customer.ResponseCustomerOne

import com.preetTractor.galaxyAndroid.databinding.ActivityCustomerDetailBinding
import com.preetTractor.galaxyAndroid.helper.APiPayloadKeys
import com.preetTractor.galaxyAndroid.helper.Constant
import com.preetTractor.galaxyAndroid.helper.Globals
import com.preetTractor.galaxyAndroid.helper.LocationPermissionHelper
import com.preetTractor.galaxyAndroid.helper.PrefsByShubh

import com.preetTractor.galaxyAndroid.helper.PrefsByShubh.businessPartnerDetails
import com.preetTractor.galaxyAndroid.mvvmSetUp.DefaultMainRepositories
import com.preetTractor.galaxyAndroid.mvvmSetUp.MainRepos
import com.preetTractor.galaxyAndroid.mvvmSetUp.MainViewModel
import com.preetTractor.galaxyAndroid.mvvmSetUp.MainViewModelProvider

import com.preetTractor.galaxyAndroid.retrofit.RetrofitClient
import com.preetTractor.galaxyAndroid.ui.fragment.NotesFragment
import com.preetTractor.galaxyAndroid.ui.fragment.OrderFragment
import com.preetTractor.galaxyAndroid.ui.fragment.SecondaryFragment
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import com.pixplicity.easyprefs.library.Prefs
import com.preetTractor.galaxyAndroid.ui.activity.BaseActivity
import com.preetTractor.galaxyAndroid.ui.fragment.CustomerBeatPlanFragment
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class CustomerDetailActivity : BaseActivity() {
    lateinit var binding: ActivityCustomerDetailBinding
    lateinit var navController: NavController
    var fromWhere = ""
    var Type = ""

    lateinit var viewModel: MainViewModel


    var selectedTab = "BeatPlan"

    companion object {
        private const val TAG = "CustomerDetailActivity"
        var cardName = ""
        var cardCode = ""
        var customerCardCode = ""
        var customerModuleFlag = ""
        private const val REQUEST_IMAGE_MAKE_MODEL_PHOTO = 9956
    }

    var builder: AlertDialog.Builder? = null
    var alertDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCustomerDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpViewModel()


//        setupNavController()

        fromWhere = intent.getStringExtra(Constant.WHERE_INTENT).toString()
        cardCode = intent.getStringExtra(Constant.WHERE_CARDCODE).toString()
        beatPlanId = intent.getStringExtra(Constant.WHERE_BEATPLAN_ID).toString()
        Type = intent.getStringExtra("Type").toString()

        customerCardCode = intent.getStringExtra(Constant.CustomerCardCode).toString()
        customerModuleFlag = intent.getStringExtra(Constant.flagCustomerModule).toString()

        if(customerModuleFlag == "CustomerModule"){
            binding.includeLayout.title.text = customerCardCode
            binding.tabOrder.visibility = View.GONE
            binding.tabSecondary.visibility = View.GONE
        }
        else{
            binding.includeLayout.title.text = cardCode
        }

        replaceFragment(CustomerBeatPlanFragment.newInstance(if(cardCode.isEmpty() || customerCardCode != "null") customerCardCode else cardCode))
        builder = AlertDialog.Builder(this)
        builder!!.setView(R.layout.progress_dialog_alert)
            .setCancelable(false)
        alertDialog = builder!!.create()


        /*   //todo new cardcode
           cardCode="10093"*/
        //  getBpDetails()

        if (fromWhere.equals("beatPlan", ignoreCase = true)) {
            getBeatPlanOneDetails()

            binding.apply {
                cardMarkVisit.visibility = View.VISIBLE
//                addNotes.visibility = View.VISIBLE
            }
        } else {
            binding.apply {
                cardMarkVisit.visibility = View.INVISIBLE
//                addNotes.visibility = View.INVISIBLE
            }
        }

        binding.linearMarkVisit.setOnClickListener {

/*
            if (visitStatus.isEmpty() || visitStatus.equals(
                    "Arrived",
                    ignoreCase = true
                )
            ) {
                visitStatus = "Completed"
            } else {
                visitStatus = "Arrived"

            }
            if (!visitStatus.equals("Completed", ignoreCase = true)) {
                if (!LocationPermissionHelper.hasLocationPermission(this)) {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    intent.data = Uri.parse("package:${packageName}")
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)

                } else {
                    dispatchMakeModelPictureIntent()
                }


            }*/

            if (!LocationPermissionHelper.hasLocationPermission(this)) {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.data = Uri.parse("package:${packageName}")
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)

            } else {
                if (PrefsByShubh.getBoolean(Globals.isCheckingStart, false)) {
                    Log.d("checkStatus", "onCreate: $approvalStatus")
                    if (approvalStatus == "Approved") {
                        dispatchMakeModelPictureIntent()
                    }
                    else if (approvalStatus == "Rejected") {
                        Toast.makeText(
                            this@CustomerDetailActivity,
                            "Beat Plan is Already Rejected",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            this@CustomerDetailActivity,
                            "Approval Required",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                else {
                    Toast.makeText(
                        this@CustomerDetailActivity,
                        "Check In Required",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }

        }



        binding.includeLayout.ivBack.setOnClickListener {
            finish()
        }

        binding.ivInfo.setOnClickListener {
            // Create an Intent to start the CustomerProfileAndOutletPicActivity
            Intent(this, CustomerProfileAndOutletPicActivity::class.java).also { intent ->

                intent.putExtra("CustomerCardCode", customerCardCode)
                intent.putExtra("CustomerModuleFlag", customerModuleFlag)
                startActivity(intent)  // startActivity using the intent
            }
        }


        /*  binding.includeLayout.ivBack.setOnClickListener {
              finish()
          }
          binding.includeLayout.title.setText("Customer")*/


        binding.tabsStrip.setOnCheckedChangeListener { group, checkedId ->

            when (checkedId) {
                binding.tabNotes.id -> {
                    selectedTab = "Notes"

                    if (fromWhere.equals("beatPlan", ignoreCase = true)) {
                        binding.apply {

                            addNotes.visibility = View.VISIBLE
                        }
                    } else {
                        binding.apply {

                            addNotes.visibility = View.INVISIBLE
                        }
                    }
                    // binding.addNotes.visibility = View.VISIBLE
                    //   findNavController(R.id.fragmentContainerCustomerActivity).navigate(R.id.notesFragment)
                    replaceFragment(NotesFragment())
                }

                binding.tabOrder.id -> {
                    selectedTab = "Order"
                    binding.addNotes.visibility = View.GONE
                    // findNavController(R.id.fragmentContainerCustomerActivity).navigate(R.id.orderFragment)
                    replaceFragment(OrderFragment())
                }

                binding.tabOverView.id -> {
                    selectedTab = "Overview"
//                    binding.addNotes.visibility = View.GONE
                    //findNavController(R.id.fragmentContainerCustomerActivity).navigate(R.id.overViewFragment)
                   // replaceFragment(OverViewFragment())
                }
                binding.tabBeatPlan.id -> {
                    selectedTab = "BeatPlan"
                    binding.addNotes.visibility = View.GONE
                    replaceFragment( CustomerBeatPlanFragment.newInstance(if(cardCode.isEmpty()) customerCardCode else cardCode))
                }

                binding.tabSecondary.id -> {
                    selectedTab = "Secondary"

//                    binding.addNotes.visibility = View.VISIBLE
                    // findNavController(R.id.fragmentContainerCustomerActivity).navigate(R.id.secondaryFragment)

                    replaceFragment(SecondaryFragment())
                }
            }

        }

        if (Type == "Customer") {
            viewModel.bPOneApi(JsonObject().apply {
                addProperty(
                    APiPayloadKeys.CardCode,
                    cardCode
                )
            }, this)

            bindBpOneObserver()
        }else{
            binding.tabNotes.visibility = View.VISIBLE
        }

        if(customerModuleFlag == "CustomerModule"){
            viewModel.bPOneApi(JsonObject().apply {
                addProperty(
                    APiPayloadKeys.CardCode,
                    customerCardCode
                )
            }, this)

            bindBpOneObserver()
            binding.tabNotes.visibility = View.GONE
        }

    }

    private fun bindBpOneObserver() {
        viewModel.bPOneDetailData.observe(this, Event.EventObserver(onError = {
            alertDialog!!.dismiss()
            Globals.warningMessage(this, it)
        }, onLoading = {
            alertDialog!!.show()
        }, { response ->
            alertDialog!!.dismiss()
            if (response.status == 200) {
                //todo set dealer, special and additional discount
                if (response.data.isNotEmpty()) {
                    response.data[0].apply {

                        businessPartnerDetails = response
                        Log.i("BP_DETAILS", "$businessPartnerDetails")

                        binding.apply {
                            if (Type == "Customer" || customerModuleFlag == "CustomerModule") {
                                tvCustomerName.text = response.data[0].CardName.ifEmpty { "NA" }
                                // tvCustomerAddress.text = it.data[0].GroupName
                                if (response.data[0].BPAddresses.isNotEmpty()) {
                                    if (response.data[0].BPAddresses[0].Street.isNotEmpty()) {
                                        tvCustomerAddress.text =
                                            response.data[0].BPAddresses[0].Street
                                        //  tvCustomerAddress.text = it.data[0].BPAddresses[0].Street

                                    } else {
                                        tvCustomerAddress.text = "N/A"

                                    }

                                }
                            }




                            cardName = response.data[0].CardName
                            Prefs.putString(
                                Globals.CARD_CODE_BP_ONE,
                                response.data[0].CardCode
                            )
                        }

                        if (SalesPersonCode.isNotEmpty()) {
                            Prefs.putString(
                                Globals.SALES_EMPLOYEE_CODE,
                                SalesPersonCode[0].SalesEmployeeCode
                            )
                        }

                        Prefs.putString(
                            Globals.CURRENCY,
                            Currency
                        )

                        Prefs.putString(
                            Globals.DEALER_DISC,
                            U_UTL_DLRD
                        )
                        Prefs.putString(
                            Globals.SPECIAL_DISC,
                            U_UTL_SPCL
                        )
                        Prefs.putString(
                            Globals.ADDITIONAL_DISC,
                            U_CIS_AD
                        )

                        Prefs.putString(
                            Globals.DISCOUNT_PERCENT,
                            DiscountPercent
                        )
                        if (BPAddresses.isNotEmpty()) {
                            Prefs.putString(
                                Globals.BLOCK,
                                BPAddresses[0].Block
                            )
                            Prefs.putString(
                                Globals.CITY,
                                BPAddresses[0].City
                            )
                            Prefs.putString(
                                Globals.STATE,
                                BPAddresses[0].State
                            )
                        }

                        if (ContactEmployees.isNotEmpty()) {
                            Prefs.putString(
                                Globals.CONTACT_PERSON_CODE,
                                ContactEmployees[0].InternalCode
                            )

                        }

                        if (PayTermsGrpCode.isNotEmpty()) {
                            Prefs.putString(
                                Globals.PAYMENT_GROUP_CODE,
                                PayTermsGrpCode[0].GroupNumber
                            )

                        }


                    }
                }


            } else if (response.status == 201) {
                Globals.warningMessage(this, response.message)
            } else if (response.status == 401) {
                //sessionManagement.ClearSession()
                PrefsByShubh.ClearSession()
                Globals.logoutScreen(this)

            }


        }))
    }

    private fun setUpViewModel() {
        val dispatchers: CoroutineDispatcher = Dispatchers.Main
        val mainRepos = DefaultMainRepositories() as MainRepos
        val fanxApi: ApisInterface = ApiClient().service(this)
        val viewModelProviderfactory =
            MainViewModelProvider(application, mainRepos, dispatchers, fanxApi)
        viewModel = ViewModelProvider(this, viewModelProviderfactory)[MainViewModel::class.java]

    }


    var photoURI: Uri? = null

    override fun dispatchMakeModelPictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                var photoFile: File? = try {
                    createImageFile()
                } catch (ex: java.io.IOException) {
                    Toast.makeText(
                        this,
                        "Error occurred while creating the file",
                        Toast.LENGTH_SHORT
                    ).show()
                    null
                }
                photoFile?.also {
                    photoURI = FileProvider.getUriForFile(
                        this, "${BuildConfig.APPLICATION_ID}.FileProvider", it
                    )
//                    val photoURI: Uri = Uri.fromFile(it) //todo ==> using Uri.fromFile to create the URI for the photo file, which leads to a FileUriExposedException on Android 7.0 and above
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(
                        takePictureIntent,
                        REQUEST_IMAGE_MAKE_MODEL_PHOTO
                    )
                }
            }
        }
    }

    override var currentPhotoPath = ""
    override var makeModelPhoto = ""
    override lateinit var fileMakeModelPhotoUri: Uri

    override fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String =
            SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
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

    private fun replaceFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainerCustomerActivity, fragment)
        transaction.commit() // Commit the transaction without adding to the back stack
    }


    private fun getBpDetails() {
        alertDialog!!.show()

        val hde = JsonObject().apply {
            addProperty("FromDate", "")
            addProperty("CardCode", cardCode)
            addProperty("ToDate", "")
        }
        val call = RetrofitClient.apiService.getBpOne(hde)

        call.enqueue(object : Callback<ResponseCustomerOne> {
            override fun onResponse(
                call: Call<ResponseCustomerOne>,
                response: Response<ResponseCustomerOne>
            ) {
                alertDialog!!.dismiss()

                response.body()?.let {


                    if (it.status == 200) {

                        if (it.data.isNotEmpty()) {

                            binding.apply {
                                tvCustomerName.text = it.data[0].CardName.ifEmpty { "NA" }
                                // tvCustomerAddress.text = it.data[0].GroupName
                                if (it.data[0].BPAddresses.isNotEmpty()) {
                                    if (it.data[0].BPAddresses[0].Street.isNotEmpty()) {
                                        tvCustomerAddress.text = it.data[0].BPAddresses[0].Street
                                        //  tvCustomerAddress.text = it.data[0].BPAddresses[0].Street

                                    } else {
                                        tvCustomerAddress.text = "N/A"

                                    }

                                }

                                cardName = it.data[0].CardName
                            }

                        }


                    } else if (it.status == 201) {
                        Toast.makeText(this@CustomerDetailActivity, it.message, Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }

            override fun onFailure(call: Call<ResponseCustomerOne>, t: Throwable) {
                alertDialog!!.dismiss()
                Log.e(TAG, "onFailure: ${t.message}")
                Toast.makeText(
                    this@CustomerDetailActivity,
                    "Something went wrong",
                    Toast.LENGTH_SHORT
                ).show()

            }
        })
    }

    var beatPlanId = ""
    var visitStatus = ""
    var approvalStatus = ""
    var visitDate = ""

    private fun getBeatPlanOneDetails() {
        //   alertDialog!!.show()
        /*{
            id:76
            SalesEmployeeCode:-1
            Lat:28.8859644
            Long:77.13107815
            Visit_Status:"",#Arrived/Completed
            Timestamp:2024-07-10 20:30:40 PM
            Update_Date:2024-07-10
            Update_Time:01:03 PM
        }*/

        val hde = JsonObject().apply {
            addProperty("id", beatPlanId)


        }
        val call = RetrofitClient.apiService.getBeatPlanOne(hde)

        call.enqueue(object : Callback<ResponseBeatPlan> {
            override fun onResponse(
                call: Call<ResponseBeatPlan>,
                response: Response<ResponseBeatPlan>
            ) {
                // alertDialog!!.dismiss()

                response.body()?.let {


                    if (it.status == "200") {
                        if (it.data.isNotEmpty()) {

                            if (Type == "Other" || Type == "Lead") {
                                binding.tvCustomerName.text = it.data[0].ProspectName.ifEmpty { "NA" }
                                binding.tvCustomerAddress.text = it.data[0].ProspectNumber.ifEmpty { "NA" }
                                binding.ivInfo.visibility = View.GONE
                                binding.tabOrder.visibility = View.GONE
                                binding.tabSecondary.visibility = View.GONE
                            }

                            approvalStatus = it.data[0].Approval_Status
                            visitStatus = it.data[0].Visit_Status
                            visitDate = it.data[0].Visit_Date

                            when {
                                visitStatus.isEmpty() -> {
                                    Globals.isBeatPlanWorking = false
                                    binding.tvBeatPlanVisitStatus.text = "Mark Visit"
                                    binding.linearMarkVisit.isEnabled = true
                                    viewModel.editAdapterData(false)
                                }

                                visitStatus.equals("Arrived", ignoreCase = true) -> {
                                    Globals.isBeatPlanWorking = true
                                    binding.tvBeatPlanVisitStatus.text = "Complete Visit"
                                    binding.linearMarkVisit.isEnabled = true
                                    viewModel.editAdapterData(true)
                                }

                                visitStatus.equals("Completed", ignoreCase = true) -> {
                                    Globals.isBeatPlanWorking = false
                                    binding.tvBeatPlanVisitStatus.text = "Completed"
                                    binding.linearMarkVisit.isEnabled = false
                                    viewModel.editAdapterData(false)
                                }

                                visitStatus.equals("Not Attended", ignoreCase = true) -> {
                                    Globals.isBeatPlanWorking = false
                                    binding.tvBeatPlanVisitStatus.text = "Not Attended"
                                    binding.linearMarkVisit.isEnabled = false
                                    viewModel.editAdapterData(false)
                                }

                                else -> {
                                    Globals.isBeatPlanWorking = false
                                    binding.tvBeatPlanVisitStatus.text = "Mark Visit"
                                    binding.linearMarkVisit.isEnabled = true
                                    viewModel.editAdapterData(false)
                                }

                            }
                            binding.linearMarkVisit.isVisible = visitDate == Globals.getTodaysDatervrsfrmt()


                            /*      if (visitStatus.isEmpty() || visitStatus.equals(
                                          "Arrived",
                                          ignoreCase = true
                                      )
                                  ) {
                                      if (visitStatus.equals(
                                              "Arrived",
                                              ignoreCase = true
                                          )
                                      ) {
                                          Globals.isBeatPlanWorking = true
                                      }

                                      binding.tvBeatPlanVisitStatus.text = "Mark Completed"
                                  } else {
                                      Globals.isBeatPlanWorking = false
                                      binding.tvBeatPlanVisitStatus.text = "Mark Visit"
                                  }
      */

                        }


                    } else if (it.status.equals("201")) {
                        if (it.message.contains("File")) {
                            Toast.makeText(
                                this@CustomerDetailActivity,
                                "No Image Selected",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        } else {
                            Toast.makeText(
                                this@CustomerDetailActivity,
                                it.message,
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }

                    }
                }
            }

            override fun onFailure(call: Call<ResponseBeatPlan>, t: Throwable) {
                alertDialog!!.dismiss()
                Log.e(TAG, "onFailure: ${t.message}")
                Toast.makeText(
                    this@CustomerDetailActivity,
                    "Something went wrong",
                    Toast.LENGTH_SHORT
                ).show()

            }
        })
    }

    private fun updateBeatPlan(requestBody: MultipartBody) {
        alertDialog!!.show()
        /*{
            id:76
            SalesEmployeeCode:-1
            Lat:28.8859644
            Long:77.13107815
            Visit_Status:"",#Arrived/Completed
            Timestamp:2024-07-10 20:30:40 PM
            Update_Date:2024-07-10
            Update_Time:01:03 PM
        }*/
/*
        val hde = JsonObject().apply {
            addProperty("id", beatPlanId)
            addProperty("Visit_Status", visitStatus)
            addProperty("Timestamp", Globals.getCurrentDateTimeFormatted_hh_mm_ss())
            addProperty("Lat", Globals.globalLatitude)
            addProperty("Long", Globals.globalLongitude)
            addProperty("Update_Date", Globals.getTodaysDatervrsfrmt())
            addProperty("Update_Time", Globals.getTCurrentTime())
            addProperty("SalesEmployeeCode", PrefsByShubh.getSalesEmployeeCode())

        }*/
        val call = RetrofitClient.apiService.updateBeatPlan(requestBody)

        call.enqueue(object : Callback<ResponseGlobal> {
            override fun onResponse(
                call: Call<ResponseGlobal>,
                response: Response<ResponseGlobal>
            ) {
                alertDialog!!.dismiss()

                response.body()?.let {


                    if (it.status == 200) {

                        getBeatPlanOneDetails()
/*
                        if (it.data.isNotEmpty()) {

                            binding.apply {
                                tvCustomerName.text = it.data[0].CardName.ifEmpty {"NA"}
                                tvCustomerAddress.text = it.data[0].GroupName
                                includeLayout.title.setText(it.data[0].CardCode)
                                cardName = it.data[0].CardName
                            }

                        }*/


                    } else if (it.status == 201) {
                        if (it.message.contains("File")) {
                            Toast.makeText(
                                this@CustomerDetailActivity,
                                "No Image Selected",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        } else {
                            Toast.makeText(
                                this@CustomerDetailActivity,
                                it.message,
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }


                    }
                }
            }

            override fun onFailure(call: Call<ResponseGlobal>, t: Throwable) {
                alertDialog!!.dismiss()
                Log.e(TAG, "onFailure: ${t.message}")
                Toast.makeText(
                    this@CustomerDetailActivity,
                    "Something went wrong",
                    Toast.LENGTH_SHORT
                ).show()

            }
        })
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != RESULT_OK) {
            Log.d(TAG, "Camera cancelled by user")
            return
        }

        when (requestCode) {


            REQUEST_IMAGE_MAKE_MODEL_PHOTO -> {

                val imgFile = File(makeModelPhoto)
                if (imgFile.exists()) {
                    fileMakeModelPhotoUri = Uri.fromFile(imgFile)
                    currentPhotoPath = fileMakeModelPhotoUri.path!!
                    Log.e("fileUri---", fileMakeModelPhotoUri.toString())
                }



                visitStatus = when {
                    visitStatus.isEmpty() -> {
                        "Arrived"
                    }
                    visitStatus.equals("Arrived", ignoreCase = true) -> {
                        "Completed"
                    }
                    else -> {
                        "Completed"
                    }
                }




                Log.e(TAG, "onActivityResultCUSTOMERPHOTO>>>>>>>>>: $currentPhotoPath")
                val builder: MultipartBody.Builder = MultipartBody.Builder()
                builder.setType(MultipartBody.FORM)
                builder.addFormDataPart("address", Globals.globalAddress.toString())
                /*if(visitStatus == "Arrived"){
                    builder.addFormDataPart("arrived_address", Globals.globalAddress.toString())
                }else{
                    builder.addFormDataPart("completed_address", Globals.globalAddress.toString())
                }*/

                builder.addFormDataPart("id", beatPlanId)
                builder.addFormDataPart("Visit_Status", visitStatus)
                builder.addFormDataPart(
                    "Timestamp",
                    Globals.getCurrentDateTimeFormatted_hh_mm_ss()!!
                )
                builder.addFormDataPart("Lat", Globals.globalLatitude.toString())
                builder.addFormDataPart("Long", Globals.globalLongitude.toString())

                builder.addFormDataPart("Update_Date", Globals.getTodaysDatervrsfrmt()!!)
                builder.addFormDataPart("Update_Time", Globals.getTCurrentTime()!!)
                builder.addFormDataPart("SalesEmployeeCode", Globals.SalesEmployeeCode)



                try {
                    val file: File =
                        Globals.compressImageFile(this,File(currentPhotoPath))
                    builder.addFormDataPart(
                        "File",
                        file.name,
                        RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file)
                    )
                } catch (e: Exception) {

                    Log.e(TAG, "onActivityResult: ERROR")
                }

                // **Print the payload**
                printPayload(builder, currentPhotoPath)

                val requestBody: MultipartBody = builder.build()


                if (currentPhotoPath.isNotEmpty()) {
                    updateBeatPlan(requestBody)
                }


            }
        }
    }


    private fun printPayload(builder: MultipartBody.Builder, currentPhotoPath: String) {
        val payloadStringBuilder = StringBuilder()

        for (part in builder.build().parts) {
            val headers = part.headers
            val body = part.body

            // Extract key-value pairs
            val key = headers?.get("Content-Disposition")?.split("name=\"")?.getOrNull(1)?.split("\"")?.firstOrNull() ?: "Unknown"
            val value = if (key == "File") File(currentPhotoPath).name else bodyToString(body)

            payloadStringBuilder.append("$key: $value\n")
        }

        Log.e("PAYLOAD", payloadStringBuilder.toString().trim())
    }

    private fun bodyToString(requestBody: RequestBody): String {
        return try {
            val buffer = okio.Buffer()
            requestBody.writeTo(buffer)
            buffer.readUtf8()
        } catch (e: Exception) {
            "ERROR"
        }
    }


    private fun setupNavController() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerCustomerActivity) as NavHostFragment
        navController = navHostFragment.navController

        // If you need to set up any specific behavior or default destinations, you can do so here.
        navController.addOnDestinationChangedListener { _, destination, _ ->
            // Handle any specific logic for destination changes if needed
            when (destination.id) {
                R.id.pendingOrderInnerFirstFragment -> {
                    // Perform actions specific to this fragment
                }
            }
        }
    }


}