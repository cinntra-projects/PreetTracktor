package com.preetTractor.galaxyAndroid.ui.orderUi.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.preetTractor.galaxyAndroid.R
import com.preetTractor.galaxyAndroid.databinding.ItemPendingByOrderBinding
import com.preetTractor.galaxyAndroid.helper.Globals
import com.preetTractor.galaxyAndroid.helper.Globals.setDynamicValueWithStringXml
import com.preetTractor.galaxyAndroid.orderUi.model.PendingByOrderModel

class PendingItemByOrderAdapter(private val context: Context) :
    ListAdapter<PendingByOrderModel.Data,PendingItemByOrderAdapter.ViewHolder>(
        DiffUtils()
    ) {

    private var onItemClickClickListener: ((PendingByOrderModel.Data, Int) -> Unit)? = null

    fun setOnItemClickListener(listener: (PendingByOrderModel.Data, Int) -> Unit) {
        onItemClickClickListener = listener
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemPendingByOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }



    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var item=getItem(position)
        holder.binding.apply {
            title.text = " ${item.ItemDescription}"
          customerName.text = context.setDynamicValueWithStringXml(
                R.string.amount_with_rupee_symbol_pending_amount,
                Globals.numberToK(item.PendingAmount)!!
            )
           pendingQuantitiy.text = "Due Date ${Globals.convert_yyyy_mm_dd_to_dd_mm_yyyy(item.DocDueDate)}"
           dueDateDoc.text = "Quantity: ${item.PendingQty}"

        }

        holder.itemView.setOnClickListener {

            onItemClickClickListener?.let { click->
                click(item,position)
            }

        }

    }


    inner class ViewHolder(val binding: ItemPendingByOrderBinding) :
        RecyclerView.ViewHolder(binding.root) {


    }

    class DiffUtils : DiffUtil.ItemCallback<PendingByOrderModel.Data>() {
        override fun areItemsTheSame(
            oldItem: PendingByOrderModel.Data,
            newItem: PendingByOrderModel.Data
        ): Boolean {
            return oldItem.OrderID == newItem.OrderID
        }

        override fun areContentsTheSame(
            oldItem: PendingByOrderModel.Data,
            newItem: PendingByOrderModel.Data
        ): Boolean {
            return oldItem == newItem
        }

    }


}