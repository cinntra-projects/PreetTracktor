package com.preetTractor.galaxyAndroid.ui.orderUi.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.preetTractor.galaxyAndroid.BuildConfig
import com.preetTractor.galaxyAndroid.searchUi.model.DataSearchItemDmsSuggestion
import com.preetTractor.galaxyAndroid.R
import com.preetTractor.galaxyAndroid.apiHelper.AppConstants
import com.preetTractor.galaxyAndroid.databinding.ItemCategoryForOrderBinding

class RecentSearchAdapter(private val context: Context) :
    ListAdapter<DataSearchItemDmsSuggestion, RecentSearchAdapter.ViewHolder>(
        DiffUtils()
    ) {

    private var onItemClickClickListener: ((DataSearchItemDmsSuggestion, Int) -> Unit)? = null

    fun setOnItemClickListener(listener: (DataSearchItemDmsSuggestion, Int) -> Unit) {
        onItemClickClickListener = listener
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemCategoryForOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var item = getItem(position)
        holder.binding.apply {
            tvCatgoryName.text = " ${item.Name}"
            if (item.ItemImage.isNotEmpty()) {
                Glide.with(context).load(BuildConfig.IMAGE_URL + item.ItemImage).into(ivCategoryImage)
            } else {
                Glide.with(context).load(R.drawable.no_pictures).into(ivCategoryImage)
            }

        }

        holder.itemView.setOnClickListener {

            onItemClickClickListener?.let { click ->
                click(item, position)
            }

        }

    }


    inner class ViewHolder(val binding: ItemCategoryForOrderBinding) :
        RecyclerView.ViewHolder(binding.root) {


    }

    class DiffUtils : DiffUtil.ItemCallback<DataSearchItemDmsSuggestion>() {
        override fun areItemsTheSame(
            oldItem: DataSearchItemDmsSuggestion,
            newItem: DataSearchItemDmsSuggestion
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: DataSearchItemDmsSuggestion,
            newItem: DataSearchItemDmsSuggestion
        ): Boolean {
            return oldItem == newItem
        }

    }


}