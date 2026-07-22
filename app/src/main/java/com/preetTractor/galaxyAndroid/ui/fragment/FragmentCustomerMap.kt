package com.preetTractor.galaxyAndroid.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.JsonObject
import com.preetTractor.galaxyAndroid.R
import com.preetTractor.galaxyAndroid.data.BeatPlanCustomerDropDownModel
import com.preetTractor.galaxyAndroid.data.localdata.LocationLocalData
import com.preetTractor.galaxyAndroid.databinding.BottomSheetForCustomerBinding
import com.preetTractor.galaxyAndroid.databinding.FragmentCustomerMapBinding
import com.preetTractor.galaxyAndroid.helper.Constant
import com.preetTractor.galaxyAndroid.helper.Globals
import com.preetTractor.galaxyAndroid.helper.PrefsByShubh
import com.preetTractor.galaxyAndroid.retrofit.RetrofitClient
import com.preetTractor.galaxyAndroid.ui.activity.customer.CustomerDetailActivity
import com.preetTractor.galaxyAndroid.ui.recyclerview.CustomerListingPrimaryAdapter
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val TAG = "FragmentCustomerMap"

class FragmentCustomerMap : Fragment(), OnMapReadyCallback {

    lateinit var binding: FragmentCustomerMapBinding

     var googleMap: GoogleMap?=null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentCustomerMapBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment
        Globals.CURRENT_CLASS =this::class.simpleName
        return binding.root
    }

    var customerListingPrimaryAdapter: CustomerListingPrimaryAdapter? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Dynamically add SupportMapFragment to the FrameLayout
        val mapFragment = SupportMapFragment.newInstance()
        childFragmentManager.beginTransaction()
            .replace(R.id.frameMapCustomers, mapFragment)
            .commit()

        // Get notified when the map is ready to use
        mapFragment.getMapAsync(this)


        Log.e(TAG, "onMapReady: initialize")
        CoroutineScope(Dispatchers.IO).launch {
            delay(2000)

            locationList.add(LocationLocalData(-34.0, 151.0, "Marker in Sydney"))
            locationList.add(LocationLocalData(37.7749, -122.4194, "Marker in San Francisco"))
            locationList.add(LocationLocalData(51.5074, -0.1278, "Marker in London"))
            Log.e(TAG, "onMapReady: Getting Data ${locationList}")

            withContext(Dispatchers.Main) {
                // Add a marker for each location in the list
                for (location in locationList) {
                    val latLng = LatLng(location.latitude, location.longitude)
                    googleMap!!.addMarker(
                        MarkerOptions()
                            .position(latLng)
                            .title(location.title)
                    )
                }

                // Move the camera to the first location if the list is not empty
                if (locationList.isNotEmpty()) {
                    val firstLocation = LatLng(locationList[0].latitude, locationList[0].longitude)
                    googleMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(firstLocation, 10f))
                }
            }

        }
        showCustomerListBottomSheetDialog()

        binding.tvShowCustomers.setOnClickListener {
            showCustomerListBottomSheetDialog()
        }


    }

    val locationList = mutableListOf<LocationLocalData>()

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

    }

    private var checkedRadioButtonString = ""

    private fun showCustomerListBottomSheetDialog() {
        checkedRadioButtonString = "primary"
/*        val addOnBottomSheetDialogFragment = AddOnBottomSheetDialogFragment()
        addOnBottomSheetDialogFragment.show(childFragmentManager, "AddOnBottomSheetDialogFragment")*/
        val bottomSheetDialog = BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme)
        val bindingBottomSheet: BottomSheetForCustomerBinding =
            BottomSheetForCustomerBinding.inflate(layoutInflater)
        bottomSheetDialog.setContentView(bindingBottomSheet.getRoot())

        bottomSheetDialog.show()

        // bindingBottomSheet.rgCustomerBottomSheet.check(0)
        initialStatus(bindingBottomSheet)
        callCustomerListApi(bindingBottomSheet)


    }


    private fun initialStatus(bindingBottomSheet: BottomSheetForCustomerBinding) {
        bindingBottomSheet.rgCustomerBottomSheet.setOnCheckedChangeListener { group, checkedId ->
            //    val radio: RadioButton = requireActivity().findViewById(checkedId)
            when (checkedId) {
                R.id.rbPrimaryCustomer -> {
                    Toast.makeText(requireContext(), "Primary", Toast.LENGTH_SHORT).show()
                    checkedRadioButtonString = "primary"
                }

                R.id.rbSecondary -> {
                    checkedRadioButtonString = "Secondary"
                    Toast.makeText(requireContext(), "Secondary", Toast.LENGTH_SHORT).show()
                }

            }


        }
    }

    //todo calling customer list drop down api here---
    var customerList: ArrayList<BeatPlanCustomerDropDownModel.Data> =
        ArrayList<BeatPlanCustomerDropDownModel.Data>()

    fun callCustomerListApi(bindingBottomSheet: BottomSheetForCustomerBinding) {
        bindingBottomSheet.loadingView.visibility = View.VISIBLE
        var jsonObject = JsonObject()
        jsonObject.addProperty("SalesEmployeeCode", PrefsByShubh.getSalesEmployeeCode())
        val call = RetrofitClient.apiService.getCustomerListing(jsonObject)
        call.enqueue(object : Callback<BeatPlanCustomerDropDownModel> {
            override fun onResponse(
                call: Call<BeatPlanCustomerDropDownModel>,
                response: Response<BeatPlanCustomerDropDownModel>
            ) {

                bindingBottomSheet.loadingView.visibility = View.GONE

                response.body()?.let {

                    if (it.status == 200) {
                        customerList.clear()
                        customerList.addAll(it.data)


                        try {
                            customerListingPrimaryAdapter =
                                CustomerListingPrimaryAdapter(customerList, requireContext())
                            bindingBottomSheet.rvCustomerList.adapter =
                                customerListingPrimaryAdapter
                            bindingBottomSheet.rvCustomerList.layoutManager =
                                LinearLayoutManager(requireContext())
                            customerListingPrimaryAdapter!!.notifyDataSetChanged()

                            customerListingPrimaryAdapter!!.setOnItemClickListener { item, pos ->

                                Log.d(
                                    "PREF_SHUBH",
                                    "By Item-> ${Globals.CURRENT_CLASS}\nCardCode: ${item.CardCode}\nCardName: ${item.CardName}\nDistributorID: ${item.id}"
                                )
                                PrefsByShubh.setCardCode(item.CardCode)
                                PrefsByShubh.setCardName(item.CardName)
                                //PrefsByShubh.setDistributorID(item.id.toString())
                                Log.d(
                                    "PREF_SHUBH",
                                    "By PrefByShubh-> ${Globals.CURRENT_CLASS}\nCardCode: ${PrefsByShubh.getCardCode()}\nCardName: ${PrefsByShubh.getCardName()}\nDistributorID: ${PrefsByShubh.getDistributorID()}"
                                )

                                Intent(requireActivity(), CustomerDetailActivity::class.java).also {
                                    it.putExtra(Constant.WHERE_INTENT, "primary")
                                    it.putExtra(Constant.WHERE_CARDCODE, item.CardCode)

                                    it.putExtra(Constant.WHERE_BEATPLAN_ID, "")
                                    startActivity(it)
                                }


                            }


                            bindingBottomSheet.edtSearchActual.addTextChangedListener(object :
                                TextWatcher {
                                override fun beforeTextChanged(
                                    p0: CharSequence?,
                                    p1: Int,
                                    p2: Int,
                                    p3: Int
                                ) {

                                }

                                override fun onTextChanged(
                                    p0: CharSequence?,
                                    p1: Int,
                                    p2: Int,
                                    p3: Int
                                ) {
                                    if (customerList.isNotEmpty()){
                                        customerListingPrimaryAdapter!!.filterList(p0.toString())
                                    }


                                }

                                override fun afterTextChanged(p0: Editable?) {

                                }
                            })

                        } catch (e: Exception) {
                        }

                    }
                }
            }

            override fun onFailure(call: Call<BeatPlanCustomerDropDownModel>, t: Throwable) {
                bindingBottomSheet.loadingView.visibility = View.GONE
                try {
                    Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_SHORT)
                        .show()
                } catch (e: Exception) {
                }
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }


}