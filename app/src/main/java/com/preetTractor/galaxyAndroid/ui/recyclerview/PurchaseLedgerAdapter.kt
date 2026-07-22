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



class PurchaseLedgerAdapter(
    private val context: Context,
    private val branchList: List<MonthGroupSalesList>,
    private val cardCode: String,
    private val cardName: String
) : RecyclerView.Adapter<PurchaseLedgerAdapter.ContactViewHolder>() {

    private lateinit var binding: SaleReceiptReceivableAdapterBinding // Change to your binding class name

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        binding = SaleReceiptReceivableAdapterBinding.inflate(LayoutInflater.from(context), parent, false)
        return ContactViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val monthPurchase = branchList[position]
        holder.bind(monthPurchase)
    }

    override fun getItemCount(): Int = branchList.size

    inner class ContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val title: TextView = binding.title
        private val unitPrice: TextView = binding.unitPrice // Use your View Binding variable names

        init {
            itemView.setOnClickListener {
                val arr = branchList[adapterPosition].month.split(" ")
                val calendar = Calendar.getInstance()
                calendar.set(Calendar.MONTH, findIndex(monthArr, arr[0].trim()) - 1)
                val month = calendar.get(Calendar.MONTH) + 1
                val startDate = calendar.getActualMinimum(Calendar.DAY_OF_MONTH)
                val endDate = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

                val start = "20${arr[1].trim()}-${String.format("%02d", month)}-${String.format("%02d", startDate)}"
                val end = "20${arr[1].trim()}-${String.format("%02d", month)}-${String.format("%02d", endDate)}"

               /* val intent = Intent(context, ParticularCustomerSaleInfo::class.java).apply {
                    putExtra("FromWhere", "SaleLedger")
                    putExtra("cardCode", cardCode)
                    putExtra("cardName", cardName)
                    putExtra("summary", "purchase")
                    putExtra("startDate", start)
                    putExtra("endDate", end)
                }
                context.startActivity(intent)*/
            }
        }

        fun bind(monthPurchase: MonthGroupSalesList) {
            title.text = monthPurchase.month
            unitPrice.text = "₹ ${Globals.numberToK(Globals.changeDecemal(monthPurchase.docTotal))}"
        }
    }

    private val monthArr = arrayOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")

    private fun findIndex(arr: Array<String>, t: String): Int {
        return arr.indexOfFirst { it.equals(t, ignoreCase = true) } + 1 // +1 for the offset
    }
}
