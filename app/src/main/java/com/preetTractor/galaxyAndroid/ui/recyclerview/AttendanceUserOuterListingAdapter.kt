package com.preetTractor.galaxyAndroid.ui.recyclerview

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.preetTractor.galaxyAndroid.data.DataOuterAttendanceListing
import com.preetTractor.galaxyAndroid.databinding.ItemUserTrackingListingBinding
import com.preetTractor.galaxyAndroid.helper.Globals


class AttendanceUserOuterListingAdapter(
    itemList: List<DataOuterAttendanceListing>,
    context: Context
) :
    RecyclerView.Adapter<AttendanceUserOuterListingAdapter.ItemViewHolder>() {
    private val itemList: List<DataOuterAttendanceListing>
    private val context: Context

    init {
        this.itemList = itemList
        this.context = context
    }

    private var onItemClickListener: ((DataOuterAttendanceListing, Int) -> Unit)? = null
    fun setOnItemClickListener(listener: (DataOuterAttendanceListing, Int) -> Unit) {
        onItemClickListener = listener
    }


    private var onItemViewsClickListener: ((String,String) -> Unit)? = null
    fun setOnItemViewsClickListener(listener: (String,String) -> Unit) {
        onItemViewsClickListener = listener
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: ItemUserTrackingListingBinding =
            ItemUserTrackingListingBinding.inflate(inflater, parent, false)
        return ItemViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item: DataOuterAttendanceListing = itemList[position]
        holder.binding.tvDate.text = Globals.convertDateToDayMonth(item.Create_Date)
        holder.binding.tvTotalHrs.text = Globals.convertSecondsToHMS(item.Total_Hour)
        holder.binding.tvCheckInTime.text = item.Create_Time
        holder.binding.tvTimeCheckOut.text = item.Update_Time


        holder.itemView.setOnClickListener { _ ->
            onItemClickListener?.let { click ->
                click(item, position)
            }
        }


        holder.binding.ivChekcInImage.setOnClickListener { click ->
            onItemViewsClickListener?.let { click ->
                click(item.CheckIn_Image,"CHECK_IN_IMAGE")
            }
        }
        holder.binding.ivcheckInPin.setOnClickListener { click ->
            if (item.CheckIn_Address.isEmpty()){
                Toast.makeText(context, "No Address Found", Toast.LENGTH_SHORT).show()
            }else{
                onItemViewsClickListener?.let { click ->
                    click(item.CheckIn_Address,"CHECK_IN_ADDRESS")
                }
            }

        }
        holder.binding.ivCheckOutImage.setOnClickListener { click ->
            onItemViewsClickListener?.let { click ->
                click(item.CheckOut_Image,"CHECK_OUT_IMAGE")
            }
        }
        holder.binding.ivCheckOutPin.setOnClickListener { click ->
            if (item.CheckOut_Address.isEmpty()){
                Toast.makeText(context, "No Address Found", Toast.LENGTH_SHORT).show()
            }else{
                onItemViewsClickListener?.let { click ->
                    click(item.CheckOut_Address,"CHECK_OUT_ADDRESS")
                }
            }

        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    inner class ItemViewHolder(val binding: ItemUserTrackingListingBinding) :
        RecyclerView.ViewHolder(binding.getRoot()) {
    }
}