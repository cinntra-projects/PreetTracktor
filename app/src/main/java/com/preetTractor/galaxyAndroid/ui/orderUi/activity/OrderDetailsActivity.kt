package com.preetTractor.galaxyAndroid.ui.orderUi.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.preetTractor.galaxyAndroid.data.model.order.model.response.ModelOrderListing
import com.preetTractor.galaxyAndroid.databinding.ActivityOrderDetailsBinding
import com.preetTractor.galaxyAndroid.helper.Globals
import com.preetTractor.galaxyAndroid.helper.Globals.toPrettyJson
import com.preetTractor.galaxyAndroid.ui.orderUi.adapter.PendingOrderItemAdapter

class OrderDetailsActivity : AppCompatActivity() {
      private lateinit var binding: ActivityOrderDetailsBinding
      private var orderData: ModelOrderListing.Data? = null
      private lateinit var adapterItem: PendingOrderItemAdapter
      var NetAmountCalculate: Double = 0.0
      override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            binding = ActivityOrderDetailsBinding.inflate(layoutInflater)
            setContentView(binding.root)
            initViews()
            setListeners()
      }

      private fun initViews() {
            intent?.let {
                  orderData = it.getParcelableExtra("data")
                  Log.i("ORDER_DATA", "ORDER : ${toPrettyJson(orderData)}")
                  setOrderData(orderData)
            }
      }

      var TotalItemAmount = 0.0
      var TotalDiscount = 0.0

      @SuppressLint("SetTextI18n")
      private fun setOrderData(orderData: ModelOrderListing.Data?) {
            binding.apply {
                  toolbarPendingList.headTitle.text = "Order Details"
                  tvCustomerNameShubh.text = orderData?.CardName
                  tvDateShubh.text = orderData?.CreateDate
                  tvOrderHashShubh.text = "Order: #${orderData?.id}"


                  if (orderData?.Comments?.isEmpty() == true) {
                        tvNarationShubh.text = "N/A"
                  } else {
                        tvNarationShubh.text = orderData?.Comments
                  }


                  tvGrossTotalsShubh.text = Globals.numberToK(orderData?.DocTotal)
                  tvFinalDestinationShubh.text = "N/A"
                  tvDueDate.text = Globals.convert_yyyy_mm_dd_to_dd_mm_yyyy(orderData?.DocDueDate)
                  tvShippedBy.text = orderData?.AddressExtension?.UShptyps
                  tvOrderRef.text = "No Reference"
                  rvBillsItemShubh.run {

                        layoutManager = LinearLayoutManager(this@OrderDetailsActivity, LinearLayoutManager.VERTICAL, false)
                        adapterItem = orderData?.DocumentLines?.let {
                              for(i in 0 until it.size) {

                                    val totalPrice = (it[i].UnitPrice * it[i].Quantity)

                                    TotalItemAmount += totalPrice


                                    TotalDiscount += (it[i].DiscountPercent * totalPrice)/100

//                                    val totalPrice = (it[i].UnitPrice * it[i].Quantity)
//
//                                    var discountPercentage = it[i].DiscountPercent
//
//                                    val discountedAmount = totalPrice - (totalPrice * discountPercentage / 100)
//
//                                    NetAmountCalculate += discountedAmount

                              }
                              // Optionally, log the current NetAmountCalculate
                              Log.d("NetAmountCalculate", "Current Total: $NetAmountCalculate")
                              PendingOrderItemAdapter(this@OrderDetailsActivity, it) }!!
                        adapter = adapterItem


                        tvNetAmountTotalsShubh.text = (TotalItemAmount-TotalDiscount).toString()

//                        tvNetAmountTotalsShubh.text = NetAmountCalculate.toString()
                        adapterItem.notifyDataSetChanged()
                  }

            }
      }

      private fun setListeners() {
            binding.apply {
                  toolbarPendingList.back.setOnClickListener {
                        finish()
                  }
            }
      }
}