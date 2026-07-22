package com.preetTractor.galaxyAndroid.ui.orderUi.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.preetTractor.galaxyAndroid.BuildConfig
import com.preetTractor.galaxyAndroid.R
import com.preetTractor.galaxyAndroid.apiHelper.AppConstants
import com.preetTractor.galaxyAndroid.databinding.ItemCategoryForOrderBinding
import com.preetTractor.galaxyAndroid.orderUi.model.response.ResponseItemAllCategoryList

class CategoryInOrderAdapter(
      private val context: Context,
      private var dataList: List<ResponseItemAllCategoryList.Data> = emptyList(),
      private val onItemClicked: (ResponseItemAllCategoryList.Data, Int) -> Unit,
) : RecyclerView.Adapter<CategoryInOrderAdapter.CategoryViewHolder>() {

      inner class CategoryViewHolder(private val binding: ItemCategoryForOrderBinding) : RecyclerView.ViewHolder(binding.root) {

            @SuppressLint("SetTextI18n")
            fun bindData(item: ResponseItemAllCategoryList.Data) {
                  binding.apply {
                        item.apply {
                              tvCatgoryName.text = U_UTL_ITMCT
                              if (CategoryImage.isNotEmpty()) {
                                    Glide.with(context).load(BuildConfig.IMAGE_URL + CategoryImage)
                                          .into(ivCategoryImage)
                              } else {
                                    Glide.with(context).load(R.drawable.no_pictures)
                                          .into(ivCategoryImage)
                              }

                        }

                        itemView.setOnClickListener {
                              val position = bindingAdapterPosition
                              if (position != RecyclerView.NO_POSITION) {
                                    onItemClicked(item,position)
                              }
                        }
                  }
            }
      }

      override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
            val binding = ItemCategoryForOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return CategoryViewHolder(binding)
      }

      override fun getItemCount(): Int {
            return dataList.size
      }

      override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
            dataList[position].let { holder.bindData(it) }
      }
}
