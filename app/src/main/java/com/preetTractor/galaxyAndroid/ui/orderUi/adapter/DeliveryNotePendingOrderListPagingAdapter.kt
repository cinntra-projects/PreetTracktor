package com.preetTractor.galaxyAndroid.ui.orderUi.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.preetTractor.galaxyAndroid.R
import com.preetTractor.galaxyAndroid.databinding.ItemPendingOrderBinding
import com.preetTractor.galaxyAndroid.helper.Globals
import com.preetTractor.galaxyAndroid.helper.Globals.setDynamicValueWithStringXml
import com.preetTractor.galaxyAndroid.orderUi.model.response.ResponsePendingDeliveryNote


class DeliveryNotePendingOrderListPagingAdapter : RecyclerView.Adapter<DeliveryNotePendingOrderListPagingAdapter.ItemViewHolder>() {

    private val items = mutableListOf<ResponsePendingDeliveryNote.Data>()

    private var onItemClickListener: ((ResponsePendingDeliveryNote.Data, Int) -> Unit)? = null
    fun setOnItemClickListener(listener: (ResponsePendingDeliveryNote.Data, Int) -> Unit) {
        onItemClickListener = listener
    }


    private var onItemCallClickListener: ((ResponsePendingDeliveryNote.Data) -> Unit)? = null
    fun setOnItemCallClickListener(listener: (ResponsePendingDeliveryNote.Data) -> Unit) {
        onItemCallClickListener = listener
    }

    fun clearAllData() {
        items.removeAll(items)
        notifyDataSetChanged()
    }

    fun setItems(newItems: List<ResponsePendingDeliveryNote.Data>) {
        items.addAll(newItems)
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            ItemPendingOrderBinding.inflate(
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

    class ItemViewHolder(var binding: ItemPendingOrderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ResponsePendingDeliveryNote.Data, context: Context) {

            // Bind item properties to the UI elements
            binding.apply {
                binding.tvCode.text = item.ItemCode
                binding.tvAmount.text = context.setDynamicValueWithStringXml(R.string.amount_with_rupee_symbol, Globals.numberToK(item.LineTotalSum)!!)
                binding.tvQuantity.text = item.Quantity
                binding.tvParticular.text = item.ItemDescription


            }
        }
    }
}
