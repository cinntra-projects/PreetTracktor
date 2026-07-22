package com.preetTractor.galaxyAndroid.ui.orderUi.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.preetTractor.galaxyAndroid.R
import com.preetTractor.galaxyAndroid.databinding.ItemContentRowBinding


class ScrollableContentDispatchAdapter :
    RecyclerView.Adapter<ScrollableContentDispatchAdapter.ContentViewHolder>() {


    var rowsItems = mutableListOf<List<String>>()

    lateinit var scrollabelInnerContentDispatchedAdapter: ScrollabelInnerContentDispatchedAdapter


    fun clearAllData() {
        rowsItems.removeAll(rowsItems)
        notifyDataSetChanged()
    }

    fun setItems(newItems: MutableList<List<String>>) {
        rowsItems.addAll(newItems)



        notifyDataSetChanged()


    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContentViewHolder {
        val binding =
            ItemContentRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ContentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ContentViewHolder, position: Int) {
        holder.bind(rowsItems[position])



    }

    override fun getItemCount(): Int = rowsItems.size

    inner class ContentViewHolder(val binding: ItemContentRowBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(rowData: List<String>) {
            scrollabelInnerContentDispatchedAdapter =
                ScrollabelInnerContentDispatchedAdapter(rowData as MutableList<String>)
            binding.rowRecyclerView.adapter = scrollabelInnerContentDispatchedAdapter
            binding.rowRecyclerView.layoutManager =
                LinearLayoutManager(itemView.context, LinearLayoutManager.HORIZONTAL, false)
            scrollabelInnerContentDispatchedAdapter.notifyDataSetChanged()


            if (position == 0) {
                binding.apply {
                    linearHorizontalFirstHeading.setBackgroundColor(
                        itemView.context.resources.getColor(
                            R.color.offline_grey
                        )
                    )

                }
            } else {
                binding.apply {
                    linearHorizontalFirstHeading.setBackgroundColor(
                        itemView.context.resources.getColor(
                            R.color.white
                        )
                    )

                }
            }

        }
    }
}
