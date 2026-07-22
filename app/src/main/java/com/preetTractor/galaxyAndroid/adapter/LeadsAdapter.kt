package com.preetTractor.galaxyAndroid.adapter

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.preetTractor.galaxyAndroid.R
import com.preetTractor.galaxyAndroid.data.LeadValue
import com.preetTractor.galaxyAndroid.databinding.LeadsAdapterScreenBinding
import com.preetTractor.galaxyAndroid.helper.Globals

class LeadsAdapter(
    private val leadList: ArrayList<LeadValue>,
    private val onOptionClick: (LeadValue) -> Unit,
    private val onLeadClick: (LeadValue) -> Unit,
    private val onFollowUpClick: (LeadValue) -> Unit,
    private val onUpdateClick: (LeadValue) -> Unit
) : RecyclerView.Adapter<LeadsAdapter.LeadViewHolder>() {

    inner class LeadViewHolder(
        val binding: LeadsAdapterScreenBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): LeadViewHolder {

        val binding = LeadsAdapterScreenBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )

        return LeadViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: LeadViewHolder, position: Int
    ) {

        val lv = leadList[position]

        // Bind your views here
        holder.binding.itemTitle.text = lv.companyName
        holder.binding.itemDate.text = Globals.convert_yyyy_MM_dd_into_dd_MM_yyyy(lv.date)
        if(lv.assignedTo != null)
            holder.binding.assigned.text = lv.assignedTo.SalesEmployeeName
        holder.binding.price.text = lv.status
        holder.binding.colorType.setBackgroundTintList(
            ColorStateList.valueOf(
                ContextCompat.getColor(
                    holder.itemView.context,
                    when (lv.leadType) {
                        "Hot" -> R.color.red
                        "Warm" -> R.color.yellow
                        "Cold" -> R.color.colorPrimary
                        else -> R.color.white
                    }
                )
            )
        )
        holder.binding.person.setOnClickListener {
            onOptionClick(lv)
        }
        holder.binding.option.setOnClickListener {
            onUpdateClick(lv)
        }
        holder.binding.followUp.setOnClickListener {
            onFollowUpClick(lv)
        }
        holder.binding.root.setOnClickListener {
            onLeadClick(lv)
        }
    }

    override fun getItemCount(): Int {
        return leadList.size
    }

    fun setData(
        newList: List<LeadValue>
    ) {

        leadList.clear()
        leadList.addAll(newList)

        notifyDataSetChanged()
    }

    fun addData(
        newList: List<LeadValue>
    ) {

        val startPosition = leadList.size

        leadList.addAll(newList)

        notifyItemRangeInserted(
            startPosition, newList.size
        )
    }

    fun clearData() {

        leadList.clear()

        notifyDataSetChanged()
    }

    fun getData(): List<LeadValue> {
        return leadList
    }
}