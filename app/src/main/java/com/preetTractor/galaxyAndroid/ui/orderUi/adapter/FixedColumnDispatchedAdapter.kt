package com.preetTractor.galaxyAndroid.ui.orderUi.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.preetTractor.galaxyAndroid.R
import com.preetTractor.galaxyAndroid.databinding.ItemFixedColumnBinding
import com.preetTractor.galaxyAndroid.orderUi.model.local.LocalDataForDispatch


class FixedColumnDispatchedAdapter :
    RecyclerView.Adapter<FixedColumnDispatchedAdapter.FixedColumnViewHolder>() {

    private val items = mutableListOf<LocalDataForDispatch>()

    private var onItemClickListener: ((LocalDataForDispatch, Int) -> Unit)? = null
    fun setOnItemClickListener(listener: (LocalDataForDispatch, Int) -> Unit) {
        onItemClickListener = listener
    }


    fun clearAllData() {
        items.removeAll(items)
        notifyDataSetChanged()
    }

    fun setItems(newItems: List<LocalDataForDispatch>) {
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FixedColumnViewHolder {
        val binding =
            ItemFixedColumnBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FixedColumnViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FixedColumnViewHolder, position: Int) {
        holder.bind(items[position], position)

        holder.itemView.setOnClickListener {
            onItemClickListener?.let { click ->
                click(items[position], position)

            }

        }
    }

    override fun getItemCount(): Int = items.size

    class FixedColumnViewHolder(private val binding: ItemFixedColumnBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(text: LocalDataForDispatch, position: Int) {
            binding.textView.text = text.DocDate
            if (position == 0) {
                binding.apply {
                    linearLayoutPdfDateColumn.setBackgroundColor(
                        itemView.context.resources.getColor(
                            R.color.offline_grey
                        )
                    )

                }
            } else {
                binding.apply {
                    linearLayoutPdfDateColumn.setBackgroundColor(
                        itemView.context.resources.getColor(
                            R.color.white
                        )
                    )

                }
            }


        }
    }
}
