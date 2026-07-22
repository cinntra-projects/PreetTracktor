package com.preetTractor.galaxyAndroid.ui.fragment

import Event
import android.R
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.gson.JsonObject
import com.preetTractor.galaxyAndroid.adapter.CustomerBeatPlanAdapter
import com.preetTractor.galaxyAndroid.data.AchievementData
import com.preetTractor.galaxyAndroid.data.BdrcModel
import com.preetTractor.galaxyAndroid.data.beatplan.DataBeatPlan
import com.preetTractor.galaxyAndroid.databinding.CustomerBeatPlanBinding
import com.preetTractor.galaxyAndroid.databinding.DialogBdrcInputBinding
import com.preetTractor.galaxyAndroid.helper.Globals
import com.preetTractor.galaxyAndroid.helper.Globals.transformIntoDatePicker
import com.preetTractor.galaxyAndroid.helper.PrefsByShubh
import com.preetTractor.galaxyAndroid.mvvmSetUp.MainViewModel
import com.preetTractor.galaxyAndroid.retrofit.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.text.equals

class CustomerBeatPlanFragment : Fragment() {

    private lateinit var sharedViewModel: MainViewModel
    private lateinit var binding: CustomerBeatPlanBinding
    private var adapter: CustomerBeatPlanAdapter? = null
    private var cardCode: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        cardCode = arguments?.getString(ARG_CARD_CODE)
    }

    companion object {

        private const val ARG_CARD_CODE = "cardCode"

        fun newInstance(cardCode: String): CustomerBeatPlanFragment {
            return CustomerBeatPlanFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_CARD_CODE, cardCode)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = CustomerBeatPlanBinding.inflate(layoutInflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        binding.tvDate.text = Globals.getTodaysDate()
        callApi()
        setUpObserver()
        eventListener()

    }

    private fun eventListener() {
        binding.tvDate.transformIntoDatePicker(requireContext(), "dd-MM-yyyy", null) {
            callApi()
        }
    }

    private fun callApi() {
        val date = Globals.convert_dd_MM_yyyy_into_yyyy_MM_dd(binding.tvDate.text.toString())
        if (Globals.checkForInternet(requireContext())) {
            val jsonObject = JsonObject().apply {
                addProperty("SalesEmployeeCode", PrefsByShubh.getSalesEmployeeCode())
                addProperty("Status", "")
                addProperty("Latitude", Globals.globalLatitude)
                addProperty("Longitude", Globals.globalLongitude)
                addProperty("Date", date)
                addProperty("Priority", "")
                addProperty("CardCode", cardCode)
            }

            sharedViewModel.getBeatPlanListing(jsonObject, requireContext())
        }
    }

    private fun setUpObserver() {

        sharedViewModel.beatPlanItemAllList.observe(
            requireActivity(),
            Event.EventObserver(onError = {
                binding.loader.loader.isVisible = false
                binding.noDataFound.ivNoDataFound.visibility = View.VISIBLE
            }, onLoading = {
                binding.loader.loader.isVisible = true
            }, onSuccess = { response ->
                binding.loader.loader.isVisible = false
                if (response.data.isEmpty()) {
                    binding.noDataFound.ivNoDataFound.visibility = View.VISIBLE
                } else {
                    binding.noDataFound.ivNoDataFound.visibility = View.GONE

                }
                setUpRecyclerview(response.data)
            })
        )

        sharedViewModel.refreshList.observe(viewLifecycleOwner) { it ->
            adapter?.refreshList(it)
        }
    }

    private fun setUpRecyclerview(data: List<DataBeatPlan>) {

        adapter = CustomerBeatPlanAdapter(data.toMutableList()) { beatPlan ->
            if (binding.tvDate.text.toString() == Globals.getTodaysDate()) openBDRCEditDialog(
                beatPlan.id
            )
        }

        binding.rvBeatPlan.apply {
            adapter = this@CustomerBeatPlanFragment.adapter
        }
        if(data.isNotEmpty() && data[0].Visit_Status.equals("Arrived", ignoreCase = true)){
            sharedViewModel.editAdapterData(true)
        }
    }

    private fun openBDRCEditDialog(beatPlanId: Int) {
        val dialog = Dialog(requireContext())

        val dialogBinding = DialogBdrcInputBinding.inflate(layoutInflater)

        dialog.setContentView(dialogBinding.root)

        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )

        dialog.window?.setBackgroundDrawableResource(R.color.transparent)

        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()

        dialogBinding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialogBinding.btnDone.setOnClickListener {
            val achievementData = AchievementData(
                BillingAchieved = if (dialogBinding.actvBilling.text.isNotEmpty()) Integer.parseInt(
                    dialogBinding.actvBilling.text.toString()
                )
                else 0,
                CollectionAchieved = if (dialogBinding.actvCollection.text.isNotEmpty()) Integer.parseInt(
                    dialogBinding.actvCollection.text.toString()
                )
                else 0,
                DeliveryAchieved = if (dialogBinding.actvDelivery.text.isNotEmpty()) Integer.parseInt(
                    dialogBinding.actvDelivery.text.toString()
                )
                else 0,
                RetailAchieved = if (dialogBinding.actvRetail.text.isNotEmpty()) Integer.parseInt(
                    dialogBinding.actvRetail.text.toString()
                )
                else 0
            )
            callBDRCsaveAPI(beatPlanId, achievementData)
            dialog.dismiss()
        }
    }

    private fun callBDRCsaveAPI(beatPlanId: Int, achievementData: AchievementData) {
        val date = Globals.convert_dd_MM_yyyy_into_yyyy_MM_dd(binding.tvDate.text.toString())
        val jsonObject = JsonObject().apply {
            addProperty("Date", date)
            addProperty("CardCode", cardCode)
            addProperty("BeatPlanId", beatPlanId)

            add("Achievements", JsonObject().apply {
                addProperty("BillingAchieved", achievementData.BillingAchieved)
                addProperty("DeliveryAchieved", achievementData.DeliveryAchieved)
                addProperty("RetailAchieved", achievementData.RetailAchieved)
                addProperty("CollectionAchieved", achievementData.CollectionAchieved)
            })
        }
        val call = RetrofitClient.apiService.saveBDRCData(
            "Token " + Globals.GalaxyVistaToken.toString(),
            jsonObject
        )
        call.enqueue(object : Callback<BdrcModel> {
            override fun onResponse(
                call: Call<BdrcModel>, response: Response<BdrcModel>
            ) {

                response.body()?.let {

                    if (it.status == 200) {
                        Toast.makeText(requireContext(), "Update Successfully", Toast.LENGTH_SHORT)
                            .show()
                    }else{
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<BdrcModel>, t: Throwable) {
                Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_SHORT).show()
            }
        })
    }
}


