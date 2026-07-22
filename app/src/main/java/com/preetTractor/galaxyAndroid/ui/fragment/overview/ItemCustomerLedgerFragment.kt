package com.preetTractor.galaxyAndroid.ui.fragment.overview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.JsonObject
import com.preetTractor.galaxyAndroid.R
import com.preetTractor.galaxyAndroid.data.model.customer.ResponseJournalEntryBpWise
import com.preetTractor.galaxyAndroid.databinding.FragmentItemCustomerLedgerBinding
import com.preetTractor.galaxyAndroid.helper.Globals
import com.preetTractor.galaxyAndroid.retrofit.RetrofitClient
import com.preetTractor.galaxyAndroid.ui.activity.customer.CustomerDetailActivity
import com.preetTractor.galaxyAndroid.ui.recyclerview.LedgerGeneralEntriesAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ItemCustomerLedgerFragment : Fragment() {
    lateinit var binding: FragmentItemCustomerLedgerBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentItemCustomerLedgerBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment
        return binding.root
    }

    companion object {
        private const val TAG = "ItemCustomerLedgerFragm"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        SetUPDialog()
        ledgerGeneralEntryReport()
    }

    var builder: AlertDialog.Builder? = null
    var alertDialog: AlertDialog? = null

    // TODO: Rename and change types and number of parameters
    private fun SetUPDialog() {
        builder = AlertDialog.Builder(requireContext())
        builder!!.setTitle("Loading....")
            .setMessage("Please Wait")
            .setCancelable(false)
        alertDialog = builder!!.create()
    }

    private fun ledgerGeneralEntryReport() {
        alertDialog!!.show()

        var jsonObject = JsonObject().apply {
            addProperty("CardCode", CustomerDetailActivity.cardCode)
            addProperty("FromDate", Globals.firstDateOfFinancialYear())
            addProperty("ToDate", Globals.lastDateOfFinancialYear())
        }


        val call = RetrofitClient.apiService.bp_general_entries(jsonObject)
        call.enqueue(object : Callback<ResponseJournalEntryBpWise> {
            override fun onResponse(
                call: Call<ResponseJournalEntryBpWise>,
                response: Response<ResponseJournalEntryBpWise>
            ) {
                response.body()?.let { body ->
                    if (body.status == 200 && body.status != null) {
                        alertDialog!!.dismiss()


                        if (body.data.isNotEmpty()) {
                            var closing = Globals.foo(body.data[0].closingBalance!!.toDouble())
                            var opening = Globals.foo(body.data[0].openingBalance!!.toDouble())

                            if (closing.isNullOrEmpty()) closing = "0"
                            if (opening.isNullOrEmpty()) opening = "0"

                            binding.closingBalance.text =
                                getString(R.string.Rs) + " " + Globals.numberToK(closing)
                            binding.openingBalance.text =
                                getString(R.string.Rs) + " " + Globals.numberToK(opening)


                            val adapter = LedgerGeneralEntriesAdapter(
                                requireActivity(),
                                body.data[0].journalEntryLines!!,
                                alertDialog!!
                            )

                            binding.customerLeadList.layoutManager =
                                LinearLayoutManager(requireActivity(), RecyclerView.VERTICAL, false)
                            binding.customerLeadList.adapter = adapter

                            adapter.notifyDataSetChanged()

                            /*    adapter.setOnItemClickListener { id ->
                                    val url = Globals.journalVoucher + id
                                    val title = getString(R.string.share_journal)
                                    val addPhotoBottomDialogFragment =
                                        WebViewBottomSheetFragment.newInstance(dialogWeb, url, title)
                                    addPhotoBottomDialogFragment.show(supportFragmentManager, "")
                                }*/
                        }
                    }
                }
            }

            override fun onFailure(call: Call<ResponseJournalEntryBpWise>, t: Throwable) {
                // loader.visibility = View.GONE
                alertDialog!!.dismiss()
            }
        })
    }

}