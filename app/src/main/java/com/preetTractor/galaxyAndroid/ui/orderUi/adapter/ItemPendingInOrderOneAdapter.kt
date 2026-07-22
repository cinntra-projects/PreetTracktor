package com.preetTractor.galaxyAndroid.ui.orderUi.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.preetTractor.galaxyAndroid.databinding.ItemInOrderOneBinding
import com.preetTractor.galaxyAndroid.orderUi.model.OrderOneDetailModel

class ItemPendingInOrderOneAdapter(val context: Context,var docList:ArrayList<OrderOneDetailModel.DocumentLine>) :
    RecyclerView.Adapter<ItemPendingInOrderOneAdapter.CategoryListViewHolder>() {

    private var onItemClickClickListener: ((OrderOneDetailModel.DocumentLine, Int) -> Unit)? = null

    fun setOnItemClickListener(listener: (OrderOneDetailModel.DocumentLine, Int) -> Unit) {
        onItemClickClickListener = listener
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryListViewHolder {
        return CategoryListViewHolder(
            ItemInOrderOneBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return docList.size
    }

    override fun onBindViewHolder(holder: CategoryListViewHolder, position: Int) {
        val item = docList[position]
        holder.bind(item, holder.itemView.context)

        holder.itemView.setOnClickListener {


            onItemClickClickListener?.let { click ->
                click(item, position)
            }
        }

    }

    class CategoryListViewHolder(val binding: ItemInOrderOneBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: OrderOneDetailModel.DocumentLine, context: Context) {
            binding.title.text = item.ItemDescription
            binding.tvRate.text = "Rate: ${item.UnitPrice}/Nos."
            binding.tvQTy.text = "Qty: ${item.Quantity}/Nos."


        }

    }





}