package com.preetTractor.galaxyAndroid.ui.activity.log

import Event
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.preetTractor.galaxyAndroid.adapter.LogsAdapter
import com.preetTractor.galaxyAndroid.apiHelper.ApiClient
import com.preetTractor.galaxyAndroid.apiHelper.ApisInterface
import com.preetTractor.galaxyAndroid.data.ba.ModelAllLogs
import com.preetTractor.galaxyAndroid.databinding.ActivityLogsBinding
import com.preetTractor.galaxyAndroid.helper.Globals
import com.preetTractor.galaxyAndroid.helper.Globals.getMonthFirstLastDateOfSelectedMonthYear
import com.preetTractor.galaxyAndroid.helper.Globals.toPrettyJson
import com.preetTractor.galaxyAndroid.helper.PrefsByShubh
import com.preetTractor.galaxyAndroid.mvvmSetUp.DefaultMainRepositories
import com.preetTractor.galaxyAndroid.mvvmSetUp.MainRepos
import com.preetTractor.galaxyAndroid.mvvmSetUp.MainViewModel
import com.preetTractor.galaxyAndroid.mvvmSetUp.MainViewModelProvider
import com.google.gson.JsonObject
import com.preetTractor.galaxyAndroid.ui.activity.BaseActivity
import com.whiteelephant.monthpicker.MonthPickerDialog
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import java.util.Calendar

class LogsActivity : BaseActivity() {
      private lateinit var binding: ActivityLogsBinding
      private lateinit var adapterLogs: LogsAdapter
      private lateinit var viewModel: MainViewModel
      var builder: AlertDialog.Builder? = null
      var alertDialog: AlertDialog? = null
      private var selectedMonthInt:Int =0
      private var selectedYearInt:Int=0

      val months = listOf(
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
      )
      override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            binding = ActivityLogsBinding.inflate(layoutInflater)
            initView()
            clickListener()
            setContentView(binding.root)
      }

      private fun setUpViewModel() {
            val dispatchers: CoroutineDispatcher = Dispatchers.Main
            val mainRepos = DefaultMainRepositories() as MainRepos
            val fanxApi: ApisInterface = ApiClient().service(this)
            val viewModelProviderfactory =
                  MainViewModelProvider(application, mainRepos, dispatchers, fanxApi)
            viewModel = ViewModelProvider(this, viewModelProviderfactory)[MainViewModel::class.java]

      }

      private fun clickListener() {
            binding.apply {
                  ivBackPress.setOnClickListener {
                        finish()
                  }

                  tvMonthFilter.setOnClickListener {
                        val today = Calendar.getInstance()

                        val builder = MonthPickerDialog.Builder(
                              this@LogsActivity,
                              { selectedMonth, selectedYear ->
                                    // Handle the selected month and year here
                                    selectedMonthInt = selectedMonth
                                    selectedYearInt = selectedYear

                                    val monthName = java.text.DateFormatSymbols().months[selectedMonthInt]

                                    // Set the formatted string into the TextView
                                    val formattedDate = "$monthName, $selectedYearInt"
                                    binding.tvMonthFilter.text = formattedDate
                                    callLogsApi(selectedYearInt,selectedMonthInt)
                                    //Toast.makeText(this@LogsActivity, "Selected Month: $selectedMonth, Year: $selectedYear", Toast.LENGTH_SHORT).show()
                              },
                              today.get(Calendar.YEAR),  // Set the current year
                              today.get(Calendar.MONTH) // Set the current month
                        )

                        builder.setActivatedMonth(today.get(Calendar.MONTH)) // Activate current month by default
                              .setActivatedYear(today.get(Calendar.YEAR))      // Activate current year by default
                              .setMinYear(1990)
                              .setMaxYear(2035)
                              .setMinMonth(Calendar.JANUARY)
                              .setTitle("Select trading month")
                              .setMonthRange(Calendar.JANUARY, Calendar.DECEMBER)
                              .setOnMonthChangedListener { selectedMonth ->
                                    // Handle month change here
                              }
                              .setOnYearChangedListener { selectedYear ->
                                    // Handle year change here
                              }
                              .build()
                              .show()

                  }
            }
      }

      private fun bindBaLogsObserver() {
            viewModel.baLogData.observe(this, Event.EventObserver(onError = {
                  alertDialog!!.dismiss()
                  Globals.warningMessage(this, it)
            }, onLoading = {
                  alertDialog!!.show()
            }, { response ->
                  alertDialog!!.dismiss()
                  if (response.status == 200) {
                        //todo set dealer, special and additional discount
                        if (response.data?.isNotEmpty() == true) {
                              binding.ivNoDataFound.visibility = View.GONE
                              binding.rvLogs.visibility  = View.VISIBLE
                              setAdapter(response.data)
                        }else{
                              binding.ivNoDataFound.visibility = View.VISIBLE
                              binding.rvLogs.visibility  = View.GONE
                        }
                  } else if (response.status == 201) {
                        response.message?.let { Globals.warningMessage(this, it) }
                  } else if (response.status == 401) {
                        //sessionManagement.ClearSession()
                        PrefsByShubh.ClearSession()
                        Globals.logoutScreen(this)
                  }
            }))
      }

      private fun initView() {
            //setAdapter()
            setUpViewModel()
            builder = AlertDialog.Builder(this)
            builder!!.setView(com.preetTractor.galaxyAndroid.R.layout.progress_dialog_alert)
                  .setCancelable(false)
            alertDialog = builder!!.create()
            val calendar = Calendar.getInstance()
            val currentMonth = calendar.get(Calendar.MONTH)  // Months are 0-based, so add 1
            val currentYear = calendar.get(Calendar.YEAR)
            val monthName = java.text.DateFormatSymbols().months[currentMonth]

            // Set the formatted string into the TextView
            val formattedDate = "$monthName, $currentYear"
            binding.tvMonthFilter.text = formattedDate
            callLogsApi(currentYear,currentMonth)


      }

      private fun callLogsApi(currentYear:Int,currentMonth: Int){
            val (firstDate,lastDate) =getMonthFirstLastDateOfSelectedMonthYear(currentYear,currentMonth)
            val jsonObj = JsonObject().apply {
                  addProperty("SalesPersonCode", PrefsByShubh.getSalesEmployeeCode())
                  addProperty("NoOfBP", 10000000)
                  addProperty("FromDate", firstDate)
                  addProperty("ToDate", lastDate)
            }
            viewModel.getDashboardLogs(jsonObj, this@LogsActivity)
            bindBaLogsObserver()
      }


      private fun setAdapter(data: List<ModelAllLogs.Data?>) {
            Log.e("AdapterList","LogList: \n${toPrettyJson(data)}")
            binding.rvLogs.apply {
                  layoutManager = LinearLayoutManager(this@LogsActivity, LinearLayoutManager.VERTICAL, false)
                  adapterLogs = LogsAdapter(this@LogsActivity, data)
                  adapter = adapterLogs
                  adapterLogs.notifyDataSetChanged()
            }
      }
}