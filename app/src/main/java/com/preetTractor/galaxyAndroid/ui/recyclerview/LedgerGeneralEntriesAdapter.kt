package com.preetTractor.galaxyAndroid.ui.recyclerview

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.preetTractor.galaxyAndroid.data.model.customer.JournalEntryLineBodyData
import com.preetTractor.galaxyAndroid.databinding.ItemCustomerLedgerBinding
import com.preetTractor.galaxyAndroid.helper.Globals

class LedgerGeneralEntriesAdapter(
    private val context: Context,
    private var branchList: List<JournalEntryLineBodyData>,
    private val alertDialog: AlertDialog
) : RecyclerView.Adapter<LedgerGeneralEntriesAdapter.ContactViewHolder>() {

    private var tempList: List<JournalEntryLineBodyData> = ArrayList(branchList)
    private var clickListener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val binding = ItemCustomerLedgerBinding.inflate(LayoutInflater.from(context), parent, false)
        return ContactViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val entry = branchList[position]
        holder.binding.invoiceId.text = entry.reference1
        val reverseDate = Globals.convertDateToDDMMYYYY(entry.dueDate!!)
        holder.binding.invoiceDate.text = reverseDate
        holder.binding.refNo.text = entry.accountName
        val amountString = Globals.foo(entry.balance)
        holder.binding.totalAmount.text = Globals.numberToK(amountString)

        val credit = entry.credit!!.toDoubleOrNull() ?: 0.0
        val debit = entry.debit!!.toDoubleOrNull() ?: 0.0

        if (credit > 0.0) {
            holder.binding.receivedAmount.text = Globals.numberToK(Globals.getRoundOffUpTOTwo(entry.credit!!))
            holder.binding.receivedAmount.setTextColor(Color.parseColor("#FF0000"))
        } else {
            holder.binding.receivedAmount.text = Globals.numberToK(Globals.getRoundOffUpTOTwo(entry.debit!!))
            holder.binding.receivedAmount.setTextColor(Color.parseColor("#4ebf08"))
        }

       /* holder.itemView.setOnClickListener {
            callAPiForGetDocID(entry.originalJournal, entry.original, position)
        }*/
    }

    override fun getItemCount(): Int = branchList.size

    inner class ContactViewHolder(val binding: ItemCustomerLedgerBinding) : RecyclerView.ViewHolder(binding.root)

/*    private fun callAPiForGetDocID(originalJournal: String, original: String, position: Int) {
        alertDialog.show()

        val jsonObject = JsonObject().apply {
            addProperty("OriginalJournal", originalJournal)
            addProperty("Original", original)
        }

        val call = NewApiClient.getInstance().getApiService(context).getDocIDForGeneralEntry(jsonObject)
        call.enqueue(object : Callback<ResponseForDocId> {
            override fun onResponse(call: Call<ResponseForDocId>, response: Response<ResponseForDocId>) {
                alertDialog.dismiss()
                if (response.isSuccessful && response.body()?.status == "200") {
                    handleSuccessfulResponse(response.body()!!, position)
                } else {
                    Toast.makeText(context, response.body()?.message ?: "Error", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseForDocId>, t: Throwable) {
                alertDialog.dismiss()
                Toast.makeText(context, t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun handleSuccessfulResponse(response: ResponseForDocId, position: Int) {
        val entry = branchList[position]
        val intent = Intent(context, when (entry.originalJournal) {
            "ttARInvoice" -> InvoiceTransactionFullInfo::class.java
            "ttAPInvoice" -> InvoiceTransactionFullInfo::class.java
            "ttReceipt" -> ReceiptTransactionFullInfo::class.java
            "ttARCredItnote" -> CreditOneActivity::class.java
            "ttAPCreditNote" -> CreditOneActivity::class.java
            "ttJournalEntry" -> {
                clickListener?.onItemClick(entry.id)
                return
            }
            "ttVendorPayment" -> ReceiptTransactionFullInfo::class.java
            else -> return
        }).apply {
            putExtra("FromWhere", "Ledger")
            putExtra("ID", response.docId)
            putExtra("Heading", entry.originalJournal)
            putExtra("status", entry)
        }
        context.startActivity(intent)
    }*/

    interface OnItemClickListener {
        fun onItemClick(id: String)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        clickListener = listener
    }
}
