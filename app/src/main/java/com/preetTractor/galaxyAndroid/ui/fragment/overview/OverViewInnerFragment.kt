package com.preetTractor.galaxyAndroid.ui.fragment.overview

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.JsonObject
import com.preetTractor.galaxyAndroid.R
import com.preetTractor.galaxyAndroid.data.model.customer.MonthGroupSalesList
import com.preetTractor.galaxyAndroid.data.model.customer.UnderList
import com.preetTractor.galaxyAndroid.data.model.notes.DataBpOverview
import com.preetTractor.galaxyAndroid.data.model.notes.ResponseBpOverview
import com.preetTractor.galaxyAndroid.databinding.FragmentOverViewInnerBinding
import com.preetTractor.galaxyAndroid.helper.Globals
import com.preetTractor.galaxyAndroid.retrofit.RetrofitClient
import com.preetTractor.galaxyAndroid.ui.activity.customer.CustomerDetailActivity
import com.preetTractor.galaxyAndroid.ui.recyclerview.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class OverViewInnerFragment : Fragment(), View.OnClickListener {
    lateinit var binding: FragmentOverViewInnerBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentOverViewInnerBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment
        return binding.root
    }

    companion object {
        private const val TAG = "OverViewInnerFragment"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getBpDetails()
        // Set click listeners
        binding.saleLay.setOnClickListener(this)
        binding.receiptLay.setOnClickListener(this)
        binding.receivableLay.setOnClickListener(this)
        binding.purchaseLay.setOnClickListener(this)
        binding.payableLay.setOnClickListener(this)

        binding.ibOverviewArrow.setOnClickListener {
            if (binding.linearOverView.visibility == View.VISIBLE) {
                binding.linearOverView.visibility = View.GONE
                binding.ibOverviewArrow.setImageResource(R.drawable.ic_arrow_up)
            } else {
                binding.linearOverView.visibility = View.VISIBLE
                binding.ibOverviewArrow.setImageResource(R.drawable.ic_arrow_down)
            }
        }

        // Toggle overview on upperLay click
        binding.upperLay.setOnClickListener {
            if (binding.linearOverView.visibility == View.VISIBLE) {
                binding.linearOverView.visibility = View.GONE
                binding.ibOverviewArrow.setImageResource(R.drawable.ic_arrow_up)
            } else {
                binding.linearOverView.visibility = View.VISIBLE
                binding.ibOverviewArrow.setImageResource(R.drawable.ic_arrow_down)
            }
        }

    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.saleLay -> {
                val count = saleAdapter?.itemCount ?: 0
                setViewChange(
                    binding.saleRecyclerview,
                    binding.saleArrow,
                    binding.saleDivider,
                    count
                )
            }
            R.id.purchaseLay -> {
                val countPurchase = purchaseLedgerAdapter?.itemCount ?: 0
                setViewChange(
                    binding.purchaseRecyclerview,
                    binding.purchaseArrow,
                    binding.purchaseDivider,
                    countPurchase
                )
            }
            R.id.receiptLay -> {
                val count = receiptAdapter?.itemCount ?: 0
                setViewChange(
                    binding.receiptRecyclerview,
                    binding.receiptArrow,
                    binding.receiptDivider,
                    count
                )
            }
            R.id.receivableLay -> {
                setViewChangeReceivable(
                    binding.receivableRecyclerview,
                    binding.receivableArrow,
                    binding.creditView,
                    null,
                    0
                )
            }
            R.id.payableLay -> {
                setViewChangeReceivable(
                    binding.payableRecyclerview,
                    binding.payableArrow,
                    binding.creditView,
                    null,
                    0
                )
            }
        }
    }

    private fun setViewChange(
        recyclerView: RecyclerView,
        imageView: ImageView,
        divider: View?,
        count: Int
    ) {
        if (recyclerView.visibility == View.VISIBLE) {
            recyclerView.visibility = View.GONE
            imageView.setImageResource(R.drawable.ic_arrow_down)
            divider?.let {
                if (count > 0) it.visibility = View.VISIBLE
            }
        } else {
            recyclerView.visibility = View.VISIBLE
            imageView.setImageResource(R.drawable.ic_arrow_up)
            divider?.let {
                if (count > 0) it.visibility = View.GONE
            }
        }
    }

    private fun setViewChangeReceivable(
        recyclerView: RecyclerView,
        imageView: ImageView,
        textView: LinearLayout,
        divider: View?,
        count: Int
    ) {
        if (recyclerView.visibility == View.VISIBLE) {
            recyclerView.visibility = View.GONE
            textView.visibility = View.GONE
            imageView.setImageResource(R.drawable.ic_arrow_down)
            divider?.let {
                if (count > 0) it.visibility = View.VISIBLE
            }
        } else {
            recyclerView.visibility = View.VISIBLE
            textView.visibility = View.VISIBLE
            imageView.setImageResource(R.drawable.ic_arrow_up)
            divider?.let {
                if (count > 0) it.visibility = View.GONE
            }
        }
    }

    private lateinit var saleAdapter: SaleLedgerAdapter
    private lateinit var purchaseLedgerAdapter: PurchaseLedgerAdapter
    private lateinit var receiptAdapter: ReceiptLedgerAdapter
    private lateinit var receivableAdapter: ReceivableLedgerAdapter
    private lateinit var payableLedgerAdapter: PayableLedgerAdapter
    private var cardName: String = ""


    private fun getBpDetails() {

        binding.progressBar.visibility = View.VISIBLE
        val hde = JsonObject().apply {
            addProperty("FromDate", Globals.firstDateOfFinancialYear())
            addProperty("CardCode", CustomerDetailActivity.cardCode)
            addProperty("ToDate", Globals.lastDateOfFinancialYear())
        }
        val call = RetrofitClient.apiService.getBpOverView(hde)

        call.enqueue(object : Callback<ResponseBpOverview> {
            override fun onResponse(
                call: Call<ResponseBpOverview>,
                response: Response<ResponseBpOverview>
            ) {

                binding.progressBar.visibility = View.GONE

                response.body()?.let {


                    if (it.status == 200) {

                        if (it.data.isNotEmpty()) {
                            setData(it.data[0])

                            binding.apply {
                                /*tvCustomerName.text = it.data[0].CardName
                                tvCustomerAddress.text = it.data[0].GroupName
                                includeLayout.title.setText(it.data[0].CardCode)
                                CustomerDetailActivity.cardName = it.data[0].CardName*/


                            }

                        }


                    } else if (it.status == 201) {
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }

            override fun onFailure(call: Call<ResponseBpOverview>, t: Throwable) {
                binding.progressBar.visibility = View.GONE
                Log.e(TAG, "onFailure: ${t.message}")
                Toast.makeText(
                    requireContext(),
                    "Something went wrong",
                    Toast.LENGTH_SHORT
                ).show()

            }
        })
    }

    private fun setOverDue(list: List<UnderList>?): String {
        return list?.sumOf { it.docTotal!!.toDouble() }.toString()
    }

    private fun setOverDue0(list: List<UnderList>?): String {
        return list?.filter { it.overDueGroup!!.toInt() == 0 }
            ?.sumOf { it.docTotal!!.toDouble() }
            .toString()
    }

    private fun setOverDue30(list: List<UnderList>?): String {
        return list?.filter { it.overDueGroup!!.toInt() in 1..30 }
            ?.sumOf { it.docTotal!!.toDouble() }
            .toString()
    }

    private fun setOverDue60(list: List<UnderList>?): String {
        return list?.filter { it.overDueGroup!!.toInt() in 31..60 }
            ?.sumOf { it.docTotal!!.toDouble() }
            .toString()
    }

    private fun setOverDueLists(list: List<UnderList>?): List<MonthGroupSalesList> {
        val overUnderList = mutableListOf<MonthGroupSalesList>()

        val objOver60 = MonthGroupSalesList().apply {
            month = ">60 Days"
            docTotal = setOverDue60(list)
        }
        overUnderList.add(objOver60)

        val objOver30 = MonthGroupSalesList().apply {
            month = ">30 Days"
            docTotal = setOverDue30(list)
        }
        overUnderList.add(objOver30)

        val objOver = MonthGroupSalesList().apply {
            month = ">0 Days"
            docTotal = setOverDue0(list)
        }
        overUnderList.add(objOver)

        return overUnderList
    }

    private fun monthSorting(list: List<MonthGroupSalesList>): List<MonthGroupSalesList> {
        val formatter = SimpleDateFormat("MMM yy", Locale.getDefault())

        return list.sortedWith { item1, item2 ->
            try {
                val date1 = formatter.parse(item1.month)
                val date2 = formatter.parse(item2.month)
                date1?.compareTo(date2) ?: 0 // Safe call for null dates
            } catch (e: ParseException) {
                0 // Handle the parsing exception if necessary.
            }
        }
    }

    private fun setData(res: DataBpOverview) {
        // Handle last sales date
        if (res.LastSalesDate.equals("None", ignoreCase = true) || res.LastSalesDate.isEmpty()) {
            binding.lastSaleDate.text = "None"
        } else {
            binding.lastSaleDate.text = Globals.convertDateFormat(res.LastSalesDate)
        }

        // Handle last receipt date
        if (res.LastRecipetDate.equals(
                "None",
                ignoreCase = true
            ) || res.LastRecipetDate.isEmpty()
        ) {
            binding.lastReceiptDate.text = "None"
        } else {
            binding.lastReceiptDate.text = Globals.convertDateFormat(res.LastRecipetDate)
        }

        // Set the rest of the text views
        binding.noOfInvoices.text = res.InvoiceCount
        binding.avgSale.text = "₹ " + Globals.numberToK(res.AvgInvoiceAmount.toString())
        binding.saleAmount.text = "₹ " + Globals.numberToK(res.TotalSales.toString())
        binding.receiptAmount.text = "₹ " + Globals.numberToK(res.TotalReceipt.toString())

        // Calculate and set receivable amount
        val totalReceivable = res.TotalReceivable.toDouble() + res.TotalJECreditNote.toDouble()
        binding.receivableAmount.text = "₹ " + Globals.numberToK(totalReceivable.toString())


        // Calculate and set payable amount
        val totalPayable = res.TotalPayable.toDouble() + res.TotalJECreditNotepay.toDouble()
        binding.payableAmount.text = "₹ " + Globals.numberToK(totalPayable.toString())



        binding.linearAdvancePayment.visibility = View.VISIBLE
        binding.tvAdvancePayment.text = "₹ " + Globals.numberToK(res.Advance.toString())

        /*// Handle advance payment
        if (!Prefs.getBoolean(Globals.ISPURCHASE, false)) {
            binding.linearAdvancePayment.visibility = View.GONE
        } else {

        }*/

        // Additional summary information
        binding.tvAvgPaymentDays.text = res.AvgPayDays.toString()
        binding.tvPendingSaleOrder.text = "₹ " + Globals.numberToK(res.PendingAmount.toString())
        binding.tvCreditNoteSummary.text = "₹ " + Globals.numberToK(res.TotalCreditNote.toString())
        binding.tvje.text = "₹ " + Globals.numberToK(res.TotalJECreditNote.toString())


        // Set credit limit and JE credit info
        binding.tvCreditLimitAndDysCustomerSummary.text =
            "Credit: " + Globals.numberToK(res.CreditLimit) + " | " + res.CreditLimitLeft
        binding.tvJeCredit.text =
            "JE/Credit: ₹ " + Globals.numberToK(res.TotalJECreditNote.toString())

        try {// Sale and receipt adapters
            val salesMonthList = monthSorting(res.MonthGroupSalesList)
            saleAdapter =
                SaleLedgerAdapter(requireActivity(), salesMonthList, res.CardCode, res.CardName)
            binding.saleRecyclerview.adapter = saleAdapter

            val receiptList = monthSorting(res.MonthGroupReceiptList)
            receiptAdapter =
                ReceiptLedgerAdapter(requireActivity(), receiptList, res.CardCode, res.CardName)
            binding.receiptRecyclerview.adapter = receiptAdapter
        } catch (e: Exception) {
        }

        // Purchase ledger adapter with sorted month data
        val purchaseMonthList = res.MonthGroupPurchaseList
        val formatter = SimpleDateFormat("MMM yy", Locale.getDefault())


        purchaseMonthList?.sortWith { item1, item2 ->
            try {
                val date1 = formatter.parse(item1.month)
                val date2 = formatter.parse(item2.month)
                date1.compareTo(date2)
            } catch (e: ParseException) {
                0
            }
        }

        try {
            purchaseLedgerAdapter = PurchaseLedgerAdapter(
                requireActivity(),
                purchaseMonthList,
                res.CardCode,
                res.CardName
            )
            binding.purchaseRecyclerview.adapter = purchaseLedgerAdapter
        } catch (e: Exception) {
        }

        // Set up over/under lists for receivables and payables
        try {
            val overUnder = setOverDueLists(res.OverList)
            receivableAdapter =
                ReceivableLedgerAdapter(requireActivity(), overUnder, res.CardCode, res.CardName)
            binding.receivableRecyclerview.adapter = receivableAdapter
        } catch (e: Exception) {
        }

        try {
            val overUnderPay = setOverDueLists(res.OverListpay)
            payableLedgerAdapter =
                PayableLedgerAdapter(requireActivity(), overUnderPay, res.CardCode, res.CardName)
            binding.payableRecyclerview.adapter = payableLedgerAdapter
        } catch (e: Exception) {
        }

        // Handle purchase section
        if (res.LinkedBusinessPartner.equals(
                "None",
                ignoreCase = true
            ) || res.LinkedBusinessPartner.isEmpty()
        ) {
            binding.purchaseSection.visibility = View.GONE
        } else {
            binding.purchaseSection.visibility = View.VISIBLE
            binding.purchaseAmount.text = "₹ " + Globals.numberToK(res.TotalPurchases.toString())
            binding.purchaseReceiptAmount.text =
                "₹ " + Globals.numberToK(res.TotalPurchasesReceipt.toString())
            binding.purchaseReceivableAmount.text =
                "₹ " + Globals.numberToK(res.PurchaseCreditNote.toString())
        }
    }

}
