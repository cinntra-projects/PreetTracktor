package com.preetTractor.galaxyAndroid.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.preetTractor.galaxyAndroid.R
import com.preetTractor.galaxyAndroid.data.beatplan.DataBeatPlan
import com.preetTractor.galaxyAndroid.databinding.RvBeatPlanItemBinding


class BeatPlanListingAdapter1(
    private val itemList: List<DataBeatPlan>,
    private val context: Context, private val Type: String
) :
    RecyclerView.Adapter<BeatPlanListingAdapter1.ItemViewHolder>() {

    private var onItemClickListener: ((String, String) -> Unit)? = null
    fun setOnItemClickListener(listener: (String, String) -> Unit) {
        onItemClickListener = listener
    }


    private var onItemWholeClickListener: ((DataBeatPlan) -> Unit)? = null
    fun setOnItemWholeClickListener(listener: (DataBeatPlan) -> Unit) {
        onItemWholeClickListener = listener
    }


    private var onItemRescheduleClickListener: ((DataBeatPlan) -> Unit)? = null
    fun setOnItemRescheduleClickListener(listener: (DataBeatPlan) -> Unit) {
        onItemRescheduleClickListener = listener
    }


    private var onItemApprovalClickListener: ((DataBeatPlan) -> Unit)? = null
    fun setOnItemApprovalClickListener(listener: (DataBeatPlan) -> Unit) {
        onItemApprovalClickListener = listener
    }

    private var onItemMapClickListener: ((DataBeatPlan) -> Unit)? = null
    fun setOnItemMapClickListener(listener: (DataBeatPlan) -> Unit) {
        onItemMapClickListener = listener
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
//        val binding: RvBeatPlanItemBinding =
//            RvBeatPlanItemBinding.inflate(inflater, parent, false)

//        todo TarunSharma
        val binding: RvBeatPlanItemBinding =
            RvBeatPlanItemBinding.inflate(inflater, parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item: DataBeatPlan = itemList[position]

        holder.binding.apply {

            tvLeaveStatus.visibility = View.GONE
            // todo uncomment this code for approving the beat plan from the customer
//            if (fromWhere.equals("approval", ignoreCase = true)) {
//                tvLeaveStatus.visibility = View.VISIBLE
//            } else {
//                tvLeaveStatus.visibility = View.GONE
//            }

            if(Type == "Customer"){
                tvUserName.text = item.CardName.ifEmpty { "NA" }
            }
            else{
                tvUserName.text = item.ProspectName.ifEmpty { "NA" }
            }


            tvPriority.text = item.Priority
            tvTiming.text = item.Shop_StartAt
            tvRemarks.text = item.Remark
            etKm.setText("${item.distance} Km")
            tvApproveStatus.text = item.Approval_Status
            tvModeOfTravel.text = item.transport_mode


        }


        if(Type == "Customer"){
            if (item.bp_address_detail.isNotEmpty()) {
                holder.binding.tvLocation.text = item.bp_address_detail[0].City.ifEmpty { "NA" }
            }
        }
        else{
            holder.binding.tvLocation.text = item.ProspectNumber.ifEmpty { "NA" }
        }




        holder.binding.ivPhone.setOnClickListener {
            onItemClickListener?.let { click ->
                click(item.phone, "PHONE")
            }
        }



        holder.binding.etKm.setOnClickListener {
            onItemMapClickListener?.let { click ->
                click(item)
            }
        }

        holder.itemView.setOnClickListener {
            onItemWholeClickListener?.let { click ->
                click(item)
            }
        }


        holder.binding.etReschedule.setOnClickListener {
            onItemRescheduleClickListener?.let { click ->
                click(item)
            }
        }





        when (item.Approval_Status) {
            "Pending" -> {

                holder.binding.tvLeaveStatus.text = "Pending"
                holder.binding.tvLeaveStatus.setTextColor(context.resources.getColor(R.color.pending_text_color));
                holder.binding.tvLeaveStatus.setBackgroundResource(R.drawable.pending_status_bg)

                holder.binding.tvLeaveStatus.setOnClickListener {
                    onItemApprovalClickListener?.let { click ->
                        click(item)
                    }
                }

            }

            "Approved" -> {

                holder.binding.tvLeaveStatus.text = "Approved"
                holder.binding.tvLeaveStatus.setTextColor(context.resources.getColor(R.color.purple_700));
                holder.binding.tvLeaveStatus.setBackgroundResource(R.drawable.approved_status_bg)
                holder.binding.tvLeaveStatus.setOnClickListener {
                    Toast.makeText(context, "Already Approved", Toast.LENGTH_SHORT).show()
                }

            }

            "Rejected" -> {
                holder.binding.tvLeaveStatus.text = "Rejected"
                holder.binding.tvLeaveStatus.setTextColor(context.resources.getColor(R.color.red_reject_text));
                holder.binding.tvLeaveStatus.setBackgroundResource(R.drawable.reject_status_bg)

                holder.binding.tvLeaveStatus.setOnClickListener {
                    Toast.makeText(context, "Already Rejected", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }


    override fun getItemCount(): Int {
        return itemList.size
    }

//    inner class ItemViewHolder(val binding: RvBeatPlanItemBinding) :
//        RecyclerView.ViewHolder(binding.root)

    // todo TarunSharma
    inner class ItemViewHolder(val binding: RvBeatPlanItemBinding) :
        RecyclerView.ViewHolder(binding.root)
}