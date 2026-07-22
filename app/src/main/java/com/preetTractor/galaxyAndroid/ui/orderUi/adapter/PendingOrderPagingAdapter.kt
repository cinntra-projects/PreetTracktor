package com.preetTractor.galaxyAndroid.ui.orderUi.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.preetTractor.galaxyAndroid.R
import com.preetTractor.galaxyAndroid.data.model.order.model.response.ModelOrderListing
import com.preetTractor.galaxyAndroid.databinding.ItemOrderUnderApprovalBinding
import com.preetTractor.galaxyAndroid.helper.Globals
import com.preetTractor.galaxyAndroid.helper.Globals.setDynamicValueWithStringXml

import java.util.*
import kotlin.Int
import kotlin.Unit
import kotlin.apply
import kotlin.let


class PendingOrderPagingAdapter : RecyclerView.Adapter<PendingOrderPagingAdapter.ItemViewHolder>() {

    private val items = mutableListOf<ModelOrderListing.Data>()

    private var onItemClickListener: ((ModelOrderListing.Data, Int) -> Unit)? = null
    fun setOnItemClickListener(listener: (ModelOrderListing.Data, Int) -> Unit) {
        onItemClickListener = listener
    }



    private var onItemDeleteClickListener: ((ModelOrderListing.Data, Int) -> Unit)? = null
    fun setOnItemDeleteClickListener(listener: (ModelOrderListing.Data, Int) -> Unit) {
        onItemDeleteClickListener = listener
    }


    private var onItemEditClickListener: ((ModelOrderListing.Data, Int) -> Unit)? = null
    fun setOnItemEditDeleteClickListener(listener: (ModelOrderListing.Data, Int) -> Unit) {
        onItemEditClickListener = listener
    }


    private var onItemCallClickListener: ((ModelOrderListing.Data) -> Unit)? = null
    fun setOnItemCallClickListener(listener: (ModelOrderListing.Data) -> Unit) {
        onItemCallClickListener = listener
    }

    fun clearAllData() {
        items.removeAll(items)
        notifyDataSetChanged()
    }

    fun setItems(newItems: List<ModelOrderListing.Data>) {
        items.addAll(newItems)
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            ItemOrderUnderApprovalBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item, holder.itemView.context)
        holder.itemView.setOnClickListener {
            onItemClickListener?.let { click ->
                click(item, position)
            }
        }

        holder.binding.apply {
            ibDeleteOrder.setOnClickListener {
                onItemDeleteClickListener?.let { click->
                    click(item,position)

                }
            }


            ibEditOrder.setOnClickListener {
                onItemEditClickListener?.let { click->
                    click(item,position)

                }
            }
        }


    }

    override fun getItemCount(): Int {
        return items.size
    }

    class ItemViewHolder(var binding: ItemOrderUnderApprovalBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ModelOrderListing.Data, context: Context) {

            // Bind item properties to the UI elements
            binding.apply {
                binding.title.text = "Order Id :- ${item.id}"
                binding.customerName.text = context.setDynamicValueWithStringXml(
                    R.string.amount_with_rupee_symbol_amount,
                    Globals.numberToK(item.DocTotal)!!
                )
                binding.dueDateDoc.text = "Due Date ${Globals.convert_yyyy_mm_dd_to_dd_mm_yyyy(item.DocDueDate)}"
//                binding.pendingQuantitiy.text = "Quantity: ${item.}"

            }
        }
    }
}
