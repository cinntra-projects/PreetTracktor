package com.preetTractor.galaxyAndroid.ui.recyclerview

import android.content.Context
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.preetTractor.galaxyAndroid.R
import com.preetTractor.galaxyAndroid.data.LeaveStatusData
import com.preetTractor.galaxyAndroid.databinding.ItemLeaveBinding
import com.preetTractor.galaxyAndroid.helper.Globals
import com.preetTractor.galaxyAndroid.helper.LeaveDiffCallback


class AllLeaveStatusAdapter(
    private val itemList: ArrayList<LeaveStatusData>,
    private val context: Context,
    private val showEditButton: Boolean = true
) :
    RecyclerView.Adapter<AllLeaveStatusAdapter.ItemViewHolder>() {

    private var onEditBtnClickListener: ((LeaveStatusData, Int) -> Unit)? = null

    fun setonEditBtnClickListener(listener: (LeaveStatusData, Int) -> Unit) {
        onEditBtnClickListener = listener
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: ItemLeaveBinding =
            ItemLeaveBinding.inflate(inflater, parent, false)
        return ItemViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item: LeaveStatusData = itemList[position]

        var dateStr = Globals.dateStringConvertToDesiredFormat(
            item.Leave_Date,
            "yyyy-MM-dd",
            "dd/MM/yyyy"
        )
        holder.binding.tvDate.text = dateStr
        Log.e("AllLeaveStatus Adapter", "onFailure: ${dateStr}")
        holder.binding.tvLeaveType.text = item.Type

        when (item.Approval_Status) {
            "Pending" -> {

                holder.binding.groupRejectionReason.visibility = View.VISIBLE
                holder.binding.tvLeaveStatus.text = "Pending"
                holder.binding.tvLeaveStatus.setTextColor(context.resources.getColor(R.color.pending_text_color));
                holder.binding.tvLeaveStatus.setBackgroundResource(R.drawable.pending_status_bg)
                holder.binding.tvReasonMsg.text = item.Reason

                holder.binding.btnEdit.setOnClickListener { _ ->
                    onEditBtnClickListener?.invoke(item, position)
                }
            }

            "Approved" -> {
                holder.binding.groupRejectionReason.visibility = View.GONE
                holder.binding.tvLeaveStatus.text = "Approved"
                holder.binding.tvLeaveStatus.setTextColor(context.resources.getColor(R.color.purple_700));
                holder.binding.tvLeaveStatus.setBackgroundResource(R.drawable.approved_status_bg)

            }

            "Rejected" -> {
                holder.binding.groupRejectionReason.visibility = View.GONE
                holder.binding.tvLeaveStatus.text = "Rejected"
                holder.binding.tvLeaveStatus.setTextColor(context.resources.getColor(R.color.red_reject_text));
                holder.binding.tvLeaveStatus.setBackgroundResource(R.drawable.reject_status_bg)
            }
        }

        /*holder.itemView.setOnClickListener { _ ->
            onItemClickListener?.let { click ->
                click(item, position)
            }
        }*/

        if (showEditButton) {
            holder.binding.btnEdit.visibility = View.VISIBLE
        } else {
            holder.binding.btnEdit.visibility = View.INVISIBLE
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    fun updateEmployeeListItems(employees: List<LeaveStatusData>) {
        val diffCallback: LeaveDiffCallback =
            LeaveDiffCallback(itemList, employees as List<LeaveStatusData>)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this.itemList.clear()
        this.itemList.addAll(employees)
        diffResult.dispatchUpdatesTo(this)


    }

    inner class ItemViewHolder(val binding: ItemLeaveBinding) :
        RecyclerView.ViewHolder(binding.getRoot()) {

    }
}