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
import java.util.Calendar

class ReceiptLedgerAdapter(
    private val context: Context,
    private var branchList: List<MonthGroupSalesList>, // Use var to allow updates
    private val cardCode: String,
    private val cardName: String
) : RecyclerView.Adapter<ReceiptLedgerAdapter.ContactViewHolder>() {

    private lateinit var binding: SaleReceiptReceivableAdapterBinding // Change to your binding class name

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
        private val unitPrice: TextView = binding.unitPrice // Use your View Binding variable names

        init {
            itemView.setOnClickListener {
                val arr = branchList[adapterPosition].month.split(" ")

                val calendar = Calendar.getInstance().apply {
                    set(Calendar.MONTH, findIndex(monthArr, arr[0].trim()) - 1)
                }
                val month = calendar.get(Calendar.MONTH) + 1
                val startDate = calendar.getActualMinimum(Calendar.DAY_OF_MONTH)
                val endDate = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

                val start = "20${arr[1].trim()}-${String.format("%02d", month)}-${String.format("%02d", startDate)}"
                val end = "20${arr[1].trim()}-${String.format("%02d", month)}-${String.format("%02d", endDate)}"

                /*Prefs.putString(Globals.FROM_DATE, start)
                Prefs.putString(Globals.TO_DATE, end)

                val intent = Intent(context, ParticularCustomerReceiptInfo::class.java).apply {
                    putExtra("FromWhere", "ReceiptLedger")
                    putExtra("cardCode", cardCode)
                    putExtra("cardName", cardName)
                    putExtra("startDate", start)
                    putExtra("endDate", end)
                }
                context.startActivity(intent)*/
            }
        }

        fun bind(monthSales: MonthGroupSalesList) {
            title.text = monthSales.month
            unitPrice.text = "₹ ${Globals.numberToK(monthSales.docTotal)}"
        }
    }

    private val monthArr = arrayOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")

    private fun findIndex(arr: Array<String>, t: String): Int {
        return arr.indexOfFirst { it.equals(t, ignoreCase = true) } + 1 // +1 for the offset
    }

    // Uncomment and adapt if you need a filtering method
    /*
    fun filter(charText: String) {
        val filteredList = if (charText.isEmpty()) {
            tempList // Assume tempList is preserved somewhere
        } else {
            tempList.filter { it.cardName?.toLowerCase(Locale.getDefault())?.contains(charText.toLowerCase(Locale.getDefault())) == true }
        }
        branchList = filteredList
        notifyDataSetChanged()
    }
    */
}

