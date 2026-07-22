package com.preetTractor.galaxyAndroid.ui.orderUi.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.preetTractor.galaxyAndroid.R
import com.preetTractor.galaxyAndroid.data.ModelPreviousOrderOne
import com.preetTractor.galaxyAndroid.data.model.order.model.response.ModelOrderListing
import com.preetTractor.galaxyAndroid.databinding.ItemInOrderDetailsBinding
import com.preetTractor.galaxyAndroid.helper.Globals.HEADER_DISCOUNT_PERCENT
import com.preetTractor.galaxyAndroid.helper.Globals.calculateItemTotal
import com.pixplicity.easyprefs.library.Prefs

class PreviousOrderOneAdapter(
      private val dataList: List<ModelOrderListing.Data.DocumentLine?>? = emptyList(),
      private val onItemClicked: (Int, ModelPreviousOrderOne.Data) -> Unit,
) : RecyclerView.Adapter<PreviousOrderOneAdapter.ExpenseViewHolder>() {
      private var isVisible:Boolean = false


      inner class ExpenseViewHolder(private val binding: ItemInOrderDetailsBinding) : RecyclerView.ViewHolder(binding.root) {

            @SuppressLint("SetTextI18n")
            fun bindData(item: ModelOrderListing.Data.DocumentLine?) {
                  binding.apply {
                        tvItemName.text = "${position + 1} - ${item?.ItemDescription}"
                        tvItemPrice.text = "Unit Price: ${item?.UnitPrice}"
                        tvItemQty.text = "Quantity: ${item?.Quantity}"
                        /*calculateAndSetTotalsForAdapter(
                              dataList as List<ModelOrderListing.Data.DocumentLine>,
                              freightCharge = 0.00, // Example freight charge
                              tvBasicAmount = binding.tvTotalBillingAmount,
                              tvItemDiscount = binding.tvItemDiscAmount,
                              tvHeaderDiscount = binding.tvHeaderDescAmount,
                              tvTaxAmount = binding.tvTaxRateAmount,
                              tvFreightCharge = binding.tvFreightChargeAmount,
                              tvGrandTotal = binding.tvTotalInvoiceAmount
                        )*/

                        val itemCalculation = item?.let { calculateItemTotal(it, Prefs.getString(HEADER_DISCOUNT_PERCENT,"").trimEnd('%').toDoubleOrNull() ?: 0.0) }

                        // Access individual calculated values
                        val basicAmount = itemCalculation?.get("BasicAmount") ?: 0.0
                        val itemDiscount = itemCalculation?.get("ItemDiscount") ?: 0.0
                        val headerDiscount = itemCalculation?.get("HeaderDiscount") ?: 0.0
                        val taxAmount = itemCalculation?.get("TaxAmount") ?: 0.0
                        val totalAmount = itemCalculation?.get("TotalAmount") ?: 0.0

                        tvTotalBillingAmount.text = String.format("₹ %.2f", basicAmount)
                        tvItemDiscAmount.text = String.format("₹ %.2f", itemDiscount)
                        tvHeaderDescAmount.text = String.format("₹ %.2f", headerDiscount)
                        tvTaxRateAmount.text = String.format("₹ %.2f", taxAmount)
                        tvFreightChargeAmount.text = String.format("₹ %.2f", 0.00)
                        tvTotalInvoiceAmount.text = String.format("₹ %.2f", totalAmount)
                        ivShowHideArrow.setOnClickListener {
                              isVisible = !isVisible
                              if (isVisible) {
                                    layoutShowHide.visibility = View.VISIBLE
                                    ivShowHideArrow.setImageResource(R.drawable.ic_expand) // Change icon
                              } else {
                                    layoutShowHide.visibility = View.GONE
                                    ivShowHideArrow.setImageResource(R.drawable.ic_collapse) // Change icon
                              }
                        }

                  }
            }
      }

      override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
            val binding = ItemInOrderDetailsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ExpenseViewHolder(binding)
      }

      // No need to implement getItemCount() since ListAdapter handles it
      override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
            dataList?.get(position).let { holder.bindData(it) }
      }

      override fun getItemCount(): Int {
            return dataList?.size ?: 0
      }
}