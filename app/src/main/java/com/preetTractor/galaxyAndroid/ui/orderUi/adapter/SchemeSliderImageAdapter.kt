package com.preetTractor.galaxyAndroid.ui.orderUi.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.preetTractor.galaxyAndroid.BuildConfig
import com.preetTractor.galaxyAndroid.orderUi.model.response.ResponseSchemeList
import com.preetTractor.galaxyAndroid.apiHelper.AppConstants
import com.preetTractor.galaxyAndroid.databinding.SliderImageLayoutBinding
import com.preetTractor.galaxyAndroid.ui.orderUi.activity.ItemListFromSchemesActivity

class SchemeSliderImageAdapter(
    var context: Context,
    var list: java.util.ArrayList<ResponseSchemeList.Data>
) : RecyclerView.Adapter<SchemeSliderImageAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            SliderImageLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        var currentItem = list[position]
        if (currentItem.attachment.isNotEmpty()) {
            Glide.with(context.applicationContext)
                .load(BuildConfig.IMAGE_URL + currentItem.attachment[0].File)
                .into(holder.binding.sliderImage)
            holder.binding.tvPercont.text = "${currentItem.discount_percent.toString()} % OFF"

        }

        holder.itemView.setOnClickListener {
            Intent(context, ItemListFromSchemesActivity::class.java).also {
                it.putExtra("id",currentItem.id)
                it.putExtra("discount",currentItem.discount_percent)
                context.startActivity(it)
            }
        }

        /*      try {
                  holder.binding.tvPercont.text = context.setDynamicValueWithStringXml(
                      R.string.percent_dynamic,
                      currentItem.discount_percent.toString()
                  )
              } catch (e: Exception) {
                  holder.binding.tvPercont.text = context.setDynamicValueWithStringXml(
                      R.string.percent_dynamic,
                   "0"
                  )
              }*/


//        Toast.makeText(context, currentItem.id.toString(), Toast.LENGTH_SHORT).show()
    }


    override fun getItemCount(): Int {
        return list.size
    }

    inner class ViewHolder(val binding: SliderImageLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {}

}