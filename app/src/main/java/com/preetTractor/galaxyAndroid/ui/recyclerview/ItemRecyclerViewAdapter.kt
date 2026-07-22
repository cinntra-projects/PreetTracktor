package com.preetTractor.galaxyAndroid.ui.recyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.preetTractor.galaxyAndroid.data.ResponseJsonDataItem
import com.preetTractor.galaxyAndroid.databinding.ItemRecyclerBinding

class ItemRecyclerViewAdapter : Adapter<ItemRecyclerViewAdapter.ItemRecyclerViewHolder>() {

    private val diffUtil = object : DiffUtil.ItemCallback<ResponseJsonDataItem>() {
        override fun areItemsTheSame(oldItem: ResponseJsonDataItem, newItem: ResponseJsonDataItem):
                Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: ResponseJsonDataItem,
            newItem: ResponseJsonDataItem
        ):
                Boolean {
            return oldItem == newItem
        }
    }

    private val asyncListDiffer = AsyncListDiffer(this, diffUtil)

    fun saveData(dataResponse: List<ResponseJsonDataItem>) {
        asyncListDiffer.submitList(dataResponse)
    }


    inner class ItemRecyclerViewHolder(var binding: ItemRecyclerBinding) : ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemRecyclerViewHolder {
        return ItemRecyclerViewHolder(
            ItemRecyclerBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return asyncListDiffer.currentList.size
    }

    override fun onBindViewHolder(holder: ItemRecyclerViewHolder, position: Int) {
        val currentItem = asyncListDiffer.currentList[position]

        holder.binding.apply {
            Glide.with(holder.itemView.context).load(currentItem.thumbnailUrl).into(ivCircleImage)
            tvTitle.text = currentItem.title.toString()
            tvSubTitle.text = currentItem.thumbnailUrl
            tvBody.text = currentItem.albumId
        }
    }

}