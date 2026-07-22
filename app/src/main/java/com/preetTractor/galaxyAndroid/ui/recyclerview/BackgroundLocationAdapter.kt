package com.preetTractor.galaxyAndroid.ui.recyclerview

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.preetTractor.galaxyAndroid.data.ResponseBackGroundLocation
import com.preetTractor.galaxyAndroid.databinding.ItemLocationBackgroundBinding


class BackgroundLocationAdapter(itemList: List<ResponseBackGroundLocation.Datum>, context: Context) :
    RecyclerView.Adapter<BackgroundLocationAdapter.ItemViewHolder>() {
    private val itemList: List<ResponseBackGroundLocation.Datum>
    private val context: Context

    init {
        this.itemList = itemList
        this.context = context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: ItemLocationBackgroundBinding =
            ItemLocationBackgroundBinding.inflate(inflater, parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item: ResponseBackGroundLocation.Datum = itemList[position]
        holder.binding.tvTime.setText(item.createTime)
        holder.binding.tvLocation.setText(item.address)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

   inner class ItemViewHolder(binding: ItemLocationBackgroundBinding) :
        RecyclerView.ViewHolder(binding.getRoot()) {
        val binding: ItemLocationBackgroundBinding

        init {
            this.binding = binding
        }
    }
}