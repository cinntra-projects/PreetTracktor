package com.preetTractor.galaxyAndroid.ui.activity.beat

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.preetTractor.galaxyAndroid.data.BeatPlanCustomerDropDownModel
import com.preetTractor.galaxyAndroid.data.beatplan.*
import com.preetTractor.galaxyAndroid.databinding.ActivityAddBeatPlanBinding
import com.preetTractor.galaxyAndroid.databinding.DialogCustomBinding
import com.preetTractor.galaxyAndroid.helper.Globals
import com.preetTractor.galaxyAndroid.helper.PrefsByShubh
import com.preetTractor.galaxyAndroid.retrofit.RetrofitClient
import com.preetTractor.galaxyAndroid.ui.recyclerview.BeatPlanAdditionAdapter
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.preetTractor.galaxyAndroid.viewmodel.FragmentRefreshPage
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.preetTractor.galaxyAndroid.R
import com.preetTractor.galaxyAndroid.data.BdrcModel
import com.preetTractor.galaxyAndroid.data.ModeOfTravelData
import com.preetTractor.galaxyAndroid.data.ModeOfTravelResponse
import com.preetTractor.galaxyAndroid.helper.Globals.showDatePicker
import com.preetTractor.galaxyAndroid.ui.activity.BaseActivity


class AddBeatPlanActivity : BaseActivity() {

    lateinit var binding: ActivityAddBeatPlanBinding

    //    private lateinit var customerAdapter: DemoAdapter
    private lateinit var customerAdapter: BeatPlanAdditionAdapter

    //    var beatItem = mutableListOf<BeatPlanData>()
    var beatItem = mutableListOf<BeatPlanCustomerDropDownModel.Data>()

    var isFilterClickable = false

    companion object {
        private const val TAG = "MainActivity"
    }

    var cardName = ""
    var cardCode = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddBeatPlanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.apply {
            title = "Add Existing Dealer Beat Plan"
            setDisplayHomeAsUpEnabled(true)
            setHomeButtonEnabled(true)
        }

        if (PrefsByShubh.getString("role", "")!! == "Business Analyst") {
            binding.addFloatingButton.visibility = View.GONE
        } else {
            binding.addFloatingButton.visibility = View.VISIBLE
        }



        callStateApi()
        binding.tvDate.text = Globals.getTodaysDate()
        binding.tvDate.setOnClickListener {
            binding.tvDate.showDatePicker()
        }

        binding.tvShowFilter.isEnabled = false
        callModeOfTravelApi()
        callCustomerListApi()


        /*binding.tvShowFilter.setOnClickListener {

            if (isFilterClickable) {
                Toast.makeText(this, "Please go back to choose another location", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            showCustomDialog(this, stateList, cityList) { selectedItem1, selectedItem2 ->
                callCustomerListApi()
            }

        }*/



        beatItem.add(
            BeatPlanCustomerDropDownModel.Data(
                CardName = "",
                CardCode = "",
                timing = Globals.getTCurrentTime()!!,
                priority = "High",
                remark = "",
                transport_mode =  ""
            )
        )

        binding.addFloatingButton.setOnClickListener {
/*
            if (selectedStateCode.isEmpty() || selectedCityName.isEmpty()) {
                Toast.makeText(this, "Select City and State First", Toast.LENGTH_SHORT).show()
            } else {

                checkCustomerValidation(customerAdapter.getAttachList())


            }*/
            try{
                checkCustomerValidation(customerAdapter.getAttachList())
            }catch (e: Exception){

            }


        }
        /* binding.btnSHowLIsting.setOnClickListener {
             Log.e(TAG, "onCreate: " + beatItem.toString())
         }*/


        binding.postBeatPlanBtn.setOnClickListener {
            try {
                // Attempt to access customerAdapter
                if (Globals.isValidBeatPlanAdditionList(customerAdapter.getAttachList())) {
                    createBeatPlanApi()
                } else {
                    checkValidation(customerAdapter.getAttachList())
                }
            } catch (e: UninitializedPropertyAccessException) {
//                // Handle the uninitialized lateinit variable
//                Toast.makeText(this, "CustomerAdapter is not initialized", Toast.LENGTH_SHORT).show()
            }

        }
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

    fun checkCustomerValidation(dataList: List<BeatPlanCustomerDropDownModel.Data>): Boolean {
        dataList.forEachIndexed { index, it ->
            if (it.CardName.isEmpty()) {
                Toast.makeText(this, "Select Customer at Card ${index + 1}", Toast.LENGTH_SHORT).show()
                return false
            }
        }

        // Validation passed for all items – now add a new item
        val newItem = BeatPlanCustomerDropDownModel.Data(
            CardName = cardName,
            CardCode = cardCode,
            timing = Globals.getTCurrentTime() ?: "",
            priority = "High",
            remark = "",
            selectedDate = binding.tvDate.text.toString()
        )

        customerAdapter.addItem(newItem)

        // Clear for next input
        cardName = ""
        cardCode = ""

        return true
    }


    fun checkValidation(dataList: List<BeatPlanCustomerDropDownModel.Data>): Boolean {
        dataList.forEachIndexed { index, it ->
            when {
                it.CardName.isEmpty() -> {
                    Toast.makeText(this, "Select Customer at Card ${index + 1}", Toast.LENGTH_SHORT)
                        .show()
                    return false
                }
                it.timing.isEmpty() -> {
                    Toast.makeText(this, "Select Timing at Card ${index + 1}", Toast.LENGTH_SHORT)
                        .show()
                    return false
                }
                it.priority.isEmpty() -> {
                    Toast.makeText(this, "Select Priority at Card ${index + 1}", Toast.LENGTH_SHORT)
                        .show()
                    return false
                }
                it.remark.isEmpty() -> {
                    Toast.makeText(this, "Enter Remark at Card ${index + 1}", Toast.LENGTH_SHORT)
                        .show()
                    return false
                }
            }
        }
        return true // All validations passed
    }

    var selectedItem2 = ""
    private fun showCustomDialog(
        context: Context,
        items: ArrayList<DataStateAll>,
        city: ArrayList<DataCityAll>,
        onApply: (DataStateAll, DataCityAll) -> Unit
    ) {
        val dialogBinding = DialogCustomBinding.inflate(LayoutInflater.from(context))
        val dialog = AlertDialog.Builder(context).setView(dialogBinding.root).create()



        setupAutoCompleteTextViewState(
            dialogBinding.autoCompleteTextViewState,
            items, dialogBinding
        )


        dialogBinding.applyButton.setOnClickListener {
            val selectedItem1 = dialogBinding.autoCompleteTextViewState.text.toString()
            selectedItem2 = dialogBinding.autoCompleteTextViewCity.text.toString()

            // Validate that no field is empty
            if (selectedItem1.isEmpty() || selectedItem2.isEmpty()) {
                Toast.makeText(context, "Please fill out both fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Find the selected items from the list
            val item1 = items.find { it.Name.equals(selectedItem1, ignoreCase = true) }
            val item2 = cityList.find { it.CityName.equals(selectedItem2, ignoreCase = true) }

            if (item1 != null && item2 != null) {
                onApply(item1, item2)
                dialog.dismiss()
            } else {
                Toast.makeText(context, "Invalid selection", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show()
    }

    private fun setupAutoCompleteTextViewState(
        autoCompleteTextView: AutoCompleteTextView,
        customers: ArrayList<DataStateAll>,
        dialogBinding: DialogCustomBinding
    ) {
        val adapter = ArrayAdapter(
            autoCompleteTextView.context,R.layout.drop_down_textview,
            RESULT_CANCELED,
            customers.map { it.Name })
        autoCompleteTextView.setAdapter(adapter)
        autoCompleteTextView.threshold = 0
        autoCompleteTextView.setOnClickListener { autoCompleteTextView.showDropDown() }
        autoCompleteTextView.setOnItemClickListener { parent, _, position, _ ->
            val selectedName = parent.getItemAtPosition(position) as String
            //  Toast.makeText(this, selectedName, Toast.LENGTH_SHORT).show()
            selectedStateName = parent.getItemAtPosition(position) as String
            selectedStateCode =
                stateList[Globals.getStatePosForCode(selectedStateName, stateList)].Code
            callCityApi(dialogBinding)

        }
    }

    var selectedCityCode = ""
    var selectedCityName = ""
    var selectedStateCode = ""
    var selectedStateName = ""
    private fun setupAutoCompleteTextViewCity(
        autoCompleteTextView: AutoCompleteTextView,
        customers: ArrayList<DataCityAll>
    ) {
        val adapter = ArrayAdapter(
            autoCompleteTextView.context,
            R.layout.drop_down_textview,
            customers.map { it.CityName })
        autoCompleteTextView.setAdapter(adapter)

        autoCompleteTextView.threshold = 0
        autoCompleteTextView.setOnClickListener {
            autoCompleteTextView.showDropDown()
        }
        autoCompleteTextView.setOnItemClickListener { parent, _, position, _ ->
            selectedCityName = cityList[position].CityName
            selectedCityCode = cityList[position].CityName

            //    Toast.makeText(this, selectedName, Toast.LENGTH_SHORT).show()
        }
    }


    override fun onDestroy() {
        super.onDestroy()


    }

    private fun onItemClicked(position: Int) {
        // Handle the click event
        // Toast.makeText(this, "Clicked item at position $position", Toast.LENGTH_SHORT).show()

        // Example: Remove the item from the list
        customerAdapter.removeItem(position)
    }




    var modeOfTravelList: ArrayList<ModeOfTravelData> =
        ArrayList<ModeOfTravelData>()

    fun callModeOfTravelApi() {

        val call = RetrofitClient.apiService.getModeOfTravel(token)
        call.enqueue(object : Callback<ModeOfTravelResponse> {
            override fun onResponse(
                call: Call<ModeOfTravelResponse>,
                response: Response<ModeOfTravelResponse>
            ) {
                response.body()?.let {

                    if (it.status == 200) {
                        modeOfTravelList.clear()
                        modeOfTravelList.addAll(it.data)
                    }
                }
            }



            override fun onFailure(call: Call<ModeOfTravelResponse>, t: Throwable) {
                Toast.makeText(this@AddBeatPlanActivity, "Something went wrong", Toast.LENGTH_SHORT)
                    .show()
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }

    //todo calling customer list drop down api here---
    var customerList: ArrayList<BeatPlanCustomerDropDownModel.Data> =
        ArrayList<BeatPlanCustomerDropDownModel.Data>()
    fun callCustomerListApi() {
        var jsonObject = JsonObject()
        jsonObject.addProperty("SalesEmployeeCode", PrefsByShubh.getEmpCode())
        jsonObject.addProperty("City", selectedItem2)
        jsonObject.addProperty("State", selectedStateCode)
        val call = RetrofitClient.apiService.getCustomerListing(jsonObject)
        call.enqueue(object : Callback<BeatPlanCustomerDropDownModel> {
            override fun onResponse(
                call: Call<BeatPlanCustomerDropDownModel>,
                response: Response<BeatPlanCustomerDropDownModel>
            ) {

                response.body()?.let {

                    if (it.status == 200) {
                        customerList.clear()
                        customerList.addAll(it.data)

                        isFilterClickable = true

                        customerAdapter =
                            BeatPlanAdditionAdapter(beatItem,  modeOfTravelList.map { it.Name }, customerList,
                                onanyItemClicked={ position ->
                                onItemClicked(position)
                            },
                                onCustomerClick = { cardCode, position ->
                                    callBdrcApi(cardCode,position)
                                }
                            )

                        binding.rvBeatPlan.adapter = customerAdapter
                        binding.rvBeatPlan.layoutManager =
                            LinearLayoutManager(this@AddBeatPlanActivity)

                    }
                }
            }



            override fun onFailure(call: Call<BeatPlanCustomerDropDownModel>, t: Throwable) {
                Toast.makeText(this@AddBeatPlanActivity, "Something went wrong", Toast.LENGTH_SHORT)
                    .show()
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }



    private fun callBdrcApi(cardCode: String, pos: Int) {
        var jsonObject = JsonObject()
        jsonObject.addProperty("date", Globals.convert_dd_MM_yyyy_into_yyyy_MM_dd(binding.tvDate.text.toString()))
        jsonObject.addProperty("business_partner", cardCode)
        jsonObject.addProperty("PageNo", 1)
        jsonObject.addProperty("maxItem",10)
        val call = RetrofitClient.apiService.getBDRCData("Token " +Globals.GalaxyVistaToken.toString(),jsonObject)
        call.enqueue(object : Callback<BdrcModel> {
            override fun onResponse(
                call: Call<BdrcModel>,
                response: Response<BdrcModel>
            ) {

                response.body()?.let {

                    if (it.status == 200) {
                        customerAdapter.updateBDRCData(position = pos, response.body()?.data ?: emptyList())
                    }
                }
            }

            override fun onFailure(call: Call<BdrcModel>, t: Throwable) {
                Toast.makeText(this@AddBeatPlanActivity, "Something went wrong", Toast.LENGTH_SHORT)
                    .show()
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }
    var stateList = ArrayList<DataStateAll>()
    var cityList = ArrayList<DataCityAll>()

    private fun callStateApi() {
        var jsonObject = JsonObject()
        jsonObject.addProperty("Country", "IN")
        val call = RetrofitClient.apiService.getStateAll(jsonObject)
        call.enqueue(object : Callback<ResponseStateAll> {
            override fun onResponse(
                call: Call<ResponseStateAll>,
                response: Response<ResponseStateAll>
            ) {

                response.body()?.let {

                    if (it.status == 200) {
                        binding.tvShowFilter.isEnabled = true
                        stateList.clear()
                        stateList.addAll(it.data)


                    }
                }
            }

            override fun onFailure(call: Call<ResponseStateAll>, t: Throwable) {
                Toast.makeText(this@AddBeatPlanActivity, "Something went wrong", Toast.LENGTH_SHORT)
                    .show()
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }


    private fun callCityApi(dialogBinding: DialogCustomBinding) {


        var jsonObject = JsonObject()
        jsonObject.addProperty("PageNo", 1)
        jsonObject.addProperty("maxItem", "All")
        jsonObject.addProperty("order_by_field", "id")
        jsonObject.addProperty("order_by_value", "desc")
        jsonObject.addProperty("SearchText", "")
        var innerJson = JsonObject().apply {
            addProperty("StateCode", selectedStateCode)
        }
        jsonObject.add("field", innerJson)
        val call = RetrofitClient.apiService.getCityALL(jsonObject)
        call.enqueue(object : Callback<ResponseCityAll> {
            override fun onResponse(
                call: Call<ResponseCityAll>,
                response: Response<ResponseCityAll>
            ) {

                response.body()?.let {

                    if (it.status == 200) {

                        cityList.clear()
                        cityList.addAll(it.data)
                        setupAutoCompleteTextViewCity(
                            dialogBinding.autoCompleteTextViewCity,
                            cityList
                        )


                    }
                }
            }

            override fun onFailure(call: Call<ResponseCityAll>, t: Throwable) {
                Toast.makeText(this@AddBeatPlanActivity, "Something went wrong", Toast.LENGTH_SHORT)
                    .show()
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }

    var fragmentRefreshPage: FragmentRefreshPage? = null

    fun createBeatPlanApi() {

        binding.loadingBackFrame.visibility = View.VISIBLE

        var beatPlanList = mutableListOf<BeatPlanCustomerDropDownModel.Data>()
        beatPlanList.addAll(customerAdapter.getAttachList())

        val sparePartListJsonArray = JsonArray()

        beatPlanList.forEach {
            val jsonObject = JsonObject().apply {
                addProperty("bpid", it.id)
                addProperty("CardCode", it.CardCode)
                addProperty("CardName", it.CardName)
                addProperty("Priority", it.priority)
//                addProperty("Purpose", it.timing) // shubh
                addProperty("Purpose", "") // Tarun Sharma
                addProperty("Remark", it.remark)
                addProperty("transport_mode", it.transport_mode)
                addProperty(
                    "Visit_Date",
                    Globals.convert_dd_MM_yyyy_into_yyyy_MM_dd(binding.tvDate.text.toString())
                )
                addProperty("Type", "Customer")
                addProperty("AssignedTo", PrefsByShubh.getSalesEmployeeCode())
                addProperty("CreatedBy", PrefsByShubh.getSalesEmployeeCode())
                addProperty("ApprovedBy", PrefsByShubh.getSalesEmployeeCode())
                addProperty("Approval_Status", "Pending")
                addProperty("Create_Date", Globals.getTodaysDatervrsfrmt())
                addProperty("Create_Time", Globals.getTCurrentTime())
//                addProperty("City", selectedCityName) // shubh
                addProperty("City", selectedItem2) // Tarun Sharma
                addProperty("State", selectedStateCode)
                addProperty("Shop_StartAt", it.CheckinTime)
                addProperty("Shop_CloseAt", it.CheckoutTime)
            }
            sparePartListJsonArray.add(jsonObject)
        }


        checkUniqueShopStartTime(this, sparePartListJsonArray)

    }
    val token = "Token ${Globals.GalaxyVistaToken}"
    fun checkUniqueShopStartTime(context: Context, jsonArray: JsonArray) {
        val shopStartTimes = mutableSetOf<String>()
        var isDuplicateFound = false




        for (i in 0 until jsonArray.size()) {
            val jsonObject = jsonArray[i].asJsonObject
            val shopStartAt = jsonObject.get("Shop_StartAt").asString

            // Check if the time is already in the set
            if (shopStartTimes.contains(shopStartAt)) {
                isDuplicateFound = true
                break  // Stop checking further if a duplicate is found
            } else {
                shopStartTimes.add(shopStartAt)
            }
        }

        if (isDuplicateFound) {
            Toast.makeText(context, "Please Select Different Time", Toast.LENGTH_SHORT).show()
            binding.loadingBackFrame.visibility = View.GONE
        } else {
            val jsonString = jsonArray.toString()
            Log.e("REQUEST>>>>>", "onCreate: $jsonString")

            val call = RetrofitClient.apiService.createBeatPlan(token, jsonArray)
            call.enqueue(object : Callback<BeatPlanCustomerDropDownModel> {
                override fun onResponse(
                    call: Call<BeatPlanCustomerDropDownModel>,
                    response: Response<BeatPlanCustomerDropDownModel>
                ) {

                    response.body()?.let {
                        binding.loadingBackFrame.visibility = View.GONE
                        if (it.status == 200) {

                            Globals.successMessage(this@AddBeatPlanActivity, "SuccessFul Created")

                            finish()

                            fragmentRefreshPage?.onRefresh()

                        } else {
                            Globals.errorMessage(this@AddBeatPlanActivity, "${it.message}")

                        }
                    }
                }

                override fun onFailure(call: Call<BeatPlanCustomerDropDownModel>, t: Throwable) {
                    Toast.makeText(
                        this@AddBeatPlanActivity,
                        "Something went wrong",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    Log.e(TAG, "onFailure: ${t.message}")
                    binding.loadingBackFrame.visibility = View.GONE
                }
            })
        }
    }

}