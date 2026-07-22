package com.preetTractor.galaxyAndroid.ui.activity

import android.app.AlertDialog
import android.app.Dialog
import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.preetTractor.galaxyAndroid.R
import com.preetTractor.galaxyAndroid.adapter.BeatPlanListingAdapter1
import com.preetTractor.galaxyAndroid.apiHelper.ApiClient
import com.preetTractor.galaxyAndroid.apiHelper.ApisInterface
import com.preetTractor.galaxyAndroid.apiHelper.AppConstants
import com.preetTractor.galaxyAndroid.data.ResponseGlobal
import com.preetTractor.galaxyAndroid.data.beatplan.DataBeatPlan
import com.preetTractor.galaxyAndroid.databinding.ActivityBeatPlan2Binding
import com.preetTractor.galaxyAndroid.databinding.DialogLeaveStatusBinding
import com.preetTractor.galaxyAndroid.databinding.DialogRescheduleBeatPlanBinding
import com.preetTractor.galaxyAndroid.helper.Constant
import com.preetTractor.galaxyAndroid.helper.Globals
import com.preetTractor.galaxyAndroid.helper.Globals.transformIntoDatePicker
import com.preetTractor.galaxyAndroid.helper.Globals.transformIntoTimePicker
import com.preetTractor.galaxyAndroid.helper.PrefsByShubh
import com.preetTractor.galaxyAndroid.mvvmSetUp.DefaultMainRepositories
import com.preetTractor.galaxyAndroid.mvvmSetUp.MainRepos
import com.preetTractor.galaxyAndroid.mvvmSetUp.MainViewModel
import com.preetTractor.galaxyAndroid.mvvmSetUp.MainViewModelProvider
import com.preetTractor.galaxyAndroid.retrofit.RetrofitClient
import com.preetTractor.galaxyAndroid.ui.activity.customer.CustomerDetailActivity
import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class BeatPlanActivity2 : BaseActivity() {

    lateinit var binding: ActivityBeatPlan2Binding
    private lateinit var adapter: BeatPlanListingAdapter1
    private lateinit var viewModel: MainViewModel
    lateinit var dialog: Dialog

    var salesEmployeeCode: String = ""
    var currentPage: String = ""
    var currentSelectedDate: String = ""
    var filterPriority: String = ""
    var id: String = ""
    var Type: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBeatPlan2Binding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpViewModel()

        // Initialize intent values
        salesEmployeeCode = intent.getStringExtra("salesEmployeeCode").toString()
        currentPage = intent.getStringExtra("currentPage").toString()
        currentSelectedDate = intent.getStringExtra("currentSelectedDate").toString()
        filterPriority = intent.getStringExtra("filterPriority").toString()
        id = intent.getStringExtra("id").toString()
        Type = intent.getStringExtra("Type").toString()

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        if(Type.equals("Customer")){
            supportActionBar?.let {
                it.title = "Customer List"
                it.setHomeButtonEnabled(true)
            }
        }
        else if(Type.equals("Lead")){
            supportActionBar?.let {
                it.title = "Lead List"
                it.setHomeButtonEnabled(true)
            }
        }
        else{
            supportActionBar?.let {
                it.title = "Non Customer List"
                it.setHomeButtonEnabled(true)
            }
        }






        // Initialize the dialog
        dialog = Dialog(this)
        callBeatPlanListingApi()
        setUpObserver()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setUpViewModel() {
        val dispatchers: CoroutineDispatcher = Dispatchers.Main
        val mainRepos = DefaultMainRepositories() as MainRepos
        val fanxApi: ApisInterface = ApiClient().service(this)
        val viewModelProviderfactory =
            MainViewModelProvider(application, mainRepos, dispatchers, fanxApi)
        viewModel = ViewModelProvider(this, viewModelProviderfactory)[MainViewModel::class.java]

    }

    private fun setUpObserver() {
        binding.noDataFoundLayout.ivNoDataFound.visibility = View.GONE
//        viewModel.beatPlanItemAllList.observe(this@BeatPlanActivity2, Event.EventObserver(
        viewModel.beatPlanItemCustomerAllList.observe(this@BeatPlanActivity2, Event.EventObserver(
            onError = {
                binding.spinKitLoader.isVisible = false
                binding.noDataFoundLayout.ivNoDataFound.visibility = View.VISIBLE
            },
            onLoading = {

            },
            onSuccess = { response ->
                binding.spinKitLoader.isVisible = false
                if (response.data.isEmpty()) {
                    binding.noDataFoundLayout.ivNoDataFound.visibility = View.VISIBLE
                    setUpRecyclerview(response.data)
                } else {
                    binding.noDataFoundLayout.ivNoDataFound.visibility = View.GONE
                    setUpRecyclerview(response.data)
                }
            }
        ))


    }


    private fun setUpRecyclerview(data: List<DataBeatPlan>) {

        val fromWhere = intent.getStringExtra("fromWhere")


        adapter = BeatPlanListingAdapter1(data, this@BeatPlanActivity2, Type.toString())
        binding.beatPlanCards.adapter = adapter
        adapter.setOnItemClickListener { _url, _msg ->
            Globals.openDialerWithNumber(this@BeatPlanActivity2, _url)
        }

        adapter.setOnItemMapClickListener {
            Globals.openLocationInGoogleMaps(this@BeatPlanActivity2, it.Lat, it.Long)
        }

        adapter.setOnItemWholeClickListener { item ->
            AppConstants.cartListForOrderRequest.clear()
            AppConstants.saveCartListToPreferences(this, mutableListOf())

            PrefsByShubh.setCardCode(item.CardCode)
            PrefsByShubh.setCardName(item.CardName)

            Intent(this@BeatPlanActivity2, CustomerDetailActivity::class.java).also {

                it.putExtra("Type",Type)
                it.putExtra(Constant.WHERE_INTENT, "beatPlan")
                it.putExtra(Constant.WHERE_CARDCODE, item.CardCode)

                it.putExtra(Constant.WHERE_BEATPLAN_ID, item.id.toString())
                startActivity(it)
            }

        }


        adapter.setOnItemRescheduleClickListener { item ->
            if (item.Approval_Status.equals("Pending", ignoreCase = true)) {
                openRescheduleDialog(item)
            } else {
                Globals.warningMessage(this@BeatPlanActivity2, "Cannot reschedule")
            }

        }


        adapter.setOnItemApprovalClickListener { leaveStatusData ->

            if (leaveStatusData.AssignedTo == PrefsByShubh.getSalesEmployeeCode()) {

                Globals.warningMessage(this@BeatPlanActivity2, "Not Authorized")
            } else {
                showStatusDialog(leaveStatusData)
            }

        }



        adapter.notifyDataSetChanged()
    }

    lateinit var alertBinding: DialogRescheduleBeatPlanBinding
    lateinit var mAlert: AlertDialog

    var selectedTiming = ""
    var selectedPriority = ""

    private fun openRescheduleDialog(item: DataBeatPlan) {
        alertBinding = DialogRescheduleBeatPlanBinding.inflate(layoutInflater)

        val builder = AlertDialog.Builder(this@BeatPlanActivity2)

        mAlert = builder.setView(alertBinding.root)
            .setCancelable(true)
            .create()
        mAlert.window?.setBackgroundDrawable(
            ContextCompat.getDrawable(
                this@BeatPlanActivity2,
                R.drawable.alert_bg
            )
        )

        mAlert.show()

        alertBinding.apply {
            tvTitle.text = "Update Time"
            etRescheduleDate.visibility = View.GONE

            btnCancel.setOnClickListener {
                mAlert.dismiss()
            }

            btnClose.setOnClickListener {
                mAlert.dismiss()
            }

            edtTiming.visibility = View.VISIBLE
            etRescheduleDate.setText(Globals.convert_yyyy_MM_dd_into_dd_MM_yyyy(item.Visit_Date))
            edtTiming.setText(item.Shop_StartAt)
            etRescheduleDate.transformIntoDatePicker(etRescheduleDate.context, "dd-MM-yyyy", null)
            edtTiming.transformIntoTimePicker(this@BeatPlanActivity2, "hh:mm a")
            // setupSpinner(alertBinding.spinnerTiming, R.array.timing_array)
            setupSpinner(alertBinding.spinnerPriority, R.array.priority_array)


            when (item.Purpose) {
                "Morning" -> {
                    selectedTiming = "Morning"
                    spinnerTiming.setSelection(0)
                }
                "Evening" -> {
                    selectedTiming = "Evening"
                    spinnerTiming.setSelection(1)
                }
                "Afternoon" -> {
                    selectedTiming = "Afternoon"
                    spinnerTiming.setSelection(2)
                }
            }

            when (item.Priority) {
                "High" -> {
                    selectedPriority = "High"
                    spinnerPriority.setSelection(0)
                }
                "Medium" -> {
                    selectedPriority = "Medium"
                    spinnerPriority.setSelection(1)
                }
                "Low" -> {
                    selectedPriority = "Low"
                    spinnerPriority.setSelection(2)
                }
            }

            spinnerTiming.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {

                    selectedTiming = parent?.getItemAtPosition(position).toString()

                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

            spinnerPriority.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    selectedPriority = parent?.getItemAtPosition(position).toString()

                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

            btnSave.setOnClickListener {
                btnSave.alpha = 0.3f
                btnSave.isClickable = false
                updateCustomerTime(item)
//                rescheduleBeatPlan(item)

            }


        }


    }

    private fun updateCustomerTime(item: DataBeatPlan) {

        val hde = JsonObject()
        hde.addProperty("id", item.id)
        hde.addProperty("SalesEmployeeCode", Globals.SalesEmployeeCode)
        hde.addProperty("Update_Date", Globals.getTodaysDatervrsfrmt())
        hde.addProperty("Update_Time", Globals.getTCurrentTime())
        hde.addProperty("Shop_StartAt", alertBinding.edtTiming.text.toString())

        val call: Call<ResponseGlobal> = RetrofitClient.apiService.updateBeatPlanCustomerTiming(hde)

        call.enqueue(object : Callback<ResponseGlobal> {
            override fun onResponse(
                call: Call<ResponseGlobal>,
                response: Response<ResponseGlobal>
            ) {
                if (response != null) {
                    if (response.body()!!.status == 200
                    ) {
                        alertBinding.btnSave.alpha = 0.3f
                        alertBinding.btnSave.isClickable = true
                        mAlert.dismiss()

                        Globals.successMessage(this@BeatPlanActivity2, "Time Update Successfully")
                        callBeatPlanListingApi()
                        Log.e(
                            TAG,
                            "onResponseBackground: " + response.body()!!.message
                        )
                    } else if (response.body()!!.status == 201) {
                        Globals.warningMessage(this@BeatPlanActivity2, response.message())
                    }
                }
            }

            override fun onFailure(call: Call<ResponseGlobal>, t: Throwable) {
                // binding.loaderLayout.loader.setVisibility(View.GONE);
                Log.e(TAG, "onFailure: " + t.message)
            }
        })

    }

    private fun rescheduleBeatPlan(item: DataBeatPlan) {
        /*   {
               "id": "35",
               "SalesEmployeeCode": "",
               "Visit_Status": "",
               "Visit_Date": "",
               "Update_Date": "",
               "Update_Time": ""
           }*/

        val inputFormat = SimpleDateFormat("hh:mm a", Locale.US) // 12-hour format with AM/PM

        try {
            val closeTime = inputFormat.parse(item.Shop_CloseAt) // Convert Shop_CloseAt to Date
            val enteredTime = inputFormat.parse(alertBinding.edtTiming.text.toString()) // Convert input time to Date

            if (closeTime != null && enteredTime != null) {
                if (closeTime.before(enteredTime)) {
                    Toast.makeText(this, "Closing time is before Start time!", Toast.LENGTH_SHORT).show()
                } else {

                    val hde = JsonObject()
                    hde.addProperty("SalesEmployeeCode", Globals.SalesEmployeeCode)
                    hde.addProperty("id", item.id)
                    hde.addProperty("Priority", selectedPriority)
                    hde.addProperty("Purpose", selectedTiming)
                    hde.addProperty("Visit_Status", item.Visit_Status.toString())
                    hde.addProperty("Approval_Status", item.Approval_Status.toString())
                    hde.addProperty(
                        "Visit_Date",
                        Globals.convert_dd_MM_yyyy_into_yyyy_MM_dd(alertBinding.etRescheduleDate.text.toString())
                    )
                    hde.addProperty("Update_Date", Globals.getTodaysDatervrsfrmt())
                    hde.addProperty("Update_Time", Globals.getTCurrentTime())
                    hde.addProperty("Shop_StartAt", alertBinding.edtTiming.text.toString())
                    hde.addProperty("Shop_CloseAt", item.Shop_CloseAt)

                    val call: Call<ResponseGlobal> = RetrofitClient.apiService.rescheduleBeatPlan(hde)
                    call.enqueue(object : Callback<ResponseGlobal> {
                        override fun onResponse(
                            call: Call<ResponseGlobal>,
                            response: Response<ResponseGlobal>
                        ) {
                            if (response != null) {
                                if (response.body()!!.status == 200
                                ) {
                                    alertBinding.btnSave.alpha = 0.3f
                                    alertBinding.btnSave.isClickable = true
                                    mAlert.dismiss()

                                    Globals.successMessage(this@BeatPlanActivity2, "Reschedule Successfully")
                                    callBeatPlanListingApi()
                                    Log.e(
                                        TAG,
                                        "onResponseBackground: " + response.body()!!.message
                                    )
                                } else if (response.body()!!.status == 201) {
                                    Globals.warningMessage(this@BeatPlanActivity2, response.message())
                                }
                            }
                        }

                        override fun onFailure(call: Call<ResponseGlobal>, t: Throwable) {
                            // binding.loaderLayout.loader.setVisibility(View.GONE);
                            Log.e(TAG, "onFailure: " + t.message)
                        }
                    })
                }
            }
        } catch (e: ParseException) {
            e.printStackTrace()
            Toast.makeText(this, "Invalid time format!", Toast.LENGTH_SHORT).show()
        }




    }

    private fun callBeatPlanListingApi() {


        binding.spinKitLoader.isVisible = true
        if (Globals.checkForInternet(this@BeatPlanActivity2)) {
            val jsonObject = JsonObject().apply {
                addProperty("SalesEmployeeCode", salesEmployeeCode)
                addProperty("id", id)
                addProperty("Latitude", Globals.globalLatitude)
                addProperty("Longitude", Globals.globalLongitude)
            }

//            viewModel.getBeatPlanListing(jsonObject, this@BeatPlanActivity2)
            viewModel.getBeatPlanCustomerAllListing(jsonObject, this@BeatPlanActivity2)
        }
    }

    private fun setupSpinner(spinner: Spinner, arrayResId: Int) {
        ArrayAdapter.createFromResource(
            spinner.context,
            arrayResId,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }
    }

    private fun showStatusDialog(leaveStatusData: DataBeatPlan) {

        val binding = DialogLeaveStatusBinding.inflate(LayoutInflater.from(this@BeatPlanActivity2))

        dialog.setContentView(binding.root)

        // todo Set the dialog window to be larger
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        // Set up the spinner with an adapter
        binding.spinnerStatus.apply {
            adapter = ArrayAdapter(
                context,
                android.R.layout.simple_spinner_dropdown_item,
                resources.getStringArray(R.array.status_array)
            )
        }

        // Set the button click listener
        binding.btnSave.setOnClickListener {
            val selectedStatus = binding.spinnerStatus.selectedItem.toString()
            // Trigger your desired function here
            handleStatusSave(selectedStatus, leaveStatusData)
            dialog.dismiss()
        }

        // Show the dialog
        dialog.show()
    }

    private fun handleStatusSave(status: String, leaveStatusData: DataBeatPlan) {
        // Handle the save action based on the selected status
        when (status) {
            "Approved" -> {
                changeLeaveStatusAPi(status, leaveStatusData)
            }
            "Rejected" -> {
                // Handle cancelled action
                changeLeaveStatusAPi(status, leaveStatusData)
            }
            "Pending" -> {
                changeLeaveStatusAPi(status, leaveStatusData)
                // Handle pending action
            }
        }
    }

    private fun changeLeaveStatusAPi(status: String, leaveStatusData: DataBeatPlan) {
        binding.spinKitLoader.visibility = View.VISIBLE
        /* {
             "Beatplan_id": "1",
             "SalesEmployeeCode": "-1",
             "Approval_Status": "Approved",
             "Update_Date": "",
             "Update_Time": ""
         }*/

        val hde = JsonObject().apply {
            addProperty("Beatplan_id", leaveStatusData.id)
            addProperty("Approval_Status", status)
            addProperty("SalesEmployeeCode", salesEmployeeCode)
            addProperty("Update_Date", Globals.getTodaysDatervrsfrmt())
            addProperty("Update_Time", Globals.getTCurrentTime())
        }
        val call = RetrofitClient.apiService.galaxyApproveBeatPlanApi(hde)

        call.enqueue(object : Callback<ResponseGlobal> {
            override fun onResponse(
                call: Call<ResponseGlobal>,
                response: Response<ResponseGlobal>
            ) {
                binding.spinKitLoader.visibility = View.GONE
                response.body()?.let {
                    dialog.dismiss()

                    when (it.status) {
                        200 -> {
                            Globals.successMessage(this@BeatPlanActivity2, "Updated SuccessFully")
                            callBeatPlanListingApi()
                            setUpObserver()
                        }
                        201 -> {
                            Globals.successMessage(this@BeatPlanActivity2, it.message)
                        }
                        else -> {
                            Globals.successMessage(this@BeatPlanActivity2, response.message())
                        }
                    }
                }
            }

            override fun onFailure(call: Call<ResponseGlobal>, t: Throwable) {
                Log.e(TAG, "onFailure: ${t.message}")
                Toast.makeText(this@BeatPlanActivity2, "Something went wrong", Toast.LENGTH_SHORT)
                    .show()
                binding.spinKitLoader.visibility = View.GONE
            }
        })
    }
}