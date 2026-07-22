package com.preetTractor.galaxyAndroid.ui.orderUi.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.preetTractor.galaxyAndroid.databinding.ItemContentRowBinding


class ContentAdapter(private val data: List<List<String>>) :
    RecyclerView.Adapter<ContentAdapter.ContentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContentViewHolder {
        val binding = ItemContentRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ContentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ContentViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount(): Int = data.size

    class ContentViewHolder(private val binding: ItemContentRowBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(rowData: List<String>) {
            binding.rowRecyclerView.adapter = RowAdapter(rowData)
        }
    }
}
