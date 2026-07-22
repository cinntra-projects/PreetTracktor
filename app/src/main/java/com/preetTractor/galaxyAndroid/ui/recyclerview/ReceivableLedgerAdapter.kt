package com.preetTractor.galaxyAndroid.ui.recyclerview

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.preetTractor.galaxyAndroid.data.model.customer.MonthGroupSalesList
import com.preetTractor.galaxyAndroid.databinding.SaleReceiptReceivableAdapterBinding
import com.preetTractor.galaxyAndroid.helper.Globals
import java.util.*

class ReceivableLedgerAdapter(
    private val context: Context,
    private var branchList: List<MonthGroupSalesList>, // Use var for mutability
    private val cardCode: String,
    private val cardName: String
) : RecyclerView.Adapter<ReceivableLedgerAdapter.ContactViewHolder>() {

    private lateinit var binding: SaleReceiptReceivableAdapterBinding // Replace with your binding class

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        binding = SaleReceiptReceivableAdapterBinding.inflate(LayoutInflater.from(context), parent, false)
        return ContactViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        holder.bind(branchList[position])
    }

    override fun getItemCount(): Int = branchList.size

    inner class ContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val title: TextView = binding.title
        private val unitPrice: TextView = binding.unitPrice // Adjust based on your binding

        init {
            itemView.setOnClickListener {
                val month = branchList[adapterPosition].month
         /*       val filterValue = when {
                    month.equals("Not Due", ignoreCase = true) -> {
                        Prefs.putString(Globals.FROM_DATE_receivable, "nondue")
                        "-1"
                    }
                    month.equals(">30 Days", ignoreCase = true) -> {
                        Prefs.putString(Globals.FROM_DATE_receivable, "30")
                        "30"
                    }
                    month.equals(">60 Days", ignoreCase = true) -> {
                        Prefs.putString(Globals.FROM_DATE_receivable, "60")
                        "60"
                    }
                    month.equals(">0 Days", ignoreCase = true) -> {
                        Prefs.putString(Globals.FROM_DATE_receivable, "All")
                        ""
                    }
                    else -> ""
                }

                Intent(context, ParticularCustomerReceivableInfo::class.java).apply {
                    putExtra("FromWhere", "Receivable")
                    putExtra("cardCode", cardCode)
                    putExtra("cardName", cardName)
                    putExtra("filterValue", filterValue)
                }.also {
                    context.startActivity(it)
                }*/
            }
        }

        fun bind(monthSales: MonthGroupSalesList) {
            title.text = monthSales.month
            unitPrice.text = "₹ ${Globals.numberToK(monthSales.docTotal)}"
        }
    }

    private val monthArr = arrayOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")

    private fun findIndex(arr: Array<String>, t: String): Int {
        return arr.indexOfFirst { it.equals(t, ignoreCase = true) } + 1 // +1 for index offset
    }

    // Uncomment and adapt if you need a filtering method
    /*
    fun filter(charText: String) {
        val filteredList = if (charText.isEmpty()) {
            tempList // Assume tempList is preserved somewhere
        } else {
            tempList.filter {
                it.cardName?.toLowerCase(Locale.getDefault())?.contains(charText.toLowerCase(Locale.getDefault())) == true
            }
        }
        branchList = filteredList
        notifyDataSetChanged()
    }
    */
}
