package com.preetTractor.galaxyAndroid.ui.recyclerview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.preetTractor.galaxyAndroid.data.beatplan.LocalDataTodayBeatPlan
import com.preetTractor.galaxyAndroid.databinding.ItemJointWorkBeatPlanBinding

class JointWorkSelectListAdapter(
    private val locations: MutableList<LocalDataTodayBeatPlan>,
    private val onSelectionChanged: (LocalDataTodayBeatPlan) -> Unit
) : RecyclerView.Adapter<JointWorkSelectListAdapter.LocationViewHolder>() {

    private val selectedItems = mutableSetOf<LocalDataTodayBeatPlan>()

    inner class LocationViewHolder(val binding: ItemJointWorkBeatPlanBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(location: LocalDataTodayBeatPlan) {
            if(location.Type.equals("Other")){
                binding.tvType.text = "Non Customer"
                binding.titleTextView.visibility = View.GONE
            }
            else if(location.Type.equals("Customer")){
                binding.titleTextView.text = location.City
                binding.tvType.text = location.Type
            }
            else{
                binding.titleTextView.visibility = View.GONE
                binding.tvType.text = location.Type
            }

            binding.coordinatesTextView.text = "Status - ${location.approval_status}"
            binding.checkBox.isChecked = selectedItems.contains(location)
            binding.tvAssignedTo.text = "Assigned To - ${location.assigned_name}"


            // Handle checkbox click
            binding.checkBox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    selectedItems.add(location)
                    location.isSelected = true
                } else {
                    location.isSelected = false
                    selectedItems.remove(location)
                }
                onSelectionChanged(location)
            }

            // Handle item click to toggle selection
            binding.root.setOnClickListener {
                binding.checkBox.isChecked = !binding.checkBox.isChecked
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationViewHolder {
        val binding =
            ItemJointWorkBeatPlanBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LocationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LocationViewHolder, position: Int) {
        holder.bind(locations[position])
    }

    override fun getItemCount(): Int = locations.size

    // Add selected items back to the list
    fun addSelectedItemsToList() {
        //  locations.addAll(selectedItems)
        notifyDataSetChanged()
    }
}