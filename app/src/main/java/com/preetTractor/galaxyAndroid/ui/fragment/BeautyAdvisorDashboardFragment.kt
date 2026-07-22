package com.preetTractor.galaxyAndroid.ui.fragment

import Event
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.preetTractor.galaxyAndroid.R
import com.preetTractor.galaxyAndroid.data.ba.ModelBaTargetSales
import com.preetTractor.galaxyAndroid.data.ba.ModelTargetVsAchievedSales
import com.preetTractor.galaxyAndroid.databinding.FragmentBeautyAdvisorDashboardBinding
import com.preetTractor.galaxyAndroid.helper.Globals
import com.preetTractor.galaxyAndroid.helper.Globals.formatDoublevlauUpToTwoDecimal
import com.preetTractor.galaxyAndroid.helper.Globals.getCurrentFinancialYear
import com.preetTractor.galaxyAndroid.helper.PrefsByShubh
import com.preetTractor.galaxyAndroid.mvvmSetUp.MainViewModel
import com.preetTractor.galaxyAndroid.ui.activity.MainActivity
import com.preetTractor.galaxyAndroid.ui.activity.log.LogsActivity
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.gson.Gson
import com.google.gson.JsonObject
import org.json.JSONArray

class BeautyAdvisorDashboardFragment : Fragment() {
      private lateinit var binding:FragmentBeautyAdvisorDashboardBinding
      lateinit var viewModel: MainViewModel
      private lateinit var financialYear: Pair<String, String>

      override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
      }

      @RequiresApi(Build.VERSION_CODES.O)
      override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
      ): View? {
            // Inflate the layout for this fragment
            binding = FragmentBeautyAdvisorDashboardBinding.inflate(inflater,container,false)
            initView()
            setListener()
            return binding.root
      }

      private fun setListener() {
            binding.apply {
                  cvLogs.setOnClickListener {
                        startActivity(Intent(requireContext(), LogsActivity::class.java))
                  }
            }
      }

      @RequiresApi(Build.VERSION_CODES.O)
      private fun initView() {
            financialYear = getCurrentFinancialYear()
            viewModel = (activity as MainActivity).viewModel
           /* callTargetIncentivesApi()
            callTotalSalesApi()
            callTargetAssignedApi()*/
      }

      private fun callTotalSalesApi() {
            viewModel.getDashboardTotalSales(JsonObject().apply {
                  addProperty("SalesPersonCode", PrefsByShubh.getSalesEmployeeCode())
                  addProperty("FromDate", financialYear.first)
                  addProperty("ToDate", financialYear.second)
            }, requireContext())
            bindTotalSalesObserver()
      }

      private fun bindTotalSalesObserver() {
            viewModel.totalTargetSales.observe(
                  viewLifecycleOwner,
                  Event.EventObserver(onError = {
                        //binding.spinKitLoader.visibility = View.GONE
                        Globals.warningMessage(requireContext(), it)
                  }, onLoading = {
                        //binding.spinKitLoader.visibility = View.VISIBLE
                  }, { response ->
                        if (response.status == 200) {
                              //binding.spinKitLoader.visibility = View.VISIBLE
                              if (response.data.isNotEmpty()) {
                                    response.data.let {
                                          setupSalesGraph(it)
                                    }
                              }
                        } else if (response.status == 401) {
                              //sessionManagement.ClearSession()
                              PrefsByShubh.ClearSession()
                              Globals.logoutScreen(requireContext())

                        } else {
                              Globals.warningMessage(requireContext(), response.message)
                        }
                  })
            )
      }

      private fun setupSalesGraph(salesList: List<ModelBaTargetSales.Data>) {
            // Parse API Response
            val gson = Gson()
            val jsonString = gson.toJson(salesList)

// Convert the JSON string to a JSONArray
            val jsonArray = JSONArray(jsonString)

// Prepare Data
            val entries = ArrayList<Entry>()
            val months = ArrayList<String>()

            for (i in 0 until jsonArray.length()) {
                  val jsonObject = jsonArray.getJSONObject(i)
                  val sales = jsonObject.getDouble("MonthlyAchievedSales").toFloat()
                  val monthOriginal = jsonObject.getString("Month")
                  val parts = monthOriginal.split("-")
                  val month = parts[0].substring(0, 3) // Get the first 3 characters of the month
                  val year = parts[1] // Get the year

                  /*val parts = monthOriginal.split("-")

                  // Extract the month and the last two digits of the year
                  val month = parts[0]
                  val year = parts[1].takeLast(2)*/  // Get last two digits of the year

                  // Combine the formatted result
                  val formattedResult = "$month-$year"
                  Log.e("GRAPH", "Months: $formattedResult")

                  entries.add(Entry(i.toFloat(), sales))
                  months.add(month)
            }

            // Optionally, you can also hide the legend, description, etc., if you want
            binding.mpChartSales.legend.isEnabled = false // Hide the legend
            //binding.mpChartSales.description.isEnabled = false // Hide the description text

            // Create LineDataSet
            val lineDataSet = LineDataSet(entries, "Sales")
            lineDataSet.color = resources.getColor(R.color.blue)
            lineDataSet.circleRadius = 4f
            lineDataSet.setCircleColor(resources.getColor(R.color.blue))
            lineDataSet.lineWidth = 2f

            // Disable grid lines on the axes
            val leftAxis = binding.mpChartSales.axisLeft
            leftAxis.setDrawGridLines(false)

            val rightAxis = binding.mpChartSales.axisRight
            rightAxis.setDrawGridLines(false)
            rightAxis.isEnabled = false // Disable right Y-axis

            val xAxis = binding.mpChartSales.xAxis
            xAxis.position = XAxis.XAxisPosition.BOTTOM // Place X-axis at the bottom
            xAxis.setDrawGridLines(false) // Disable X-axis grid lines
            xAxis.granularity = 1f // Ensure one label per interval
            xAxis.labelCount = months.size // Show all months
            //xAxis.labelRotationAngle = -90f
            xAxis.valueFormatter = object : ValueFormatter() {
                  override fun getFormattedValue(value: Float): String {
                        return months.getOrNull(value.toInt()) ?: "" // Map X-axis value to month name
                  }
            }

            // Optionally disable chart borders (if any are set)
            binding.mpChartSales.setDrawBorders(false)

            // Configure LineChart
            val lineData = LineData(lineDataSet)
            binding.mpChartSales.data = lineData

            val description = Description()
            description.text = "" //Total Sales
            binding.mpChartSales.description = description

            binding.mpChartSales.animateX(1200)
            binding.mpChartSales.animateY(1200)
            binding.mpChartSales.invalidate() // Refresh the chart

      }

      private fun callTargetAssignedApi() {

            viewModel.getDashboardTargetVsAchieved(JsonObject().apply {
                  addProperty("SalesPersonCode", PrefsByShubh.getSalesEmployeeCode())
                  addProperty("FromDate", financialYear.first)
                  addProperty("ToDate", financialYear.second)
            }, requireContext())
            bindTargetAssignedObserver()
      }

      private fun bindTargetAssignedObserver() {
            viewModel.targetVsAchievedSales.observe(
                  viewLifecycleOwner,
                  Event.EventObserver(onError = {
                        //binding.spinKitLoader.visibility = View.GONE
                        Globals.warningMessage(requireContext(), it)
                  }, onLoading = {
                        //binding.spinKitLoader.visibility = View.VISIBLE
                  }, { response ->
                        if (response.status == 200) {
                              //binding.spinKitLoader.visibility = View.VISIBLE
                              if (response.data.isNotEmpty()) {
                                    response.data.let {
                                          setUpTargetAchievedGraph(it)
                                          //setUpStackedBarChart(binding.mpChartTargetAchieved,it)
                                    }
                              }
                        } else if (response.status == 401) {
                              //sessionManagement.ClearSession()
                              PrefsByShubh.ClearSession()
                              Globals.logoutScreen(requireContext())

                        } else {
                              Globals.warningMessage(requireContext(), response.message)
                        }
                  })
            )
      }

      private fun setUpStackedBarChart(barChart: BarChart, data: List<ModelTargetVsAchievedSales.Data>) {
            // Prepare data entries
            val entries = ArrayList<BarEntry>()
            val months = ArrayList<String>() // X-axis labels

            for ((index, item) in data.withIndex()) {
                  val targetSales = item.MonthlyTargetSales.toFloat()
                  val achievedSales = item.MonthlyAchievedSales.toFloat()

                  // Add stacked data (achieved sales and target sales in one bar)
                  entries.add(BarEntry(index.toFloat(), floatArrayOf(targetSales, achievedSales)))

                  // Add month for X-axis
                  months.add(item.Month)
            }

            // Create a BarDataSet
            val dataSet = BarDataSet(entries, "Target vs Achieved")
            dataSet.setColors(Color.LTGRAY, resources.getColor(R.color.blue)) // Assign colors for target and achieved
            dataSet.stackLabels = arrayOf("Target Assigned", "Target Achieved")

            // Prepare BarData
            val barData = BarData(dataSet)
            barData.barWidth = 0.4f // Adjust bar width

            // Configure X-axis
            val xAxis = barChart.xAxis
            xAxis.valueFormatter = IndexAxisValueFormatter(months) // Set month names on the X-axis
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.setDrawGridLines(false)
            xAxis.granularity = 1f // Ensure labels are shown for each bar
            xAxis.labelRotationAngle = -45f // Optional: Rotate labels if they overlap

            // Configure Y-axis
            barChart.axisLeft.axisMinimum = 0f // Ensure Y-axis starts at 0
            barChart.axisRight.isEnabled = false // Disable the right Y-axis

            // General Chart Settings
            barChart.data = barData
            barChart.description.isEnabled = false // Disable the description label
            barChart.legend.isEnabled = true // Show legend for stack labels
            barChart.setFitBars(true) // Ensure bars fit in the chart view
            barChart.animateX(1200)
            barChart.animateY(1200)
            barChart.invalidate() // Refresh the chart
      }

      private fun setUpTargetAchievedGraph(data: List<ModelTargetVsAchievedSales.Data>) {


// Parse JSON Data

            // Parse API Response
            val gson = Gson()
            val jsonString = gson.toJson(data)

// Convert the JSON string to a JSONArray
            val jsonArray = JSONArray(jsonString)

            val targetSalesEntries = ArrayList<BarEntry>()
            val achievedSalesEntries = ArrayList<BarEntry>()
            val months = ArrayList<String>()

            for (i in 0 until jsonArray.length()) {
                  val item = jsonArray.getJSONObject(i)
                  val targetSales = item.getDouble("MonthlyTargetSales").toFloat()
                  val achievedSales = item.getDouble("MonthlyAchievedSales").toFloat()
                  //val month = item.getString("Month").split("-")[0] // Extract month (e.g., "Apr")

                  val monthOriginal = item.getString("Month")
                  val parts = monthOriginal.split("-")
                  val month = parts[0].substring(0, 3) // Get the first 3 characters of the month
                  val year = parts[1] // Get the year

// Combine the formatted result
                  val formattedResult = "$month\n$year"
                  Log.e("GRAPH", "Months: $formattedResult")

                  targetSalesEntries.add(BarEntry(i.toFloat(), targetSales))
                  achievedSalesEntries.add(BarEntry(i.toFloat(), achievedSales))
                  months.add(month) // Add month to x-axis labels
            }

            // Optionally, you can also hide the legend, description, etc., if you want
            binding.mpChartTargetAchieved.legend.isEnabled = false // Hide the legend
            //binding.mpChartTargetAchieved.description.isEnabled = false // Hide the description text

            // Disable grid lines on the axes
            val leftAxis = binding.mpChartTargetAchieved.axisLeft
            leftAxis.setDrawGridLines(false)

            val rightAxis = binding.mpChartTargetAchieved.axisRight
            rightAxis.setDrawGridLines(false)
            rightAxis.isEnabled = false // Disable right Y-axis
// Create BarDataSet
            val targetDataSet = BarDataSet(targetSalesEntries, "Target Assigned")
            targetDataSet.color = Color.LTGRAY

            val achievedDataSet = BarDataSet(achievedSalesEntries, "Target Achieved")
            achievedDataSet.color = resources.getColor(R.color.blue)

// Combine datasets into BarData
            val barData = BarData(targetDataSet, achievedDataSet)
            barData.barWidth = 0.24f // Set bar width

// Initialize binding.mpChartTargetAchieved (Assume it's in your layout with ID binding.mpChartTargetAchieved)
            binding.mpChartTargetAchieved.data = barData

// Configure X-Axis
            val xAxis = binding.mpChartTargetAchieved.xAxis
            xAxis.position = XAxis.XAxisPosition.BOTTOM // Place X-axis at the bottom
            xAxis.setDrawGridLines(false) // Disable X-axis grid lines
            xAxis.granularity = 1f // Ensure one label per interval
            xAxis.labelCount = months.size // Show all months
            xAxis.setDrawGridLines(false)
            xAxis.valueFormatter = object : ValueFormatter() {
                  override fun getFormattedValue(value: Float): String {
                        return months.getOrNull(value.toInt()) ?: "" // Map X-axis value to month name
                  }
            }



            /*val xAxis = binding.mpChartTargetAchieved.xAxis
            xAxis.valueFormatter = IndexAxisValueFormatter(xAxisLabels) // Set custom labels
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.granularity = 1f*/ // Ensure labels align with bars
// Optionally, you can also hide the legend, description, etc., if you want
            binding.mpChartTargetAchieved.legend.isEnabled = false // Hide the legend
            //binding.mpChartTargetAchieved.description.isEnabled = false // Hide the description text

// Configure Y-Axis
            binding.mpChartTargetAchieved.axisLeft.axisMinimum = 0f
            binding.mpChartTargetAchieved.axisRight.isEnabled = false

// Style Chart
            binding.mpChartTargetAchieved.description.isEnabled = false
            binding.mpChartTargetAchieved.legend.isEnabled = true
            binding.mpChartTargetAchieved.setFitBars(true) // Adjust bars to fit within the chart
            binding.mpChartTargetAchieved.animateX(1200)
            binding.mpChartTargetAchieved.animateY(1200)

// Group Bars
            binding.mpChartTargetAchieved.groupBars(0f, 0.6f, 0.05f) // Group bars with spacing
            binding.mpChartTargetAchieved.invalidate() // Refresh chart

      }

      private fun callTargetIncentivesApi() {
            val role_id = PrefsByShubh.getString("role_id", "")
            val salesEmpId = PrefsByShubh.getSalesEmployeeCode()
            viewModel.getDashboardIncentive(JsonObject().apply {
                  addProperty("Role", role_id)
                  addProperty("SalesEmployeeCode", PrefsByShubh.getSalesEmployeeCode())
            }, requireContext())
            bindIncentiveObserver()
      }

      private fun bindIncentiveObserver() {
            viewModel.totalIncentives.observe(
                  viewLifecycleOwner,
                  Event.EventObserver(onError = {
                        //binding.spinKitLoader.visibility = View.GONE
                        Globals.warningMessage(requireContext(), it)
                  }, onLoading = {
                        //binding.spinKitLoader.visibility = View.VISIBLE
                  }, { response ->
                        if (response.status == 200) {
                              //binding.spinKitLoader.visibility = View.VISIBLE
                              if (response.data.isNotEmpty()) {
                                    response.data.let {
                                          binding.tvIncentiveAmount.text = formatDoublevlauUpToTwoDecimal(response.data[0].TotalIncentiveCalculations)
                                    }
                              }
                        } else if (response.status == 401) {
                              //sessionManagement.ClearSession()
                              PrefsByShubh.ClearSession()
                              Globals.logoutScreen(requireContext())

                        } else {
                              Globals.warningMessage(requireContext(), response.message)
                        }
                  })
            )
      }

}