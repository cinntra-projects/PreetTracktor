package com.preetTractor.galaxyAndroid.ui.activity.test

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.preetTractor.galaxyAndroid.databinding.ItemHerarchyBinding

class MultiLevelAdapter(
    private val items: List<Itemlevel>,
    private val level: Int = 1, // Indicates the depth level (1 to 4)
    private val onItemClickListener: (Itemlevel, Int) -> Unit
) : RecyclerView.Adapter<MultiLevelAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemHerarchyBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemHerarchyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        /*holder.binding.itemText.text = item.name

        // Handle clicks to expand/collapse the nested list
        holder.binding.itemText.setOnClickListener {
            item.isExpanded = !item.isExpanded
            notifyItemChanged(position)
            onItemClickListener(item, level) // Trigger the click callback
        }

        // If the item is expanded and has sub-items, display them
        if (item.isExpanded && item.subItems != null) {
            holder.binding.subListRecyclerView.visibility = View.VISIBLE
            holder.binding.subListRecyclerView.layoutManager = LinearLayoutManager(holder.itemView.context)
            holder.binding.subListRecyclerView.adapter = MultiLevelAdapter(item.subItems, level + 1, onItemClickListener)
        } else {
            holder.binding.subListRecyclerView.visibility = View.GONE
        }*/
    }

    override fun getItemCount(): Int = items.size
}
