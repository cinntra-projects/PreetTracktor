package com.preetTractor.galaxyAndroid.ui.orderUi.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.preetTractor.galaxyAndroid.R
import com.preetTractor.galaxyAndroid.databinding.ItemDispatchOrderBinding
import com.preetTractor.galaxyAndroid.helper.Globals
import com.preetTractor.galaxyAndroid.helper.Globals.setDynamicValueWithStringXml
import com.preetTractor.galaxyAndroid.orderUi.model.response.ResponseDispatchList

import kotlin.Int
import kotlin.Unit
import kotlin.apply
import kotlin.let


class DispatchListPagingAdapter : RecyclerView.Adapter<DispatchListPagingAdapter.ItemViewHolder>() {

    private val items = mutableListOf<ResponseDispatchList.Data>()

    private var onItemClickListener: ((ResponseDispatchList.Data, Int) -> Unit)? = null
    fun setOnItemClickListener(listener: (ResponseDispatchList.Data, Int) -> Unit) {
        onItemClickListener = listener
    }


    private var onItemCallClickListener: ((ResponseDispatchList.Data) -> Unit)? = null
    fun setOnItemCallClickListener(listener: (ResponseDispatchList.Data) -> Unit) {
        onItemCallClickListener = listener
    }

    fun clearAllData() {
        items.removeAll(items)
        notifyDataSetChanged()
    }

    fun setItems(newItems: List<ResponseDispatchList.Data>) {
        items.addAll(newItems)
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            ItemDispatchOrderBinding.inflate(
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

    class ItemViewHolder(var binding: ItemDispatchOrderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ResponseDispatchList.Data, context: Context) {

            // Bind item properties to the UI elements
            binding.apply {
                binding.tvTransport.text = "--"
                binding.tvAmount.text = context.setDynamicValueWithStringXml(
                    R.string.amount_with_rupee_symbol,
                    Globals.numberToK(item.NetTotal)!!
                )
                binding.tvLrNumber.text = "--"
                binding.tvInvoice.text = item.DocEntry
                binding.tvDate.text = Globals.convert_yyyy_mm_dd_to_dd_mm_yyyy(item.DocDate)

            }
        }
    }
}
