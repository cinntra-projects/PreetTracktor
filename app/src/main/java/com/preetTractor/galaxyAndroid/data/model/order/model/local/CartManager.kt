package com.preetTractor.galaxyAndroid.data.model.order.model.local

import com.preetTractor.galaxyAndroid.apiHelper.AppConstants
import com.preetTractor.galaxyAndroid.orderUi.model.local.LocalDataForBACart

class CartManager {
      private val cartItems = mutableListOf<LocalDataForBACart>()

      fun addItem(item: LocalDataForBACart) {
            val existingItem = cartItems.find { it.id == item.id }
            if (existingItem != null) {
                  existingItem.Quantity = item.Quantity // Update quantity
            } else {
                  cartItems.add(item)
            }
            saveCartToPreferences()
      }

      fun removeItem(itemId: String) {
            cartItems.removeAll { it.id == itemId }
            saveCartToPreferences()
      }

      fun getCartItems(): MutableList<LocalDataForBACart> = cartItems

      private fun saveCartToPreferences() {
            AppConstants.saveBaCartListToPreferences(cartItems)
      }

      companion object {
            val instance: CartManager by lazy { CartManager() }
      }
}


