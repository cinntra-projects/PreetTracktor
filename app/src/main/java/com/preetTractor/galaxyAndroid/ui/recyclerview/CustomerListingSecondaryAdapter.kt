package com.preetTractor.galaxyAndroid.ui.recyclerview

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.preetTractor.galaxyAndroid.data.model.customer.DataSecondaryCustomerList
import com.preetTractor.galaxyAndroid.databinding.ItemSecondaryCustomerListBinding


class CustomerListingSecondaryAdapter(
    itemList: List<DataSecondaryCustomerList>,
    context: Context
) :
    RecyclerView.Adapter<CustomerListingSecondaryAdapter.ItemViewHolder>() {
    private var itemList: List<DataSecondaryCustomerList>
    private val context: Context
    private var fullList: List<DataSecondaryCustomerList> = itemList.toList()


    init {
        this.itemList = itemList
        this.context = context
    }

    private var onItemClickListener: ((DataSecondaryCustomerList, Int) -> Unit)? = null
    fun setOnItemClickListener(listener: (DataSecondaryCustomerList, Int) -> Unit) {
        onItemClickListener = listener
    }

    private var onItemMapClickListener: ((DataSecondaryCustomerList) -> Unit)? = null
    fun setOnItemMapClickListener(listener: (DataSecondaryCustomerList) -> Unit) {
        onItemMapClickListener = listener
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: ItemSecondaryCustomerListBinding =
            ItemSecondaryCustomerListBinding.inflate(inflater, parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item: DataSecondaryCustomerList = itemList[position]
        holder.binding.apply {

            tvCustomerName.text = item.CompanyName
            tvLocation.text = "${item.Address} - ${item.Pincode}"
            tvMobile.visibility=View.VISIBLE
            tvMobile.text = item.Mobile

        }


        holder.itemView.setOnClickListener {
            onItemClickListener?.let { click ->
                click(item,position)
            }
        }



        holder.binding.tvThreeDot.setOnClickListener { click ->
            onItemMapClickListener?.let { click ->
                click(item)
            }
        }

    }

    // Filter function
    fun filterList(query: String) {
        val filteredList = if (query.isEmpty()) {
            fullList
        } else {
            fullList.filter { item ->
                item.CardName.contains(query, ignoreCase = true) ||
                        item.CardCode.contains(query, ignoreCase = true)
            }
        }
        itemList = filteredList
        notifyDataSetChanged()
    }


    override fun getItemCount(): Int {
        return itemList.size
    }

    inner class ItemViewHolder(val binding: ItemSecondaryCustomerListBinding) :
        RecyclerView.ViewHolder(binding.root)
}