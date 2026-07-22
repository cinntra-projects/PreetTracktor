package com.preetTractor.galaxyAndroid.ui.recyclerview


import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.preetTractor.galaxyAndroid.data.model.customer.DataCustomerLedger
import com.preetTractor.galaxyAndroid.databinding.ItemCustomerLedgerBinding

import java.util.*

class LedgerCustomerEntriesAdapter(
    private val context: Context,
    private var branchList: MutableList<DataCustomerLedger>
) : RecyclerView.Adapter<LedgerCustomerEntriesAdapter.ContactViewHolder>() {

    private var tempList: MutableList<DataCustomerLedger> = ArrayList()

    init {
        tempList.addAll(branchList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val binding = ItemCustomerLedgerBinding.inflate(LayoutInflater.from(context), parent, false)
        return ContactViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val item = branchList[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = branchList.size

    inner class ContactViewHolder(private val binding: ItemCustomerLedgerBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: DataCustomerLedger) {
          //  binding.tvCustomerNameLedgerEntry.text = item.cardName
          //  binding.tvCustomerAmountLedgerEntry.text = Globals.numberToK(item.totalSales)

            binding.root.setOnClickListener {
                // Handle click if necessary
            }
        }
    }

    fun updateAllData(newList: List<DataCustomerLedger>) {
        tempList.clear()
        tempList.addAll(newList)
        notifyDataSetChanged()
    }

    fun filter(charText: String) {
        val lowerCaseText = charText.lowercase(Locale.getDefault())
        branchList.clear()
        if (lowerCaseText.isEmpty()) {
            branchList.addAll(tempList)
        } else {
          /*  tempList.forEach { item ->
                if (!item.cardName.isNullOrEmpty() &&
                    (item.cardName.lowercase(Locale.getDefault()).contains(lowerCaseText)
                            || item.cardCode.lowercase(Locale.getDefault()).contains(lowerCaseText))
                ) {
                    branchList.add(item)
                }
            }*/
        }
        notifyDataSetChanged()
        Log.e("Search==>", "${branchList.size}")
    }
}
