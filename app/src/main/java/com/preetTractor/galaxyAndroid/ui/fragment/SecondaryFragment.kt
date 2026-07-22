package com.preetTractor.galaxyAndroid.ui.fragment

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.JsonObject
import com.preetTractor.galaxyAndroid.R
import com.preetTractor.galaxyAndroid.data.ResponseGlobal
import com.preetTractor.galaxyAndroid.data.model.customer.DataSecondaryCustomerList
import com.preetTractor.galaxyAndroid.data.model.customer.ResponseSecondaryCustomerList
import com.preetTractor.galaxyAndroid.databinding.FragmentSecondaryBinding
import com.preetTractor.galaxyAndroid.databinding.ItemAddSecondaryBinding
import com.preetTractor.galaxyAndroid.helper.Globals
import com.preetTractor.galaxyAndroid.retrofit.RetrofitClient
import com.preetTractor.galaxyAndroid.ui.activity.customer.CustomerDetailActivity
import com.preetTractor.galaxyAndroid.ui.recyclerview.CustomerListingSecondaryAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SecondaryFragment : Fragment() {
    lateinit var binding: FragmentSecondaryBinding
    var builder: androidx.appcompat.app.AlertDialog.Builder? = null
    var alertDialog: androidx.appcompat.app.AlertDialog? = null

    var customerListingSecondaryAdapter: CustomerListingSecondaryAdapter? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSecondaryBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment
        return binding.root
    }

    companion object {
        private const val TAG = "SecondaryFragment"
    }

    var fab: FloatingActionButton? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        builder = androidx.appcompat.app.AlertDialog.Builder(requireContext())
        builder!!.setView(R.layout.progress_dialog_alert)
            .setCancelable(false)
        alertDialog = builder!!.create()
        fab = requireActivity().findViewById<FloatingActionButton>(R.id.addNotes)
        clickedEvents()
        callCustomerListApi()

    }


    lateinit var alertBinding: ItemAddSecondaryBinding
    lateinit var mAlert: AlertDialog

    var isUpdatingSecondary = false
    private fun openAlert() {
        alertBinding = ItemAddSecondaryBinding.inflate(layoutInflater)

        val builder = AlertDialog.Builder(requireContext())

        mAlert = builder.setView(alertBinding.root)
            .setCancelable(true)
            .create()
        mAlert.window?.setBackgroundDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                R.drawable.alert_bg
            )
        )

        mAlert.show()


        if (isUpdatingSecondary) {
            alertBinding.apply {
                etCompanyName.setText(updatedataSeconodary.CompanyName)
                etAddress.setText(updatedataSeconodary.Address)
                etMobile.setText(updatedataSeconodary.Mobile)
                etPinCode.setText(updatedataSeconodary.Pincode)
                etContactPersonName.setText(updatedataSeconodary.ContactPersonName)
                tvTitle.setText("Update Customer")
            }
        }else{
            alertBinding.apply {
                etCompanyName.setText("")
                etAddress.setText("")
                etMobile.setText("")
                etPinCode.setText("")
                etContactPersonName.setText("")
                tvTitle.setText("Add Customer")
            }
        }

        alertBinding.apply {
            btnCancel.setOnClickListener {
                mAlert.dismiss()
            }
            btnClose.setOnClickListener {
                mAlert.dismiss()
            }

            btnSave.setOnClickListener {
                if (isFormValid()) {
                    if (isUpdatingSecondary) {
                        updateSecondaryCustomer()
                    } else {
                        createSecondaryCustomer()
                    }


                } else {
                    //Toast.makeText(requireContext(), "saving...", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun isFormValid(): Boolean {
        if (alertBinding.etCompanyName.text.toString().trim().isEmpty()) {
            alertBinding.etCompanyName.error = "Company name required"
            return false
        }

        if (alertBinding.etAddress.text.toString().trim().isEmpty()) {
            alertBinding.etAddress.error = "Address required"
            return false
        }


        if (alertBinding.etPinCode.text.toString().trim().isEmpty()) {
            alertBinding.etPinCode.error = "pin code required"
            return false
        }


        if (alertBinding.etMobile.text.toString().trim().isEmpty()) {
            alertBinding.etMobile.error = "mobile required"
            return false
        }

        if (alertBinding.etContactPersonName.text.toString().trim().isEmpty()) {
            alertBinding.etContactPersonName.error = "Contact person required"
            return false
        }

        return true
    }

    private fun clickedEvents() {
        fab!!.setOnClickListener {
            isUpdatingSecondary=false
            openAlert()
        }
    }


    fun createSecondaryCustomer() {

        alertDialog!!.show()


        val jsonObject = JsonObject().apply {

            addProperty("CardCode", CustomerDetailActivity.cardCode)
            addProperty("CardName", CustomerDetailActivity.cardName)
            addProperty("CompanyName", alertBinding.etCompanyName.text.toString())
            addProperty("Address", alertBinding.etAddress.text.toString())
            addProperty("Pincode", alertBinding.etPinCode.text.toString())
            addProperty("Mobile", alertBinding.etMobile.text.toString())
            addProperty("ContactPersonName", alertBinding.etContactPersonName.text.toString())
            addProperty("SalesPersonCode", Globals.SalesEmployeeCode)
            addProperty("CreateTime", Globals.getTCurrentTime())
            addProperty("CreateDate", Globals.getTodaysDatervrsfrmt())
        }


        val call = RetrofitClient.apiService.createSecondaryCustomer(jsonObject)
        call.enqueue(object : Callback<ResponseGlobal> {
            override fun onResponse(
                call: Call<ResponseGlobal>,
                response: Response<ResponseGlobal>
            ) {

                response.body()?.let {
                    alertDialog!!.dismiss()
                    if (it.status == 200) {

                        Globals.successMessage(requireContext(), "SuccessFul Created")
                        mAlert.dismiss()
                        callCustomerListApi()


                    }
                }
            }

            override fun onFailure(call: Call<ResponseGlobal>, t: Throwable) {
                Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_SHORT)
                    .show()
                Log.e(TAG, "onFailure: ${t.message}")
                alertDialog!!.dismiss()
            }
        })
    }


    fun updateSecondaryCustomer() {

        alertDialog!!.show()


        val jsonObject = JsonObject().apply {

            addProperty("CardCode", updatedataSeconodary.CardCode)
            addProperty("id", updatedataSeconodary.id)
            addProperty("CardName", updatedataSeconodary.CardName)
            addProperty("CompanyName", alertBinding.etCompanyName.text.toString())
            addProperty("Address", alertBinding.etAddress.text.toString())
            addProperty("Pincode", alertBinding.etPinCode.text.toString())
            addProperty("Mobile", alertBinding.etMobile.text.toString())
            addProperty("ContactPersonName", alertBinding.etContactPersonName.text.toString())
            addProperty("SalesPersonCode", Globals.SalesEmployeeCode)
            addProperty("UpdateTime", Globals.getTCurrentTime())
            addProperty("UpdateDate", Globals.getTodaysDatervrsfrmt())
        }


        val call = RetrofitClient.apiService.updateSecondaryCustomer(jsonObject)
        call.enqueue(object : Callback<ResponseGlobal> {
            override fun onResponse(
                call: Call<ResponseGlobal>,
                response: Response<ResponseGlobal>
            ) {

                response.body()?.let {
                    alertDialog!!.dismiss()
                    if (it.status == 200) {
                        isUpdatingSecondary = false
                        Globals.successMessage(requireContext(), "SuccessFul Updated")
                        mAlert.dismiss()
                        callCustomerListApi()


                    } else if (it.status == 201) {
                        Globals.warningMessage(requireContext(), it.message)
                    }
                }
            }

            override fun onFailure(call: Call<ResponseGlobal>, t: Throwable) {
                Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_SHORT)
                    .show()
                Log.e(TAG, "onFailure: ${t.message}")
                alertDialog!!.dismiss()
            }
        })
    }


    //todo calling customer list drop down api here---
    var customerList: ArrayList<DataSecondaryCustomerList> =
        ArrayList<DataSecondaryCustomerList>()

    var updatedataSeconodary = DataSecondaryCustomerList()

    fun callCustomerListApi() {
        alertDialog!!.show()
        var jsonObject = JsonObject()
        jsonObject.addProperty("CardCode", CustomerDetailActivity.cardCode)
        val call = RetrofitClient.apiService.getSecondaryCustomerListing(jsonObject)
        call.enqueue(object : Callback<ResponseSecondaryCustomerList> {
            override fun onResponse(
                call: Call<ResponseSecondaryCustomerList>,
                response: Response<ResponseSecondaryCustomerList>
            ) {


                response.body()?.let {
                    alertDialog!!.dismiss()

                    if (it.status == 200) {
                        customerList.clear()
                        customerList.addAll(it.data)

                        if (customerList.isNotEmpty()) {
                            binding.noDataFound.visibility = View.GONE
                        } else {
                            binding.noDataFound.visibility = View.VISIBLE
                        }


                        try {
                            customerListingSecondaryAdapter =
                                CustomerListingSecondaryAdapter(customerList, requireContext())
                            binding.rvCustomer.adapter =
                                customerListingSecondaryAdapter
                            binding.rvCustomer.layoutManager =
                                LinearLayoutManager(requireContext())
                            customerListingSecondaryAdapter!!.notifyDataSetChanged()

                            customerListingSecondaryAdapter!!.setOnItemMapClickListener { data ->
                                updatedataSeconodary.apply {
                                    Address = data.Address
                                    CardCode = data.CardCode
                                    CardName = data.CardName
                                    Mobile = data.Mobile
                                    Pincode = data.Pincode
                                    CompanyName = data.CompanyName
                                    ContactPersonName = data.ContactPersonName
                                    SalesPersonCode = data.SalesPersonCode
                                    id = data.id
                                }

                                isUpdatingSecondary = true
                                openAlert()
                            }


                        } catch (e: Exception) {
                        }

                    } else if (it.status == 201) {
                        try {
                            Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT)
                                .show()
                        } catch (e: Exception) {
                        }
                    }
                }
            }

            override fun onFailure(call: Call<ResponseSecondaryCustomerList>, t: Throwable) {
                alertDialog!!.dismiss()
                try {
                    Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                }
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }
}