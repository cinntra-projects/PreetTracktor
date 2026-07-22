package com.preetTractor.galaxyAndroid.ui.orderUi.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.preetTractor.galaxyAndroid.R
import com.preetTractor.galaxyAndroid.databinding.ItemOrderUnderApprovalBinding
import com.preetTractor.galaxyAndroid.helper.Globals
import com.preetTractor.galaxyAndroid.helper.Globals.setDynamicValueWithStringXml
import com.preetTractor.galaxyAndroid.orderUi.model.response.DataSoRequestAllFilter

import java.util.*
import kotlin.Int
import kotlin.Unit
import kotlin.apply
import kotlin.let


class OrderUnderApprovalPagingAdapter : RecyclerView.Adapter<OrderUnderApprovalPagingAdapter.ItemViewHolder>() {

    private val items = mutableListOf<DataSoRequestAllFilter>()

    private var onItemClickListener: ((DataSoRequestAllFilter, Int) -> Unit)? = null
    fun setOnItemClickListener(listener: (DataSoRequestAllFilter, Int) -> Unit) {
        onItemClickListener = listener
    }



    private var onItemDeleteClickListener: ((DataSoRequestAllFilter, Int) -> Unit)? = null
    fun setOnItemDeleteClickListener(listener: (DataSoRequestAllFilter, Int) -> Unit) {
        onItemDeleteClickListener = listener
    }


    private var onItemEditClickListener: ((DataSoRequestAllFilter, Int) -> Unit)? = null
    fun setOnItemEditDeleteClickListener(listener: (DataSoRequestAllFilter, Int) -> Unit) {
        onItemEditClickListener = listener
    }


    private var onItemCallClickListener: ((DataSoRequestAllFilter) -> Unit)? = null
    fun setOnItemCallClickListener(listener: (DataSoRequestAllFilter) -> Unit) {
        onItemCallClickListener = listener
    }

    fun clearAllData() {
        items.removeAll(items)
        notifyDataSetChanged()
    }

    fun setItems(newItems: List<DataSoRequestAllFilter>) {
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
        fun bind(item: DataSoRequestAllFilter, context: Context) {

            // Bind item properties to the UI elements
            binding.apply {
                binding.title.text = "Order Id :- ${item.DocNum}"
                binding.customerName.text = context.setDynamicValueWithStringXml(
                    R.string.amount_with_rupee_symbol_amount,
                    Globals.numberToK(item.DocTotal)!!
                )
                binding.dueDateDoc.text = "Due Date ${Globals.convert_yyyy_mm_dd_to_dd_mm_yyyy(item.DocDueDate)}"
                binding.pendingQuantitiy.text = "Quantity: ${item.TotalQuantity}"

            }
        }
    }
}
