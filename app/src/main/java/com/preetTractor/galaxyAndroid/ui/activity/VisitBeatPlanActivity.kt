package com.preetTractor.galaxyAndroid.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.preetTractor.galaxyAndroid.data.beatplan.DataBeatPlan
import com.preetTractor.galaxyAndroid.data.beatplan.ResponseBeatPlan
import com.preetTractor.galaxyAndroid.databinding.ActivityVisitBeatPlanBinding
import com.preetTractor.galaxyAndroid.helper.Globals
import com.preetTractor.galaxyAndroid.retrofit.RetrofitClient
import com.preetTractor.galaxyAndroid.ui.recyclerview.BeatPlanListListingAdapter
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class VisitBeatPlanActivity : BaseActivity() {

    lateinit var binding: ActivityVisitBeatPlanBinding
    var fromDateString: String? = Globals.getFirstDateofMonth()
    var toDateString: String? = Globals.getTodaysDatervrsfrmt()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVisitBeatPlanBinding.inflate(layoutInflater)
        setContentView(binding.root)
        intialStatus()
        getListing()
    }


    private var allItemList = ArrayList<DataBeatPlan>()
    private lateinit var adapter: BeatPlanListListingAdapter

    private fun intialStatus() {
        binding.tvDate.text = Globals.getFirstDateofMonth()
        fromDateString = Globals.dateStringConvertToDesiredFormat(
            Globals.getFirstDateofMonth().toString(),
            "dd-MM-yyyy",
            "yyyy-MM-dd"
        )
        binding.tvDate.setOnClickListener {
            Globals.openDatePicker(binding.tvDate) { formattedDate ->
                fromDateString = formattedDate
                binding.tvDate.text = Globals.dateStringConvertToDesiredFormat(
                    formattedDate,
                    "yyyy-MM-dd",
                    "dd-MM-yyyy"
                )
                getListing()
            }
        }
        binding.tvToDate.text = Globals.getTodaysDate()
        toDateString = binding.tvToDate.text.toString()
        toDateString = Globals.dateStringConvertToDesiredFormat(
            Globals.getTodaysDate().toString(),
            "dd-MM-yyyy",
            "yyyy-MM-dd"
        )
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
                getListing()
            }
        }

        /*  binding.etFromDateSelected.setText(
              Globals.dateStringConvertToDesiredFormat(
                  Globals.getTodaysDate() ?: "", "dd-MM-yyyy", "dd-MM-yyyy"
              )
          )


          binding.etFromDateSelected.transformIntoDatePickerWithDisablePastDates(
              requireContext(),
              "dd-MM-yyyy",
              null
          )
          binding.etToDateSelected.transformIntoDatePicker(requireContext(), "dd-MM-yyyy", null)*/
    }

    private fun getListing() {
        binding.spinKitLoader.visibility = View.VISIBLE
        // {"SalesEmployeeCode":3, "FromDate":"2024-12-05", "ToDate":"2024-12-16", "Status":""}

        val hde = JsonObject().apply {
            addProperty("SalesEmployeeCode", Globals.SalesEmployeeCode)
            addProperty("FromDate", fromDateString)
            addProperty("ToDate", toDateString)
            addProperty("Status", "")


        }

        val call = RetrofitClient.apiService.galaxyVisitBeatPlanList(hde)

        call.enqueue(object : Callback<ResponseBeatPlan> {
            override fun onResponse(
                call: Call<ResponseBeatPlan>,
                response: Response<ResponseBeatPlan>
            ) {
                binding.spinKitLoader.visibility = View.GONE
                response.body()?.let {
                    if (it.status.equals("200", ignoreCase = true)) {

                        if (it.data.isNotEmpty()) {
                            allItemList.clear()
                            allItemList.addAll(it.data)
                            binding.noDataFound.visibility = View.GONE
                        } else {
                            allItemList.clear()
                            allItemList.addAll(it.data)
                            binding.noDataFound.visibility = View.VISIBLE
                        }


                        adapter =
                            BeatPlanListListingAdapter(allItemList, this@VisitBeatPlanActivity)

                        /* binding.rvBeatplanTodayList.apply {
                             setHasFixedSize(true)
                         }*/

                        binding.rvUsers.adapter = adapter
                        binding.rvUsers.layoutManager =
                            LinearLayoutManager(this@VisitBeatPlanActivity)
                        adapter.notifyDataSetChanged()

                        Log.e(TAG, "onResponseBackground: ${it.message}")
                    }
                }
            }

            override fun onFailure(call: Call<ResponseBeatPlan>, t: Throwable) {
                Log.e(TAG, "onFailure: ${t.message}")
                binding.spinKitLoader.visibility = View.GONE
                binding.noDataFound.visibility = View.VISIBLE
            }
        })
    }


    companion object {
        private const val TAG = "VisitBeatPlanActivity"
    }
}