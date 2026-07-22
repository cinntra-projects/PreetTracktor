package com.preetTractor.galaxyAndroid.searchUi.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.preetTractor.galaxyAndroid.BuildConfig
import com.preetTractor.galaxyAndroid.R
import com.preetTractor.galaxyAndroid.databinding.ItemCategoryForOrderBinding
import com.preetTractor.galaxyAndroid.orderUi.model.response.DataRecentSearchAndOrder

class RecentOrderAndSearchAdapter(private val context: Context) :
    ListAdapter<DataRecentSearchAndOrder, RecentOrderAndSearchAdapter.ViewHolder>(
        DiffUtils()
    ) {

    private var onItemClickClickListener: ((DataRecentSearchAndOrder, Int) -> Unit)? = null

    fun setOnItemClickListener(listener: (DataRecentSearchAndOrder, Int) -> Unit) {
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
            tvCatgoryName.text = " ${item.ItemDescription}"
            if (item.Image.isNotEmpty()) {
                Glide.with(context).load(BuildConfig.IMAGE_URL + item.Image).into(ivCategoryImage)
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
        RecyclerView.ViewHolder(binding.root)

    class DiffUtils : DiffUtil.ItemCallback<DataRecentSearchAndOrder>() {
        override fun areItemsTheSame(
            oldItem: DataRecentSearchAndOrder, newItem: DataRecentSearchAndOrder
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: DataRecentSearchAndOrder, newItem: DataRecentSearchAndOrder
        ): Boolean {
            return oldItem == newItem
        }

    }


}