package com.preetTractor.galaxyAndroid.ui.activity

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.preetTractor.galaxyAndroid.data.BeatPlanCustomerDropDownModel
import com.preetTractor.galaxyAndroid.data.LeadSourceAllResponseModel
import com.preetTractor.galaxyAndroid.databinding.ActivityAddNonCustomerBinding
import com.preetTractor.galaxyAndroid.helper.Globals
import com.preetTractor.galaxyAndroid.helper.PrefsByShubh
import com.preetTractor.galaxyAndroid.retrofit.RetrofitClient
import com.preetTractor.galaxyAndroid.ui.recyclerview.NonCustomerAdditionAdapter
import com.preetTractor.galaxyAndroid.viewmodel.FragmentRefreshPage
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.preetTractor.galaxyAndroid.data.ModeOfTravelData
import com.preetTractor.galaxyAndroid.data.ModeOfTravelResponse
import com.preetTractor.galaxyAndroid.ui.activity.AddLeadBeatPlanActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddNonCustomerActivity : BaseActivity() {
    lateinit var binding: ActivityAddNonCustomerBinding
    val leadSourceAll: MutableList<String> = mutableListOf()

    lateinit var adapter: NonCustomerAdditionAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddNonCustomerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        callModeOfTravelApi()


        supportActionBar?.apply {
            title = "Add New Dealer Beat Plan"
            setDisplayHomeAsUpEnabled(true)
            setHomeButtonEnabled(true)
        }

        binding.tvDate.text = Globals.getTodaysDate()

        binding.tvDate.setOnClickListener {
            Globals.enableAllCalenderDateSelect(this, binding.tvDate)
        }

        // Handle Floating Button Click
        binding.addFloatingButton.setOnClickListener {
            adapter.addNewCard()

        }

        binding.createNonCustomerBtn.setOnClickListener {

                if (adapter.isAllNamesFilled(binding.rvNonCustomer, this)) {
                    // Proceed with saving
                    logNonCustomerData()

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


    private fun logNonCustomerData() {
        val dataList = adapter.getNonCustomerList()


        val sparePartListJsonArray = JsonArray()

        dataList.forEach {
            val jsonObject = JsonObject().apply {
                addProperty("bpid", 0)
                addProperty("CardCode", "")
                addProperty("CardName", "")
                addProperty("Priority", it.priority)
                addProperty("Purpose", "")
                addProperty("Remark", it.remark)
                addProperty(
                    "Visit_Date",
                    Globals.convert_dd_MM_yyyy_into_yyyy_MM_dd(binding.tvDate.text.toString())
                )
                addProperty("AssignedTo", PrefsByShubh.getSalesEmployeeCode())
                addProperty("CreatedBy", PrefsByShubh.getSalesEmployeeCode())
                addProperty("ApprovedBy", PrefsByShubh.getSalesEmployeeCode())
                addProperty("Approval_Status", "Pending")
                addProperty("Create_Date", Globals.getTodaysDatervrsfrmt())
                addProperty("Create_Time", Globals.getTCurrentTime())
                addProperty("City", "")
                addProperty("State", "")
                addProperty("Shop_StartAt", it.timing)
                addProperty("Shop_CloseAt","")
                addProperty("Type","Other")
                addProperty("transport_mode", it.transport_mode)
                addProperty("ProspectName",it.prospectName)
                addProperty("ProspectNumber",it.prospectNumber)
                addProperty("Source",it.selectedSourceId)
                addProperty("Industry",it.industry)
                addProperty("Zone",it.zone)
                if(it.createLeadCheck){
                    addProperty("LeadStatus",1)
                }
                else{
                    addProperty("LeadStatus",0)
                }

                addProperty("LeadId", 0)
            }
            sparePartListJsonArray.add(jsonObject)


            Log.d("NonCustomerData", "Name: ${it.prospectName}, Number: ${it.prospectNumber}, Remark: ${it.remark}, Source: ${it.source}, Zone: ${it.zone}, Industry: ${it.industry}, Priority: ${it.priority}, Timing: ${it.timing}, CreateLeadCheck: ${it.createLeadCheck}")
        }


        checkUniqueShopStartTime(this, sparePartListJsonArray)

        Log.e("REQUEST>>>>>", "onCreate: $sparePartListJsonArray")

//        dataList.forEachIndexed { index, item ->
//            Log.d("NonCustomerData", "Item $index: Name: ${item.prospectName}, Number: ${item.prospectNumber}, Remark: ${item.remark}, Source: ${item.source}, Zone: ${item.zone}, Industry: ${item.industry}, Priority: ${item.priority}, Timing: ${item.timing}, CreateLeadCheck: ${item.createLeadCheck}")
//        }
    }

    var fragmentRefreshPage: FragmentRefreshPage? = null

    fun checkUniqueShopStartTime(context: Context, jsonArray: JsonArray) {
        val shopStartTimes = mutableSetOf<String>()
        var isDuplicateFound = false

        val token = "Token ${Globals.GalaxyVistaToken}"

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

                            Globals.successMessage(this@AddNonCustomerActivity, "SuccessFul Created")


                            fragmentRefreshPage?.onRefresh()
                            finish()

                        }else{
                            Globals.errorMessage(this@AddNonCustomerActivity, "${it.message}")

                        }
                    }
                }

                override fun onFailure(call: Call<BeatPlanCustomerDropDownModel>, t: Throwable) {
                    Toast.makeText(
                        this@AddNonCustomerActivity,
                        "Something went wrong",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    Log.e("check", "onFailure: ${t.message}")
                    binding.loadingBackFrame.visibility = View.GONE
                }
            })
        }
    }
    val token = "Token ${Globals.GalaxyVistaToken}"
    private fun setUpLeadSource() {



        binding.addFloatingButton.isEnabled = false
        binding.addFloatingButton.isClickable = false


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

                            adapter = NonCustomerAdditionAdapter(ArrayList(), modeOfTravelList =modeOfTravelList.map { it.Name } ,responseObj!!, this@AddNonCustomerActivity)
                            binding.rvNonCustomer.layoutManager = LinearLayoutManager(this@AddNonCustomerActivity)
                            binding.rvNonCustomer.adapter = adapter

                            binding.addFloatingButton.isEnabled = true
                            binding.addFloatingButton.isClickable = true


                        } else {
                            adapter = NonCustomerAdditionAdapter(ArrayList(), modeOfTravelList =modeOfTravelList.map { it.Name } ,response.body()?.data!!, this@AddNonCustomerActivity)
                            binding.rvNonCustomer.layoutManager = LinearLayoutManager(this@AddNonCustomerActivity)
                            binding.rvNonCustomer.adapter = adapter

                            binding.addFloatingButton.isEnabled = true
                            binding.addFloatingButton.isClickable = true

                            Toast.makeText(this@AddNonCustomerActivity, "No Lead Found", Toast.LENGTH_LONG).show()
                        }
                    }
                }
                catch (e: Exception){
                    Toast.makeText(this@AddNonCustomerActivity, e.message.toString(), Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<LeadSourceAllResponseModel?>, t: Throwable) {
                Toast.makeText(this@AddNonCustomerActivity, "API Failed", Toast.LENGTH_SHORT).show()
            }
        })
    }

    var modeOfTravelList: ArrayList<ModeOfTravelData> =
        ArrayList<ModeOfTravelData>()

    fun callModeOfTravelApi() {

        val call = RetrofitClient.apiService.getModeOfTravel(token = token)
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
                Toast.makeText(this@AddNonCustomerActivity, "Something went wrong", Toast.LENGTH_SHORT)
                    .show()
            }
        })
        setUpLeadSource()
    }
}