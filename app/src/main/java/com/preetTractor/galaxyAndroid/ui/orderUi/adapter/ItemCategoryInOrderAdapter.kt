package com.preetTractor.galaxyAndroid.ui.orderUi.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.preetTractor.galaxyAndroid.BuildConfig
import com.preetTractor.galaxyAndroid.R
import com.preetTractor.galaxyAndroid.apiHelper.AppConstants
import com.preetTractor.galaxyAndroid.data.ResponseCategoryAllList
import com.preetTractor.galaxyAndroid.databinding.ItemCategoryForOrderBinding

class ItemCategoryInOrderAdapter(val context: Context) :
    ListAdapter<ResponseCategoryAllList.CategoryAllListData, ItemCategoryInOrderAdapter.CategoryListViewHolder>(
        DiffUtils()
    ) {

    private var onItemClickClickListener: ((ResponseCategoryAllList.CategoryAllListData, Int) -> Unit)? = null

    fun setOnItemClickListener(listener: (ResponseCategoryAllList.CategoryAllListData, Int) -> Unit) {
        onItemClickClickListener = listener
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryListViewHolder {
        return CategoryListViewHolder(
            ItemCategoryForOrderBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: CategoryListViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, holder.itemView.context)

        holder.itemView.setOnClickListener {

            onItemClickClickListener?.let { click ->
                click(item, position)
            }
        }

    }

    class CategoryListViewHolder(val binding: ItemCategoryForOrderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ResponseCategoryAllList.CategoryAllListData, context: Context) {
            binding.tvCatgoryName.text = item.CategoryName
            if (item.CategoryImageURL.isNotEmpty()) {
                Glide.with(context).load("${BuildConfig.IMAGE_URL}${item.CategoryImageURL}")
                    .into(binding.ivCategoryImage)
            } else {
                Glide.with(context).load(R.drawable.no_pictures)
                    .into(binding.ivCategoryImage)
            }


            /*    if (item.PaymentStatus == "Partially Paid") {
                    binding.status.text = item.PaymentStatus
                    binding.status.setTextColor(Color.parseColor("#ffa500"))
                    binding.status.setVisibility(View.VISIBLE)
                } else if (item.PaymentStatus == "Unpaid") {
                    binding.status.text = item.PaymentStatus
                    binding.status.setTextColor(Color.parseColor("#FF0000"))
                    binding.status.setVisibility(View.VISIBLE)
                } else if (item.PaymentStatus == "Paid") {
                    binding.status.text = item.PaymentStatus
                    binding.status.setTextColor(Color.parseColor("#00ff00"))
                    binding.status.setVisibility(View.VISIBLE)
                } else {
                    binding.status.text = "Unpaid"
                }*/

        }

    }


    class DiffUtils : DiffUtil.ItemCallback<ResponseCategoryAllList.CategoryAllListData>() {
        override fun areItemsTheSame(
            oldItem: ResponseCategoryAllList.CategoryAllListData,
            newItem: ResponseCategoryAllList.CategoryAllListData
        ): Boolean {
            return oldItem == newItem

        }

        override fun areContentsTheSame(
            oldItem: ResponseCategoryAllList.CategoryAllListData,
            newItem: ResponseCategoryAllList.CategoryAllListData
        ): Boolean {
            return oldItem == newItem
        }
    }

}