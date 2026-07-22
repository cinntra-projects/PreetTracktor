package com.preetTractor.galaxyAndroid.ui.recyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.preetTractor.galaxyAndroid.BuildConfig
import com.preetTractor.galaxyAndroid.apiHelper.AppConstants
import com.preetTractor.galaxyAndroid.data.model.customer.Attach
import com.preetTractor.galaxyAndroid.data.model.customer.DataOutletPicsFromCustomer
import com.preetTractor.galaxyAndroid.databinding.ItemImageOutletPicBinding
import com.preetTractor.galaxyAndroid.databinding.ItemOutletPicsBinding

class OutletPicAdapter(
    private val parentItems: List<DataOutletPicsFromCustomer>
) : RecyclerView.Adapter<OutletPicAdapter.ParentViewHolder>() {

    inner class ParentViewHolder(private val binding: ItemOutletPicsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(parentItem: DataOutletPicsFromCustomer) {
            binding.tvOutletHeading.text = parentItem.Remark

            // Set up the inner RecyclerView
            val childAdapter = OutletPicInnerAdapter(parentItem.Attach)
            binding.rvSelectedImages.adapter = childAdapter
           // binding.rvSelectedImages.layoutManager = LinearLayoutManager(binding.root.context)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParentViewHolder {
        val binding = ItemOutletPicsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ParentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ParentViewHolder, position: Int) {
        holder.bind(parentItems[position])
    }

    override fun getItemCount(): Int = parentItems.size
}

class OutletPicInnerAdapter(
    private val childItems: List<Attach>
) : RecyclerView.Adapter<OutletPicInnerAdapter.ChildViewHolder>() {

    inner class ChildViewHolder(private val binding: ItemImageOutletPicBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(childItem: Attach) {
          //  binding.root.text = childItem
            Glide.with(itemView.context).load(BuildConfig.IMAGE_URL+childItem.File).into(binding.ivImage)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChildViewHolder {
        val binding = ItemImageOutletPicBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChildViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChildViewHolder, position: Int) {
        holder.bind(childItems[position])
    }

    override fun getItemCount(): Int = childItems.size
}
