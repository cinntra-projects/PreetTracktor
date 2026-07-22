package com.preetTractor.galaxyAndroid.ui.orderUi.activity

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.preetTractor.galaxyAndroid.data.model.order.model.response.ModelOrderListing
import com.preetTractor.galaxyAndroid.databinding.ActivityPreviousOrderDetailsBinding
import com.preetTractor.galaxyAndroid.ui.orderUi.adapter.PreviousOrderOneAdapter
import com.preetTractor.galaxyAndroid.utils.ColorGenerator
import com.preetTractor.galaxyAndroid.utils.TextDrawable

class PreviousOrderDetailsActivity : AppCompatActivity() {
      private lateinit var binding:ActivityPreviousOrderDetailsBinding
      private lateinit var mContext:Context
      private lateinit var data: ModelOrderListing.Data
      private lateinit var orderOneItemAdapter: PreviousOrderOneAdapter
      override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            binding = ActivityPreviousOrderDetailsBinding.inflate(layoutInflater)
            mContext = this
            initViews()
            setListeners()
            setContentView(binding.root)
      }

      private fun initViews() {
            intent?.let { it ->
                  data = it.getParcelableExtra<ModelOrderListing.Data>("data")!!
                  setDefaultData(data)
            }
            setAdapter()
      }

      private fun setDefaultData(data: ModelOrderListing.Data) {
            binding.apply {
                  tvTitle.text = data.CardName
                  tvEmail.text = data.C_Email
                  tvMobile.text = data.C_Mobile
                  val generator: ColorGenerator = ColorGenerator.MATERIAL
                  val color: Int = generator.randomColor
                  val firstChar = data.CardName.firstOrNull()?.uppercaseChar() ?: ' '
                  val drawable: TextDrawable = TextDrawable.builder()
                        .beginConfig()
                        .withBorder(2) /* thickness in px */
                        .endConfig()
                        .buildRound(firstChar.toString(), color)
                  ivProfileIcon.setImageDrawable(drawable)
            }
      }

      @SuppressLint("SetTextI18n")
      private fun setAdapter() {

            /*val list = listOf(
                  ModelPreviousOrderOne.Data("Water Bottle", 200.00, 6),
                  ModelPreviousOrderOne.Data("Laptop", 60000.00, 2),
                  ModelPreviousOrderOne.Data("Mouse Pad", 300.00, 2),
                  ModelPreviousOrderOne.Data("Lunch Box", 450.00, 1),
                  ModelPreviousOrderOne.Data("Almirah Tanisk", 19500.00, 1),
                  ModelPreviousOrderOne.Data("Curtain White Print", 1200.00, 6),
                  ModelPreviousOrderOne.Data("Water Bottle", 200.00, 6),
                  ModelPreviousOrderOne.Data("Laptop", 60000.00, 2),
                  ModelPreviousOrderOne.Data("Mouse Pad", 300.00, 2),
                  ModelPreviousOrderOne.Data("Lunch Box", 450.00, 1),
                  ModelPreviousOrderOne.Data("Almirah Tanisk", 19500.00, 1)
            )*/
            binding.tvItemCounts.text = "Items (${data.DocumentLines.size})"
            binding.rvOrderListItems.apply {
                  layoutManager = LinearLayoutManager(this@PreviousOrderDetailsActivity, LinearLayoutManager.VERTICAL, false)
                  orderOneItemAdapter = PreviousOrderOneAdapter(data.DocumentLines, onItemClicked = { pos, data ->

                  })
                  adapter = orderOneItemAdapter
                  orderOneItemAdapter.notifyDataSetChanged()
            }
      }

      private fun setListeners() {
            binding.apply {
                  ivBackPress.setOnClickListener {
                        finish()
                  }
            }
      }
}