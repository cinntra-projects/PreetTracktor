package com.preetTractor.galaxyAndroid.ui.recyclerview

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.preetTractor.galaxyAndroid.data.BeatPlanCustomerDropDownModel
import com.preetTractor.galaxyAndroid.databinding.ItemCustomerBottomListBinding


class CustomerListingPrimaryAdapter(
    itemList: List<BeatPlanCustomerDropDownModel.Data>,
    context: Context
) :
    RecyclerView.Adapter<CustomerListingPrimaryAdapter.ItemViewHolder>() {
    private var itemList: List<BeatPlanCustomerDropDownModel.Data>
    private val context: Context
    private var fullList: List<BeatPlanCustomerDropDownModel.Data> = itemList.toList()


    init {
        this.itemList = itemList
        this.context = context
    }

    private var onItemClickListener: ((BeatPlanCustomerDropDownModel.Data, Int) -> Unit)? = null
    fun setOnItemClickListener(listener: (BeatPlanCustomerDropDownModel.Data, Int) -> Unit) {
        onItemClickListener = listener
    }

    private var onItemMapClickListener: ((BeatPlanCustomerDropDownModel.Data) -> Unit)? = null
    fun setOnItemMapClickListener(listener: (BeatPlanCustomerDropDownModel.Data) -> Unit) {
        onItemMapClickListener = listener
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: ItemCustomerBottomListBinding =
            ItemCustomerBottomListBinding.inflate(inflater, parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item: BeatPlanCustomerDropDownModel.Data = itemList[position]
        holder.binding.apply {

            tvCustomerName.text = item.CardName
            tvLocation.text = item.remark

        }


        holder.itemView.setOnClickListener {
            onItemClickListener?.let { click ->
                click(item,position)
            }
        }



        holder.binding.tvKilometeres.setOnClickListener { click ->
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

    inner class ItemViewHolder(val binding: ItemCustomerBottomListBinding) :
        RecyclerView.ViewHolder(binding.root)
}