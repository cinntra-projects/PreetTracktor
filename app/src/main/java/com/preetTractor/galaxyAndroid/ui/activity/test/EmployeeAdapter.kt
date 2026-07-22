package com.preetTractor.galaxyAndroid.ui.activity.test

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.preetTractor.galaxyAndroid.R
import com.preetTractor.galaxyAndroid.databinding.ItemHerarchyBinding
import com.preetTractor.galaxyAndroid.databinding.ItemHerarchyInnerBinding

class EmployeeAdapter(
    private val employees: List<DataHeirarchYList>,
    private val level: Int = 1, // Indicates the depth level (1 to 4)
    private val onItemClickListener: (DataHeirarchYList, Int) -> Unit,
    private val onItemInnerListForwardClickListener: (DataHeirarchYList, Int) -> Unit,
) : RecyclerView.Adapter<EmployeeAdapter.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_HEADER = 0
        private const val VIEW_TYPE_ITEM = 1
    }


    override fun getItemViewType(position: Int): Int {
        return if (level == 1) VIEW_TYPE_HEADER else VIEW_TYPE_ITEM
    }



    private var onItemForwardClickListener: ((DataHeirarchYList, Int) -> Unit)? = null

    fun setOnForwardItemClickListener(listener: (DataHeirarchYList, Int) -> Unit) {
        onItemForwardClickListener = listener
    }

    inner class ViewHolder(val binding: ViewBinding) : RecyclerView.ViewHolder(binding.root)



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = if (viewType == VIEW_TYPE_HEADER) {
            // Inflate header layout
            ItemHerarchyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        } else {
            // Inflate item layout (for inner levels)
            ItemHerarchyInnerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        }
        return ViewHolder(binding)
    }



    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val employee = employees[position]
        val viewType = getItemViewType(position)

        when (viewType) {
            VIEW_TYPE_HEADER -> {
                val headerBinding = holder.binding as ItemHerarchyBinding
                if (employee.EmployeeID.isNotEmpty()){
                    headerBinding.tvUser.text = "${employee.full_name} -(${employee.EmployeeID})"
                }else{
                    headerBinding.tvUser.text = "${employee.full_name}"
                }

                headerBinding.tvUser.setOnClickListener {
                    employee.isExpanded = !employee.isExpanded
                    notifyItemChanged(position)
                    onItemClickListener(employee, level)
                }

                headerBinding.ivForward.setOnClickListener {
                    onItemForwardClickListener?.let { click ->
                        click(employee, level)
                    }
                }
            }

            VIEW_TYPE_ITEM -> {
                val itemBinding = holder.binding as ItemHerarchyInnerBinding
                if (employee.EmployeeID.isNotEmpty()){
                    itemBinding.tvUser.text = "${employee.full_name} -(${employee.EmployeeID})"
                }else{
                    itemBinding.tvUser.text = "${employee.full_name}"
                }



                // Adjust the margin of ivArrowInnerList based on the level
                val marginStart = 32 * level // Adjust the multiplier as needed for desired spacing
                val layoutParams = itemBinding.ivArrowInnerList.layoutParams as ViewGroup.MarginLayoutParams
                layoutParams.marginStart = marginStart
                itemBinding.ivArrowInnerList.layoutParams = layoutParams



                if (employee.reportees.isEmpty()) {
                    itemBinding.ivArrowInnerList.visibility = View.INVISIBLE
                } else {
                    itemBinding.ivArrowInnerList.visibility = View.VISIBLE
                }

                itemBinding.ivArrowInnerList.setOnClickListener {
                    employee.isExpanded = !employee.isExpanded
                    notifyItemChanged(position)
                    onItemClickListener(employee, level)
                }

                itemBinding.tvUser.setOnClickListener {
                    employee.isExpanded = !employee.isExpanded
                    notifyItemChanged(position)
                    onItemClickListener(employee, level)
                }

                itemBinding.ivForwardInner.setOnClickListener {
                    notifyItemChanged(position)
                   /* onItemForwardClickListener?.let { click ->
                        click(employee, level)
                    }*/
                    onItemInnerListForwardClickListener(employee, level)
                }
            }
        }

        // If the item is expanded and has reportees, display them
        if (employee.isExpanded && employee.reportees.isNotEmpty()) {
            holder.binding.root.findViewById<RecyclerView>(R.id.reporteesRecyclerView).apply {
                visibility = View.VISIBLE
                layoutManager = LinearLayoutManager(holder.itemView.context)
                adapter = EmployeeAdapter(
                    employee.reportees,
                    level + 1,
                    onItemClickListener,onItemInnerListForwardClickListener
                )
            }
        } else {
            holder.binding.root.findViewById<RecyclerView>(R.id.reporteesRecyclerView)?.visibility = View.GONE
        }
    }



    override fun getItemCount(): Int = employees.size


}
