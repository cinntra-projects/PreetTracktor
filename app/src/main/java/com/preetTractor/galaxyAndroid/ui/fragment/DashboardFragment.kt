package com.preetTractor.galaxyAndroid.ui.fragment

import android.app.AlertDialog
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
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.gson.JsonObject
import com.preetTractor.galaxyAndroid.R
import com.preetTractor.galaxyAndroid.data.DashBoardCounterResponse
import com.preetTractor.galaxyAndroid.data.ResponseGlobal
import com.preetTractor.galaxyAndroid.data.beatplan.DataBeatPlan
import com.preetTractor.galaxyAndroid.data.beatplan.ResponseBeatPlan
import com.preetTractor.galaxyAndroid.data.model.TodayVisitDashboardResponse
import com.preetTractor.galaxyAndroid.databinding.DialogRescheduleBeatPlanBinding
import com.preetTractor.galaxyAndroid.databinding.FragmentDashboardBinding
import com.preetTractor.galaxyAndroid.helper.Globals
import com.preetTractor.galaxyAndroid.helper.Globals.todayBeatPlanList
import com.preetTractor.galaxyAndroid.helper.Globals.transformIntoDatePickerWithDisablePastDates
import com.preetTractor.galaxyAndroid.helper.Globals.transformIntoTimePicker
import com.preetTractor.galaxyAndroid.helper.PrefsByShubh
import com.preetTractor.galaxyAndroid.retrofit.RetrofitClient
import com.preetTractor.galaxyAndroid.ui.activity.AddLeadBeatPlanActivity
import com.preetTractor.galaxyAndroid.ui.activity.AddNonCustomerActivity
import com.preetTractor.galaxyAndroid.ui.activity.BeatPlanActivity
import com.preetTractor.galaxyAndroid.ui.activity.BeatPlanActivity2
import com.preetTractor.galaxyAndroid.ui.activity.LeadsActivity
import com.preetTractor.galaxyAndroid.ui.activity.beat.AddBeatPlanActivity
import com.preetTractor.galaxyAndroid.ui.activity.customermodule.CustomerActivity
import com.preetTractor.galaxyAndroid.ui.recyclerview.BeatPlanListingAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class DashboardFragment : Fragment() {

    lateinit var binding: FragmentDashboardBinding

    //    private lateinit var adapter: BeatPlanListListingAdapter
    private lateinit var adapter: BeatPlanListingAdapter // add by tarun

    var todayVisitValue = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentDashboardBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        callSuperAdminApiForToken()
        binding.addCustomer.setOnClickListener {

            var intent = Intent(requireContext(), CustomerActivity::class.java)
            startActivity(intent)
        }
        binding.leadCard.setOnClickListener {

                var intent = Intent(requireContext(), LeadsActivity::class.java)
                startActivity(intent)
            }



        binding.myPlans.setOnClickListener {
            val intent = Intent(requireActivity(), BeatPlanActivity::class.java)
            startActivity(intent)
        }


    }

    private fun callSuperAdminApiForToken() {

        val jsonObject = JsonObject().apply {
            addProperty("email", PrefsByShubh.getUserEmail())
            addProperty("password", PrefsByShubh.getUserPassword())
            addProperty("FCM", PrefsByShubh.getFirebaseFCMToken())
            addProperty("app_id", PrefsByShubh.getUserAppId())
        }

        val call = RetrofitClient.apiService1.getLoginToken(jsonObject)
        call.enqueue(object : Callback<JsonObject?> {
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                if (response.isSuccessful) {
                    response.body()?.let { jsonResponse ->
                        if (jsonResponse.has("status") && jsonResponse.get("status").asInt == 200) {
                            // Store token globally
                            Globals.GalaxyVistaToken = jsonResponse.get("token").asString
                            Log.d("superAdminToken", "Token Stored: ${Globals.GalaxyVistaToken}")

                            callTodayVisitApi()

                        } else {
                            Log.e(
                                "superAdminToken",
                                "Login Failed: ${jsonResponse.get("message").asString}"
                            )
                        }
                    }
                } else {
                    Log.e("superAdminToken", "Response Error: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                Log.e("superAdminToken", "API Failure: ${t.message}")
            }
        })


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


    private fun callTodayVisitApi() {
        val jsonObject = JsonObject()
        jsonObject.addProperty("SalesEmployeeCode", PrefsByShubh.getSalesEmployeeCode())
        jsonObject.addProperty("FromDate", "")
        jsonObject.addProperty("ToDate", "")
        val call: Call<TodayVisitDashboardResponse> =
            RetrofitClient.apiService.getTodayVisitDashboard(jsonObject)
        call.enqueue(object : Callback<TodayVisitDashboardResponse?> {
            override fun onResponse(
                call: Call<TodayVisitDashboardResponse?>,
                response: Response<TodayVisitDashboardResponse?>
            ) {
                if (response.body() != null && response.body()!!.status.equals("200")) {

                    if (response.body()!!.data.isNotEmpty()) {
                        val module = response.body()!!.data[0]
                        todayVisitValue = "${module.completed_visit}/${module.total_visit}"
                        binding.tvVisitNo.text = todayVisitValue
                        setUpVisitProgressBar(todayVisitValue, binding.visitIndicator)
                    }
                } else if (response.body() != null && response.code() == 201) {
                    Toast.makeText(requireContext(), response.body()!!.message, Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onFailure(call: Call<TodayVisitDashboardResponse?>, t: Throwable) {
                Log.e(TAG, "onFailure: " + t.message)
            }
        })



    }

    private fun callDashboardCounterApi() {
        val jsonObject = JsonObject()
        jsonObject.addProperty("SalesEmployeeCode", PrefsByShubh.getEmpCode())
        jsonObject.addProperty("FromDate", "")
        jsonObject.addProperty("ToDate", "")
        val countercall: Call<DashBoardCounterResponse> =
            RetrofitClient.apiService.getDashBoardData(jsonObject)
        countercall.enqueue(object : Callback<DashBoardCounterResponse?> {
            override fun onResponse(
                call: Call<DashBoardCounterResponse?>,
                response: Response<DashBoardCounterResponse?>
            ) {
                if (response.body() != null) {

                    if (response.body()!!.data.isNotEmpty()) {
                        binding.leadCount.text = "Total Leads : "+response.body()!!.data[0].Leads.toString()

                        binding.billingAchieveCounter.text =  response.body()!!.data[0].AchievementData.BillingAchieved.toInt().toString()
                        binding.billingTarget.text =response.body()!!.data[0].TargetData.BillingTarget.toInt().toString()
                        binding.billingAllCounterText.text = response.body()!!.data[0].AchievementData.BillingAchieved.toInt().toString() + " of " + response.body()!!.data[0].TargetData.BillingTarget.toInt().toString() + " completed"
                        val billingprogress = (response.body()!!.data[0].AchievementData.BillingAchieved.toFloat() / response.body()!!.data[0].TargetData.BillingTarget.toFloat()) *100
                        binding.billingIndicator.progress = billingprogress.toInt()
                        binding.billingProgressNo.text = billingprogress.toInt().toString() + "%"

                        binding.deliveryAchieveCounter.text = response.body()!!.data[0].AchievementData.DeliveryAchieved.toInt().toString()
                        binding.deliveryTarget.text =response.body()!!.data[0].TargetData.DeliveryTarget.toInt().toString()
                        binding.deliveryAllCounterText.text = response.body()!!.data[0].AchievementData.DeliveryAchieved.toInt().toString() + " of " + response.body()!!.data[0].TargetData.DeliveryTarget.toInt().toString() + " completed"
                        val deliveryprogress = (response.body()!!.data[0].AchievementData.DeliveryAchieved.toFloat() / response.body()!!.data[0].TargetData.DeliveryTarget.toFloat())*100
                        binding.deliveryIndicator.progress = deliveryprogress.toInt()
                        binding.deliveryProgressNo.text = deliveryprogress.toInt().toString() + "%"

                        binding.retailAchieveCounter.text = response.body()!!.data[0].AchievementData.RetailAchieved.toInt().toString()
                        binding.retailTarget.text = response.body()!!.data[0].TargetData.RetailTarget.toInt().toString()
                        binding.retailAllCounterText.text = response.body()!!.data[0].AchievementData.RetailAchieved.toInt().toString() + " of " + response.body()!!.data[0].TargetData.RetailTarget.toInt().toString() + " completed"
                        val retailprogress =( response.body()!!.data[0].AchievementData.RetailAchieved.toFloat() / response.body()!!.data[0].TargetData.RetailTarget.toFloat())*100
                        binding.retailIndicator.progress = retailprogress.toInt()
                        binding.retailProgressNo.text = retailprogress.toInt().toString() + "%"


                        binding.collectionTarget.text = response.body()!!.data[0].TargetData.CollectionTarget.toInt().toString()
                        binding.collectionAchieveCounter.text = response.body()!!.data[0].AchievementData.CollectionAchieved.toInt().toString()
                        binding.collectionAllCounterText.text = response.body()!!.data[0].AchievementData.CollectionAchieved.toInt().toString() + " of " + response.body()!!.data[0].TargetData.CollectionTarget.toInt().toString() + " completed"
                        val collectionprogress = (response.body()!!.data[0].AchievementData.CollectionAchieved.toFloat() / response.body()!!.data[0].TargetData.CollectionTarget.toFloat())*100
                        binding.collectionIndicator.progress = collectionprogress.toInt()
                        binding.collectionProgressNo.text = collectionprogress.toInt().toString() + "%"


                    }
                } else if (response.body() != null && response.code() == 201) {
                    Toast.makeText(requireContext(), response.body()!!.message, Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onFailure(call: Call<DashBoardCounterResponse?>, t: Throwable) {
                Log.e(TAG, "onFailure: " + t.message)
            }
        })
    }

    private fun setUpVisitProgressBar(
        todayVisitValue: String, visitIndicator: CircularProgressIndicator
    ) {

        val visitText = todayVisitValue
        Log.d("ProgressIndicator", "Progress: $visitText")
        if (visitText.contains("/")) {
            try {
                // Split the text to extract the numerator and denominator
                val parts = visitText.split("/")
                val completedVisits = parts[0].trim().toInt()
                val totalVisits = parts[1].trim().toInt()

                if (totalVisits > 0) {
                    val progressFraction = completedVisits.toFloat() / totalVisits
                    val progressPercentage = (progressFraction * 100).toInt()

                    visitIndicator.progress = progressPercentage

                    // Optional: Log the calculated progress
                    Log.d("ProgressIndicator", "Calculated Progress: $progressPercentage%")
                } else {
                    Log.e("ProgressIndicator", "Total visits cannot be zero.")
                }
            } catch (e: NumberFormatException) {
                Log.e("ProgressIndicator", "Invalid format in TextView: ${e.message}")
            }
        } else {
            Log.e("ProgressIndicator", "TextView text does not contain '/' separator.")
        }

    }

    override fun onResume() {
        super.onResume()
        callDashboardCounterApi()
        getListing()
    }


    private var allItemList = ArrayList<DataBeatPlan>()

    private fun getListing() {
        todayBeatPlanList.clear()
        val hde = JsonObject().apply {
            addProperty("SalesEmployeeCode", Globals.SalesEmployeeCode)
            addProperty("Date", Globals.getTodaysDatervrsfrmt())
            addProperty("Status", "today")
            addProperty("Priority", "")
            addProperty("Latitude", Globals.globalLatitude)
            addProperty("Longitude", Globals.globalLongitude)
            addProperty("Level", "all")

        }

        val call = RetrofitClient.apiService.galaxyBeatPlanList(hde)

        call.enqueue(object : Callback<ResponseBeatPlan> {
            override fun onResponse(
                call: Call<ResponseBeatPlan>, response: Response<ResponseBeatPlan>
            ) {
                response.body()?.let {
                    if (it.status.equals("200", ignoreCase = true)) {


                        allItemList.clear()
                        allItemList.addAll(it.data)
//                        adapter = BeatPlanListListingAdapter(allItemList, requireContext())
                        adapter = BeatPlanListingAdapter(
                            allItemList, requireContext(), ""
                        ) // add by tarun

                        adapter.setOnItemRescheduleClickListener { item ->
                            if (item.Approval_Status.equals("Pending", ignoreCase = true)) {
                                openRescheduleDialog(item)
                            } else {
                                Globals.warningMessage(requireContext(), "Cannot reschedule")
                            }

                        }

                        if (it.data.isNotEmpty()) {
                            for (current in it.data) {

                                Globals.assignedTo.add(current.AssignedTo)
//                                val cureentBeat = LocalDataTodayBeatPlan(
//                                    id = current.id.toString(),
//                                    approval_status = current.Approval_Status,
//                                    City = current.City
//                                )
//                                todayBeatPlanList.add(cureentBeat)
                            }
                        } else {
                            Globals.assignedTo.clear()
                            todayBeatPlanList.clear()
                        }



                        // todo commit by Tarun Sharma
//                        adapter.setOnItemClickListener { _url, _msg ->
//                            Globals.openDialerWithNumber(requireContext(), _url)
//                        }
//                        adapter.setOnItemMapClickListener {
////                            Globals.openLocationInGoogleMaps(requireContext(),it.Lat,it.Long)
////                        }

                        // todo commit by Tarun Sharma
//                        adapter.setOnItemWholeClickListener {item->
//                            PrefsByShubh.setCardCode(item.CardCode)
//                            PrefsByShubh.setCardName(item.CardName)
//                            Intent(requireActivity(), CustomerDetailActivity::class.java).also {
//                                it.putExtra(Constant.WHERE_INTENT,"beatPlan")
//                                it.putExtra(Constant.WHERE_CARDCODE,item.CardCode)
//
//                                it.putExtra(Constant.WHERE_BEATPLAN_ID,item.id.toString())
//                                startActivity(it)
//                            }
//
//                        }

                        adapter.setOnItemWholeClickListener { item ->
                            val intent = Intent(requireContext(), BeatPlanActivity2::class.java)

                            intent.putExtra("fromWhere", "today")
                            intent.putExtra("id", item.id.toString())
                            intent.putExtra("Type", item.Type.toString() ?: "NA")
                            intent.putExtra("salesEmployeeCode", Globals.SalesEmployeeCode)
                            intent.putExtra("currentPage", "today")
                            intent.putExtra("currentSelectedDate", Globals.getTodaysDatervrsfrmt())
                            intent.putExtra("filterPriority", "")
                            startActivity(intent)

                        }

//

                        Log.e(TAG, "onResponseBackground: ${it.message}")
                    }
                }
            }

            override fun onFailure(call: Call<ResponseBeatPlan>, t: Throwable) {
                Log.e(TAG, "onFailure: ${t.message}")
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
                etRescheduleDate.context, "dd-MM-yyyy", null
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

                if (response.body()!!.status == 200) {
                    alertBinding.btnSave.alpha = 0.3f
                    alertBinding.btnSave.isClickable = true
                    mAlert.dismiss()

                    Globals.successMessage(requireActivity(), "Reschedule Successfully")
                    getListing()
                    Log.e(
                        TAG, "onResponseBackground: " + response.body()!!.message
                    )
                } else if (response.body()!!.status == 201) {
                    Globals.warningMessage(requireActivity(), response.message())
                    alertBinding.btnSave.alpha = 0.3f
                    alertBinding.btnSave.isClickable = true
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


    companion object {
        private const val TAG = "DashboardFragment"
    }


}