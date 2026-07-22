package com.preetTractor.galaxyAndroid.ui.recyclerview

import androidx.recyclerview.widget.DiffUtil
import com.preetTractor.galaxyAndroid.data.ResponseJsonDataItem

class MyDiffUtilCallback : DiffUtil.ItemCallback<ResponseJsonDataItem>() {

    override fun areItemsTheSame(oldItem: ResponseJsonDataItem, newItem: ResponseJsonDataItem): Boolean {
        // Check if items have the same ID (unique identifier)
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: ResponseJsonDataItem, newItem: ResponseJsonDataItem): Boolean {
        // Check if the content of items are the same (i.e., all fields except ID)
        return oldItem == newItem
    }
}
