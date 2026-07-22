package com.preetTractor.galaxyAndroid.ui.orderUi.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.preetTractor.galaxyAndroid.R
import com.preetTractor.galaxyAndroid.databinding.ItemPendingByOrderBinding
import com.preetTractor.galaxyAndroid.helper.Globals
import com.preetTractor.galaxyAndroid.helper.Globals.setDynamicValueWithStringXml
import com.preetTractor.galaxyAndroid.orderUi.model.response.ResponsePendingOrderInner

import java.util.*
import kotlin.Int
import kotlin.Unit
import kotlin.apply
import kotlin.let


class PendingOrderInnerFirstPagingAdapter : RecyclerView.Adapter<PendingOrderInnerFirstPagingAdapter.ItemViewHolder>() {

    private val items = mutableListOf<ResponsePendingOrderInner.Data>()

    private var onItemClickListener: ((ResponsePendingOrderInner.Data, Int) -> Unit)? = null
    fun setOnItemClickListener(listener: (ResponsePendingOrderInner.Data, Int) -> Unit) {
        onItemClickListener = listener
    }


    private var onItemCallClickListener: ((ResponsePendingOrderInner.Data) -> Unit)? = null
    fun setOnItemCallClickListener(listener: (ResponsePendingOrderInner.Data) -> Unit) {
        onItemCallClickListener = listener
    }

    fun clearAllData() {
        items.removeAll(items)
        notifyDataSetChanged()
    }

    fun setItems(newItems: List<ResponsePendingOrderInner.Data>) {
        items.addAll(newItems)
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            ItemPendingByOrderBinding.inflate(
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


    }

    override fun getItemCount(): Int {
        return items.size
    }

    class ItemViewHolder(var binding: ItemPendingByOrderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ResponsePendingOrderInner.Data, context: Context) {

            // Bind item properties to the UI elements
            binding.apply {
                binding.title.text = "Order Id :- ${item.DocNum}"
                binding.customerName.text = context.setDynamicValueWithStringXml(
                    R.string.amount_with_rupee_symbol_amount,
                    Globals.numberToK(item.LineTotalSum)!!
                )
                binding.dueDateDoc.text = "Due Date ${Globals.convert_yyyy_mm_dd_to_dd_mm_yyyy(item.DocDueDate)}"
                binding.pendingQuantitiy.text = "Quantity: ${item.Quantity}"

            }
        }
    }
}
