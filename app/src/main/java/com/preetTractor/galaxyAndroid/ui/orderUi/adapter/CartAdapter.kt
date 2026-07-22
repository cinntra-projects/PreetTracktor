package com.preetTractor.galaxyAndroid.ui.orderUi.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.preetTractor.galaxyAndroid.R
import com.preetTractor.galaxyAndroid.apiHelper.AppConstants
import com.preetTractor.galaxyAndroid.databinding.RecyclerviewItemForItemCartBinding
import com.preetTractor.galaxyAndroid.helper.Globals
import com.preetTractor.galaxyAndroid.helper.Globals.stringToInt
import com.preetTractor.galaxyAndroid.orderUi.model.local.LocalDataForCart
import com.pixplicity.easyprefs.library.Prefs


class CartAdapter() :
    RecyclerView.Adapter<CartAdapter.ItemViewHolder>() {

    companion object {
       // private const val TAG = "ItemListFromSubCategory"
    }

    private val items = mutableListOf<LocalDataForCart>()

    private var onItemClickListener: ((LocalDataForCart, Int) -> Unit)? = null
    fun setOnItemClickListener(listener: (LocalDataForCart, Int) -> Unit) {
        onItemClickListener = listener
    }


    private var onItemRefreshCalculationClickListener: ((LocalDataForCart, Int) -> Unit)? = null
    fun setOnItemRefreshCalculationClickListener(listener: (LocalDataForCart, Int) -> Unit) {
        onItemRefreshCalculationClickListener = listener
    }


    private var onItemCallClickListener: ((LocalDataForCart) -> Unit)? = null
    fun setOnItemCallClickListener(listener: (LocalDataForCart) -> Unit) {
        onItemCallClickListener = listener
    }

    fun clearAllData() {
        items.removeAll(items)
        notifyDataSetChanged()
    }

    fun setItems(newItems: List<LocalDataForCart>) {
        items.addAll(newItems)
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            RecyclerviewItemForItemCartBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item, holder.itemView.context)
        holder.itemView.setOnClickListener {
            onItemClickListener?.let { click ->
                click(item, position)
            }
        }


    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class ItemViewHolder(var binding: RecyclerviewItemForItemCartBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(currentDocLine: LocalDataForCart, context: Context) {

            // Bind item properties to the UI elements
            binding.apply {
                tvItemName.text = currentDocLine.ItemDescription

                var basePrice =
                    currentDocLine.UnitPrice * currentDocLine.Quantity /** stringToInt(
                        currentDocLine.SalesQtyPerPackUnit
                    ).toDouble()*/

                /*  currentDocLine.UnitPrice.toInt() * currentDocLine.itemquantity.toInt() * currentDocLine.SalesQtyPerPackUnit.toInt()*/


                var dealerDiscount = stringToInt(Prefs.getString(Globals.DEALER_DISC)) * basePrice / 100.0

                var specialDiscount = stringToInt(Prefs.getString(Globals.SPECIAL_DISC)) * basePrice / 100.0

                var disc = stringToInt(Prefs.getString( Globals.ADDITIONAL_DISC))


                var additonalDiscount = disc * basePrice / 100.0

                tvAdditionalDiscoiuntAmount.text = "₹ "+ Globals.numberToK(additonalDiscount.toString())
                tvSpecialDiscountTotal.text ="₹ "+  Globals.numberToK(specialDiscount.toString())
                tvDlrDiscAmount.text ="₹ "+  Globals.numberToK(dealerDiscount.toString())
                tvTotalBillingAmount.text = "₹ "+ Globals.numberToK(basePrice.toString())


                headingAdditionalDisc.text = "Adl Disc (${Prefs.getString(Globals.ADDITIONAL_DISC)}%)"
                headingDealerDiscount.text = "Dlr Disc (${Prefs.getString( Globals.DEALER_DISC)}%)"
                headingSplDiscount.text = "Spl Disc (${Prefs.getString( Globals.SPECIAL_DISC)}%)"


                tvDiscAmount.text = currentDocLine.DiscountPercent.toString()

                /*if (currentDocLine.PriceType.equals("MRPRATE")) {
                    linearAdlDiscount.visibility = View.GONE
                    linearSpecialDiscount.visibility = View.VISIBLE
                    linearDealerDiscount.visibility = View.VISIBLE


                } else if (currentDocLine.PriceType.equals("FLATRATE")) {
                    linearAdlDiscount.visibility = View.VISIBLE
                    linearSpecialDiscount.visibility = View.GONE
                    linearDealerDiscount.visibility = View.GONE
                }*/


            }

            if (AppConstants.cartListForOrderRequest.isNotEmpty()) {

                /*   if ( Globals.cartListForOrderRequest.get(position).ItemCode==currentDocLine.ItemCode){
                       binding.addNewItem.visibility = View.INVISIBLE
                       binding.addQuantity.visibility = View.VISIBLE
                       binding.total.text = currentDocLine.itemquantity.toString()
                   }else{
                       binding.addNewItem.visibility = View.VISIBLE
                       binding.addQuantity.visibility = View.INVISIBLE
                   }*/




                if (setupLocalArrayList(currentDocLine, itemView.context)) {

                    binding.addQuantity.visibility = View.VISIBLE
                    for (currentInItem in AppConstants.cartListForOrderRequest) {
                        if (currentInItem.ItemCode == currentDocLine.ItemCode) {

                            binding.total.text = currentInItem.Quantity.toString()
                        } else {
                            //   binding.total.setText("${currentDocLine.itemquantity.toString()}")
                        }
                    }


                } else {

                    binding.addQuantity.visibility = View.INVISIBLE
                }


                /*  for (currentItem in  Globals.cartListForOrderRequest) {
                      if (currentItem!!.ItemCode == AllitemsList.get(position).ItemCode) {
                          binding.addNewItem.visibility = View.INVISIBLE
                          binding.addQuantity.visibility = View.VISIBLE
                          binding.total.text = currentItem.itemquantity.toString()

                      } else {
                           binding.addNewItem.visibility = View.VISIBLE
                           binding.addQuantity.visibility = View.INVISIBLE
                      }
                  }*/
//            notifyDataSetChanged()
            } else {
                //  notifyDataSetChanged()
            }







            binding.ibCross.setOnClickListener {
                var pos = -1
                AppConstants.cartListForOrderRequest.forEachIndexed { index, documentLine ->

                    if (currentDocLine.ItemCode == documentLine.ItemCode) {
                        pos = index
                         Globals.showAlertDialog(
                            context,
                            "Remove Item",
                            "Are you sure you want to Remove this Item?",
                            "Remove",
                            "Cancel",
                            onDelete = {
                                Toast.makeText(
                                    context,
                                    "Removed Successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                                AppConstants.cartListForOrderRequest.removeAt(pos)


                                items.removeAt(pos)
                                AppConstants.saveCartListToPreferences(
                                    itemView.context,
                                    mutableListOf()
                                )

                                AppConstants.saveCartListToPreferences(
                                    itemView.context,
                                    AppConstants.cartListForOrderRequest
                                )
                                onItemRefreshCalculationClickListener?.let { click ->
                                    click(currentDocLine, pos)
                                }
                                notifyDataSetChanged()
                                binding.addQuantity.visibility = View.INVISIBLE

                                /*    var basePrice =
                                        stringToInt(currentDocLine.UnitPrice) * currentDocLine.itemquantity * stringToInt(
                                            currentDocLine.SalesQtyPerPackUnit
                                        )

                                    *//*  currentDocLine.UnitPrice.toInt() * currentDocLine.itemquantity.toInt() * currentDocLine.SalesQtyPerPackUnit.toInt()*//*


                                var dealerDiscount =
                                    stringToInt(Prefs.getString( Globals.DEALER_DISC)) * (basePrice / 100)

                                var specialDiscount =
                                    stringToInt(Prefs.getString( Globals.SPECIAL_DISC)) * (basePrice / 100)


                                var additonalDiscount =
                                    stringToInt(Prefs.getString( Globals.ADDITIONAL_DISC)) * (basePrice / 100)

                                binding.tvAdditionalDiscoiuntAmount.text = additonalDiscount.toString()
                                binding.tvSpecialDiscountTotal.text = specialDiscount.toString()
                                binding.tvDlrDiscAmount.text = dealerDiscount.toString()
                                binding.tvTotalBillingAmount.text = basePrice.toString()*/
                            },
                            onCancel = {


                            },
                            iconImg = R.drawable.ic_delete


                        )



                        /*Log.e(
                            TAG,
                            "onBindViewHolder: MINUS CART - ${AppConstants.cartListForOrderRequest.toString()}"
                        )
                        Log.e(
                            TAG,
                            "onBindViewHolder: MINUS CART SIZECROSS- ${AppConstants.cartListForOrderRequest.size}"
                        )*/
                        notifyDataSetChanged()



                        return@setOnClickListener
                    }
                }
            }



            binding.minus.setOnClickListener {
                var pos = -1
                AppConstants.cartListForOrderRequest.forEachIndexed { index, documentLine ->

                    if (currentDocLine.ItemCode == documentLine.ItemCode) {
                        pos = index
                        if (AppConstants.cartListForOrderRequest[pos]!!.Quantity > 1) {
                            AppConstants.cartListForOrderRequest[pos]!!.Quantity--
                            binding.total.text =
                                AppConstants.cartListForOrderRequest[pos]!!.Quantity.toString()

                            binding.addQuantity.visibility = View.VISIBLE

                            AppConstants.saveCartListToPreferences(
                                itemView.context,
                                mutableListOf()
                            )

                            AppConstants.saveCartListToPreferences(
                                itemView.context,
                                AppConstants.cartListForOrderRequest
                            )

                            notifyDataSetChanged()

                        } else {

                             Globals.showAlertDialog(
                                context,
                                "Remove Item",
                                "Are you sure you want to Remove this Item?",
                                "Remove",
                                "Cancel",
                                onDelete = {
                                    Toast.makeText(
                                        context,
                                        "Removed Successfully",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    AppConstants.cartListForOrderRequest.removeAt(pos)

                                    AppConstants.saveCartListToPreferences(
                                        itemView.context,
                                        mutableListOf()
                                    )

                                    AppConstants.saveCartListToPreferences(
                                        itemView.context,
                                        AppConstants.cartListForOrderRequest
                                    )


                                    binding.addQuantity.visibility = View.INVISIBLE
                                },
                                onCancel = {


                                },
                                iconImg = R.drawable.ic_delete


                            )

                           /* Log.e(
                                TAG,
                                "onBindViewHolder: MINUS CART - ${AppConstants.cartListForOrderRequest.toString()}"
                            )
                            Log.e(
                                TAG,
                                "onBindViewHolder: MINUS CART SIZE- ${AppConstants.cartListForOrderRequest.size}"
                            )*/
                            notifyDataSetChanged()

                        }
                        onItemRefreshCalculationClickListener?.let { click ->
                            click(currentDocLine, pos)
                        }


                        /*  var basePrice =
                              stringToInt(currentDocLine.UnitPrice) * currentDocLine.itemquantity * stringToInt(
                                  currentDocLine.SalesQtyPerPackUnit
                              )

                          *//*  currentDocLine.UnitPrice.toInt() * currentDocLine.itemquantity.toInt() * currentDocLine.SalesQtyPerPackUnit.toInt()*//*


                        var dealerDiscount =
                            stringToInt(Prefs.getString( Globals.DEALER_DISC)) * (basePrice / 100)

                        var specialDiscount =
                            stringToInt(Prefs.getString( Globals.SPECIAL_DISC)) * (basePrice / 100)


                        var additonalDiscount =
                            stringToInt(Prefs.getString( Globals.ADDITIONAL_DISC)) * (basePrice / 100)

                        binding.tvAdditionalDiscoiuntAmount.text = additonalDiscount.toString()
                        binding.tvSpecialDiscountTotal.text = specialDiscount.toString()
                        binding.tvDlrDiscAmount.text = dealerDiscount.toString()
                        binding.tvTotalBillingAmount.text = basePrice.toString()*/

                        return@setOnClickListener
                    }
                }


            }

            binding.plus.setOnClickListener {
                var pos = -1
                AppConstants.cartListForOrderRequest.forEachIndexed { index, documentLine ->
                    if (items.get(position).ItemCode == documentLine.ItemCode) {
                        pos = index
                        if (AppConstants.cartListForOrderRequest[pos]!!.Quantity < 20) {
                            AppConstants.cartListForOrderRequest[pos]!!.Quantity++
                            binding.total.text =
                                AppConstants.cartListForOrderRequest[pos]!!.Quantity.toString()

                            AppConstants.saveCartListToPreferences(
                                itemView.context,
                                mutableListOf()
                            )

                            AppConstants.saveCartListToPreferences(
                                itemView.context,
                                AppConstants.cartListForOrderRequest
                            )

                        }
                        notifyDataSetChanged()
                        onItemRefreshCalculationClickListener?.let { click ->
                            click(currentDocLine, pos)
                        }

                        return@setOnClickListener
                    }
                }

                /*        var basePrice =
                            stringToInt(currentDocLine.UnitPrice) * currentDocLine.itemquantity * stringToInt(
                                currentDocLine.SalesQtyPerPackUnit
                            )

                        *//*  currentDocLine.UnitPrice.toInt() * currentDocLine.itemquantity.toInt() * currentDocLine.SalesQtyPerPackUnit.toInt()*//*


                var dealerDiscount =
                    stringToInt(Prefs.getString( Globals.DEALER_DISC)) * (basePrice / 100)

                var specialDiscount =
                    stringToInt(Prefs.getString( Globals.SPECIAL_DISC)) * (basePrice / 100)


                var additonalDiscount =
                    stringToInt(Prefs.getString( Globals.ADDITIONAL_DISC)) * (basePrice / 100)

                binding.tvAdditionalDiscoiuntAmount.text = additonalDiscount.toString()
                binding.tvSpecialDiscountTotal.text = specialDiscount.toString()
                binding.tvDlrDiscAmount.text = dealerDiscount.toString()
                binding.tvTotalBillingAmount.text = basePrice.toString()*/


            }


        }
    }

    private fun setupLocalArrayList(
        currentItem: LocalDataForCart, context: Context

    ): Boolean {
        var pos = AppConstants.getCartListFromPreferences(context).any { item ->
            item.ItemCode.equals(currentItem.ItemCode, ignoreCase = true)

        }

        return AppConstants.getCartListFromPreferences(context).any { item ->
            //  posit=    Globals.getItemCodelPos( Globals.cartListQuotationOrder as ArrayList<DocumentLine>,currentItem.ItemCode)
            item.ItemCode.equals(currentItem.ItemCode, ignoreCase = true)


        }


    }


}
