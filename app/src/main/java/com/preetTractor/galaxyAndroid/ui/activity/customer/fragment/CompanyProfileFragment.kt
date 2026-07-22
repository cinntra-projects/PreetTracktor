package com.preetTractor.galaxyAndroid.ui.activity.customer.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.gson.JsonObject
import com.preetTractor.galaxyAndroid.R
import com.preetTractor.galaxyAndroid.data.model.customer.ResponseCustomerOne
import com.preetTractor.galaxyAndroid.databinding.FragmentCompanyProfileBinding
import com.preetTractor.galaxyAndroid.retrofit.RetrofitClient
import com.preetTractor.galaxyAndroid.ui.activity.customer.CustomerDetailActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class CompanyProfileFragment : Fragment() {
    lateinit var binding: FragmentCompanyProfileBinding

    var builder: AlertDialog.Builder? = null
    var alertDialog: AlertDialog? = null

    private fun getBpDetails() {
        alertDialog!!.show()

        Log.d(
            "CompanyProfile",
            "CustomerCardCode: ${CustomerDetailActivity.customerCardCode}, flagCustomerModule: ${CustomerDetailActivity.customerModuleFlag}"
        )
        val hde = JsonObject()

        if (CustomerDetailActivity.customerModuleFlag.equals("CustomerModule")) {
            hde.addProperty("CardCode", CustomerDetailActivity.customerCardCode)
        } else {
            hde.addProperty("CardCode", CustomerDetailActivity.cardCode)
        }

        val call = RetrofitClient.apiService.getBpGalaxyOne(hde)

        call.enqueue(object : Callback<ResponseCustomerOne> {
            override fun onResponse(
                call: Call<ResponseCustomerOne>,
                response: Response<ResponseCustomerOne>
            ) {
                alertDialog!!.dismiss()

                response.body()?.let {


                    if (it.status == 200) {

                        if (it.data.isNotEmpty()) {

                            binding.apply {
                                tvCustomerName.text = it.data[0].CardName
                                if (it.data[0].BPAddresses.isNotEmpty()) {
                                    if (it.data[0].BPAddresses[0].Street.isNotEmpty()) {
                                        tvCustomerAddress.text = it.data[0].BPAddresses[0].Street
                                        tvAddress.text = it.data[0].BPAddresses[0].Street
                                        tvGstNumber.text = it.data[0].BPAddresses[0].GSTIN
                                    } else {
                                        tvCustomerAddress.text = "N/A"
                                        tvAddress.text = "N/A"
                                        tvGstNumber.text = "N/A"
                                    }

                                }

                                if (it.data[0].Phone1.isNotEmpty()) {
                                    tvPhoneNumber.text = it.data[0].Phone1
                                } else {
                                    tvPhoneNumber.text = "N/A"
                                }


                                if (it.data[0].EmailAddress.isNotEmpty()) {
                                    tvMail.text = it.data[0].EmailAddress
                                } else {
                                    tvMail.text = "N/A"
                                }


                                if (it.data[0].Shop_StartAt.isNotEmpty()) {
                                    tvShopTiming.text = it.data[0].CheckinTime +" - "+it.data[0].CheckoutTime
                                } else {
                                    tvShopTiming.text = "N/A"
                                }


                                /*if (it.data[0].CustomerType.isNotEmpty()) {
                                    tvCompanyType.text = it.data[0].CustomerType
                                } else {
                                    tvCompanyType.text = "N/A"
                                }*/

                              /*  if (it.data[0].GroupCode.isNotEmpty()) {
                                    tvCompanyNumber.text = it.data[0].GroupCode
                                } else {
                                    tvCompanyNumber.text = "N/A"
                                }*/






                                CustomerDetailActivity.cardName = it.data[0].CardName

                            }

                        }


                    } else if (it.status == 201) {
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }

            override fun onFailure(call: Call<ResponseCustomerOne>, t: Throwable) {
                alertDialog!!.dismiss()
                Log.e(TAG, "onFailure: ${t.message}")
                Toast.makeText(
                    requireContext(),
                    "Something went wrong",
                    Toast.LENGTH_SHORT
                ).show()

            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding = FragmentCompanyProfileBinding.inflate(layoutInflater)
        return binding.root
    }

    companion object {
        private const val TAG = "CompanyProfileFragment"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        builder = AlertDialog.Builder(requireContext())
        builder!!.setView(com.preetTractor.galaxyAndroid.R.layout.progress_dialog_alert)
            .setCancelable(false)
        alertDialog = builder!!.create()
        getBpDetails()
    }

}