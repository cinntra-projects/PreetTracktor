package com.preetTractor.galaxyAndroid.ui.activity

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.preetTractor.galaxyAndroid.data.AllLeadResponse
import com.preetTractor.galaxyAndroid.data.BeatPlanCustomerDropDownModel
import com.preetTractor.galaxyAndroid.databinding.ActivityAddLeadBinding
import com.preetTractor.galaxyAndroid.helper.Globals
import com.preetTractor.galaxyAndroid.helper.PrefsByShubh
import com.preetTractor.galaxyAndroid.retrofit.RetrofitClient
import com.preetTractor.galaxyAndroid.ui.recyclerview.LeadAdditionAdapter
import com.preetTractor.galaxyAndroid.viewmodel.FragmentRefreshPage
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.preetTractor.galaxyAndroid.data.ModeOfTravelData
import com.preetTractor.galaxyAndroid.data.ModeOfTravelResponse
import com.preetTractor.galaxyAndroid.ui.activity.beat.AddBeatPlanActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddLeadBeatPlanActivity : BaseActivity() {
    lateinit var binding: ActivityAddLeadBinding
    val leadSourceAll: MutableList<String> = mutableListOf()

    lateinit var adapter: LeadAdditionAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddLeadBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.apply {
            title = "Add Lead Beat Plan"
            setDisplayHomeAsUpEnabled(true)
            setHomeButtonEnabled(true)
        }

        callModeOfTravelApi()
        setUpLeadSource()
        binding.tvDate.text = Globals.getTodaysDate()

        binding.tvDate.setOnClickListener {
            Globals.enableAllCalenderDateSelect(this, binding.tvDate)
        }

        // Handle Floating Button Click
        binding.addFloatingButton.setOnClickListener {
            adapter.addNewCard()

        }

        binding.createNonCustomerBtn.setOnClickListener {

                if (adapter.isAllNamesFilled(binding.rvLeadList, this)) {
                    // Proceed with saving
                    logNonCustomerData()

                }

        }

    }



    private fun logNonCustomerData() {
        val dataList = adapter.getleadList()

        val sparePartListJsonArray = JsonArray()

        dataList.forEach {
            val jsonObject = JsonObject().apply {
                addProperty("bpid", 0)
                addProperty("CardCode", "")
                addProperty("CardName", "")
                addProperty("Priority", it.priority)
                addProperty("Purpose", "")
                addProperty("Remark", it.remark)
                addProperty("transport_mode", it.transport_mode)
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
                addProperty("Type","Lead")
                addProperty("ProspectName","")
                addProperty("ProspectNumber","")
                addProperty("Source","")
                addProperty("Industry","")
                addProperty("Zone","")
                addProperty("LeadStatus",0)
                addProperty("LeadId", it.selectedLeadId)
            }
            sparePartListJsonArray.add(jsonObject)


            Log.d("LeadData", "Name: ${it.leadName}, Remark: ${it.remark}, Priority: ${it.priority}, Timing: ${it.timing}")
        }


        checkUniqueShopStartTime(this, sparePartListJsonArray)

        Log.e("REQUEST>>>>>", "onCreate: $sparePartListJsonArray")
    }


    var fragmentRefreshPage: FragmentRefreshPage? = null
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

                            Globals.successMessage(this@AddLeadBeatPlanActivity, "SuccessFul Created")

                            finish()

                            fragmentRefreshPage?.onRefresh()
                            finish()

                        }else{
                            Globals.errorMessage(this@AddLeadBeatPlanActivity, "${it.message}")

                        }

                    }
                }

                override fun onFailure(call: Call<BeatPlanCustomerDropDownModel>, t: Throwable) {
                    Toast.makeText(
                        this@AddLeadBeatPlanActivity,
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
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
                Toast.makeText(this@AddLeadBeatPlanActivity, "Something went wrong", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }

    private fun setUpLeadSource() {

        val token = "Token ${Globals.GalaxyVistaToken}"

        binding.addFloatingButton.isEnabled = false
        binding.addFloatingButton.isClickable = false


        val jsonObject = JsonObject()
        jsonObject.addProperty("SalesPersonCode", Globals.empCode)

        val call = RetrofitClient.apiService.getAllLead(token, jsonObject)

        call.enqueue(object : Callback<AllLeadResponse?> {
            override fun onResponse(
                call: Call<AllLeadResponse?>,
                response: Response<AllLeadResponse?>
            ) {
                try{
                    if (response.body()?.status == 200) {
                        if (response.body()?.data?.isNotEmpty() == true) {
                            val responseObj = response.body()?.data

                            responseObj?.let { // ✅ Null safety check
                                for (item in it) {
                                    leadSourceAll.add(item.companyName) // ✅ Add names safely
                                }
                            }

                            adapter = LeadAdditionAdapter(ArrayList(), modeOfTravelList =modeOfTravelList.map { it.Name }, responseObj!!, this@AddLeadBeatPlanActivity)
                            binding.rvLeadList.layoutManager = LinearLayoutManager(this@AddLeadBeatPlanActivity)
                            binding.rvLeadList.adapter = adapter

                            binding.addFloatingButton.isEnabled = true
                            binding.addFloatingButton.isClickable = true



                        } else {
                            adapter = LeadAdditionAdapter(ArrayList(),modeOfTravelList.map { it.Name }, response.body()?.data!!, this@AddLeadBeatPlanActivity)
                            binding.rvLeadList.layoutManager = LinearLayoutManager(this@AddLeadBeatPlanActivity)
                            binding.rvLeadList.adapter = adapter

                            binding.addFloatingButton.isEnabled = true
                            binding.addFloatingButton.isClickable = true
                            Toast.makeText(this@AddLeadBeatPlanActivity, "No Data Found", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                catch (e: Exception){
                    Toast.makeText(this@AddLeadBeatPlanActivity, e.message.toString(), Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<AllLeadResponse?>, t: Throwable) {
                Toast.makeText(this@AddLeadBeatPlanActivity, "API Failed", Toast.LENGTH_SHORT).show()
            }
        })

    }
}