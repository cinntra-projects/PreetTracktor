package com.preetTractor.galaxyAndroid.mediaUi.adapter


import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.preetTractor.galaxyAndroid.R
import com.preetTractor.galaxyAndroid.databinding.ItemCategorySelectorInSalesBinding
import com.preetTractor.galaxyAndroid.localdata.LocalDataForCategoryInSalesAdapter
import com.preetTractor.galaxyAndroid.moreUi.model.documentsModel.DocumentListModel


class HeadingMediaDynamicAdapter(
    private var context: Context,
    private var dataList: ArrayList<DocumentListModel.Data>
) : RecyclerView.Adapter<HeadingMediaDynamicAdapter.AppPromotionViewHolder>() {

    private val items = mutableListOf<LocalDataForCategoryInSalesAdapter>()

    private var selectedItemIndex: Int = 0


    private var onItemClickClickListener: ((DocumentListModel.Data, Int) -> Unit)? =
        null

    fun setOnItemClickListener(listener: (DocumentListModel.Data, Int) -> Unit) {
        onItemClickClickListener = listener
    }


    fun clearAllData() {
        items.removeAll(items)
        notifyDataSetChanged()
    }

    fun setItems(newItems: List<LocalDataForCategoryInSalesAdapter>) {
        items.addAll(newItems)
        notifyDataSetChanged()
    }


    class AppPromotionViewHolder(val binding: ItemCategorySelectorInSalesBinding) :
        RecyclerView.ViewHolder(binding.root)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppPromotionViewHolder {
        return AppPromotionViewHolder(
            ItemCategorySelectorInSalesBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: AppPromotionViewHolder, position: Int) {
        var cur = dataList[position]


        holder.binding.apply {
            tvCategory.text = cur.name
            // Load your custom font from the font resource folder
            val customTypeface =
                ResourcesCompat.getFont(holder.itemView.context, R.font.poppins_regular)


            if (position == selectedItemIndex) {
                tvCategory.setTextColor(
                    ContextCompat.getColor(
                        holder.itemView.context,
                        R.color.colorPrimary
                    )
                )
                viewGrey.setBackgroundColor(
                    ContextCompat.getColor(
                        holder.itemView.context,
                        R.color.colorPrimary
                    )
                )
                tvCategory.setTypeface(customTypeface, Typeface.BOLD)
                viewGrey.visibility = View.VISIBLE
            } else {
                tvCategory.setTypeface(customTypeface, Typeface.NORMAL)
                tvCategory.setTextColor(
                    ContextCompat.getColor(
                        holder.itemView.context,
                        `in`.aabhasjindal.otptextview.R.color.black
                    )
                )
                viewGrey.setBackgroundColor(
                    ContextCompat.getColor(
                        holder.itemView.context,
                        `in`.aabhasjindal.otptextview.R.color.grey
                    )
                )
                viewGrey.visibility = View.INVISIBLE
            }
        }



        holder.itemView.setOnClickListener {
            // Update selected item index
            val previousSelectedItemIndex = selectedItemIndex
            selectedItemIndex = holder.adapterPosition
            // Notify item changes for both the previously selected item and the currently selected item
            notifyItemChanged(previousSelectedItemIndex)
            notifyItemChanged(selectedItemIndex)

            onItemClickClickListener?.let { click ->
                click(dataList[position], position)
            }


        }

    }


    override fun getItemCount(): Int {
        return dataList.size
    }


}