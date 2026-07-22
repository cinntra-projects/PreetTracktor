package com.preetTractor.galaxyAndroid.ui.orderUi.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.preetTractor.galaxyAndroid.databinding.ItemRowCellBinding


class RowAdapter(private val rowData: List<String>) :
    RecyclerView.Adapter<RowAdapter.RowViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RowViewHolder {
        val binding = ItemRowCellBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RowViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RowViewHolder, position: Int) {
        holder.bind(rowData[position])
    }

    override fun getItemCount(): Int = rowData.size

    class RowViewHolder(private val binding: ItemRowCellBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(text: String) {
            binding.textView.text = text
        }
    }
}
