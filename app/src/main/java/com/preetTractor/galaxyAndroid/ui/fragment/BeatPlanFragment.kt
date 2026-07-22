package com.preetTractor.galaxyAndroid.ui.fragment

import Event
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout
import com.google.gson.JsonObject
import com.pixplicity.easyprefs.library.Prefs
import com.preetTractor.galaxyAndroid.R
import com.preetTractor.galaxyAndroid.data.ResponseGlobal
import com.preetTractor.galaxyAndroid.data.beatplan.DataBeatPlan
import com.preetTractor.galaxyAndroid.databinding.DialogLeaveStatusBinding
import com.preetTractor.galaxyAndroid.databinding.DialogRescheduleBeatPlanBinding
import com.preetTractor.galaxyAndroid.databinding.FragmentBeatPlanBinding
import com.preetTractor.galaxyAndroid.helper.Constant
import com.preetTractor.galaxyAndroid.helper.Globals
import com.preetTractor.galaxyAndroid.helper.Globals.transformIntoDatePicker
import com.preetTractor.galaxyAndroid.helper.Globals.transformIntoDatePickerWithDisablePastDates
import com.preetTractor.galaxyAndroid.helper.Globals.transformIntoTimePicker
import com.preetTractor.galaxyAndroid.helper.PrefsByShubh
import com.preetTractor.galaxyAndroid.mvvmSetUp.MainViewModel
import com.preetTractor.galaxyAndroid.retrofit.RetrofitClient
import com.preetTractor.galaxyAndroid.ui.activity.AddLeadBeatPlanActivity
import com.preetTractor.galaxyAndroid.ui.activity.AddNonCustomerActivity
import com.preetTractor.galaxyAndroid.ui.activity.BeatPlanActivity
import com.preetTractor.galaxyAndroid.ui.activity.BeatPlanActivity2
import com.preetTractor.galaxyAndroid.ui.activity.beat.AddBeatPlanActivity
import com.preetTractor.galaxyAndroid.ui.recyclerview.BeatPlanListingAdapter
import com.preetTractor.galaxyAndroid.viewmodel.FragmentRefreshPage
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class BeatPlanFragment : Fragment(), FragmentRefreshPage {

    lateinit var binding: FragmentBeatPlanBinding
    private lateinit var adapter: BeatPlanListingAdapter
    lateinit var viewModel: MainViewModel
    var filterPriority = ""
    var currentPage = "today"
    var currentSelectedDate = Globals.getTodaysDatervrsfrmt()

    var salesEmployeeCode = ""
    var fromWhere = ""
    lateinit var dialog: Dialog


    companion object {
        private const val TAG = "BeatPlanFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentBeatPlanBinding.inflate(layoutInflater)
        viewModel = (activity as BeatPlanActivity).viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        if (requireActivity().intent!!.hasExtra(Constant.WHERE_INTENT_VALUE_SALES)) {
            salesEmployeeCode =
                requireActivity().intent!!.getStringExtra(Constant.WHERE_INTENT_VALUE_SALES)
                    .toString()
            fromWhere = "approval"

            Log.e(TAG, "onViewCreated: APPROVAL")
        } else {
            salesEmployeeCode = PrefsByShubh.getSalesEmployeeCode().toString()
            fromWhere = "listing"
            Log.e(TAG, "onViewCreated: LISTING")
        }
        dialog = Dialog(requireActivity())


        setUpTabLayout()
        callBeatPlanListingApi()
        callBusinessPartnerApi()
        callEmployeeListApi()
        setUpObserver()
        setUpListeners()

    }

    private fun setUpListeners() {
        //  binding.tvDate.text = Globals.getTodaysDate()
        /*  binding.tvDate.transformIntoDatePicker(
              requireContext(),
              "yyyy-MM-dd",
              null
          ) { selecteddate ->
              currentSelectedDate = selecteddate
              callBeatPlanListingApi()
          }*/

        binding.tvDate.text = Globals.getTodaysDatePlusOne()

        binding.tvDate.transformIntoDatePicker(
            context = requireContext(),
            displayFormat = "dd-MM-yyyy",
            apiFormat = "yyyy-MM-dd",
            maxDate = null,
            onDateSelected = { displayDate, apiDate ->
                println("Display Date: $displayDate")
                println("API Date: $apiDate")
                currentSelectedDate = apiDate
                callBeatPlanListingApi()
                // Use the `apiDate` for your API request
            })

        binding.extendedFab.setOnClickListener {
//            findNavController().navigate(R.id.fragmentB, null) //todo comment

            showCustomerSelectDialog()
//            val intent = Intent(requireActivity(), AddBeatPlanActivity::class.java)
//            startActivity(intent)
        }
        binding.filterIcon.setOnClickListener {
            openPopUpMenu()
        }
    }

    override fun onRefresh() {
        Log.e("TAG", "onRefresh: ")
        callBeatPlanListingApi()
        setUpObserver()
    }

    private fun openPopUpMenu() {
        // Initializing the popup menu and giving the reference as current context
        val popupMenu = PopupMenu(requireContext(), binding.filterIcon)
        popupMenu.menuInflater.inflate(R.menu.priority_popup, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { menuItem ->
            filterPriority = menuItem.title.toString()
            callBeatPlanListingApi()
            true
        }
        // Showing the popup menu
        popupMenu.show()
    }

    private fun showCustomerSelectDialog() {
        val dialogView =
            LayoutInflater.from(context).inflate(R.layout.dialog_customer_selection, null)
        val dialog = AlertDialog.Builder(context).setView(dialogView).setCancelable(false).create()

        val radioGroup = dialogView.findViewById<RadioGroup>(R.id.radioGroup)
        val submitButton = dialogView.findViewById<Button>(R.id.btnSubmit)
        val cancelButton = dialogView.findViewById<Button>(R.id.btnCancel)

        submitButton.setOnClickListener {
            val selectedRadioButtonId = radioGroup.checkedRadioButtonId

            if (selectedRadioButtonId == R.id.radioCustomer) {
                val intent = Intent(requireActivity(), AddBeatPlanActivity::class.java)
                startActivity(intent)
                dialog.dismiss()

            } else if (selectedRadioButtonId == R.id.radioNonCustomer) {
                val intent = Intent(requireActivity(), AddNonCustomerActivity::class.java)
                startActivity(intent)
                dialog.dismiss()

            } else if (selectedRadioButtonId == R.id.radioLead) {

                val intent = Intent(requireActivity(), AddLeadBeatPlanActivity::class.java)
                startActivity(intent)
                dialog.dismiss()

            } else {
                Toast.makeText(requireContext(), "Please select an option", Toast.LENGTH_SHORT)
                    .show()
            }


        }

        cancelButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun callEmployeeListApi() {
        binding.spinKitLoader.isVisible = true
        if (Globals.checkForInternet(requireContext())) {
            val jsonObject = JsonObject().apply {
                addProperty("SalesEmployeeCode", PrefsByShubh.getEmpCode())
            }
            viewModel.getListingOfTeamUser(jsonObject, requireContext())
        }
    }


    private fun callBusinessPartnerApi() {
        binding.spinKitLoader.isVisible = true
        if (Globals.checkForInternet(requireContext())) {
            val jsonObject = JsonObject().apply {
                addProperty("SalesEmployeeCode", PrefsByShubh.getEmpCode())
            }
            viewModel.getBusinessPartnerApi(jsonObject, requireContext())
        }
    }

    private fun callBeatPlanListingApi() {
        binding.spinKitLoader.isVisible = true
        if (Globals.checkForInternet(requireContext())) {
            val jsonObject = JsonObject().apply {
                addProperty("SalesEmployeeCode", salesEmployeeCode)
                addProperty("Status", currentPage)
                addProperty("Latitude", Globals.globalLatitude)
                addProperty("Longitude", Globals.globalLongitude)
                addProperty("Date", currentSelectedDate)
                addProperty("Priority", filterPriority)
            }

            viewModel.getBeatPlanListing(jsonObject, requireContext())
        }
    }

    private fun setUpObserver() {
        binding.noDataFoundLayout.ivNoDataFound.visibility = View.GONE
        viewModel.beatPlanItemAllList.observe(requireActivity(), Event.EventObserver(onError = {
            binding.spinKitLoader.isVisible = false
            binding.noDataFoundLayout.ivNoDataFound.visibility = View.VISIBLE
        }, onLoading = {
            binding.spinKitLoader.isVisible=true
        }, onSuccess = { response ->
            binding.spinKitLoader.isVisible = false
            if (response.data.isEmpty()) {
                binding.noDataFoundLayout.ivNoDataFound.visibility = View.VISIBLE
                setUpRecyclerview(response.data)
            } else {
                binding.noDataFoundLayout.ivNoDataFound.visibility = View.GONE
                setUpRecyclerview(response.data)
            }
        }))


    }


    private fun setUpRecyclerview(data: List<DataBeatPlan>) {

        Log.d("nksjd", "setUpRecyclerview:12 $fromWhere")

        adapter = BeatPlanListingAdapter(data, requireContext(), fromWhere)
        binding.rvBeatPlan.adapter = adapter


        // todo add by Tarun Sharma
        adapter.setOnItemWholeClickListener { item ->
                val intent = Intent(requireContext(), BeatPlanActivity2::class.java)
                intent.putExtra("fromWhere", fromWhere)
                intent.putExtra("id", item.id.toString())
                intent.putExtra("Type", item.Type.toString())
                intent.putExtra("salesEmployeeCode", salesEmployeeCode)
                intent.putExtra("currentPage", currentPage)
                intent.putExtra("currentSelectedDate", currentSelectedDate)
                intent.putExtra("filterPriority", filterPriority)
                startActivity(intent)


        }


        adapter.setOnItemRescheduleClickListener { item ->
            if (item.Approval_Status.equals("Pending", ignoreCase = true)) {
                openRescheduleDialog(item)
            } else {
                Globals.warningMessage(requireContext(), "Cannot reschedule")
            }

        }


        adapter.setOnItemApprovalClickListener { leaveStatusData ->
            Log.d("check", "setUpRecyclerview: " + leaveStatusData.AssignedTo)
            Log.d("check", "setUpRecyclerview: " + PrefsByShubh.getSalesEmployeeCode())
            Log.d("check", "setUpRecyclerview: " + Prefs.getString(Globals.role_name))

            if (PrefsByShubh.getString("role", "").equals("Admin")) {
                showStatusDialog(leaveStatusData)
            } else if (leaveStatusData.AssignedTo == PrefsByShubh.getSalesEmployeeCode()) {

                Globals.warningMessage(requireActivity(), "Not Authorized")
            } else {
                showStatusDialog(leaveStatusData)
            }

        }



        adapter.notifyDataSetChanged()
    }


    private fun showStatusDialog(leaveStatusData: DataBeatPlan) {

        val binding = DialogLeaveStatusBinding.inflate(LayoutInflater.from(requireActivity()))

        dialog.setContentView(binding.root)

        // todo Set the dialog window to be larger
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )

        // Set up the spinner with an adapter
        binding.spinnerStatus.apply {
            adapter = ArrayAdapter(
                context,
                R.layout.drop_down_item_textview,
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
        binding.spinKitLoader.visibility = View.VISIBLE/* {
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
                call: Call<ResponseGlobal>, response: Response<ResponseGlobal>
            ) {
                binding.spinKitLoader.visibility = View.GONE
                response.body()?.let {
                    dialog.dismiss()

                    when (it.status) {
                        200 -> {
                            Globals.successMessage(requireActivity(), "Updated SuccessFully")
                            callBeatPlanListingApi()
                            setUpObserver()
                        }

                        201 -> {
                            Globals.successMessage(requireActivity(), it.message)
                        }

                        else -> {
                            Globals.successMessage(requireActivity(), response.message())
                        }
                    }
                }
            }

            override fun onFailure(call: Call<ResponseGlobal>, t: Throwable) {
                Log.e(TAG, "onFailure: ${t.message}")
                Toast.makeText(requireActivity(), "Something went wrong", Toast.LENGTH_SHORT).show()
                binding.spinKitLoader.visibility = View.GONE
            }
        })
    }

    lateinit var alertBinding: DialogRescheduleBeatPlanBinding
    lateinit var mAlert: AlertDialog

    var selectedTiming = ""
    var selectedPriority = ""
    private fun openRescheduleDialog(item: DataBeatPlan) {
        alertBinding = DialogRescheduleBeatPlanBinding.inflate(layoutInflater)

        val builder = AlertDialog.Builder(requireContext())

        mAlert = builder.setView(alertBinding.root).setCancelable(true).create()
        mAlert.window?.setBackgroundDrawable(
            ContextCompat.getDrawable(
                requireContext(), R.drawable.alert_bg
            )
        )

        mAlert.show()

        alertBinding.apply {
            btnCancel.setOnClickListener {
                mAlert.dismiss()
            }

            btnClose.setOnClickListener {
                mAlert.dismiss()
            }

            etRescheduleDate.setText(Globals.convert_yyyy_MM_dd_into_dd_MM_yyyy(item.Visit_Date))
            edtTiming.setText(item.Shop_StartAt)
            etRescheduleDate.transformIntoDatePickerWithDisablePastDates(
                etRescheduleDate.context,
                "dd-MM-yyyy",
                null
            )
            edtTiming.transformIntoTimePicker(requireContext(), "hh:mm a")
            // setupSpinner(alertBinding.spinnerTiming, R.array.timing_array)
            setupSpinner(alertBinding.spinnerPriority, R.array.priority_array)


            /*       when (item.Purpose) {
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
                   }*/

            /*     when (item.Priority) {
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
                 }*/

            spinnerTiming.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?, view: View?, position: Int, id: Long
                ) {

                    selectedTiming = parent?.getItemAtPosition(position).toString()

                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

            spinnerPriority.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?, view: View?, position: Int, id: Long
                ) {
                    selectedPriority = parent?.getItemAtPosition(position).toString()

                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

            btnSave.setOnClickListener {
                btnSave.alpha = 0.3f
                btnSave.isClickable = false
                rescheduleBeatPlan(item)

            }


        }


    }


    private fun rescheduleBeatPlan(item: DataBeatPlan) {/*   {
               "id": "35",
               "SalesEmployeeCode": "",
               "Visit_Status": "",
               "Visit_Date": "",
               "Update_Date": "",
               "Update_Time": ""
           }*/


        val hde = JsonObject()
        hde.addProperty("SalesEmployeeCode", Globals.SalesEmployeeCode)
        hde.addProperty("id", item.id)/*     hde.addProperty("Priority", selectedPriority)
        hde.addProperty("Purpose", selectedTiming)
        hde.addProperty("Visit_Status", item.Visit_Status.toString())*/
        hde.addProperty("Approval_Status", item.Approval_Status.toString())
        hde.addProperty(
            "Visit_Date",
            Globals.convert_dd_MM_yyyy_into_yyyy_MM_dd(alertBinding.etRescheduleDate.text.toString())
        )
        hde.addProperty("Update_Date", Globals.getTodaysDatervrsfrmt())
        hde.addProperty("Update_Time", Globals.getTCurrentTime())/* hde.addProperty("Shop_StartAt", alertBinding.edtTiming.text.toString())
        hde.addProperty("Shop_CloseAt", item.Shop_CloseAt)*/

        val call: Call<ResponseGlobal> = RetrofitClient.apiService.rescheduleBeatPlan(hde)
        call.enqueue(object : Callback<ResponseGlobal> {
            override fun onResponse(
                call: Call<ResponseGlobal>, response: Response<ResponseGlobal>
            ) {
                if (response != null) {
                    if (response.body()!!.status == 200) {
                        alertBinding.btnSave.alpha = 0.3f
                        alertBinding.btnSave.isClickable = true
                        mAlert.dismiss()

                        Globals.successMessage(requireActivity(), "Reschedule Successfully")
                        callBeatPlanListingApi()
                        Log.e(
                            TAG, "onResponseBackground: " + response.body()!!.message
                        )
                    } else if (response.body()!!.status == 201) {
                        Globals.warningMessage(requireActivity(), response.message())
                        alertBinding.btnSave.alpha = 0.3f
                        alertBinding.btnSave.isClickable = true
                    }
                }
            }

            override fun onFailure(call: Call<ResponseGlobal>, t: Throwable) {
                // binding.loaderLayout.loader.setVisibility(View.GONE);
                Log.e(TAG, "onFailure: " + t.message)
            }
        })
    }

    private fun setupSpinner(spinner: Spinner, arrayResId: Int) {
        ArrayAdapter.createFromResource(
            spinner.context, arrayResId, android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }
    }

    private fun setUpTabLayout() {
        binding.tvDate.text = currentSelectedDate
        binding.tabLayout.addTab(
            binding.tabLayout.newTab().setText(resources.getString(R.string.todays))
        )
        binding.tabLayout.addTab(
            binding.tabLayout.newTab().setText(resources.getString(R.string.upcoming))
        )
        binding.tabLayout.addTab(
            binding.tabLayout.newTab().setText(resources.getString(R.string.missed))
        )
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                setUpRecyclerviewItem(tab.position)

            }

            override fun onTabUnselected(tab: TabLayout.Tab) {

            }

            override fun onTabReselected(tab: TabLayout.Tab) {

            }
        })
    }

    private fun setUpRecyclerviewItem(position: Int) {
        when (position) {
            0 -> {
                currentPage = "today"
                currentSelectedDate = Globals.getTodaysDatervrsfrmt()
                binding.tvDate.visibility = View.GONE
            }

            1 -> {
                currentPage = "upcomming"
                currentSelectedDate =
                    Globals.convert_dd_MM_yyyy_into_yyyy_MM_dd(binding.tvDate.text.toString())
                binding.tvDate.visibility = View.VISIBLE
            }

            2 -> {
                currentPage = "missed"
                currentSelectedDate = ""
//                    Globals.convert_dd_MM_yyyy_into_yyyy_MM_dd(binding.tvDate.text.toString())
                binding.tvDate.visibility = View.VISIBLE
            }
        }
        callBeatPlanListingApi()
    }


}