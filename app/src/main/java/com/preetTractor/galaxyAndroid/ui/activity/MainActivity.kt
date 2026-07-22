package com.preetTractor.galaxyAndroid.ui.activity

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.PowerManager
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.gson.JsonObject
import com.preetTractor.galaxyAndroid.BuildConfig
import com.preetTractor.galaxyAndroid.R
import com.preetTractor.galaxyAndroid.apiHelper.ApiClient
import com.preetTractor.galaxyAndroid.apiHelper.ApisInterface
import com.preetTractor.galaxyAndroid.apiHelper.AppConstants
import com.preetTractor.galaxyAndroid.apiHelper.AppConstants.cartListForOrderRequest
import com.preetTractor.galaxyAndroid.data.AttachmentModel
import com.preetTractor.galaxyAndroid.data.ResponseGlobal
import com.preetTractor.galaxyAndroid.data.beatplan.LocalDataTodayBeatPlan
import com.preetTractor.galaxyAndroid.data.beatplan.ResponseBeatPlan
import com.preetTractor.galaxyAndroid.databinding.ActivityMainBinding
import com.preetTractor.galaxyAndroid.databinding.DialogCustomLottieBinding
import com.preetTractor.galaxyAndroid.databinding.DialogJointWorkBinding
import com.preetTractor.galaxyAndroid.helper.Constant
import com.preetTractor.galaxyAndroid.helper.Globals
import com.preetTractor.galaxyAndroid.helper.Globals.SalesEmployeeCode
import com.preetTractor.galaxyAndroid.helper.Globals.assignedTo
import com.preetTractor.galaxyAndroid.helper.Globals.todayBeatPlanList
import com.preetTractor.galaxyAndroid.helper.LocationPermissionHelper
import com.preetTractor.galaxyAndroid.helper.LocationService
import com.preetTractor.galaxyAndroid.helper.PrefsByShubh
import com.preetTractor.galaxyAndroid.helper.TimerManager
import com.preetTractor.galaxyAndroid.helper.WorkManagerScheduler
import com.preetTractor.galaxyAndroid.mvvmSetUp.DefaultMainRepositories
import com.preetTractor.galaxyAndroid.mvvmSetUp.MainRepos
import com.preetTractor.galaxyAndroid.mvvmSetUp.MainViewModel
import com.preetTractor.galaxyAndroid.mvvmSetUp.MainViewModelProvider
import com.preetTractor.galaxyAndroid.retrofit.RetrofitClient
import com.preetTractor.galaxyAndroid.ui.activity.customer.CustomerDetailActivity
import com.preetTractor.galaxyAndroid.ui.orderUi.activity.CartActivity
import com.preetTractor.galaxyAndroid.ui.recyclerview.JointWorkSelectListAdapter
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


class MainActivity : BaseActivity() {

    // private val viewModel: MyViewModel by viewModels()

    lateinit var viewModel: MainViewModel
    lateinit var binding: ActivityMainBinding
    private var fusedLocationClient: FusedLocationProviderClient? = null

    private val LOCATION_PERMISSION_REQUEST_CODE = 1

    var builder: AlertDialog.Builder? = null
    var alertDialog: AlertDialog? = null
    lateinit var navController: NavController


    private fun setupNavController() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerMainActivity) as NavHostFragment
        navController = navHostFragment.navController
        NavigationUI.setupWithNavController(
            binding.navigationView, navController
        )

        binding.navigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.attendanceBottomTabFragment -> {

                    PrefsByShubh.putString(Constant.FLAG, "NOT_FROM_ATTENDANCE")
                    navController.navigate(R.id.attendanceBottomTabFragment)
                    true
                }

                R.id.dashboardFragment -> {
                    navController.navigate(R.id.dashboardFragment)
                    true
                }

                R.id.teamsFragment -> {
                    navController.navigate(R.id.teamsFragment)
                    true
                }

                R.id.fragmentCustomerMap -> {

                    navController.navigate(R.id.fragmentCustomerMap)
                    true
                }

                R.id.mediaFragment -> {
                    navController.navigate(R.id.mediaFragment)
                    true
                }

                else -> false
            }
        }
    }

    private fun setupNavControllerForBa() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerBaMainActivity) as NavHostFragment
        navController = navHostFragment.navController
        NavigationUI.setupWithNavController(
            binding.bANavigationView, navController
        )

        binding.bANavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.attendanceBottomTabFragment -> {

                    PrefsByShubh.putString(Constant.FLAG, "NOT_FROM_ATTENDANCE")
                    navController.navigate(R.id.attendanceBottomTabFragment)
                    true
                }

                R.id.dashboardBeautyAdvisorFragment -> {
                    navController.navigate(R.id.dashboardBeautyAdvisorFragment)
                    true
                }

                R.id.beautyAdvisorFragment -> {
                    navController.navigate(R.id.beautyAdvisorFragment)
                    true
                }

                else -> false
            }
        }
    }


    companion object {

        private const val TAG = "MainActivityBP"
        const val PERMISSION_REQUEST_CODE = 1001
        private const val MY_PERMISSIONS_REQUEST_LOCATION = 99
        private const val MY_PERMISSIONS_REQUEST_BACKGROUND_LOCATION = 66
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_IMAGE_MAKE_MODEL_PHOTO && resultCode == RESULT_OK) {
            //  customerPhotoPath = getCustomerPhotoPath(data!!.data)
            val compressedImageFile = try {
                compressImageFile(this, File(currentPhotoPath))
            } catch (e: Exception) {
                e.printStackTrace()
                File(currentPhotoPath)
            }


            val builder: MultipartBody.Builder = MultipartBody.Builder()
            builder.setType(MultipartBody.FORM)
            builder.addFormDataPart("CheckIn_Lat", Globals.globalLatitude.toString())
            builder.addFormDataPart("CheckIn_Long", Globals.globalLongitude.toString())
            builder.addFormDataPart("CheckIn_Address", Globals.globalAddress)
            builder.addFormDataPart(
                "SalesEmployeeCode", SalesEmployeeCode
            )
            builder.addFormDataPart("Emp_Name", "Admin")
            if (PrefsByShubh.getBoolean(Globals.isCheckingStart, false)) {
                builder.addFormDataPart("Type", "stop")
            } else {
                builder.addFormDataPart("Type", "start")
            }


            builder.addFormDataPart("Total_Hour", seconds.toString())
            builder.addFormDataPart("Create_Date", Globals.getTodaysDatervrsfrmt()!!)
            builder.addFormDataPart("Create_Time", Globals.getTCurrentTime_hh_mm_ss_aa()!!)
            builder.addFormDataPart("Created_at", Globals.getCurrentDateTimeFormatted_hh_mm_ss()!!)
            var newList = ArrayList<String>()

            if (selectedJointStatus == "Self") {
                newList.clear()

                newList.add("")

            } else if (selectedJointStatus == "None") {
                newList.clear()
                newList.add("")

            } else {
                newList.clear()

                // todo old code
                for (index in todayBeatPlanList.indices) {

                    if (todayBeatPlanList[index].isSelected) {
                        newList.add(todayBeatPlanList[index].id)
                    }

                }
            }

            builder.addFormDataPart("BeatPlan_Ids", newList.toString())
            newList.joinToString(prefix = "[", postfix = "]") { it }

            builder.addFormDataPart(
                "File",
                compressedImageFile.name,
                compressedImageFile.asRequestBody("image/jpeg".toMediaType())
            )
            val requestBody: MultipartBody = builder.build()
            createAttachment(requestBody)

        }

    }

    var attendance_date = ""

    var beatPlanId = ""
    var cardCodeBeatPlan = ""
    var selectedJointStatus = "None"

    private var _bindingDialog: DialogJointWorkBinding? = null
    private val bindingDialog get() = _bindingDialog!!

    private lateinit var locationAdapter: JointWorkSelectListAdapter
    private fun openDialog() {
        selectedJointStatus = "None"
        val dialog = Dialog(this)
        _bindingDialog = DialogJointWorkBinding.inflate(layoutInflater)
        LayoutInflater.from(this)

        dialog.setContentView(bindingDialog.root)
        dialog.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT
        )






        bindingDialog.btnDone.setOnClickListener {
            if (selectedJointStatus == "None") {
                if (!LocationPermissionHelper.hasLocationPermission(this)) {
                    showTrackingAlertDialog()
                } else {
                    dispatchMakeModelPictureIntent()
                }
            }

            if (selectedJointStatus == "Joint") {

                if (todayBeatPlanList.isNotEmpty()) {
                    if (!LocationPermissionHelper.hasLocationPermission(this)) {
                        showTrackingAlertDialog()
                    } else {
                        dispatchMakeModelPictureIntent()
                    }
                } else {
                    Toast.makeText(this, "No Beat Plan Found", Toast.LENGTH_SHORT).show()
                }


            }

            if (selectedJointStatus.equals("Self")) {
                if (assignedTo.isNotEmpty()) {
                    if (beatPlanId.isNotEmpty()) {
                        if (!LocationPermissionHelper.hasLocationPermission(this)) {

                            showTrackingAlertDialog()
                        } else {
                            dispatchMakeModelPictureIntent()
                        }
                    } else {
                        Toast.makeText(this, "No Beat Plan Found", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "No Beat Plan Found", Toast.LENGTH_SHORT).show()
                }

            }




            dialog.dismiss()
        }

        bindingDialog.apply {
            rgCheckJointWork.setOnCheckedChangeListener { radioGroup, i ->
                when (i) {
                    rbNone.id -> {
                        selectedJointStatus = "None"
                        rvBeatPlanJointWork.visibility = View.GONE
                    }

                    rbJointWork.id -> {

                        selectedJointStatus = "Joint"

                        locationAdapter =
                            JointWorkSelectListAdapter(todayBeatPlanList) { selectedItems ->

                            }
                        rvBeatPlanJointWork.layoutManager = LinearLayoutManager(this@MainActivity)
                        rvBeatPlanJointWork.adapter = locationAdapter
                        rvBeatPlanJointWork.visibility = View.VISIBLE
                    }

                    rbSelf.id -> {
                        selectedJointStatus = "Self"
                        rvBeatPlanJointWork.visibility = View.GONE

                    }
                }

            }
        }



        dialog.show()
    }


    private fun getTodayBeatPlanListing() {

        val hde = JsonObject().apply {
            addProperty("SalesEmployeeCode", SalesEmployeeCode)
            addProperty("Date", Globals.getTodaysDatervrsfrmt())
            addProperty("Status", "today")
            addProperty("Priority", "")
            addProperty("Latitude", Globals.globalLatitude)
            addProperty("Longitude", Globals.globalLongitude)
            addProperty("Level", "all")
        }

        val call = RetrofitClient.apiService.galaxyBeatPlanList(hde)

        call.enqueue(object : Callback<ResponseBeatPlan> {
            override fun onResponse(
                call: Call<ResponseBeatPlan>, response: Response<ResponseBeatPlan>
            ) {
                //   binding.progressBar2.visibility = View.GONE
                response.body()?.let {
                    if (it.status.equals("200", ignoreCase = true)) {

                        // todayBeatPlanList.addAll(it.data)
                        if (it.data.isNotEmpty()) {
                            for (current in it.data) {
                                val cureentBeat = LocalDataTodayBeatPlan(
                                    id = current.id.toString(),
                                    approval_status = current.Approval_Status,
                                    City = current.City,
                                    assined_to = current.AssignedTo,
                                    assigned_name = current.AssignedName,
                                    Type = current.Type ?: ""  // Provide default value
                                )
                                todayBeatPlanList.add(cureentBeat)
                            }

                        } else {
                            todayBeatPlanList.clear()
                        }

                    }
                }
            }

            override fun onFailure(call: Call<ResponseBeatPlan>, t: Throwable) {

            }
        })
    }


    private fun callAttachmentAllApi() {
        val jsonObject = JsonObject()
        jsonObject.addProperty("SalesEmployeeCode", PrefsByShubh.getSalesEmployeeCode())
        val call: Call<AttachmentModel> =
            RetrofitClient.apiService.getNewAllAttachmentApi(jsonObject)
        call.enqueue(object : Callback<AttachmentModel> {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onResponse(
                call: Call<AttachmentModel>, response: Response<AttachmentModel>
            ) {
                if (response.body()!!.status == 200) {

                    if (response.body()!!.data.isNotEmpty()) {

                        //todo role assgin Locally
                        PrefsByShubh.putString(
                            "role", response.body()!!.data[0].employee_detail.role_name
                        )
                        PrefsByShubh.putString(
                            "expenseRate", response.body()!!.data[0].employee_detail.ExpenseRate
                        )

                        //todo nee beatplan Module
                        if (response.body()!!.data[0].today_beatplan_detail.isNotEmpty()) {
                            Globals.isBeatPlanWorking = true

                            beatPlanId =
                                response.body()!!.data[0].today_beatplan_detail[0].id.toString()

                        } else {
                            Globals.isBeatPlanWorking = false
                        }

                        if (Globals.isBeatPlanWorking) {
                            binding.constraintToolbar.constraintDuration.visibility = View.GONE
                            // Observe the timer's LiveData
                            TimerManager.getTimerLiveData().observe(this@MainActivity) { time ->
                                binding.constraintToolbar.tvTiming.text = "Duration: $time"
                            }
                        } else {
                            binding.constraintToolbar.constraintDuration.visibility = View.GONE
                            binding.constraintToolbar.tvTiming.text = "Duration: 00:00:00"
                        }


                        if (response.body()!!.data[0].profileImage.isNotEmpty()) {
                            Glide.with(this@MainActivity)
                                .load(BuildConfig.IMAGE_URL + response.body()!!.data[0].profileImage)
                                .into(binding.constraintToolbar.icUser)
                        } else {
                            Glide.with(this@MainActivity)
                                .load(resources.getDrawable(R.drawable.ic_user))
                                .into(binding.constraintToolbar.icUser)
                        }

                        attendance_date = ""
                        attendance_date = response.body()!!.data[0].attendance_timestamp
                        binding.constraintToolbar.tvUserName.text =
                            response.body()!!.data[0].employee_detail.firstName + " " + response.body()!!.data[0].employee_detail.lastName

                        if (attendance_date.isNotEmpty()) {
                            PrefsByShubh.putBoolean(Globals.isCheckingStart, true)
                        } else {
                            PrefsByShubh.putBoolean(Globals.isCheckingStart, false)
                        }
                        PrefsByShubh.setEmpName(response.body()!!.data[0].employee_detail.firstName)

                        if (PrefsByShubh.getBoolean(Globals.isCheckingStart, false)) {
                            binding.constraintToolbar.tvCheckInTextview.text = "Check-Out"
                            // Set the start date-time string
                            val startTimeString = attendance_date

                            val formatter = SimpleDateFormat("yyyy-MM-dd hh:mm:ss a", Locale.US)
                            try {
                                formatter.parse(startTimeString)

                                val secondsTimeer = Globals.secondsBetween(startTimeString)
                                running = true
                                seconds = secondsTimeer

                                handler.post(timerRunnable)
                            } catch (e: java.lang.Exception) {
                                e.printStackTrace()

                            }
                        } else {
                            binding.constraintToolbar.tvDuration.text = "Duration:\n00:00:00"
                            binding.constraintToolbar.tvCheckInTextview.text = "Check-In"
                        }

                    }

                    if (!response.body()!!.info.isNullOrEmpty()) {
                        val infoModule = response.body()!!.info[0]

                        PrefsByShubh.setUserEmail(infoModule.email)
                        PrefsByShubh.setUserPassowrd(infoModule.password)
                        PrefsByShubh.setUserFCM(infoModule.FCM)
                        PrefsByShubh.setUserAppId(infoModule.app_id)
                        callSuperAdminApiForToken()

                    }
                } else if (response.code() == 201) {
                    Toast.makeText(this@MainActivity, response.body()!!.message, Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onFailure(call: Call<AttachmentModel>, t: Throwable) {
            }
        })
    }

    private fun callSuperAdminApiForToken() {

        val jsonObject = JsonObject().apply {
            addProperty("email", PrefsByShubh.getUserEmail())
            addProperty("password", PrefsByShubh.getUserPassword())
            addProperty("FCM", PrefsByShubh.getFirebaseFCMToken())
            addProperty("app_id", PrefsByShubh.getUserAppId())
        }

        val call = RetrofitClient.apiService1.getLoginToken(jsonObject)
        call.enqueue(object : Callback<JsonObject?> {
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                if (response.isSuccessful) {
                    response.body()?.let { jsonResponse ->
                        if (jsonResponse.has("status") && jsonResponse.get("status").asInt == 200) {
                            // Store token globally
                            Globals.GalaxyVistaToken = jsonResponse.get("token").asString

                        }
                    }
                }
            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
            }
        })


    }


    private fun callLogoutWorkManager() {
        WorkManagerScheduler.refreshPeriodicWork(applicationContext)
    }

    var ischeckedIn = false
    private fun createAttachment(requestBody: MultipartBody) {
        alertDialog!!.show()
        val call: Call<ResponseGlobal> = RetrofitClient.apiService.punchDailyAttendance(requestBody)
        call.enqueue(object : Callback<ResponseGlobal> {
            override fun onResponse(
                call: Call<ResponseGlobal>, response: Response<ResponseGlobal>
            ) {
                if (response != null) {
                    alertDialog!!.dismiss()
                    if (response.body()!!.status == 200) {


                        if (PrefsByShubh.getBoolean(Globals.isCheckingStart, false)) {
                            PrefsByShubh.putBoolean(Globals.isCheckingStart, false)
                            Toast.makeText(
                                this@MainActivity, "Stopped SuccessFully", Toast.LENGTH_SHORT
                            ).show()
                            currentPhotoPath = ""
                            binding.constraintToolbar.tvCheckInTextview.text = "Check In"
                            binding.constraintToolbar.tvDuration.text = "Duration:\n00:00:00"
                            ischeckedIn = false

                            callAttachmentAllApi()
                            stopService()
                            WorkManagerScheduler.cancelWork(this@MainActivity)
                        } else {
                            PrefsByShubh.putBoolean(Globals.isCheckingStart, true)
                            Toast.makeText(
                                this@MainActivity, "Started  SuccessFully", Toast.LENGTH_SHORT
                            ).show()
                            currentPhotoPath = ""
                            binding.constraintToolbar.tvCheckInTextview.text = "Check Out"
                            ischeckedIn = true
                            callAttachmentAllApi()

                            startService()
                            callLogoutWorkManager()
                            Toast.makeText(
                                this@MainActivity, "Location sharing Started.", Toast.LENGTH_SHORT
                            ).show()
                        }

                        if (!PrefsByShubh.getBoolean(Globals.isCheckingStart, false)) {
                            binding.constraintToolbar.tvDuration.text = "Duration: 00:00:00"
                        }


                    } else if (response.body()!!.status == 201) {
                        Toast.makeText(
                            this@MainActivity, response.body()!!.message, Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            override fun onFailure(call: Call<ResponseGlobal>, t: Throwable) {
                alertDialog!!.dismiss()
                Toast.makeText(this@MainActivity, "" + t.message, Toast.LENGTH_SHORT).show()/* loader.setVisibility(View.GONE);
                alertDialog.dismiss();*/
            }
        })
    }


    var latitude = 0.0
    var longitude = 0.0
    var addressGlobal = ""


    private fun setUpViewModel() {

        val dispatchers: CoroutineDispatcher = Dispatchers.Main
        val mainRepos = DefaultMainRepositories() as MainRepos
        val fanxApi: ApisInterface = ApiClient().service(this)
        val viewModelProviderfactory =
            MainViewModelProvider(application, mainRepos, dispatchers, fanxApi)
        viewModel = ViewModelProvider(this, viewModelProviderfactory)[MainViewModel::class.java]

    }


    private fun showTrackingAlertDialog() {
        val binding = DialogCustomLottieBinding.inflate(LayoutInflater.from(this))

        val dialog = Dialog(this).apply {
            setContentView(binding.root)
            setCancelable(false) // Prevent the dialog from being canceled by clicking outside
            window?.setBackgroundDrawableResource(android.R.color.transparent)
        }

        binding.lottieAnimationView.setAnimation(R.raw.track_lottie)
        binding.lottieAnimationView.playAnimation()

        // Set click listeners for buttons
        binding.btnAgree.setOnClickListener {
            if (!LocationPermissionHelper.hasLocationPermission(this)) {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.data = Uri.parse("package:$packageName")
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
            dialog.dismiss()
        }

        binding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpViewModel()

        // Initialization
        assignedTo = ArrayList()

        supportActionBar!!.hide()

        builder = AlertDialog.Builder(this)
        builder!!.setView(R.layout.progress_dialog_alert).setCancelable(false)
        alertDialog = builder!!.create()
        if (PrefsByShubh.getString("role", "") == "Beauty Advisor") {
            binding.constraintToolbar.layoutCart.visibility = View.GONE
            binding.navigationView.visibility = View.GONE
            binding.fragmentContainerMainActivity.visibility = View.GONE
            binding.bANavigationView.visibility = View.VISIBLE
            binding.fragmentContainerBaMainActivity.visibility = View.VISIBLE
            setupNavControllerForBa()
        } else {
            binding.constraintToolbar.layoutCart.visibility = View.VISIBLE
            binding.navigationView.visibility = View.VISIBLE
            binding.fragmentContainerMainActivity.visibility = View.VISIBLE
            binding.bANavigationView.visibility = View.GONE
            binding.fragmentContainerBaMainActivity.visibility = View.GONE
            setupNavController()
        }
        checkAndRequestNotificationPermission()


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)



        binding.constraintToolbar.groupMain.visibility = View.VISIBLE
        binding.constraintToolbar.groupOther.visibility = View.GONE
        binding.constraintToolbar.constraintDuration.visibility = View.GONE


        //binding.constraintToolbar.badgeTextView.text = cartListForOrderRequest.size.toString()
        binding.button.setOnClickListener {
            startActivity(Intent(this, AttendanceBackGroundListActivity::class.java))
        }


        binding.constraintToolbar.icUser.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        binding.constraintToolbar.tvCheckInTextview.setOnClickListener {
            if (!checkBackgroundLocationPermission(this, this)) {
                return@setOnClickListener
            }

            // Get current time
            val calendar = Calendar.getInstance()
            val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
            sdf.format(calendar.time)




            if (PrefsByShubh.getBoolean(Globals.isCheckingStart, false)) {
                if (!LocationPermissionHelper.hasLocationPermission(this)) {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    intent.data = Uri.parse("package:$packageName")
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                } else {
                    dispatchMakeModelPictureIntent()
                }
            } else {
                checkAndRequestPermissions()
                //openDialog()
            }


        }

        getTodayBeatPlanListing()
        PrefsByShubh.putBoolean("SHUBH", true)


        //todo timer work
        if (PrefsByShubh.getBoolean(Globals.isCheckingStart, false)) {
            val startTimeString: String = attendance_date
            val formatter = SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.US)
            try {
                if (startTimeString.isNotEmpty()) {
                    val startDate = formatter.parse(startTimeString)
                    // Get the current time and calculate the difference in seconds
                    val currentDate = Date()
                    val difference = (currentDate.time - startDate!!.time) / 1000
                    seconds = difference.coerceAtLeast(0) // Ensure seconds are not negative
                    handler.post(timerRunnable)


                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            binding.constraintToolbar.tvDuration.text = "Duration:\n00:00:00"
        }

    }

    fun checkBackgroundLocationPermission(context: Context, activity: Activity): Boolean {
        if (!isLocationServiceEnabled(context)) {
            AlertDialog.Builder(context).setTitle("Location Services Disabled")
                .setMessage("Please enable GPS/location services to use checkin or checkout feature.")
                .setPositiveButton("Enable") { _, _ ->
                    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    context.startActivity(intent)
                }.setNegativeButton("Cancel", null).show()
            return false
        }

        if (!isBackgroundLocationGranted(context)) {
            AlertDialog.Builder(context).setTitle("Background Location Required")
                .setMessage("This app requires background location access to checkin or checkout feature.")
                .setPositiveButton("Grant Permission") { _, _ ->
                    Log.i("BACKGROUND_PERMISSION", "Grant permission clicked")
                    navigateToLocationPermissionScreen(activity)
                }.setNegativeButton("Cancel", null).show()
            return false
        }

        return true
    }

    fun isLocationServiceEnabled(context: Context): Boolean {
        val locationManager = context.getSystemService(LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    fun isBackgroundLocationGranted(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            // For Android 9 and below, foreground location implies background location
            true
        }
    }

    fun navigateToLocationPermissionScreen(activity: Activity) {
        val intent = Intent()
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                intent.data = Uri.fromParts("package", activity.packageName, null)
            }

            Build.VERSION.SDK_INT == Build.VERSION_CODES.Q -> {
                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                intent.data = Uri.fromParts("package", activity.packageName, null)
            }

            else -> {
                // Android 9 and below
                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                intent.data = Uri.fromParts("package", activity.packageName, null)
            }
        }
        activity.startActivity(intent)
    }


    private fun checkAndRequestPermissions() {
        if (allPermissionsGranted()) {
            openDialog()
        } else {
            val permissionsToRequest = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                arrayOf(

                    Manifest.permission.CAMERA,

                    Manifest.permission.READ_MEDIA_IMAGES // New for Android 13+

                )

            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                arrayOf(Manifest.permission.CAMERA)
            } else {
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE // Required only for Android 9 and below
                )
            }
            ActivityCompat.requestPermissions(this, permissionsToRequest, PERMISSION_REQUEST_CODE)

        }

    }

    private fun allPermissionsGranted(): Boolean {

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

            ActivityCompat.checkSelfPermission(
                this, Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED &&

                    ActivityCompat.checkSelfPermission(
                        this, Manifest.permission.READ_MEDIA_IMAGES
                    ) == PackageManager.PERMISSION_GRANTED

        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

            ActivityCompat.checkSelfPermission(
                this, Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED

        } else {

            ActivityCompat.checkSelfPermission(
                this, Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED &&

                    ActivityCompat.checkSelfPermission(
                        this, Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED

        }

    }


    private lateinit var locationManager: LocationManager
    private val locationListener = object : android.location.LocationListener {
        override fun onLocationChanged(location: Location) {
            val latitude = location.latitude
            val longitude = location.longitude
        }

        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
        }

        override fun onProviderEnabled(provider: String) {
        }

        override fun onProviderDisabled(provider: String) {
        }
    }


    private fun getAddressFromLocation(latitude: Double, longitude: Double) {
        val geocoder = Geocoder(this, Locale.getDefault())
        try {
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
            if (addresses != null && !addresses.isEmpty()) {
                val address = addresses[0]
                val addressLine = address.getAddressLine(0)
                val city = address.locality ?: address.subAdminArea // Get city name

                Log.e(
                    TAG,
                    "getAddressFromLocation: Latitude: $latitude\nLongitude: $longitude\nAddress: $addressLine\nCity: $addressLine"
                )

                addressGlobal = addressLine!!
                Globals.globalAddress = addressLine
                Globals.globalCity = city!!


            }
        } catch (e: IOException) {

            locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
            startLocationUpdates()
            e.printStackTrace()
            Toast.makeText(
                this, "Unable to get address from latitude and longitude.", Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, 60 * 60 * 1000L, 10f, locationListener
            )

        }


    }


    private var seconds: Long = 0
    private var running = false

    override fun onResume() {
        super.onResume()
        handler.removeCallbacks(timerRunnable)
        cartListForOrderRequest = AppConstants.getCartListFromPreferences(this)
        binding.constraintToolbar.badgeTextView.text = cartListForOrderRequest.size.toString()
        Log.e(TAG, "onResume: ${PrefsByShubh.getBoolean(Globals.isCheckingStart, false)} ")

        if (Globals.isBeatPlanWorking) {
            binding.constraintToolbar.constraintDuration.visibility = View.GONE
            // Observe the timer's LiveData
            TimerManager.getTimerLiveData().observe(this) { time ->
                binding.constraintToolbar.tvTiming.text = "Duration: $time"
            }
        } else {
            binding.constraintToolbar.constraintDuration.visibility = View.GONE
            binding.constraintToolbar.tvTiming.text = "Duration: 00:00:00"
        }

        callAttachmentAllApi()

        getTodayBeatPlanListing()

        binding.constraintToolbar.constraintDuration.setOnClickListener {
            Intent(this, CustomerDetailActivity::class.java).also {
                it.putExtra(Constant.WHERE_INTENT, "beatPlan")
                it.putExtra(Constant.WHERE_CARDCODE, cardCodeBeatPlan)

                it.putExtra(Constant.WHERE_BEATPLAN_ID, beatPlanId.toString())
                startActivity(it)
            }
        }

        binding.constraintToolbar.layoutCart.setOnClickListener {
            if (cartListForOrderRequest.isNotEmpty()) {
                binding.constraintToolbar.badgeTextView.text =
                    cartListForOrderRequest.size.toString()
                Intent(this, CartActivity::class.java).also {

                    startActivity(it)
                }
            } else {
                Globals.warningMessage(this, "Cart is Empty")
            }
        }


        if (!PrefsByShubh.getBoolean(
                Globals.isCheckingStart, false
            )
        ){
            binding.constraintToolbar.tvCheckInTextview.text = "Check In"
            stopService()
            WorkManagerScheduler.cancelWork(this)
        } else{
            binding.constraintToolbar.tvCheckInTextview.text ="Check Out"
            startService()
            callLogoutWorkManager()

        }

        if (LocationPermissionHelper.hasLocationPermission(this)) {
            val locationRequest = LocationRequest.create()
            locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            locationRequest.interval = 2*60*1000L // 10 seconds 120000
            locationRequest.fastestInterval = 60*1000L
            if (ActivityCompat.checkSelfPermission(
                    this, Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this, Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {

                return
            }
            fusedLocationClient!!.requestLocationUpdates(
                locationRequest, object : LocationCallback() {
                    override fun onLocationResult(locationResult: LocationResult) {
                        if (false) {
                            return
                        }
                        for (location: Location? in locationResult.locations) {
                            if (location != null) {
                                latitude = location.latitude
                                longitude = location.longitude
                                Globals.globalLatitude = location.latitude
                                Globals.globalLongitude = location.longitude
                                getAddressFromLocation(latitude, longitude)
                            }
                        }
                    }
                }, mainLooper
            )
        }


    }


    private val handler = Handler() // Make handler global to reuse
    private val timerRunnable = object : Runnable {
        override fun run() {
            // Stop the timer if the condition is met
            if (!PrefsByShubh.getBoolean(Globals.isCheckingStart, false)) {
                binding.constraintToolbar.tvDuration.text = "Duration:\n00:00:00"
                binding.constraintToolbar.tvCheckInTextview.text = "Check In"
                running = false
                seconds = 0
                return
            }

            // Format time
            val hours = seconds / 3600
            val minutes = seconds % 3600 / 60
            val secs = seconds % 60
            val time = String.format(Locale.US, "%d:%02d:%02d", hours, minutes, secs)

            // Update UI
            binding.constraintToolbar.tvCheckInTextview.text = "Check Out"
            binding.constraintToolbar.tvDuration.text = "Duration:\n$time"

            if (running) {
                seconds++
            }
            val pm = getSystemService(POWER_SERVICE) as PowerManager

            if (!pm.isIgnoringBatteryOptimizations(packageName)) {

                val intent = Intent(
                    Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
                ).apply {
                    data = Uri.parse("package:$packageName")
                }

                startActivity(intent)
            }
            if (!isGPSEnabled(this@MainActivity) || !LocationPermissionHelper.hasLocationPermission(
                    this@MainActivity
                )
            ) {
                checkLocationPermission()

            }
            handler.postDelayed(this, 1000)
        }
    }


    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(timerRunnable)
        running = false
    }


    fun isGPSEnabled(context: Context): Boolean {
        val locationManager = context.getSystemService(LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    private fun checkBackgroundLocation() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestBackgroundLocationPermission()
        }
    }

    private fun requestBackgroundLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ActivityCompat.requestPermissions(
                this, arrayOf(
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                ), MY_PERMISSIONS_REQUEST_BACKGROUND_LOCATION
            )
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                MY_PERMISSIONS_REQUEST_LOCATION
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_LOCATION -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (ContextCompat.checkSelfPermission(
                            this, Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        fusedLocationClient?.requestLocationUpdates(
                            locationRequest, locationCallback, Looper.getMainLooper()
                        )
                        checkBackgroundLocation()
                    }

                } else {
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show()
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(
                            this, Manifest.permission.ACCESS_FINE_LOCATION
                        )
                    ) {
                        startActivity(
                            Intent(
                                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.fromParts("package", this.packageName, null),
                            ),
                        )
                    }
                }


                if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
                    if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "Location permission granted.", Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        Toast.makeText(this, "Location permission denied.", Toast.LENGTH_SHORT)
                            .show()
                    }
                }

                return
            }

            MY_PERMISSIONS_REQUEST_BACKGROUND_LOCATION -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(
                            this, Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        fusedLocationClient?.requestLocationUpdates(
                            locationRequest, locationCallback, Looper.getMainLooper()
                        )
                        Toast.makeText(
                            this, "Granted Background Location Permission", Toast.LENGTH_LONG
                        ).show()
                    }
                } else {
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show()
                }
                return

            }

            PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openDialog()

                } else {

                    if (permissions.any { permission ->

                            !ActivityCompat.shouldShowRequestPermissionRationale(this, permission)

                        }) {
                        showSettingsDialog()

                    } else {
                        showRationaleDialog()

                    }


                }
            }

        }

    }

    private fun showRationaleDialog() {
        AlertDialog.Builder(this).setTitle("Permissions Required")
            .setMessage("Camera and storage permissions are required to take pictures. Please grant them to continue.")
            .setPositiveButton("Grant") { _, _ ->
                checkAndRequestPermissions() // Re-request permissions
            }.setNegativeButton("Cancel", null).show()
    }

    private fun showSettingsDialog() {
        AlertDialog.Builder(this).setTitle("Permissions Denied")
            .setMessage("You have denied permissions permanently. Please go to settings to enable them.")
            .setPositiveButton("Go to Settings") { _, _ ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.data = Uri.fromParts("package", packageName, null)
                startActivity(intent)
            }.setNegativeButton("Cancel", null).show()
    }

    private var locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val locationList = locationResult.locations
            if (locationList.isNotEmpty()) {
                //The last location in the list is the newest
                val location = locationList.last()
                Toast.makeText(
                    this@MainActivity, "Got Location: " + location.toString(), Toast.LENGTH_LONG
                ).show()
            }
        }
    }


    private val locationRequest: LocationRequest = LocationRequest.create().apply {
        interval = 30
        fastestInterval = 10
        priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        maxWaitTime = 60
    }


    private fun checkLocationPermission() {


        val builder: MultipartBody.Builder = MultipartBody.Builder()
        builder.setType(MultipartBody.FORM)
        builder.addFormDataPart("CheckIn_Lat", Globals.globalLatitude.toString())
        builder.addFormDataPart("CheckIn_Long", Globals.globalLongitude.toString())
        builder.addFormDataPart("CheckIn_Address", Globals.globalAddress)
        builder.addFormDataPart(
            "SalesEmployeeCode", SalesEmployeeCode
        )
        builder.addFormDataPart("Emp_Name", "Admin")

        builder.addFormDataPart("Type", "stop")


        builder.addFormDataPart("Total_Hour", seconds.toString())
        builder.addFormDataPart("Create_Date", Globals.getTodaysDatervrsfrmt()!!)
        builder.addFormDataPart("Create_Time", Globals.getTCurrentTime_hh_mm_ss_aa()!!)
        builder.addFormDataPart("Created_at", Globals.getCurrentDateTimeFormatted_hh_mm_ss()!!)
        var newList = ArrayList<String>()

        if (selectedJointStatus == "Self") {
            newList.clear()
            newList.add("")
        } else if (selectedJointStatus == "None") {
            newList.clear()
            newList.add("")
        } else {
            newList.clear()

            for (index in todayBeatPlanList.indices) {

                if (todayBeatPlanList[index].isSelected) {
                    newList.add(todayBeatPlanList[index].id)
                }

            }
        }

        //  val newList = arrayListOf("5")
        builder.addFormDataPart("BeatPlan_Ids", newList.toString())
        newList.joinToString(prefix = "[", postfix = "]") { it }

        builder.addFormDataPart(
            "File", ""
        )


        val requestBody: MultipartBody = builder.build()


        printPayload()
        createAttachment1(requestBody)


    }

    private fun printPayload() {

        try {
            // Create the main JSON object
            val finalJson = JSONObject()

            // Add individual fields
            finalJson.put("CheckIn_Lat", Globals.globalLatitude)
            finalJson.put("CheckIn_Long", Globals.globalLongitude)
            finalJson.put("CheckIn_Address", Globals.globalAddress)
            finalJson.put("SalesEmployeeCode", SalesEmployeeCode) // Static
            finalJson.put("Emp_Name", "Admin")
            finalJson.put("Type", "stop")
            finalJson.put("Total_Hour", seconds)
            finalJson.put("Create_Date", Globals.getTodaysDatervrsfrmt())
            finalJson.put("Create_Time", Globals.getTCurrentTime_hh_mm_ss_aa())
            finalJson.put("Created_at", Globals.getCurrentDateTimeFormatted_hh_mm_ss())

            var newList = ArrayList<String>()

            if (selectedJointStatus == "Self") {
                newList.clear()
                newList.add("")
            } else if (selectedJointStatus == "None") {
                newList.clear()
                newList.add("")
            } else {
                newList.clear()
                for (index in todayBeatPlanList.indices) {

                    if (todayBeatPlanList[index].isSelected) {
                        newList.add(todayBeatPlanList[index].id)
                    }

                }
            }

            val beatPlanIdsArray: JSONArray = JSONArray(newList)
            finalJson.put("BeatPlan_Ids", beatPlanIdsArray)
            finalJson.put("File", "")
            finalJson.toString()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }


    private fun createAttachment1(requestBody: MultipartBody) {
        val call: Call<ResponseGlobal> = RetrofitClient.apiService.punchDailyAttendance(requestBody)
        call.enqueue(object : Callback<ResponseGlobal> {
            override fun onResponse(
                call: Call<ResponseGlobal>, response: Response<ResponseGlobal>
            ) {
                val responseBody = response.body()
                if (response.isSuccessful && response.body() != null) {
                    if (responseBody != null && responseBody.status == 200) {
                        PrefsByShubh.putBoolean(Globals.isCheckingStart, false)
                        Toast.makeText(
                            this@MainActivity, "Stopped SuccessFully", Toast.LENGTH_SHORT
                        ).show()
                        currentPhotoPath = ""
                        binding.constraintToolbar.tvCheckInTextview.text = "Check In"
                        binding.constraintToolbar.tvDuration.text = "Duration:\n00:00:00"
                        ischeckedIn = false

                        // Create an Intent to stop the LocationService
                        callAttachmentAllApi()
                        stopService()

                        if (!PrefsByShubh.getBoolean(Globals.isCheckingStart, false)) {
                            binding.constraintToolbar.tvDuration.text = "Duration: 00:00:00"

                        }

                    } else if (response.body()!!.status == 201) {
                        Toast.makeText(
                            this@MainActivity, response.body()!!.message, Toast.LENGTH_SHORT
                        ).show()
                    }
                }


            }

            override fun onFailure(call: Call<ResponseGlobal>, t: Throwable) {
                alertDialog!!.dismiss()
                Toast.makeText(this@MainActivity, "" + t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }


}