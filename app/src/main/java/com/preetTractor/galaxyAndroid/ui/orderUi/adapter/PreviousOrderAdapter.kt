package com.preetTractor.galaxyAndroid.ui.orderUi.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.preetTractor.galaxyAndroid.data.ModelPreviousOrder
import com.preetTractor.galaxyAndroid.databinding.ItemRvPreviousOrdersBinding
import com.preetTractor.galaxyAndroid.utils.ColorGenerator
import com.preetTractor.galaxyAndroid.utils.TextDrawable

class PreviousOrderAdapter(
      private val dataList: List<ModelPreviousOrder.Data?>? = emptyList(),
      private val onItemClicked: (Int,ModelPreviousOrder.Data) -> Unit,
) : RecyclerView.Adapter<PreviousOrderAdapter.ExpenseViewHolder>() {

      inner class ExpenseViewHolder(private val binding: ItemRvPreviousOrdersBinding) : RecyclerView.ViewHolder(binding.root) {

            @SuppressLint("SetTextI18n")
            fun bindData(item: ModelPreviousOrder.Data?) {
                  binding.apply {
                        tvUserName.text = item?.name
                        tvMobileNo.text = item?.mobile
                        tvEmail.text = item?.email
                        val generator: ColorGenerator = ColorGenerator.MATERIAL
                        val color: Int = generator.randomColor
                        val firstChar = item?.name?.firstOrNull()?.uppercaseChar() ?: ' '
                        val drawable: TextDrawable = TextDrawable.builder()
                              .beginConfig()
                              .withBorder(2) /* thickness in px */
                              .endConfig()
                              .buildRound(firstChar.toString(), color)
                        ivNameIcon.setImageDrawable(drawable)

                        itemView.setOnClickListener {
                              if (item != null) {
                                    onItemClicked(position,item)
                              }
                        }
                  }
            }
      }

      override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
            val binding = ItemRvPreviousOrdersBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ExpenseViewHolder(binding)
      }

      // No need to implement getItemCount() since ListAdapter handles it
      override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
            dataList?.get(position).let { holder.bindData(it) }
      }

      override fun getItemCount(): Int {
            return dataList?.size ?: 0
      }
}