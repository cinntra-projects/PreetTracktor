package com.preetTractor.galaxyAndroid.ui.activity


import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.preetTractor.galaxyAndroid.data.ResponseBackGroundLocation
import com.preetTractor.galaxyAndroid.helper.Globals
import com.preetTractor.galaxyAndroid.helper.Globals.getDateConvertToDesiredFormat
import com.preetTractor.galaxyAndroid.retrofit.RetrofitClient
import com.preetTractor.galaxyAndroid.ui.fragment.LocationPickerDialog
import com.preetTractor.galaxyAndroid.ui.recyclerview.BackgroundLocationAdapter
import com.google.android.gms.maps.model.LatLng

import com.google.gson.JsonObject
import com.preetTractor.galaxyAndroid.databinding.ActivityAttendanceBackGroundListBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Calendar

class AttendanceBackGroundListActivity : BaseActivity() {

    private lateinit var binding: ActivityAttendanceBackGroundListBinding
    private lateinit var adapter: BackgroundLocationAdapter
    private var allItemList = ArrayList<ResponseBackGroundLocation.Datum>()
    private var allILatLong = ArrayList<LatLng>()
    private lateinit var layoutManager: LinearLayoutManager
    var salesEmployeeeCode = ""
    var salesEmployeeeName = ""
    var teamStatusId = ""

    //    private lateinit var tvSelectedDate: TextView
    private val calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAttendanceBackGroundListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()
        salesEmployeeeCode = intent.getStringExtra("sales").toString()
        salesEmployeeeName = intent.getStringExtra("name").toString()
        teamStatusId = intent.getStringExtra("teamStatusId").toString()

        Log.d("tarun123", "onCreate: $teamStatusId")

        binding.tvNameOfEmployee.text = salesEmployeeeName
        binding.etDateSelected.setText(Globals.getTodaysDatervrsfrmt().toString())

        Globals.getTodaysDatervrsfrmt()?.let { getListing(it) }
        onClickMethod()
    }

    private fun onClickMethod() {
        binding.ivBack.setOnClickListener {
            finish()
        }
        binding.etDateSelected.setOnClickListener {
            showDatePicker()
        }
        binding.etMaps.setOnClickListener {
            //todo adding local list for testing
            val dialog = LocationPickerDialog.newInstance(allILatLong)//allILatLong
            if (allILatLong.isNotEmpty())
                dialog.show(supportFragmentManager, "editLocation")

        }
    }

    private fun getListing(dateStr: String) {
        val hde = JsonObject().apply {
            addProperty("SalesEmployeeCode", teamStatusId)
//            addProperty("SalesEmployeeCode", salesEmployeeeCode)
            addProperty("DateFilter", dateStr)
        }
        binding.progressBar.visibility = View.VISIBLE
        binding.tvNoData.visibility = View.GONE
        val call = RetrofitClient.apiService.galaxyTrackingFilter(hde)
        call.enqueue(object : Callback<ResponseBackGroundLocation> {
            override fun onResponse(
                call: Call<ResponseBackGroundLocation>,
                response: Response<ResponseBackGroundLocation>
            ) {
                response.body()?.let {
                    if (it.status.equals("200", ignoreCase = true)) {
                        binding.progressBar.visibility = View.GONE

                        binding.etKm.setText("${it.totalDistance} KM ")
                        allItemList.clear()
                        allILatLong.clear()
                        allItemList.addAll(it.data)
                        it.data.forEach {
                            allILatLong.add(LatLng(it.latitude.toDouble(), it.longitude.toDouble()))
                        }
                        binding.tvNoData.visibility =
                            if (it.data.isEmpty()) View.VISIBLE else View.GONE
                        layoutManager = LinearLayoutManager(this@AttendanceBackGroundListActivity)
                        adapter = BackgroundLocationAdapter(
                            allItemList,
                            this@AttendanceBackGroundListActivity
                        )
                        binding.rvBackGroundLocation.layoutManager = layoutManager
                        binding.rvBackGroundLocation.adapter = adapter
                        adapter.notifyDataSetChanged()
                        Log.e(TAG, "onResponseBackground: ${it.message}")
                        Log.e(TAG, "onResponseBackground: ${allILatLong}")
                    }
                }
            }

            override fun onFailure(call: Call<ResponseBackGroundLocation>, t: Throwable) {
                Log.e(TAG, "onFailure: ${t.message}")
                binding.progressBar.visibility = View.GONE
                binding.tvNoData.visibility = View.GONE
            }
        })
    }

    private fun showDatePicker() {
        // Create a DatePickerDialog


        val datePickerDialog = DatePickerDialog(
            this, { DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                // Create a new Calendar instance to hold the selected date
                val selectedDate = Calendar.getInstance()
                // Set the selected date using the values received from the DatePicker dialog
                selectedDate.set(year, monthOfYear, dayOfMonth)
                val selectedDateStr = getDateConvertToDesiredFormat(selectedDate.time, "dd/MM/yyyy")
                val apiDateStr = getDateConvertToDesiredFormat(selectedDate.time, "yyyy-MM-dd")
                // Update the TextView to display the selected date with the "Selected Date: " prefix
                apiDateStr?.let {
                    getListing(it)
                }
                Log.e(TAG, "showDatePicker: $apiDateStr")
                selectedDateStr?.let {
                    binding.etDateSelected.setText(selectedDateStr)
                }
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        // Show the DatePicker dialog
        datePickerDialog.show()
    }

    companion object {
        private const val TAG = "AttendanceBackGroundLis"
    }
}