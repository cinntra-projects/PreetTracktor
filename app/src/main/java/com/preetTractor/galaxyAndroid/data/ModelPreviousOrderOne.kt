package com.preetTractor.galaxyAndroid.data

data class ModelPreviousOrderOne(
      val data: List<Data>
) {
      data class Data(
            val itemName: String,
            val itemPrice: Double,
            val itemQty: Int
      )
}
