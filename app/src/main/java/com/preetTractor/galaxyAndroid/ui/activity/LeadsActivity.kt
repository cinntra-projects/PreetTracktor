package com.preetTractor.galaxyAndroid.ui.activity

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.preetTractor.galaxyAndroid.R
import com.preetTractor.galaxyAndroid.adapter.LeadsAdapter
import com.preetTractor.galaxyAndroid.apiHelper.UiState
import com.preetTractor.galaxyAndroid.data.FieldFilter
import com.preetTractor.galaxyAndroid.data.FilterOverAll
import com.preetTractor.galaxyAndroid.data.FollowUpData
import com.preetTractor.galaxyAndroid.data.LeadSourceAllResponseModel
import com.preetTractor.galaxyAndroid.data.LeadValue
import com.preetTractor.galaxyAndroid.databinding.FollowupDialogBinding
import com.preetTractor.galaxyAndroid.databinding.FragmentLeadBinding
import com.preetTractor.galaxyAndroid.databinding.LeadFilterLayoutBinding
import com.preetTractor.galaxyAndroid.helper.Globals
import com.preetTractor.galaxyAndroid.helper.Globals.transformIntoDatePicker
import com.preetTractor.galaxyAndroid.helper.Globals.transformIntoDatePickerWithDisablePastDates
import com.preetTractor.galaxyAndroid.helper.Globals.transformIntoTimePicker
import com.preetTractor.galaxyAndroid.helper.PrefsByShubh
import com.preetTractor.galaxyAndroid.mvvmSetUp.DefaultMainRepositories
import com.preetTractor.galaxyAndroid.retrofit.RetrofitClient
import com.preetTractor.galaxyAndroid.ui.activity.customermodule.model.SalesEmployee
import com.preetTractor.galaxyAndroid.ui.activity.customermodule.model.SalesEmployeeItemKt
import com.preetTractor.galaxyAndroid.ui.bottomsheet.LeadCreateUpdateBottomSheet
import com.preetTractor.galaxyAndroid.ui.bottomsheet.LeadOptionsBottomSheet
import com.preetTractor.galaxyAndroid.ui.fragment.LeadDetail
import com.preetTractor.galaxyAndroid.ui.fragment.LeadInformation
import com.preetTractor.galaxyAndroid.viewmodel.LeadViewModel
import com.preetTractor.galaxyAndroid.viewmodel.LeadViewModelFactory
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LeadsActivity : BaseActivity(), LeadDetail.OnLeadUpdatedListener{


    private lateinit var binding: FragmentLeadBinding
    private lateinit var viewModel: LeadViewModel
    private var currentPage = 1
    private var isLoading = false
    private var isLastPage = false
    private var searchQuery = ""
    private var searchJob: Job? = null
    private lateinit var leadAdapter: LeadsAdapter
    private var filterField = FieldFilter()
    private var salesEmployeeItemList: List<SalesEmployeeItemKt> = java.util.ArrayList<SalesEmployeeItemKt>()
    private val employeeNames = mutableListOf<String>()
    val leadSourceAll: MutableList<String> = mutableListOf()

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = FragmentLeadBinding.inflate(layoutInflater)
        setContentView(binding.getRoot())
        val repository = DefaultMainRepositories()

        viewModel = ViewModelProvider(
            this, LeadViewModelFactory(repository)
        )[LeadViewModel::class.java]

        supportActionBar?.apply {
            title = "Leads"
            setDisplayHomeAsUpEnabled(true)
            setHomeButtonEnabled(true)
        }

        loadLeads()
        callSalessApi()
        setUpLeadSource()
        observestate()
        binding.swipeRefreshLayout.setOnRefreshListener(object : OnRefreshListener {
            override fun onRefresh() {
                currentPage = 1
                isLastPage = false
                leadAdapter.clearData()
                loadLeads()
            }
        })

        val layoutManager = LinearLayoutManager(this)
        binding.leadRecyclerViewList.layoutManager = layoutManager
        leadAdapter = LeadsAdapter(arrayListOf(), onOptionClick = { lead ->
            openOptionBottomSheet(lead)
        },
            onLeadClick = { lead->
                val b = Bundle()
                b.putParcelable(Globals.LeadDetails, lead)
                b.putString("From", "Lead")
                val fragment = LeadInformation(this)
                fragment.setArguments(b)
                val transaction =
                    (this as FragmentActivity).supportFragmentManager.beginTransaction()
                transaction.add(R.id.customer_lead, fragment).addToBackStack(null)
                transaction.commit()

            },
            onFollowUpClick = {lead->
                openFollowUpDialog(lead)
            },
            onUpdateClick = { lead ->
            openCreateUpdateBottomSheet(lead)
        })

        binding.leadRecyclerViewList.adapter = leadAdapter
        binding.leadRecyclerViewList.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(
                recyclerView: RecyclerView, dx: Int, dy: Int
            ) {
                super.onScrolled(recyclerView, dx, dy)

                if (dy <= 0) return

                val visibleItemCount = layoutManager.childCount

                val totalItemCount = layoutManager.itemCount

                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                val shouldLoadMore =
                    !isLoading && !isLastPage && (visibleItemCount + firstVisibleItemPosition >= totalItemCount - 5)

                if (shouldLoadMore) {

                    currentPage++

                    loadLeads()
                }
            }
        })

    }

        fun openFollowUpDialog(value: LeadValue) {
            val dialog = Dialog(this)

            val dialogBinding =
                FollowupDialogBinding.inflate(layoutInflater)

            dialog.setContentView(dialogBinding.root)
            dialog.setCanceledOnTouchOutside(false)
            dialog.window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

            dialog.window?.setBackgroundDrawable(
                ColorDrawable(Color.TRANSPARENT)
            )

            dialog.show()
            val communicationList = listOf(
                "Call",
                "SMS",
                "Email",
                "WhatsApp",
                "Visit"
            )

            val adapter = ArrayAdapter(
                this,
                android.R.layout.simple_dropdown_item_1line,
                communicationList
            )

            dialogBinding.communicationSpinner.setAdapter(adapter)
            dialogBinding.dateValue.transformIntoDatePickerWithDisablePastDates(this, "yyyy-MM-dd")
            dialogBinding.timeValue.transformIntoTimePicker(this, "HH:mm")
            dialogBinding.radioGroup.setOnCheckedChangeListener { _, checkedId ->

                when (checkedId) {

                    R.id.rbFollowUp -> {

                        dialogBinding.reminderView.visibility =
                            View.GONE
                    }

                    R.id.rbReminder -> {

                        dialogBinding.reminderView.visibility =
                            View.VISIBLE
                    }
                }
            }


            dialogBinding.cross.setOnClickListener {
                dialog.dismiss()
            }

            dialogBinding.add.setOnClickListener {

                if (dialogBinding.rbReminder.isChecked) {

                    if (dialogBinding.dateValue.text.isNullOrBlank()) {

                        dialogBinding.dateValue.error = "Select Date"
                        return@setOnClickListener
                    }

                    if (dialogBinding.timeValue.text.isNullOrBlank()) {

                        dialogBinding.timeValue.error = "Select Time"
                        return@setOnClickListener
                    }
                }

                if (dialogBinding.communicationSpinner.text.isNullOrBlank()) {

                    Toast.makeText(
                        this,
                        "Select communication mode",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@setOnClickListener
                }

                if (dialogBinding.commentValue.text.isNullOrBlank()) {

                    dialogBinding.commentValue.error = "Enter comment"

                    return@setOnClickListener
                }
                val followUpData = FollowUpData()
                followUpData.apply {
                    Subject = value.companyName
                    SourceID = value.id.toString()
                    SourceType = "Lead"
                    Comment = dialogBinding.commentValue.text.toString()
                    From = dialogBinding.dateValue.text.toString()
                    Time = dialogBinding.timeValue.text.toString()
                    Mode = dialogBinding.communicationSpinner.text.toString()
                    Type = "Followup"
                    CreateDate = Globals.getTodaysDatervrsfrmt() // Setting creation date
                    CreateTime = Globals.getTCurrentTime() // Setting creation time
                    Emp = PrefsByShubh.getEmpCode()?.toInt()
                    Emp_Name = PrefsByShubh.getEmpName()
                }

                // Call the follow-up API
                callFollowupApiMethod(followUpData)
                dialog.dismiss()

            }
        }

    private fun callFollowupApiMethod(data: FollowUpData) {
        viewModel.callFollowUpApi(this@LeadsActivity,data)
    }


    private fun showAllFilterDialog() {
        val dialog = Dialog(this)

        val dialogBinding = LeadFilterLayoutBinding.inflate(layoutInflater)

        dialog.setContentView(dialogBinding.root)

        dialog.window?.apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
            setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT
            )
        }


        // Set Previous Values
        if(filterField.source__in!=null)
            dialogBinding.acSource.setText(filterField.source__in?.get(0).toString())
        dialogBinding.acAssignedTo.setText(
            salesEmployeeItemList.find { it.id.toString() == filterField.assignedTo_id__in?.get(0) }?.salesEmployeeName)
        dialogBinding.edtFromDate.setText(filterField.CreateDate__gte)
        dialogBinding.edtToDate.setText(filterField.CreateDate__lte)

        val sourceAdapter = ArrayAdapter(
            this,
            R.layout.drop_down_textview,
            leadSourceAll
        )

        dialogBinding.acSource.setAdapter(sourceAdapter)

        val employeeAdapter = ArrayAdapter(
            this,
            R.layout.drop_down_textview,
            employeeNames
        )

        dialogBinding.acAssignedTo.setAdapter(employeeAdapter)

        dialogBinding.ivCrossIcon.setOnClickListener {
            dialog.dismiss()
        }

        dialogBinding.edtFromDate.transformIntoDatePicker(dialog.context,"dd-MM-yyyy")
        dialogBinding.edtToDate.setOnClickListener {
            Globals.enableAllCalenderDateSelect(
                this,
                dialogBinding.edtToDate
            )
        }


        dialogBinding.resetBtn.setOnClickListener {
            filterField = FieldFilter()
            currentPage = 1
            isLastPage = false
            loadLeads()
            dialog.dismiss()
        }

        dialogBinding.applyBtn.setOnClickListener {

            filterField.source__in = ArrayList<String?>().apply {
                add(dialogBinding.acSource.text.toString())
            }

            val selectedEmployee = salesEmployeeItemList.find {
                it.salesEmployeeName == dialogBinding.acAssignedTo.text.toString()
            }

            filterField.assignedTo_id__in = arrayListOf(selectedEmployee?.id.toString())
            if(dialogBinding.edtFromDate.text?.isNotEmpty() == true )
                filterField.CreateDate__gte = Globals.convert_dd_MM_yyyy_into_yyyy_MM_dd(dialogBinding.edtFromDate.text.toString())
            if(dialogBinding.edtFromDate.text?.isNotEmpty() == true )
                filterField.CreateDate__lte = Globals.convert_dd_MM_yyyy_into_yyyy_MM_dd(dialogBinding.edtToDate.text.toString())
            currentPage = 1
            isLastPage = false
            loadLeads()
            dialog.dismiss()
        }

        dialog.show()
    }






    private fun openOptionBottomSheet(
        lead: LeadValue
    ) {

        LeadOptionsBottomSheet(lead).show(
            supportFragmentManager, "LeadOptionsBottomSheet"
        )
    }

    private fun openCreateUpdateBottomSheet(
        lead: LeadValue
    ) {

        LeadCreateUpdateBottomSheet(
            lead,
            onOptionClick = {

                val b = Bundle()
                b.putParcelable(Globals.LeadDetails, lead)
                b.putString("From", "Lead")
                val fragment = LeadDetail(this)
                fragment.setArguments(b)
                val transaction =
                    (this as FragmentActivity).supportFragmentManager.beginTransaction()
                transaction.add(R.id.customer_lead, fragment).addToBackStack(null)
                transaction.commit()
            }
        ).show(
            supportFragmentManager, "LeadCreateUpdateBottomSheet"
        )
    }

    fun observestate() {
        lifecycleScope.launch {

            repeatOnLifecycle(
                Lifecycle.State.STARTED
            ) {

                viewModel.leadState.collect { state ->

                    when (state) {

                        is UiState.Loading -> {
                            isLoading = true

                            if (currentPage == 1) {
                                showLoading()
                            }
                        }

                        is UiState.Success -> {

                            isLoading = false

                            binding.swipeRefreshLayout.isRefreshing = false

                            showData()

                            val leadList = state.leads

                            if (leadList.size < 30) {
                                isLastPage = true
                            }

                            if (currentPage == 1) {

                                leadAdapter.setData(leadList)

                            } else {

                                leadAdapter.addData(leadList)

                            }
                        }

                        is UiState.Empty -> {

                            isLoading = false

                            binding.swipeRefreshLayout.isRefreshing = false

                            if (currentPage == 1) {
                                leadAdapter.clearData()
                                showEmpty()
                            } else {
                                isLastPage = true
                            }
                        }

                        is UiState.Error -> {

                            isLoading = false

                            binding.swipeRefreshLayout.isRefreshing = false

                            if (currentPage == 1) {
                                showEmpty()
                            }

                            Toast.makeText(
                                this@LeadsActivity, state.message, Toast.LENGTH_SHORT
                            ).show()
                        }

                        is UiState.Idle -> Unit
                    }
                }
            }
        }


    }

    private fun loadLeads() {

        if (isLoading) return

        val filter = FilterOverAll(
            SalesPersonCode = PrefsByShubh.getEmpCode(),
            maxItem = 30,
            PageNo = currentPage,
            order_by_field = "id",
            order_by_value = "desc",
            leadType = "lead",
            SearchText = searchQuery,
            field = filterField
        )

        viewModel.getAllLeads(
            filter, this
        )
    }

    private fun showLoading() {
        binding.shimmerLead.visibility = View.VISIBLE
        binding.swipeRefreshLayout.visibility = View.GONE
        binding.noDataLayout.visibility = View.GONE

        binding.shimmerLead.startShimmer()
    }

    private fun showData() {
        binding.shimmerLead.stopShimmer()
        binding.shimmerLead.visibility = View.GONE

        binding.swipeRefreshLayout.visibility = View.VISIBLE
        binding.noDataLayout.visibility = View.GONE
    }

    private fun showEmpty() {
        binding.shimmerLead.stopShimmer()
        binding.shimmerLead.visibility = View.GONE

        binding.swipeRefreshLayout.visibility = View.GONE
        binding.noDataLayout.visibility = View.VISIBLE
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(
            R.menu.lead_filter, menu
        )
        val searchItem = menu?.findItem(R.id.search)

        val searchView = searchItem?.actionView as SearchView

        searchView.queryHint = "Search Lead"

        setupSearch(searchView)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {

            android.R.id.home -> {
                finish()
                return true
            }

            R.id.filter -> {

                showAllFilterDialog()

                return true
            }

            R.id.plus -> {
                val intent = Intent(this, AddLead::class.java)
                startActivity(intent)

                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun setupSearch(
        searchView: SearchView
    ) {

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(
                query: String?
            ): Boolean {

                searchQuery = query.orEmpty()
                performSearch()
                searchView.clearFocus()

                return true
            }

            override fun onQueryTextChange(
                newText: String?
            ): Boolean {

                searchJob?.cancel()

                searchJob = lifecycleScope.launch {

                    delay(500)
                    searchQuery = newText.orEmpty()
                    performSearch()
                }

                return true
            }
        })

        searchView.setOnCloseListener {
            searchQuery = ""
            performSearch()

            false
        }
    }

    private fun performSearch() {

        currentPage = 1
        isLastPage = false
        leadAdapter.clearData()
        loadLeads()
    }




    private fun callSalessApi() {


        val  token = "Token ${Globals.GalaxyVistaToken}"
        val jsonObject = JsonObject()
        jsonObject.addProperty("SalesEmployeeCode", PrefsByShubh.getSalesEmployeeCode())

        val call = RetrofitClient.apiService.getSalesEmplyeeList(token, jsonObject)

        call?.enqueue(object : Callback<JsonObject?> {
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                if (response.isSuccessful) {
                    val responseObj = response.body()

                    val dataArray = responseObj?.getAsJsonArray("data")
                    salesEmployeeItemList = if (dataArray != null) {
                        val gson = Gson()
                        gson.fromJson(
                            dataArray, object : TypeToken<List<SalesEmployeeItemKt>>() {}.type
                        )
                    } else {
                        emptyList()
                    }

                    if (dataArray != null) {
                        for (employeeJson in dataArray) {
                            val employee = Gson().fromJson(employeeJson, SalesEmployee::class.java)

                            // Concatenate first name and last name
                            val fullName = "${employee.firstName} ${employee.lastName}"

                            // Add the full name to the list
                            employeeNames.add(fullName)
                        }
                    }

                }
            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                Log.e("API Error", "onFailure: ${t.message}")
            }
        })


    }
    private fun setUpLeadSource() {

        val token = "Token ${Globals.GalaxyVistaToken}"


        val call = RetrofitClient.apiService.getLeadSourceAll(token)

        call.enqueue(object : Callback<LeadSourceAllResponseModel?> {
            override fun onResponse(
                call: Call<LeadSourceAllResponseModel?>,
                response: Response<LeadSourceAllResponseModel?>
            ) {
                try{
                    if (response.body()?.status == 200) {
                        if (response.body()?.data?.isNotEmpty() == true) {
                            val responseObj = response.body()?.data

                            responseObj?.let { // ✅ Null safety check
                                for (item in it) {
                                    leadSourceAll.add(item.Name) // ✅ Add names safely
                                }
                            }


                        } else{
                            Toast.makeText(this@LeadsActivity, "Data Not Found", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                catch (e: Exception){
                    Toast.makeText(this@LeadsActivity, e.message.toString(), Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<LeadSourceAllResponseModel?>, t: Throwable) {
                Toast.makeText(this@LeadsActivity, "API Failed", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onLeadUpdated() {
        loadLeads()
    }
}