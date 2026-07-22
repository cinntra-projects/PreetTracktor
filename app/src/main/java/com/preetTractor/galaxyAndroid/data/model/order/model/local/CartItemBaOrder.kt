package com.preetTractor.galaxyAndroid.data.model.order.model.local

data class CartItemBaOrder(
      val data: List<ItemsBaOrder>
){
      data class ItemsBaOrder(
            val id: Int,
            val itemName: String,
            val unitPrice:Double=0.0,
            var itemQuantity: Int = 0
      )
}
