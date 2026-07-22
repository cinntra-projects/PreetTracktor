package com.preetTractor.galaxyAndroid.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.preetTractor.galaxyAndroid.data.beatplan.DataBeatPlan
import com.preetTractor.galaxyAndroid.databinding.BeatPlanBdrcItemListBinding
import com.preetTractor.galaxyAndroid.helper.Globals

class CustomerBeatPlanAdapter(
    private val list: MutableList<DataBeatPlan>,
    private val onClick: (DataBeatPlan) -> Unit
) : RecyclerView.Adapter<CustomerBeatPlanAdapter.ViewHolder>() {

    private var showIcon = false

    inner class ViewHolder(
        val binding: BeatPlanBdrcItemListBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        val binding = BeatPlanBdrcItemListBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {

        val item = list[position]

        holder.binding.apply {

            tvPriority.text = item.Priority
            tvRemarks.text = item.Remark
            tvApproveStatus.text = item.Approval_Status
            tvAssignedTo.text = item.AssignedName

            tvVisitDate.text = Globals.convert_yyyy_MM_dd_into_dd_MM_yyyy(item.Visit_Date)

            Log.d("c", "${item.Type}")
            tvType.text = item.Type
            tvCity.text = item.City

            if (item.Type == "Customer") {
                edit.visibility =
                    if (showIcon) View.VISIBLE else View.GONE
            }

        }

        holder.binding.edit.setOnClickListener {
            onClick(item)
        }


    }

    override fun getItemCount(): Int = list.size

    fun refreshList(refresh: Boolean) {
        showIcon = refresh
        notifyDataSetChanged()
    }
}