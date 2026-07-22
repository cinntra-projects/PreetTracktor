package com.preetTractor.galaxyAndroid.ui.recyclerview

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.preetTractor.galaxyAndroid.R
import com.preetTractor.galaxyAndroid.data.model.customer.DataItemDashboard

import com.preetTractor.galaxyAndroid.helper.Globals
import java.util.*

class ItemStockAdapter(
    private val context: Context,
    private val branchList: MutableList<DataItemDashboard>,
    private val zoneCode: String
) : RecyclerView.Adapter<ItemStockAdapter.ContactViewHolder>() {

    private val tempList: MutableList<DataItemDashboard> = mutableListOf()

    init {
        tempList.addAll(branchList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_list_dashboard, parent, false)
        return ContactViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val item = branchList[position]

        holder.itemName.text = "${item.ItemCode} - ${item.ItemName}"
        holder.itemPriceIndividual.text = "Std price: ${Globals.numberTokOnlyForStd(item.UnitPrice)}"
        holder.itemPriceTotal.text = "Total Price: ${Globals.numberToK(item.TotalPrice)}"
        holder.quantity.text = " ${Globals.numberToK(item.TotalQty)}"

        if (Globals.numberToK(item.TotalQty) == "0") {
            holder.quantity.visibility = View.INVISIBLE
        } else {
            holder.quantity.visibility = View.VISIBLE
        }

     /*   holder.itemView.setOnClickListener {
            val intent = Intent(context, ItemPurchasedByListOfCustomersActivity::class.java)
            intent.putExtra("itemcode", item.itemCode)
            intent.putExtra("zoneCode", zoneCode)
            intent.putExtra("itemname", item.itemName)
            context.startActivity(intent)
        }*/
    }

    override fun getItemCount(): Int {
        return branchList.size
    }

    inner class ContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemName: TextView = itemView.findViewById(R.id.tvItemName)
        val itemPriceTotal: TextView = itemView.findViewById(R.id.tvtotAlPrice)
        val itemPriceIndividual: TextView = itemView.findViewById(R.id.tvStandardPrice)
        val quantity: TextView = itemView.findViewById(R.id.tvQuantityNos)
    }

    fun filter(charText: String) {
        val lowerCharText = charText.lowercase()
        branchList.clear()

        if (lowerCharText.isEmpty()) {
            branchList.addAll(tempList)
        } else {
            for (item in tempList) {
                if (item.ItemName != null && item.ItemName.isNotEmpty()) {
                    if (item.ItemName.lowercase().contains(lowerCharText)) {
                        branchList.add(item)
                    }
                }
            }
        }

        notifyDataSetChanged()
    }

    fun AllData(tmp: List<DataItemDashboard>) {
        tempList.clear()
        tempList.addAll(tmp)
        notifyDataSetChanged()
    }
}
