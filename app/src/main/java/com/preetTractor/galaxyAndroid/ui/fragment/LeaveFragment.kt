package com.preetTractor.galaxyAndroid.ui.fragment

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.RadioButton
import android.widget.SpinnerAdapter
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.preetTractor.galaxyAndroid.R
import com.preetTractor.galaxyAndroid.data.AllLeaveModel
import com.preetTractor.galaxyAndroid.data.LeaveStatusData
import com.preetTractor.galaxyAndroid.data.ResponseGlobal
import com.preetTractor.galaxyAndroid.databinding.FragmentLeaveBinding
import com.preetTractor.galaxyAndroid.helper.Globals
import com.preetTractor.galaxyAndroid.helper.Globals.hideKeyboard

import com.preetTractor.galaxyAndroid.helper.Globals.transformIntoDatePicker
import com.preetTractor.galaxyAndroid.retrofit.RetrofitClient
import com.preetTractor.galaxyAndroid.ui.recyclerview.AllLeaveStatusAdapter
import com.preetTractor.galaxyAndroid.ui.recyclerview.CustomSpinnerAdapter
import com.google.gson.JsonObject
import com.preetTractor.galaxyAndroid.helper.Globals.transformIntoDatePickerWithDisablePastDates
import com.preetTractor.galaxyAndroid.retrofit.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LeaveFragment : Fragment() {
    lateinit var binding: FragmentLeaveBinding
    val TAG: String = "LeaveFragment"
    var booleanAddLeave = true
    var booleanEditLeave = false
    private lateinit var adapter: AllLeaveStatusAdapter
    var leaveTypeStr = ""
    var fromDateStr = ""
    var toDateStr = ""
    var leaveReasonStr = ""
    val spinnerItems = listOf(
        CustomSpinnerAdapter.SpinnerItem("Sick Leave"),
        CustomSpinnerAdapter.SpinnerItem("Casual Leave"),
    )
    lateinit var updateLeaveStatusData: LeaveStatusData
    private var allLeaveStatusItemList = ArrayList<LeaveStatusData>()
    lateinit var spinnerAdapter: SpinnerAdapter

    var callAPiSingleInstance: ApiService? =null

    var fromDateString: String? = Globals.getFirstDateofMonth()
    var toDateString: String? = Globals.getTodaysDatervrsfrmt()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentLeaveBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment
        return binding.root
    }

    // If you want to store the date in a string variable
    var dateString: String? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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

        clickedEvents()

/*
        binding.tvDate.setOnClickListener {
            openDatePicker(binding.tvDate) { formattedDate ->
                // Set the formatted date (yyyy-MM-dd) in the EditText
                dateString = formattedDate
                Log.e(TAG, "onViewCreated: $dateString")
                binding.tvDate.text = Globals.dateStringConvertToDesiredFormat(
                    formattedDate,
                    "yyyy-MM-dd",
                    "dd-MM-yyyy"
                )
                apiAllLeaveStatusCall()
            }
        }*/


    }


    private fun intialStatus() {
        callAPiSingleInstance= RetrofitClient.apiService
        binding.cardLeave.visibility = if (booleanAddLeave) View.GONE else View.VISIBLE
        binding.tvAdd.alpha = if (!booleanAddLeave) .5f else 1f
        binding.btnSave.text = "Save"
        binding.etFromDateSelected.setText(
            Globals.dateStringConvertToDesiredFormat(
                Globals.getTodaysDate() ?: "", "dd-MM-yyyy", "dd-MM-yyyy"
            )
        )


        binding.etFromDateSelected.transformIntoDatePicker(
            requireContext(),
            "dd-MM-yyyy",
            null
        )
        binding.etToDateSelected.transformIntoDatePicker(requireContext(), "dd-MM-yyyy", null)
        //  binding.tvDate.transformIntoDatePicker(requireContext(), "dd/MM/yyyy", null)


        /*binding.tvDate.setOnClickListener {

        }*/

      //  binding.tvDate.text = Globals.getTodaysDate()

        /*binding.tvDate.setOnClickListener {

        }*/

        adapter = AllLeaveStatusAdapter(
            allLeaveStatusItemList,
            requireContext()
        )


        spinnerAdapter = CustomSpinnerAdapter(requireContext(), spinnerItems)
        binding.spinner.adapter = spinnerAdapter
        binding.spinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val selectedItem = spinnerItems[position]
                    leaveTypeStr = selectedItem.text
                    binding.etTypeSelected.text = leaveTypeStr
//                    Toast.makeText(requireContext(), "Selected item: ${selectedItem.text}", Toast.LENGTH_SHORT).show()
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // Do nothing
                }
            }

        leaveReasonStr = binding.etReason.text.toString()

        binding.rgLeave.setOnCheckedChangeListener { group, checkedId ->
            val radio: RadioButton = requireActivity().findViewById(checkedId)
            when (checkedId) {
                R.id.allRB -> {
                    Log.e(TAG, "intialStatus: ${allLeaveStatusItemList.toString()}")
                    adapter.updateEmployeeListItems(allLeaveStatusItemList)
                    if (allLeaveStatusItemList.isEmpty()) {
                        binding.noDataFound.visibility = View.VISIBLE
                    } else {
                        binding.noDataFound.visibility = View.GONE
                    }
                }

                R.id.approvedRB -> {

                    var filterList =
                        allLeaveStatusItemList!!.filter { it.Approval_Status == "Approved" }
                    adapter.updateEmployeeListItems(filterList)
                    if (filterList.isEmpty()) {
                        binding.noDataFound.visibility = View.VISIBLE
                    } else {
                        binding.noDataFound.visibility = View.GONE
                    }
                }

                R.id.pendingRB -> {
                    var filterList =
                        allLeaveStatusItemList!!.filter { it.Approval_Status == "Pending" }
                    adapter.updateEmployeeListItems(filterList)
                    if (filterList.isEmpty()) {
                        binding.noDataFound.visibility = View.VISIBLE
                    } else {
                        binding.noDataFound.visibility = View.GONE
                    }
                }

                R.id.rejecedRB -> {
                    var filterList =
                        allLeaveStatusItemList!!.filter { it.Approval_Status == "Rejected" }
                    adapter.updateEmployeeListItems(filterList)
                    if (filterList.isEmpty()) {
                        binding.noDataFound.visibility = View.VISIBLE
                    } else {
                        binding.noDataFound.visibility = View.GONE
                    }
                }
            }


        }
    }

    private fun clickedEvents() {
        binding.etTypeSelected.setOnClickListener { binding.spinner.performClick() }
        binding.tvAdd.setOnClickListener {
            if (booleanAddLeave) {
                booleanAddLeave = !booleanAddLeave
                binding.cardLeave.visibility = if (booleanAddLeave) View.GONE else View.VISIBLE
                binding.tvAdd.alpha = if (!booleanAddLeave) .5f else 1f
                binding.etReason.setText("")
                binding.etFromDateSelected.setText(
                    Globals.dateStringConvertToDesiredFormat(Globals.getTodaysDate() ?: "", "dd-MM-yyyy", "dd-MM-yyyy"))

                binding.spinner.setSelection(0)
                leaveTypeStr = "Sick Leave"
                binding.etTypeSelected.text = leaveTypeStr

            } else {

            }

        }
        binding.btnCancel.setOnClickListener {
            hideKeyboard()
            /*      booleanAddLeave = !booleanAddLeave
                  binding.cardLeave.visibility = if (booleanAddLeave) View.GONE else View.VISIBLE
                  binding.tvAdd.alpha = if (!booleanAddLeave || !booleanEditLeave) 1f else 1f
      //            binding.tvAdd.alpha = if (!booleanAddLeave || !booleanEditLeave) .5f else 1f
                  if (booleanEditLeave) {
                      binding.btnSave.text = "Save"
                      binding.cardLeave.visibility = View.GONE
                      binding.tvAdd.alpha = 1f
                      booleanEditLeave = false
                  }*/

            booleanEditLeave = false
            booleanAddLeave = true
            binding.cardLeave.visibility =
                View.GONE
            binding.tvAdd.alpha = 1f
            binding.btnSave.text = "Save"


        }
        adapter.setonEditBtnClickListener { leaveStatusData, i ->
            Log.e(TAG, "${leaveStatusData}")
            updateLeaveStatusData = leaveStatusData
            booleanEditLeave = true
            binding.cardLeave.visibility = View.VISIBLE
            binding.btnSave.text = "Update"
            binding.tvAdd.alpha = .5f
            fromDateStr = leaveStatusData.Leave_Date

            binding.etFromDateSelected.setText(
                Globals.convert_yyyy_MM_dd_into_dd_MM_yyyy(
                    fromDateStr
                )
            )



            binding.etReason.setText(leaveStatusData.Reason)

            if (leaveStatusData.Type == "Sick Leave") {

                binding.etTypeSelected.text = spinnerItems[0].text
                binding.spinner.setSelection(0)
            } else {
                binding.etTypeSelected.text = spinnerItems[1].text
                binding.spinner.setSelection(1)
            }


        }

        binding.btnSave.setOnClickListener {
            //Todo call api for new leave

            fromDateStr = Globals.dateStringConvertToDesiredFormat(
                binding.etFromDateSelected.text.toString(),
                "dd-MM-yyyy",
                "yyyy-MM-dd"
            ).toString()

            hideKeyboard()
            Log.e(
                TAG,
                "onFailure: ${
                    binding.etReason.text.toString().isNotEmpty()
                } ${leaveTypeStr.isNotEmpty()} ${fromDateStr}"
            )
            if (fromDateStr.isNotEmpty()) {
                if (binding.etReason.text.toString().isNotEmpty()) {
                    if (leaveTypeStr.isNotEmpty()) {
                        if (booleanEditLeave) {
                            callEditLeave()
                        } else {
                            apiNewLeaveRequest()
                        }
                    } else
                        Toast.makeText(
                            requireContext(),
                            "Please select leave type",
                            Toast.LENGTH_SHORT
                        ).show()
                } else {
                    Toast.makeText(requireContext(), "Please enter reason", Toast.LENGTH_SHORT)
                        .show()
                }

            } else {
                Toast.makeText(requireContext(), "Please select date", Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun callEditLeave() {
        binding.progressBar.visibility = View.VISIBLE

        val hde = JsonObject().apply {
            addProperty("id", updateLeaveStatusData.id)
            addProperty("SalesEmployeeCode", Globals.SalesEmployeeCode)
            addProperty("Type", leaveTypeStr)
            addProperty("Leave_Date", fromDateStr)
            addProperty("Reason", binding.etReason.text.toString())
            addProperty("Update_Date", Globals.getTodaysDatervrsfrmt())
            addProperty("Update_Time", Globals.getTCurrentTime())
        }
        val call = callAPiSingleInstance!!.galaxyUpdateLeaveRequest(hde)
        call.enqueue(object : Callback<ResponseGlobal> {
            override fun onResponse(
                call: Call<ResponseGlobal>,
                response: Response<ResponseGlobal>
            ) {
                binding.progressBar.visibility = View.GONE
                response.body()?.let {

                    if (it.status == 200) {
                        booleanEditLeave = false
                        booleanAddLeave = true
                        binding.cardLeave.visibility =
                            View.GONE
                        binding.tvAdd.alpha = 1f
                        binding.btnSave.text = "Save"
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                        apiAllLeaveStatusCall()

                    } else if (it.status == 201) {
                        booleanEditLeave = false
                        booleanAddLeave = true
                        binding.cardLeave.visibility =
                            View.GONE
                        binding.tvAdd.alpha = 1f
                        binding.btnSave.text = "Save"
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                        apiAllLeaveStatusCall()
                    }
                }
            }

            override fun onFailure(call: Call<ResponseGlobal>, t: Throwable) {
                Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_SHORT).show()
                Log.e(TAG, "onFailure: ${t.message}")
                binding.progressBar.visibility = View.GONE
            }
        })
    }

    private fun apiCalling() {
        apiAllLeaveStatusCall()
    }

    private fun apiNewLeaveRequest() {
        binding.progressBar.bringToFront()
        binding.progressBar.visibility = View.VISIBLE

        val hde = JsonObject().apply {
            addProperty("SalesEmployeeCode", Globals.SalesEmployeeCode)
            addProperty(
                "Type",
                leaveTypeStr
            )//todo remove .removeSuffix("Leave").trim().toLowerCase(Locale.ROOT)
            addProperty("Leave_Date", fromDateStr)
            addProperty("Reason", binding.etReason.text!!.trim().toString())
            addProperty("Create_Date", Globals.getTodaysDatervrsfrmt())
            addProperty("Create_Time", Globals.getTCurrentTime())
        }
        val call = callAPiSingleInstance!!.galaxyNewLeaveRequest(hde)
        call.enqueue(object : Callback<ResponseGlobal> {
            override fun onResponse(
                call: Call<ResponseGlobal>,
                response: Response<ResponseGlobal>
            ) {
                binding.progressBar.visibility = View.GONE
                response.body()?.let {

                    if (it.status == 200) {
                        booleanAddLeave = true
                        //todo by shubh
                        //   booleanAddLeave = !booleanAddLeave
                        binding.cardLeave.visibility = View.GONE
                        binding.tvAdd.alpha = 1f

                        apiAllLeaveStatusCall()
                    } else if (it.status == 201) {
                        try {
                            Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                        } catch (e: Exception) {
                        }
                    }
                }
            }

            override fun onFailure(call: Call<ResponseGlobal>, t: Throwable) {
                Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_SHORT).show()
                Log.e(TAG, "onFailure: ${t.message}")
                binding.progressBar.visibility = View.GONE
            }
        })
    }

    // var dateString: String = ""

    private fun apiAllLeaveStatusCall() {
        binding.progressBar.visibility = View.VISIBLE
        val hde = JsonObject().apply {
            addProperty("SalesEmployeeCode", Globals.SalesEmployeeCode)
            addProperty("Status", "")
            addProperty("Datefilter", dateString)
            addProperty("ToDate", toDateString)
            addProperty("FromDate", fromDateString)
        }
        val call = callAPiSingleInstance!!.galaxyAllLeaveStatusList(hde)

        call.enqueue(object : Callback<AllLeaveModel> {
            override fun onResponse(
                call: Call<AllLeaveModel>,
                response: Response<AllLeaveModel>
            ) {
                binding.progressBar.visibility = View.GONE
                response.body()?.let {

                    if (it.status == 200) {
                        allLeaveStatusItemList = it.data as ArrayList<LeaveStatusData>
                        if (allLeaveStatusItemList.isNotEmpty()){
                            binding.noDataFound.visibility=View.GONE
                        }else{
                            binding.noDataFound.visibility=View.VISIBLE
                        }
                        val dividerItemDecoration = DividerItemDecoration(
                            binding.rvLeave.context,
                            (binding.rvLeave.layoutManager as LinearLayoutManager).orientation
                        )
                        val drawable: Drawable? =
                            ContextCompat.getDrawable(requireContext(), R.drawable.item_spacing)
                        binding.rvLeave.addItemDecoration(dividerItemDecoration)
                        drawable?.let { it1 -> dividerItemDecoration.setDrawable(it1) }

                        binding.rvLeave.adapter = adapter
                        adapter.updateEmployeeListItems(allLeaveStatusItemList)
                    }
                }
            }

            override fun onFailure(call: Call<AllLeaveModel>, t: Throwable) {
                Log.e(TAG, "onFailure: ${t.message}")
                Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_SHORT).show()
                binding.progressBar.visibility = View.GONE
            }
        })
    }


}