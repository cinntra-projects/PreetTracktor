package com.preetTractor.galaxyAndroid.ui.orderUi.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.preetTractor.galaxyAndroid.databinding.ItemRowCellBinding


class ScrollabelInnerContentDispatchedAdapter(private val rowData: MutableList<String>) :
    RecyclerView.Adapter<ScrollabelInnerContentDispatchedAdapter.RowViewHolder>() {




    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RowViewHolder {
        val binding = ItemRowCellBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RowViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RowViewHolder, position: Int) {
        holder.bind(rowData[position],position)
    }

    override fun getItemCount(): Int = rowData.size

    class RowViewHolder(private val binding: ItemRowCellBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(text: String,position: Int) {
            binding.textView.text=text
        }
    }
}
