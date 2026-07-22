package com.preetTractor.galaxyAndroid.ui.recyclerview

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.preetTractor.galaxyAndroid.data.beatplan.DataBeatPlan
import com.preetTractor.galaxyAndroid.databinding.ItemRvBeatPlanBinding


class BeatPlanListListingAdapter(
    itemList: List<DataBeatPlan>,
    context: Context
) :
    RecyclerView.Adapter<BeatPlanListListingAdapter.ItemViewHolder>() {
    private val itemList: List<DataBeatPlan>
    private val context: Context

    init {
        this.itemList = itemList
        this.context = context
    }

    private var onItemClickListener: ((String, String) -> Unit)? = null
    fun setOnItemClickListener(listener: (String, String) -> Unit) {
        onItemClickListener = listener
    }


    private var onItemWholeClickListener: ((DataBeatPlan) -> Unit)? = null
    fun setOnItemWholeClickListener(listener: (DataBeatPlan) -> Unit) {
        onItemWholeClickListener = listener
    }


    private var onItemMapClickListener: ((DataBeatPlan) -> Unit)? = null
    fun setOnItemMapClickListener(listener: (DataBeatPlan) -> Unit) {
        onItemMapClickListener = listener
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: ItemRvBeatPlanBinding =
            ItemRvBeatPlanBinding.inflate(inflater, parent, false)
        return ItemViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item: DataBeatPlan = itemList[position]
        holder.binding.tvUserName.text = item.CardName
        holder.binding.tvPriority.text = item.Priority
        holder.binding.tvTiming.text = item.Purpose
        holder.binding.tvRemarks.text = item.Remark
        holder.binding.etKm.setText("${item.distance} Km")
        holder.binding.tvApproveStatus.text = item.Approval_Status


        if (item.bp_address_detail != null && !item.bp_address_detail.isEmpty()) {
            holder.binding.tvLocation.setText(item.bp_address_detail.get(0).City);
        }
//        else {
//            holder.binding.tvLocation.setText(""); // Or set a default text
//        }


        holder.binding.ivPhone.setOnClickListener { click ->
            onItemClickListener?.let { click ->
                click(item.phone,"PHONE")
            }
        }

        holder.binding.etKm.setOnClickListener { click ->
            onItemMapClickListener?.let { click ->
                click(item)
            }
        }
        holder.itemView.setOnClickListener {
            onItemWholeClickListener?.let { click->
                click(item)

            }
        }


        /*     holder.binding.ivChekcInImage.setOnClickListener { click ->
                 onItemClickListener?.let { click ->
                     click(item.CheckIn_Image,"CHECK_IN_IMAGE")
                 }
             }
             holder.binding.ivcheckInPin.setOnClickListener { click ->
                 onItemClickListener?.let { click ->
                     click(item.CheckIn_Address,"CHECK_IN_ADDRESS")
                 }
             }
             holder.binding.ivCheckOutImage.setOnClickListener { click ->
                 onItemClickListener?.let { click ->
                     click(item.CheckOut_Image,"CHECK_OUT_IMAGE")
                 }
             }
             holder.binding.ivCheckOutPin.setOnClickListener { click ->
                 onItemClickListener?.let { click ->
                     click(item.CheckOut_Address,"CHECK_OUT_ADDRESS")
                 }
             }*/

    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    inner class ItemViewHolder(val binding: ItemRvBeatPlanBinding) :
        RecyclerView.ViewHolder(binding.getRoot()) {
    }
}