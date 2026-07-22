package com.preetTractor.galaxyAndroid.ui.recyclerview

import android.content.Context
import android.content.res.ColorStateList
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.preetTractor.galaxyAndroid.R
import com.preetTractor.galaxyAndroid.data.expense.newexpense.DataExpenseNewList
import com.preetTractor.galaxyAndroid.databinding.ItemExpenseListNewDesignBinding
import com.preetTractor.galaxyAndroid.helper.ExpenseDiffCallback
import com.preetTractor.galaxyAndroid.helper.Globals


class ExpenseAdapter(
    private val itemList: ArrayList<DataExpenseNewList>,
    private val context: Context
) : RecyclerView.Adapter<ExpenseAdapter.ItemViewHolder>() {

    private var onStatusBtnClickListener: ((DataExpenseNewList, Int) -> Unit)? = null

    fun setonStatusBtnClickListener(listener: (DataExpenseNewList, Int) -> Unit) {
        onStatusBtnClickListener = listener
    }


    private var onItemClickListener: ((DataExpenseNewList, Int) -> Unit)? = null

    fun setonItemClickListener(listener: (DataExpenseNewList, Int) -> Unit) {
        onItemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {

        val inflater = LayoutInflater.from(parent.context)
        val binding: ItemExpenseListNewDesignBinding =
            ItemExpenseListNewDesignBinding.inflate(inflater, parent, false)
        return ItemViewHolder(binding)


    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item: DataExpenseNewList = itemList[position]
        Log.e("ExpenseAdapter", item.expenseAmount)

        if (item.expenseAmount.isNotEmpty()) {
            holder.binding.tvAmount.text = ": \u20B9 " + item.expenseAmount
        } else {
            holder.binding.tvAmount.text = ""
        }


        holder.binding.tvExpenseName.text = "${item.expenseName} "
        holder.binding.tvStatusExpense.text = item.approvalStatus
        holder.binding.tvFromDate.text =
            Globals.dateStringConvertToDesiredFormat(
                item.fromDate,
                "yyyy-MM-dd", "dd/MM/yyyy"
            )
        holder.binding.tvToDate.text = Globals.dateStringConvertToDesiredFormat(
            item.toDate,
            "yyyy-MM-dd", "dd/MM/yyyy"
        )
        holder.binding.tvDate.text =
            Globals.dateStringConvertToDesiredFormat(
                item.fromDate,
                "yyyy-MM-dd", "dd/MM/yyyy"
            )

        holder.binding.apply {
            if (item.typeOfExpense.equals("Travelling", ignoreCase = true)) {
                linearTodate.visibility = View.VISIBLE
                tvDate.visibility = View.GONE
            } else {
                linearTodate.visibility = View.GONE
                tvDate.visibility = View.VISIBLE
            }
        }


        when (item.approvalStatus) {
            "Approved" -> {
                holder.binding.tvStatusExpense.backgroundTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(
                        holder.itemView.context,
                        R.color.green_transcluecent_color
                    )
                )

                holder.binding.tvStatusExpense.setTextColor(
                    ColorStateList.valueOf(
                        ContextCompat.getColor(
                            holder.itemView.context,
                            R.color.green_approved
                        )
                    )
                )


            }

            "Rejected" -> {
                holder.binding.tvStatusExpense.backgroundTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(
                        holder.itemView.context,
                        R.color.red_rejected_transluecent_color
                    )
                )

                holder.binding.tvStatusExpense.setTextColor(
                    ColorStateList.valueOf(
                        ContextCompat.getColor(
                            holder.itemView.context,
                            R.color.red_rejected
                        )
                    )
                )


            }

            "Pending" -> {
                holder.binding.tvStatusExpense.backgroundTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(
                        holder.itemView.context,
                        R.color.pending_trancsleucent
                    )
                )

                holder.binding.tvStatusExpense.setTextColor(
                    ColorStateList.valueOf(
                        ContextCompat.getColor(
                            holder.itemView.context,
                            R.color.pending
                        )
                    )
                )
                holder.binding.tvStatusExpense.setOnClickListener {
                    onStatusBtnClickListener?.invoke(item, position)
                }



            }
        }

        holder.itemView.setOnClickListener {
            onItemClickListener?.let { click ->
                click(item, position)
            }
        }


    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    inner class ItemViewHolder(binding: ItemExpenseListNewDesignBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val binding: ItemExpenseListNewDesignBinding

        init {
            this.binding = binding
        }
    }

    fun updateEmployeeListItems(employees: List<DataExpenseNewList>) {
        val diffCallback: ExpenseDiffCallback =
            ExpenseDiffCallback(itemList, employees as List<DataExpenseNewList>)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this.itemList.clear()
        this.itemList.addAll(employees)
        diffResult.dispatchUpdatesTo(this)


    }

}