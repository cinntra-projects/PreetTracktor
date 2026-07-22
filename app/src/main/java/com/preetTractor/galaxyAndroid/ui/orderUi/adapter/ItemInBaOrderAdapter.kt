package com.preetTractor.galaxyAndroid.ui.orderUi.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.preetTractor.galaxyAndroid.data.model.order.model.local.CartManager
import com.preetTractor.galaxyAndroid.data.model.order.model.response.ba.ModelItemAllByCategory
import com.preetTractor.galaxyAndroid.databinding.ItemInOrderAddBinding
import com.preetTractor.galaxyAndroid.helper.Globals
import com.preetTractor.galaxyAndroid.helper.Globals.setRupeesDrawable
import com.preetTractor.galaxyAndroid.orderUi.model.local.LocalDataForBACart
import com.pixplicity.easyprefs.library.Prefs

class ItemInBaOrderAdapter(
      private val context: Context,
      private val itemList: List<ModelItemAllByCategory.Data>,
      private val quantityChangeListener: OnQuantityChangeListener
) : RecyclerView.Adapter<ItemInBaOrderAdapter.ItemViewHolder>() {

      private val cartManager = CartManager.instance

      inner class ItemViewHolder(private val binding: ItemInOrderAddBinding) :
            RecyclerView.ViewHolder(binding.root) {

            @SuppressLint("SetTextI18n")
            fun bindData(item: ModelItemAllByCategory.Data) {
                  binding.apply {
                        // Get the current quantity from CartManager
                        val currentCartItem = cartManager.getCartItems().find { it.id == item.id.toString() }
                        val currentQuantity = currentCartItem?.Quantity ?: 0

                        etItemQty.setText(currentQuantity.toString())
                        tvItemName.text = "${adapterPosition + 1}. - ${item.ItemName}"
                        setRupeesDrawable(context,tvItemPrice,true,25)
                        tvItemPrice.text = if (currentQuantity > 0) "${currentQuantity * item.UnitPrice}" else "${item.UnitPrice}"

                        // Handle button clicks
                        btnPlus.setOnClickListener {
                              val newQuantity = currentQuantity + 1
                              updateItemQuantity(item, newQuantity)
                              quantityChangeListener.onPlusClicked(item, newQuantity)
                        }

                        btnMinus.setOnClickListener {
                              if (currentQuantity > 0) {
                                    val newQuantity = currentQuantity - 1
                                    updateItemQuantity(item, newQuantity)
                                    quantityChangeListener.onPlusClicked(item, newQuantity)
                              } else {
                                    Toast.makeText(context, "Quantity cannot be less than 0!", Toast.LENGTH_SHORT)
                                          .show()
                              }
                        }
                  }
            }

            private fun updateItemQuantity(item: ModelItemAllByCategory.Data, newQuantity: Int) {
                  if (newQuantity == 0) {
                        /*Globals.showAlertDialog(
                              context,
                              "Remove Item",
                              "Are you sure you want to Remove this Item?",
                              "Remove",
                              "Cancel",
                              onDelete = {
                                    // Remove item from cart and update UI
                                    cartManager.removeItem(item.id.toString())
                                    notifyItemChanged(adapterPosition)
                                    Toast.makeText(context, "Removed Successfully", Toast.LENGTH_SHORT).show()

                              },
                              onCancel = {

                              },
                              iconImg = R.drawable.ic_delete
                        )*/

                        cartManager.removeItem(item.id.toString())
                        notifyItemChanged(adapterPosition)
                        Toast.makeText(context, "Removed Successfully", Toast.LENGTH_SHORT).show()
                  } else {
                        // Update or add item in cart
                        val localDataForBACart = LocalDataForBACart(
                              id = item.id.toString(),
                              OrderID = "",
                              ItemCode = item.ItemCode,
                              ItemDescription = item.ItemName,
                              UnitPrice = item.UnitPrice.toInt(),
                              U_UTL_SD = Prefs.getString(Globals.SPECIAL_DISC),
                              Currency = "INR",
                              TaxCode = "IGST18",
                              TaxRate = item.Tax.toString(),
                              UnitPriceown = item.UnitPrice.toInt(),
                              U_UTL_TD = Prefs.getString(Globals.DISCOUNT_PERCENT),
                              DiscountPercent = item.Discount.toInt(),
                              ProjectCode = "",
                              FreeText = "",
                              UomNo = item.UnitPrice.toInt(),
                              UoMCode = "Manual",
                              Image = "",
                              U_UTL_DD = Prefs.getString(Globals.DEALER_DISC),
                              Quantity = newQuantity
                        )
                        cartManager.addItem(localDataForBACart)
                        notifyItemChanged(adapterPosition)
                  }
            }
      }

      override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
            val binding = ItemInOrderAddBinding.inflate(
                  LayoutInflater.from(parent.context),
                  parent,
                  false
            )
            return ItemViewHolder(binding)
      }

      override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
            val item = itemList[position]
            holder.bindData(item)
      }

      override fun getItemCount(): Int = itemList.size

      interface OnQuantityChangeListener {
            fun onPlusClicked(item: ModelItemAllByCategory.Data, newQuantity: Int)
            fun onMinusClicked(item: ModelItemAllByCategory.Data, newQuantity: Int)
      }
}