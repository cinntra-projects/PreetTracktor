package com.preetTractor.galaxyAndroid.mediaUi.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.preetTractor.galaxyAndroid.BuildConfig
import com.preetTractor.galaxyAndroid.databinding.ItemSchemeRecyclerviewBinding
import com.preetTractor.galaxyAndroid.apiHelper.AppConstants
import com.preetTractor.galaxyAndroid.moreUi.model.documentsModel.DocumentItemListModel


class SchemeAdapter(
    private val context: Context,
    private var dataList: ArrayList<DocumentItemListModel.Data>
) : RecyclerView.Adapter<SchemeAdapter.ViewHolder>() {


    private var onItemClickClickListener: ((DocumentItemListModel.Data, Int) -> Unit)? =
        null

    fun setOnItemClickListener(listener: (DocumentItemListModel.Data, Int) -> Unit) {
        onItemClickClickListener = listener
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSchemeRecyclerviewBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val dataPos = dataList[position]
        var videoId = ""

        holder.binding.apply {


            tvVideoTitle.text = dataPos.title
            Glide.with(context).load(BuildConfig.IMAGE_URL + dataPos.thumbnail).centerCrop()
                .into(icon)


        }


        val filePath: String = BuildConfig.IMAGE_URL + dataPos.file

        holder.itemView.setOnClickListener {
            onItemClickClickListener?.let { click ->
                click(dataPos, position)
            }


        }

    }


    override fun getItemCount(): Int {
        return dataList.size
    }


    inner class ViewHolder(val binding: ItemSchemeRecyclerviewBinding) :
        RecyclerView.ViewHolder(binding.root) {}


}