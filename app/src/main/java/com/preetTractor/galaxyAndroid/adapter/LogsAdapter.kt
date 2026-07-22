package com.preetTractor.galaxyAndroid.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.preetTractor.galaxyAndroid.data.ba.ModelAllLogs
import com.preetTractor.galaxyAndroid.databinding.ItemRvLogsBinding
import com.preetTractor.galaxyAndroid.utils.ColorGenerator
import com.preetTractor.galaxyAndroid.utils.TextDrawable

class LogsAdapter(
      private val context:Context,
      private val logList:List<ModelAllLogs.Data?>
) : RecyclerView.Adapter<LogsAdapter.ExpenseViewHolder>() {

      inner class ExpenseViewHolder(private val binding: ItemRvLogsBinding) : RecyclerView.ViewHolder(binding.root) {

            @SuppressLint("SetTextI18n")
            fun bindData(item: ModelAllLogs.Data?) {
                  binding.apply {
                        tvBpName.text = item?.CardName
                        tvTotalSalePrice.text = "${item?.Total}"
                        val generator: ColorGenerator = ColorGenerator.MATERIAL
                        val color: Int = generator.randomColor
                        val firstChar = item?.CardName?.firstOrNull()?.uppercaseChar() ?: ' '
                        val drawable: TextDrawable = TextDrawable.builder()
                              .beginConfig()
                              .withBorder(2)
                              .endConfig()
                              .buildRound(firstChar.toString(), color)
                        ivNameIcon.setImageDrawable(drawable)
                  }
            }
      }

      override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
            val binding = ItemRvLogsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ExpenseViewHolder(binding)
      }

      // No need to implement getItemCount() since ListAdapter handles it
      override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
            logList[position].let { holder.bindData(it) }
      }

      override fun getItemCount(): Int {
            return logList.size
      }
}