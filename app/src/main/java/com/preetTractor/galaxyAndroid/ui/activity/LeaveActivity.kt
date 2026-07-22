package com.preetTractor.galaxyAndroid.ui.activity

import android.app.Dialog
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.RadioButton
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.preetTractor.galaxyAndroid.data.AllLeaveModel
import com.preetTractor.galaxyAndroid.data.LeaveStatusData
import com.preetTractor.galaxyAndroid.data.ResponseGlobal
import com.preetTractor.galaxyAndroid.helper.Constant
import com.preetTractor.galaxyAndroid.helper.Globals
import com.preetTractor.galaxyAndroid.helper.PrefsByShubh
import com.preetTractor.galaxyAndroid.retrofit.RetrofitClient
import com.preetTractor.galaxyAndroid.ui.recyclerview.AllLeaveForTeamAdapter
import com.google.gson.JsonObject
import com.preetTractor.galaxyAndroid.R
import com.preetTractor.galaxyAndroid.databinding.ActivityLeaveBinding
import com.preetTractor.galaxyAndroid.databinding.DialogLeaveStatusBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response



class LeaveActivity : BaseActivity() {
    lateinit var binding: ActivityLeaveBinding
    private lateinit var adapter: AllLeaveForTeamAdapter
    private var allLeaveStatusItemList = ArrayList<LeaveStatusData>()
    var salesEmpoyeeCode = ""
    var salesEmpoyeeName = ""

    var fromDateString: String? = Globals.getFirstDateofMonth()
    var toDateString: String? = Globals.getTodaysDatervrsfrmt()

    companion object {
        private const val TAG = "LeaveActivity"
    }

    lateinit var dialog: Dialog
    var dateString: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLeaveBinding.inflate(layoutInflater)

        setContentView(binding.root)

        dialog = Dialog(this)

        salesEmpoyeeCode = intent.getStringExtra(Constant.WHERE_INTENT_VALUE_SALES).toString()
        salesEmpoyeeName = intent.getStringExtra(Constant.WHERE_INTENT_VALUE_SALES_NAME).toString()

        binding.tvDate.text = Globals.getFirstDateofMonth()
        fromDateString=Globals.dateStringConvertToDesiredFormat(Globals.getFirstDateofMonth().toString(), "dd-MM-yyyy", "yyyy-MM-dd")
        binding.tvDate.setOnClickListener {
            Globals.openDatePicker(binding.tvDate) { formattedDate ->
                fromDateString = formattedDate
                binding.tvDate.text = Globals.dateStringConvertToDesiredFormat(formattedDate, "yyyy-MM-dd", "dd-MM-yyyy")
                apiAllLeaveStatusCall()
            }
        }
        binding.tvToDate.text = Globals.getTodaysDate()
        toDateString=binding.tvToDate.text.toString()
        toDateString=Globals.dateStringConvertToDesiredFormat(Globals.getTodaysDate().toString(), "dd-MM-yyyy", "yyyy-MM-dd")
        binding.tvToDate.setOnClickListener {
            Globals.openDatePicker(binding.tvToDate)
            { formattedDate ->
                // Set the formatted date (yyyy-MM-dd) in the EditText
                toDateString = formattedDate
                binding.tvToDate.text = Globals.dateStringConvertToDesiredFormat(
                    formattedDate,
                    "yyyy-MM-dd",
                    "dd-MM-yyyy"
                )
                apiAllLeaveStatusCall()
            }
        }


        intialStatus()
        apiCalling()

        supportActionBar?.apply {
            title = salesEmpoyeeName
            setDisplayHomeAsUpEnabled(true)
            setHomeButtonEnabled(true)
        }


     //   binding.tvDate.text = Globals.getTodaysDate()

      /*  binding.tvDate.setOnClickListener {
            Globals.openDatePicker(binding.tvDate) { formattedDate ->
                // Set the formatted date (yyyy-MM-dd) in the EditText
                dateString = formattedDate
                binding.tvDate.text = Globals.dateStringConvertToDesiredFormat(formattedDate, "yyyy-MM-dd", "dd-MM-yyyy")
                apiAllLeaveStatusCall()
            }
        }*/

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

    private fun showStatusDialog(leaveStatusData: LeaveStatusData) {

        val binding = DialogLeaveStatusBinding.inflate(LayoutInflater.from(this))

        dialog.setContentView(binding.root)

        // todo Set the dialog window to be larger
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        // Set up the spinner with an adapter
        binding.spinnerStatus.apply {
            adapter = ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, resources.getStringArray(R.array.status_array))
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

    private fun handleStatusSave(status: String, leaveStatusData: LeaveStatusData) {
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


    private fun apiCalling() {
        apiAllLeaveStatusCall()
    }


    private fun intialStatus() {
        adapter = AllLeaveForTeamAdapter(
            allLeaveStatusItemList,
            this, false
        )
        adapter.setonStatusBtnClickListener { leaveStatusData, i ->

            if (leaveStatusData.SalesEmployeeCode == PrefsByShubh.getSalesEmployeeCode()){
                Log.e(TAG, "intialStatus: "+leaveStatusData.SalesEmployeeCode+" , "+PrefsByShubh.getSalesEmployeeCode().toString() )
                Globals.warningMessage(this,"Not Authorized")
            }else{
                showStatusDialog(leaveStatusData)
            }
          //  showStatusDialog(leaveStatusData)
        }

        binding.rgLeave.setOnCheckedChangeListener { group, checkedId ->
            val radio: RadioButton = findViewById(checkedId)
            when (checkedId) {
                R.id.allRB -> {
                    Log.e(TAG, "intialStatus: ${allLeaveStatusItemList.toString()}")
                    adapter.updateEmployeeListItems(allLeaveStatusItemList)
                }

                R.id.approvedRB -> {

                    var filterList =
                        allLeaveStatusItemList!!.filter { it.Approval_Status == "Approved" }
                    adapter.updateEmployeeListItems(filterList)
                }

                R.id.pendingRB -> {
                    var filterList = allLeaveStatusItemList!!.filter { it.Approval_Status == "Pending"}
                    adapter.updateEmployeeListItems(filterList)
                }

                R.id.rejecedRB -> {
                    var filterList =
                        allLeaveStatusItemList!!.filter { it.Approval_Status == "Rejected" }
                    adapter.updateEmployeeListItems(filterList)
                }
            }


        }
    }


    private fun apiAllLeaveStatusCall() {
        binding.progressBar.visibility = View.VISIBLE
        val hde = JsonObject().apply {
            addProperty("SalesEmployeeCode", salesEmpoyeeCode)
            addProperty("Status", "")
            addProperty("Datefilter", dateString)
            addProperty("ToDate", toDateString)
            addProperty("FromDate", fromDateString)
        }


        val call = RetrofitClient.apiService.galaxyAllLeaveStatusList(hde)

        call.enqueue(object : Callback<AllLeaveModel> {
            override fun onResponse(
                call: Call<AllLeaveModel>,
                response: Response<AllLeaveModel>
            ) {
                binding.progressBar.visibility = View.GONE
                response.body()?.let {

                    if (it.status == 200) {
                        if (it.data.isEmpty()){
                            allLeaveStatusItemList = it.data as ArrayList<LeaveStatusData>
                            binding.noDataFoundLayout.ivNoDataFound.visibility = View.VISIBLE
                            val dividerItemDecoration = DividerItemDecoration(
                                binding.rvLeave.context,
                                (binding.rvLeave.layoutManager as LinearLayoutManager).orientation
                            )
                            val drawable: Drawable? = ContextCompat.getDrawable(this@LeaveActivity, R.drawable.item_spacing)
                            binding.rvLeave.addItemDecoration(dividerItemDecoration)
                            drawable?.let { it1 -> dividerItemDecoration.setDrawable(it1) }

                            binding.rvLeave.adapter = adapter
                            adapter.updateEmployeeListItems(allLeaveStatusItemList)
                            adapter.notifyDataSetChanged()
                        }
                        else{
                            binding.noDataFoundLayout.ivNoDataFound.visibility = View.GONE
                            allLeaveStatusItemList = it.data as ArrayList<LeaveStatusData>
                            val dividerItemDecoration = DividerItemDecoration(
                                binding.rvLeave.context,
                                (binding.rvLeave.layoutManager as LinearLayoutManager).orientation
                            )
                            val drawable: Drawable? = ContextCompat.getDrawable(this@LeaveActivity, R.drawable.item_spacing)
                            binding.rvLeave.addItemDecoration(dividerItemDecoration)
                            drawable?.let { it1 -> dividerItemDecoration.setDrawable(it1) }

                            binding.rvLeave.adapter = adapter
                            adapter.updateEmployeeListItems(allLeaveStatusItemList)
                            adapter.notifyDataSetChanged()
                        }

                    }
                }
            }

            override fun onFailure(call: Call<AllLeaveModel>, t: Throwable) {
                Log.e(TAG, "onFailure: ${t.message}")
                Toast.makeText(this@LeaveActivity, "Something went wrong", Toast.LENGTH_SHORT).show()
                binding.progressBar.visibility = View.GONE
                binding.noDataFoundLayout.ivNoDataFound.visibility = View.GONE
            }
        })
    }


    private fun changeLeaveStatusAPi(status: String, leaveStatusData: LeaveStatusData) {
        binding.progressBar.visibility = View.VISIBLE

        val hde = JsonObject().apply {
            addProperty("id", leaveStatusData.id)
            addProperty("Approval_Status", status)
            addProperty("SalesEmployeeCode", PrefsByShubh.getSalesEmployeeCode())
            addProperty("Update_Date", Globals.getTodaysDatervrsfrmt())
            addProperty("Update_Time", Globals.getTCurrentTime())
        }
        val call = RetrofitClient.apiService.galaxyApproveLeaveApi(hde)

        call.enqueue(object : Callback<ResponseGlobal> {
            override fun onResponse(
                call: Call<ResponseGlobal>,
                response: Response<ResponseGlobal>
            ) {
                binding.progressBar.visibility = View.GONE
                response.body()?.let {
                    dialog.dismiss()

                    when (it.status) {
                        200 -> {
                            Globals.successMessage(this@LeaveActivity, "Updated SuccessFully")
                            apiCalling()
                        }
                        201 -> {
                            Globals.successMessage(this@LeaveActivity, it.message)
                        }
                        else -> {
                            Globals.successMessage(this@LeaveActivity, response.message())
                        }
                    }
                }
            }

            override fun onFailure(call: Call<ResponseGlobal>, t: Throwable) {
                Log.e(TAG, "onFailure: ${t.message}")
                Toast.makeText(this@LeaveActivity, "Something went wrong", Toast.LENGTH_SHORT)
                    .show()
                binding.progressBar.visibility = View.GONE
            }
        })
    }


}