package com.preetTractor.galaxyAndroid.ui.orderUi.adapter

import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.preetTractor.galaxyAndroid.R
import com.preetTractor.galaxyAndroid.apiHelper.AppConstants
import com.preetTractor.galaxyAndroid.databinding.RecyclerviewItemForItemSelectionBinding
import com.preetTractor.galaxyAndroid.helper.Globals
import com.preetTractor.galaxyAndroid.helper.Globals.setDynamicValueWithStringXml
import com.preetTractor.galaxyAndroid.helper.Globals.stringToInt
import com.preetTractor.galaxyAndroid.orderUi.model.local.LocalDataForCart
import com.preetTractor.galaxyAndroid.orderUi.model.response.ResponseItemListFromSubCategory
import com.pixplicity.easyprefs.library.Prefs

import java.util.*
import kotlin.Int
import kotlin.Unit
import kotlin.apply
import kotlin.let


class ItemListFromSchemeOrderRequestPagingAdapter(var discount: String, var textView: TextView) :
    RecyclerView.Adapter<ItemListFromSchemeOrderRequestPagingAdapter.ItemViewHolder>() {

    companion object {
       // private const val TAG = "ItemListFromSubCategory"
    }

    private val items = mutableListOf<ResponseItemListFromSubCategory.Data>()

    private var onItemClickListener: ((ResponseItemListFromSubCategory.Data, Int) -> Unit)? = null
    fun setOnItemClickListener(listener: (ResponseItemListFromSubCategory.Data, Int) -> Unit) {
        onItemClickListener = listener
    }


    private var onItemCallClickListener: ((ResponseItemListFromSubCategory.Data) -> Unit)? = null
    fun setOnItemCallClickListener(listener: (ResponseItemListFromSubCategory.Data) -> Unit) {
        onItemCallClickListener = listener
    }

    fun clearAllData() {
        items.removeAll(items)
        notifyDataSetChanged()
    }

    fun setItems(newItems: List<ResponseItemListFromSubCategory.Data>) {
        items.addAll(newItems)
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            RecyclerviewItemForItemSelectionBinding.inflate(
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

    var unitPriceLocal = ""

    inner class ItemViewHolder(var binding: RecyclerviewItemForItemSelectionBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(currentDocLine: ResponseItemListFromSubCategory.Data, context: Context) {

            // Bind item properties to the UI elements
            binding.apply {
                linearOffer.visibility = View.VISIBLE
                tvUnitPriceCut.visibility = View.VISIBLE

                tvUnitPriceCut.text = currentDocLine.UnitPrice
                tvUnitPriceCut.paintFlags = tvUnitPriceCut.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                tvOffer.text = "${discount} % OFF"


                unitPriceLocal = currentDocLine.DiscountAmount
                /* //todo for making calculationSimple
                 currentDocLine.UnitPrice = currentDocLine.DiscountAmount*/


                binding.tvItemName.text = currentDocLine.ItemName
                binding.tvRupeeText.text = context.setDynamicValueWithStringXml(
                    R.string.amount_with_rupee_symbol,
                     Globals.numberToK(unitPriceLocal)!!
                )
                binding.tvItemCode.text = context.setDynamicValueWithStringXml(
                    R.string.item_code_dynamic,
                    currentDocLine.ItemCode
                )

                binding.tvSpQ.text = context.setDynamicValueWithStringXml(
                    R.string.spq_dynamic,
                    currentDocLine.SalesQtyPerPackUnit
                )


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


                    //  binding.addNewItem.visibility = View.INVISIBLE
                    //  binding.addQuantity.visibility = View.VISIBLE

                    for (currentInItem in AppConstants.cartListForOrderRequest) {
                        if (currentInItem.ItemCode == currentDocLine.ItemCode) {

                            binding.total.setText("${currentInItem.Quantity.toString()}")

                            /*binding.tvUValue.setText(
                                "${
                                    ( Globals.stringToInt(
                                        currentInItem.SalesQtyPerPackUnit
                                    ) *  Globals.stringToInt(
                                        currentInItem.Quantity.toString()
                                    ))
                                } U"
                            )*/
                        } else {
                            //   binding.total.setText("${currentDocLine.itemquantity.toString()}")
                        }
                    }


                } else {
                    binding.tvUValue.setText("0 U")
                    binding.total.setText("0")
                    //  binding.addNewItem.visibility = View.VISIBLE
                    //  binding.addQuantity.visibility = View.INVISIBLE
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
            }

            /* else{
                 binding.tvUValue.setText("0 U")
                // binding.addNewItem.visibility = View.VISIBLE
                // binding.addQuantity.visibility = View.INVISIBLE
             }*/







            binding.add.setOnClickListener(View.OnClickListener {


                /*var localDataForCart = LocalDataForCart()
                localDataForCart.ItemCode = currentDocLine.ItemCode
                localDataForCart.ItemDescription = currentDocLine.ItemName
                localDataForCart.UnitPrice = unitPriceLocal
                localDataForCart.PriceType = currentDocLine.PriceType
                localDataForCart.SalesQtyPerPackUnit = currentDocLine.SalesQtyPerPackUnit
                localDataForCart.id = currentDocLine.id
                localDataForCart.apply {
                    U_UTL_DD = Prefs.getString( Globals.DEALER_DISC)
                    Currency = "INR"
                    TaxCode = "IGST18"
                    UnitPriceown = currentDocLine.UnitPrice//todo need to check
                    U_UTL_TD = Prefs.getString( Globals.DISCOUNT_PERCENT)
                    DiscountPercent = discount
                    ProjectCode = ""
                    FreeText = ""
                    UomNo = currentDocLine.UnitPrice
                    UoMCode = "Manual"
                    Image = currentDocLine.Image
                    UnitWeight = currentDocLine.UnitPrice
                    U_UTL_SD = Prefs.getString( Globals.SPECIAL_DISC)
                }



                localDataForCart.Quantity = 1*/

                val localDataForCart = LocalDataForCart(
                    id = currentDocLine.id,
                    OrderID = "",
//                    SalesQtyPerPackUnit = currentDocLine.SalesQtyPerPackUnit,
                    ItemCode = currentDocLine.ItemCode,
                    ItemDescription = currentDocLine.Description,
                    UnitPrice = stringToInt(currentDocLine.UnitPrice),
//                    PriceType = currentDocLine.PriceType,
                    U_UTL_SD = Prefs.getString( Globals.SPECIAL_DISC),
                    Currency = "INR",
                    TaxCode = "IGST18",
                    UnitPriceown = stringToInt(currentDocLine.UnitPrice),
                    U_UTL_TD = Prefs.getString( Globals.DISCOUNT_PERCENT),
                    DiscountPercent = stringToInt( currentDocLine.Discount),
                    ProjectCode = "",
                    FreeText ="",
                    UomNo = stringToInt(currentDocLine.UnitPrice),
                    UoMCode = "Manual",
                    Image = "",
//                    UnitWeight = currentDocLine.UnitWeight,
                    U_UTL_DD = Prefs.getString( Globals.DEALER_DISC),
                    Quantity = 1,
                    TaxRate = 0.0
                )



                AppConstants.cartListForOrderRequest.add(
                    localDataForCart
                )


                AppConstants.saveCartListToPreferences(
                    itemView.context,
                    mutableListOf()
                )

                AppConstants.saveCartListToPreferences(
                    itemView.context,
                    AppConstants.cartListForOrderRequest
                )
                binding.total.text = localDataForCart.Quantity.toString()
                binding.tvUValue.setText(
                    "${
                        ( Globals.stringToInt(
                            currentDocLine.SalesQtyPerPackUnit
                        ) *  Globals.stringToInt(
                            localDataForCart.Quantity.toString()
                        ))
                    } U"
                )

                binding.addQuantity.visibility = View.VISIBLE
                binding.addNewItem.visibility = View.INVISIBLE


               /* Log.e(
                    TAG,
                    "onBindViewHolder: ADDING CART SIZE- ${AppConstants.cartListForOrderRequest.size}"
                )*/
                textView.setText(AppConstants.cartListForOrderRequest.size.toString())
                notifyDataSetChanged()

            })




            binding.minus.setOnClickListener {
                var pos = -1
                AppConstants.cartListForOrderRequest.forEachIndexed { index, documentLine ->

                    if (currentDocLine.ItemCode == documentLine.ItemCode) {
                        pos = index
                        if (AppConstants.cartListForOrderRequest[pos]!!.Quantity > 1) {
                            AppConstants.cartListForOrderRequest[pos]!!.Quantity--
                            binding.total.text =
                                AppConstants.cartListForOrderRequest[pos]!!.Quantity.toString()
                            binding.addNewItem.visibility = View.INVISIBLE
                            binding.addQuantity.visibility = View.VISIBLE

                            /*        binding.tvUValue.text =
                                        "${AppConstants.cartListForOrderRequest[pos]!!.Quantity.toString()} U"*/

                           /* binding.tvUValue.setText(
                                "${
                                    ( Globals.stringToInt(
                                        AppConstants.cartListForOrderRequest[pos]!!.SalesQtyPerPackUnit
                                    ) *  Globals.stringToInt(
                                        AppConstants.cartListForOrderRequest[pos]!!.Quantity.toString()
                                    ))
                                } U"
                            )*/


                            AppConstants.saveCartListToPreferences(
                                itemView.context,
                                mutableListOf()
                            )

                            AppConstants.saveCartListToPreferences(
                                itemView.context,
                                AppConstants.cartListForOrderRequest
                            )

                            notifyDataSetChanged()
                            return@forEachIndexed

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
                                    textView.setText(AppConstants.cartListForOrderRequest.size.toString())

                                    /*   binding.addNewItem.visibility = View.VISIBLE
                                       binding.addQuantity.visibility = View.INVISIBLE*/


                                    binding.tvUValue.text = "0 U"
                                    binding.total.text = "0"
                                    notifyDataSetChanged()
                                },
                                onCancel = {


                                },
                                iconImg = R.drawable.ic_delete


                            )

                         /*   Log.e(
                                TAG,
                                "onBindViewHolder: MINUS CART - ${AppConstants.cartListForOrderRequest.toString()}"
                            )
                            Log.e(
                                TAG,
                                "onBindViewHolder: MINUS CART SIZE- ${AppConstants.cartListForOrderRequest.size}"
                            )*/
                            notifyDataSetChanged()
                            return@forEachIndexed

                        }


                    }
                }


                /*   for (currentItem in AppConstants.cartListForOrderRequest){
                       if (AllitemsList.get(holder.absoluteAdapterPosition).ItemCode==currentItem.ItemCode){
                           pos=currentItem.
                           return@setOnClickListener
                       }
                   }*/


            }

            binding.plus.setOnClickListener {
                var pos = -1
                if (AppConstants.cartListForOrderRequest.isEmpty()) {
                   /* var localDataForCart = LocalDataForCart()
                    localDataForCart.ItemCode = currentDocLine.ItemCode
                    localDataForCart.ItemDescription = currentDocLine.ItemName
                    localDataForCart.UnitPrice = currentDocLine.DiscountAmount
                    localDataForCart.PriceType = currentDocLine.PriceType
                    localDataForCart.SalesQtyPerPackUnit = currentDocLine.SalesQtyPerPackUnit
                    localDataForCart.id = currentDocLine.id
                    localDataForCart.apply {
                        U_UTL_DD = Prefs.getString( Globals.DEALER_DISC)
                        Currency = "INR"
                        TaxCode = "IGST18"
                        UnitPriceown = currentDocLine.UnitPrice//todo need to check
                        U_UTL_TD = Prefs.getString( Globals.DISCOUNT_PERCENT)
                        DiscountPercent = discount
                        ProjectCode = ""
                        FreeText = ""
                        UomNo = currentDocLine.UnitPrice
                        UoMCode = "Manual"
                        Image = currentDocLine.Image
                        UnitWeight = currentDocLine.UnitPrice
                        U_UTL_SD = Prefs.getString( Globals.SPECIAL_DISC)
                    }

                    localDataForCart.Quantity = 1*/

                    val localDataForCart = LocalDataForCart(
                        id = currentDocLine.id,
                        OrderID = "",
//                        SalesQtyPerPackUnit = currentDocLine.SalesQtyPerPackUnit,
                        ItemCode = currentDocLine.ItemCode,
                        ItemDescription = currentDocLine.Description,
                        UnitPrice = stringToInt(currentDocLine.UnitPrice),
//                        PriceType = currentDocLine.PriceType,
                        U_UTL_SD = Prefs.getString( Globals.SPECIAL_DISC),
                        Currency = "INR",
                        TaxCode = "IGST18",
                        UnitPriceown = stringToInt(currentDocLine.UnitPrice),
                        U_UTL_TD = Prefs.getString( Globals.DISCOUNT_PERCENT),
                        DiscountPercent = stringToInt( currentDocLine.Discount),
                        ProjectCode = "",
                        FreeText ="",
                        UomNo = stringToInt(currentDocLine.UnitPrice),
                        UoMCode = "Manual",
                        Image = "",
//                        UnitWeight = currentDocLine.UnitWeight,
                        U_UTL_DD = Prefs.getString( Globals.DEALER_DISC),
                        Quantity = 1,
                        TaxRate = 0.0
                    )



                    AppConstants.cartListForOrderRequest.add(
                        localDataForCart
                    )

                    AppConstants.saveCartListToPreferences(
                        itemView.context,
                        mutableListOf()
                    )
                    AppConstants.saveCartListToPreferences(
                        itemView.context,
                        AppConstants.cartListForOrderRequest
                    )

                    binding.total.text = localDataForCart.Quantity.toString()


                    /*binding.tvUValue.text = "${
                        (stringToInt(localDataForCart.Quantity.toString()) *  Globals.stringToInt(
                            localDataForCart.SalesQtyPerPackUnit
                        ))
                    } U"*/

                    /* binding.addQuantity.visibility = View.VISIBLE
                     binding.addNewItem.visibility = View.INVISIBLE*/


                   /* Log.e(
                        TAG,
                        "onBindViewHolder: ADDING CART SIZE- ${AppConstants.cartListForOrderRequest.size}"
                    )*/
                    textView.setText(AppConstants.cartListForOrderRequest.size.toString())
                    notifyDataSetChanged()
                    return@setOnClickListener
                } else {
                    /* AppConstants.cartListForOrderRequest.forEachIndexed { index, documentLine ->
                         if (items.get(position).ItemCode == documentLine.ItemCode) {
                             pos = index
                             if (AppConstants.cartListForOrderRequest[pos]!!.Quantity < 20) {
                                 AppConstants.cartListForOrderRequest[pos]!!.Quantity++
                                 binding.total.text =
                                     AppConstants.cartListForOrderRequest[pos]!!.Quantity.toString()

                                 binding.tvUValue.text =
                                     "${
                                         (stringToInt(AppConstants.cartListForOrderRequest[pos]!!.Quantity.toString()) *  Globals.stringToInt(
                                             AppConstants.cartListForOrderRequest[pos]!!.SalesQtyPerPackUnit
                                         ))
                                     } U"

                                 AppConstants.saveCartListToPreferences(
                                     itemView.context,
                                     mutableListOf()
                                 )
                                 AppConstants.saveCartListToPreferences(
                                     itemView.context,
                                     AppConstants.cartListForOrderRequest
                                 )

                             }
                             textView.setText(AppConstants.cartListForOrderRequest.size.toString())
                             return@setOnClickListener
                         } else {
                             var localDataForCart = LocalDataForCart()
                             localDataForCart.ItemCode = items.get(position).ItemCode
                             localDataForCart.ItemDescription = items.get(position).ItemName
                             localDataForCart.UnitPrice = items.get(position).UnitPrice
                             localDataForCart.PriceType = items.get(position).PriceType
                             localDataForCart.SalesQtyPerPackUnit =
                                 items.get(position).SalesQtyPerPackUnit
                             localDataForCart.id = items.get(position).id
                             localDataForCart.apply {
                                 U_UTL_DD = Prefs.getString( Globals.DEALER_DISC)
                                 Currency = "INR"
                                 TaxCode = "IGST18"
                                 UnitPriceown = items.get(position).UnitPrice//todo need to check
                                 U_UTL_TD = Prefs.getString( Globals.DISCOUNT_PERCENT)
                                 DiscountPercent = items.get(position).Discount
                                 ProjectCode = ""
                                 FreeText = ""
                                 UomNo = items.get(position).UnitPrice
                                 UoMCode = "Manual"
                                 Image = items.get(position).Image
                                 UnitWeight = items.get(position).UnitPrice
                                 U_UTL_SD = Prefs.getString( Globals.SPECIAL_DISC)
                             }



                             localDataForCart.Quantity = 1



                             AppConstants.cartListForOrderRequest.add(
                                 localDataForCart
                             )

                             AppConstants.saveCartListToPreferences(
                                 itemView.context,
                                 mutableListOf()
                             )
                             AppConstants.saveCartListToPreferences(
                                 itemView.context,
                                 AppConstants.cartListForOrderRequest
                             )

                             binding.total.text = localDataForCart.Quantity.toString()
                             binding.tvUValue.text = "${
                                 (stringToInt(localDataForCart.Quantity.toString()) *  Globals.stringToInt(
                                     localDataForCart.SalesQtyPerPackUnit
                                 ))
                             } U"

                             *//* binding.addQuantity.visibility = View.VISIBLE
                             binding.addNewItem.visibility = View.INVISIBLE*//*


                            Log.e(
                                TAG,
                                "onBindViewHolder: ADDING CART SIZE- ${AppConstants.cartListForOrderRequest.size}"
                            )
                            textView.setText(AppConstants.cartListForOrderRequest.size.toString())
                            notifyDataSetChanged()
                            return@setOnClickListener

                        }
                    }*/


                    var itemFound = false

                    AppConstants.cartListForOrderRequest.forEachIndexed { index, documentLine ->
                        if (items[position].ItemCode == documentLine.ItemCode) {
                            itemFound = true
                            pos = index
                            if (AppConstants.cartListForOrderRequest[pos].Quantity < 20) {
                                AppConstants.cartListForOrderRequest[pos].Quantity++
                                binding.total.text =
                                    AppConstants.cartListForOrderRequest[pos].Quantity.toString()

                              /*  binding.tvUValue.text = "${
                                    (stringToInt(AppConstants.cartListForOrderRequest[pos].Quantity.toString()) *  Globals.stringToInt(
                                        AppConstants.cartListForOrderRequest[pos].SalesQtyPerPackUnit
                                    ))
                                } U"*/

                                AppConstants.saveCartListToPreferences(
                                    itemView.context,
                                    mutableListOf()
                                )
                                AppConstants.saveCartListToPreferences(
                                    itemView.context,
                                    AppConstants.cartListForOrderRequest
                                )
                            }
                            textView.setText(AppConstants.cartListForOrderRequest.size.toString())
                            return@forEachIndexed // use return@forEachIndexed instead of return@setOnClickListener
                        }
                    }

// If we reach this point, the item was not found in the cart list, so add it
                    if (!itemFound) {
                        /*var localDataForCart = LocalDataForCart()
                        localDataForCart.ItemCode = items[position].ItemCode
                        localDataForCart.ItemDescription = items[position].ItemName
                        localDataForCart.UnitPrice = items[position].DiscountAmount
                        localDataForCart.PriceType = items[position].PriceType
                        localDataForCart.SalesQtyPerPackUnit = items[position].SalesQtyPerPackUnit
                        localDataForCart.id = items[position].id
                        localDataForCart.apply {
                            U_UTL_DD = Prefs.getString( Globals.DEALER_DISC)
                            Currency = "INR"
                            TaxCode = "IGST18"
                            UnitPriceown = items[position].UnitPrice // todo need to check
                            U_UTL_TD = Prefs.getString( Globals.DISCOUNT_PERCENT)
                            DiscountPercent = discount
                            ProjectCode = ""
                            FreeText = ""
                            UomNo = items[position].UnitPrice
                            UoMCode = "Manual"
                            Image = items[position].Image
                            UnitWeight = items[position].UnitPrice
                            U_UTL_SD = Prefs.getString( Globals.SPECIAL_DISC)
                        }

                        localDataForCart.Quantity = 1*/

                        val localDataForCart = LocalDataForCart(
                            id = currentDocLine.id,
                            OrderID = "",
//                            SalesQtyPerPackUnit = currentDocLine.SalesQtyPerPackUnit,
                            ItemCode = currentDocLine.ItemCode,
                            ItemDescription = currentDocLine.Description,
                            UnitPrice = stringToInt(currentDocLine.UnitPrice),
//                            PriceType = currentDocLine.PriceType,
                            U_UTL_SD = Prefs.getString( Globals.SPECIAL_DISC),
                            Currency = "INR",
                            TaxCode = "IGST18",
                            UnitPriceown = stringToInt(currentDocLine.UnitPrice),
                            U_UTL_TD = Prefs.getString( Globals.DISCOUNT_PERCENT),
                            DiscountPercent = stringToInt( currentDocLine.Discount),
                            ProjectCode = "",
                            FreeText ="",
                            UomNo = stringToInt(currentDocLine.UnitPrice),
                            UoMCode = "Manual",
                            Image = "",
//                            UnitWeight = currentDocLine.UnitWeight,
                            U_UTL_DD = Prefs.getString( Globals.DEALER_DISC),
                            Quantity = 1,
                            TaxRate = 0.0
                        )

                        AppConstants.cartListForOrderRequest.add(localDataForCart)

                        AppConstants.saveCartListToPreferences(
                            itemView.context,
                            mutableListOf()
                        )
                        AppConstants.saveCartListToPreferences(
                            itemView.context,
                            AppConstants.cartListForOrderRequest
                        )

                        binding.total.text = localDataForCart.Quantity.toString()

                      /*  binding.tvUValue.text = "${
                            (stringToInt(localDataForCart.Quantity.toString()) *  Globals.stringToInt(
                                localDataForCart.SalesQtyPerPackUnit
                            ))
                        } U"*/

                        /*Log.e(
                            TAG,
                            "onBindViewHolder: ADDING CART SIZE- ${AppConstants.cartListForOrderRequest.size}"
                        )*/
                        textView.setText(AppConstants.cartListForOrderRequest.size.toString())
                        notifyDataSetChanged()
                    }


                }


            }


        }
    }

    private fun setupLocalArrayList(
        currentItem: ResponseItemListFromSubCategory.Data,
        context: Context

    ): Boolean {
        var pos = AppConstants.cartListForOrderRequest.any { item ->
            item.ItemCode.equals(currentItem.ItemCode, ignoreCase = true)

        }

        return AppConstants.getCartListFromPreferences(context).any { item ->
            //  posit=    Globals.getItemCodelPos( Globals.cartListQuotationOrder as ArrayList<DocumentLine>,currentItem.ItemCode)
            item.ItemCode.equals(currentItem.ItemCode, ignoreCase = true)


        }


    }
}
