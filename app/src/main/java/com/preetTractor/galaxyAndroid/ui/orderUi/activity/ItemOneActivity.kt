package com.preetTractor.galaxyAndroid.ui.orderUi.activity

import Event
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.gson.JsonObject
import com.preetTractor.galaxyAndroid.R
import com.preetTractor.galaxyAndroid.apiHelper.ApiClient
import com.preetTractor.galaxyAndroid.apiHelper.ApisInterface
import com.preetTractor.galaxyAndroid.apiHelper.AppConstants
import com.preetTractor.galaxyAndroid.databinding.ActivityItemOneBinding
import com.preetTractor.galaxyAndroid.helper.APiPayloadKeys
import com.preetTractor.galaxyAndroid.helper.Globals
import com.preetTractor.galaxyAndroid.helper.Globals.setDynamicValueWithStringXml
import com.preetTractor.galaxyAndroid.helper.Globals.stringToInt
import com.preetTractor.galaxyAndroid.mvvmSetUp.DefaultMainRepositories
import com.preetTractor.galaxyAndroid.mvvmSetUp.MainRepos
import com.preetTractor.galaxyAndroid.mvvmSetUp.MainViewModel
import com.preetTractor.galaxyAndroid.mvvmSetUp.MainViewModelProvider
import com.preetTractor.galaxyAndroid.orderUi.model.local.LocalDataForCart
import com.preetTractor.galaxyAndroid.searchUi.model.DataItemOne
import com.pixplicity.easyprefs.library.Prefs
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class ItemOneActivity : AppCompatActivity() {
      lateinit var binding: ActivityItemOneBinding
      var item = ""
      //lateinit var sessionManagement: SessionManagement
      lateinit var viewModel: MainViewModel

      private fun setUpViewModel() {
            val dispatchers: CoroutineDispatcher = Dispatchers.Main
            val mainRepos = DefaultMainRepositories() as MainRepos
            val fanxApi: ApisInterface = ApiClient().service(this)
            val viewModelProviderfactory =
                  MainViewModelProvider(application, mainRepos, dispatchers, fanxApi)
            viewModel = ViewModelProvider(this, viewModelProviderfactory)[MainViewModel::class.java]

      }

      companion object {
            //  private const val TAG = "ItemOneActivity"
      }

      override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            binding = ActivityItemOneBinding.inflate(layoutInflater)
            setContentView(binding.root)
            setUpViewModel()
            //supportActionBar?.hide()!!
            item = intent.getStringExtra("id").toString()
            binding.apply {
                  ibBack.setOnClickListener {
                        finish()
                  }
                  viewModel.getItemOne(JsonObject().apply {
//                        addProperty(APiPayloadKeys.PriceListId, "")
                        addProperty(APiPayloadKeys.id, item)
                  }, this@ItemOneActivity)

                  subscribeToItemOneObserver()

                  btnProceedToBuy.setOnClickListener {
                        if (AppConstants.cartListForOrderRequest.isNotEmpty()) {
                              val intent = Intent(this@ItemOneActivity, CartActivity::class.java).also {
                                    startActivity(it)
                              }
                        } else {
                               Globals.warningMessage(this@ItemOneActivity, "No Item Found")
                        }

                  }


            }

      }

      private fun subscribeToItemOneObserver() {
            viewModel.itemOne.observe(this, Event.EventObserver(onError = {
                   Globals.warningMessage(this, it)
            }, onLoading = {

            }, { response ->
                  if (response.status.equals("200")) {
                        if (response.data.isNotEmpty()) {
                              setupUidata(response.data[0])
                        }

                  } else if (response.status.equals("201")) {
                         Globals.warningMessage(this, response.message)
                  }

            }))
      }

      private fun setupUidata(currentDocLine: DataItemOne) {
            binding.apply {
                  tvTitle.text = currentDocLine.ItemName
                  tvItemName.text = currentDocLine.ItemName
                  binding.tvRupeeText.text = this@ItemOneActivity.setDynamicValueWithStringXml(
                        R.string.amount_with_rupee_symbol,
                         Globals.numberToK(currentDocLine.UnitPrice)!!
                  )
                  binding.tvItemCode.text = this@ItemOneActivity.setDynamicValueWithStringXml(
                        R.string.item_code_dynamic,
                        currentDocLine.ItemCode
                  )

                  binding.tvSpQ.text = this@ItemOneActivity.setDynamicValueWithStringXml(
                        R.string.spq_dynamic,
                        currentDocLine.SalesQtyPerPackUnit
                  )

                  if (AppConstants.cartListForOrderRequest.isNotEmpty()) {


                        var globalpos = -1

                        /*for (current in AppConstants.cartListForOrderRequest) {
                            var pos = 0
                            if (current.ItemCode == currentDocLine.ItemCode) {
                               // binding.addQuantity.visibility = View.VISIBLE
                             //   binding.addNewItem.visibility = View.INVISIBLE
                                globalpos = pos
                            } else {
                                pos++
                            }
                        }*/

                        AppConstants.cartListForOrderRequest.forEachIndexed { index, current ->
                              if (current.ItemCode == currentDocLine.ItemCode) {
                                    globalpos = index
                                    // Uncomment and use these lines if you need to update UI visibility
                                    // binding.addQuantity.visibility = View.VISIBLE
                                    // binding.addNewItem.visibility = View.INVISIBLE
                                    return@forEachIndexed
                              }
                        }

                        if (globalpos!=-1){
                              if (AppConstants.cartListForOrderRequest[globalpos].ItemCode == currentDocLine.ItemCode) {

                                    binding.total.setText("${AppConstants.cartListForOrderRequest[globalpos].Quantity.toString()}")


                                    binding.tvUValue.setText("${AppConstants.cartListForOrderRequest[globalpos].Quantity.toString()} U")
                                    /*binding.tvUValue.text = "${
                                          ( Globals.stringToInt(AppConstants.cartListForOrderRequest[globalpos].Quantity.toString()) *  Globals.stringToInt(
                                                AppConstants.cartListForOrderRequest[globalpos].SalesQtyPerPackUnit.toString()
                                          ))
                                    } U"*/

                              } else {
                                    //   binding.total.setText("${currentDocLine.itemquantity.toString()}")
                              }
                        }else{
                              binding.tvUValue.setText("0 U")
                        }



                  } else {
                        binding.tvUValue.setText("0 U")
                        // binding.addNewItem.visibility = View.VISIBLE
                        //  binding.addQuantity.visibility = View.INVISIBLE
                  }



                  binding.add.setOnClickListener(View.OnClickListener {


                        /*val localDataForCart = LocalDataForCart()
                        localDataForCart.ItemCode = currentDocLine.ItemCode
                        localDataForCart.ItemDescription = currentDocLine.ItemName
                        localDataForCart.UnitPrice = currentDocLine.UnitPrice
                        localDataForCart.PriceType = currentDocLine.PriceType//currentDocLine.PriceType
                        localDataForCart.SalesQtyPerPackUnit =
                              currentDocLine.SalesQtyPerPackUnit//currentDocLine.SalesQtyPerPackUnit
                        localDataForCart.id = currentDocLine.id
                        localDataForCart.apply {
                              U_UTL_DD = Prefs.getString( Globals.DEALER_DISC)
                              Currency = "INR"
                              TaxCode = "IGST18"
                              UnitPriceown = currentDocLine.UnitPrice//todo need to check
                              U_UTL_TD = Prefs.getString( Globals.DISCOUNT_PERCENT)
                              DiscountPercent = currentDocLine.Discount
                              ProjectCode = ""
                              FreeText = ""
                              UomNo = currentDocLine.UnitPrice
                              UoMCode = "Manual"
                              Image = ""//currentDocLine.Image
                              UnitWeight = currentDocLine.UnitPrice
                              U_UTL_SD = Prefs.getString( Globals.SPECIAL_DISC)
                        }*/
                        val localDataForCart = LocalDataForCart(
                              id = currentDocLine.id,
                              OrderID = "",
//                              SalesQtyPerPackUnit = currentDocLine.SalesQtyPerPackUnit,
                              ItemCode = currentDocLine.ItemCode,
                              ItemDescription = currentDocLine.Description,
                              UnitPrice = stringToInt(currentDocLine.UnitPrice),
//                              PriceType = currentDocLine.PriceType,
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
//                              UnitWeight = currentDocLine.UnitWeight,
                              U_UTL_DD = Prefs.getString( Globals.DEALER_DISC),
                              Quantity = 1,
                              TaxRate = currentDocLine.Tax
                        )

                        AppConstants.cartListForOrderRequest.add(
                              localDataForCart
                        )

                        AppConstants.saveCartListToPreferences(
                              this@ItemOneActivity,
                              mutableListOf()
                        )
                        AppConstants.saveCartListToPreferences(
                              this@ItemOneActivity,
                              AppConstants.cartListForOrderRequest
                        )

                        binding.total.text = localDataForCart.Quantity.toString()
                       /* binding.tvUValue.text = "${
                              ( Globals.stringToInt(localDataForCart.Quantity.toString()) *  Globals.stringToInt(
                                    localDataForCart.SalesQtyPerPackUnit
                              ))
                        } U"*/

                        // binding.addQuantity.visibility = View.VISIBLE
                        //  binding.addNewItem.visibility = View.INVISIBLE


                        /* Log.e(
                             TAG,
                             "onBindViewHolder: ADDING CART SIZE- ${AppConstants.cartListForOrderRequest.size}"
                         )*/


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
                                          //  binding.addNewItem.visibility = View.INVISIBLE
                                          //  binding.addQuantity.visibility = View.VISIBLE

                                          /*binding.tvUValue.text =
                                                "${
                                                      ( Globals.stringToInt(AppConstants.cartListForOrderRequest[pos]!!.Quantity.toString()) *  Globals.stringToInt(
                                                            AppConstants.cartListForOrderRequest[pos]!!.SalesQtyPerPackUnit
                                                      ))
                                                } U"*/
                                          AppConstants.saveCartListToPreferences(
                                                this@ItemOneActivity,
                                                mutableListOf()
                                          )

                                          AppConstants.saveCartListToPreferences(
                                                this@ItemOneActivity,
                                                AppConstants.cartListForOrderRequest
                                          )


                                    } else {

                                           Globals.showAlertDialog(
                                                this@ItemOneActivity,
                                                "Remove Item",
                                                "Are you sure you want to Remove this Item?",
                                                "Remove",
                                                "Cancel",
                                                onDelete = {
                                                      Toast.makeText(
                                                            this@ItemOneActivity,
                                                            "Removed Successfully",
                                                            Toast.LENGTH_SHORT
                                                      ).show()
                                                      AppConstants.cartListForOrderRequest.removeAt(pos)
                                                      // textView.setText(AppConstants.cartListForOrderRequest.size.toString())
                                                      AppConstants.saveCartListToPreferences(
                                                            this@ItemOneActivity,
                                                            mutableListOf()
                                                      )

                                                      AppConstants.saveCartListToPreferences(
                                                            this@ItemOneActivity,
                                                            AppConstants.cartListForOrderRequest
                                                      )
                                                      //  binding.addNewItem.visibility = View.VISIBLE
                                                      //   binding.addQuantity.visibility = View.INVISIBLE
                                                      binding.tvUValue.text = "0 U"
                                                      binding.total.text = "0 U"
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


                                    }

                                    return@setOnClickListener
                              }
                        }


                  }

                  /*binding.plus.setOnClickListener {
                      var pos = -1
                      AppConstants.cartListForOrderRequest.forEachIndexed { index, documentLine ->
                          if (currentDocLine.ItemCode == documentLine.ItemCode) {
                              pos = index
                              if (AppConstants.cartListForOrderRequest[pos]!!.Quantity < 20) {
                                  AppConstants.cartListForOrderRequest[pos]!!.Quantity++
                                  binding.total.text =
                                      AppConstants.cartListForOrderRequest[pos]!!.Quantity.toString()
      
                                  binding.tvUValue.text =
                                      "${
                                          ( Globals.stringToInt(AppConstants.cartListForOrderRequest[pos]!!.Quantity.toString()) *  Globals.stringToInt(
                                              AppConstants.cartListForOrderRequest[pos]!!.SalesQtyPerPackUnit
                                          ))
                                      } U"
      
                                  AppConstants.saveCartListToPreferences(
                                     this@ItemOneActivity ,
                                      mutableListOf()
                                  )
                                  AppConstants.saveCartListToPreferences(
                                      this@ItemOneActivity,
                                      AppConstants.cartListForOrderRequest
                                  )
                              }
                              return@setOnClickListener
                          }
                      }
      
      
                  }*/





                  binding.plus.setOnClickListener {
                        var pos = -1
                        if (AppConstants.cartListForOrderRequest.isEmpty()) {
                              /*var localDataForCart = LocalDataForCart()
                              localDataForCart.ItemCode = currentDocLine.ItemCode
                              localDataForCart.ItemDescription = currentDocLine.ItemName
                              localDataForCart.UnitPrice = currentDocLine.UnitPrice
                              localDataForCart.PriceType = currentDocLine.PriceType
                              localDataForCart.SalesQtyPerPackUnit = currentDocLine.SalesQtyPerPackUnit
                              localDataForCart.id = currentDocLine.id
                              localDataForCart.apply {
                                    U_UTL_DD = Prefs.getString( Globals.DEALER_DISC)
                                    Currency = "INR"
                                    TaxCode = "IGST18"
                                    UnitPriceown = currentDocLine.UnitPrice//todo need to check
                                    U_UTL_TD = Prefs.getString( Globals.DISCOUNT_PERCENT)
                                    DiscountPercent = currentDocLine.Discount
                                    ProjectCode = ""
                                    FreeText = ""
                                    UomNo = currentDocLine.UnitPrice
                                    UoMCode = "Manual"
                                    Image = ""
                                    UnitWeight = currentDocLine.UnitPrice
                                    U_UTL_SD = Prefs.getString( Globals.SPECIAL_DISC)
                              }



                              localDataForCart.Quantity = 1*/

                              val localDataForCart = LocalDataForCart(
                                    id = currentDocLine.id,
                                    OrderID = "",
//                                    SalesQtyPerPackUnit = currentDocLine.SalesQtyPerPackUnit,
                                    ItemCode = currentDocLine.ItemCode,
                                    ItemDescription = currentDocLine.Description,
                                    UnitPrice = stringToInt(currentDocLine.UnitPrice),
//                                    PriceType = currentDocLine.PriceType,
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
//                                    UnitWeight = currentDocLine.UnitWeight,
                                    U_UTL_DD = Prefs.getString( Globals.DEALER_DISC),
                                    Quantity = 1,
                                    TaxRate = currentDocLine.Tax
                              )

                              AppConstants.cartListForOrderRequest.add(
                                    localDataForCart
                              )

                              AppConstants.saveCartListToPreferences(
                                    this@ItemOneActivity,
                                    mutableListOf()
                              )
                              AppConstants.saveCartListToPreferences(
                                    this@ItemOneActivity,
                                    AppConstants.cartListForOrderRequest
                              )

                              binding.total.text = localDataForCart.Quantity.toString()
                              /*binding.tvUValue.text = "${
                                    ( Globals.stringToInt(localDataForCart.Quantity.toString()) *  Globals.stringToInt(
                                          localDataForCart.SalesQtyPerPackUnit
                                    ))
                              } U"*/

                              /* binding.addQuantity.visibility = View.VISIBLE
                               binding.addNewItem.visibility = View.INVISIBLE*/


                              /* Log.e(
                                   TAG,
                                   "onBindViewHolder: ADDING CART SIZE- ${AppConstants.cartListForOrderRequest.size}"
                               )*/
                              //  textView.setText(AppConstants.cartListForOrderRequest.size.toString())
                              //  notifyDataSetChanged()
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
                                    if (currentDocLine.ItemCode == documentLine.ItemCode) {
                                          itemFound = true
                                          pos = index
                                          if (AppConstants.cartListForOrderRequest[pos].Quantity < 20) {
                                                AppConstants.cartListForOrderRequest[pos].Quantity++
                                                binding.total.text =
                                                      AppConstants.cartListForOrderRequest[pos].Quantity.toString()

                                               /* binding.tvUValue.text = "${
                                                      ( Globals.stringToInt(AppConstants.cartListForOrderRequest[pos].Quantity.toString()) *  Globals.stringToInt(
                                                            AppConstants.cartListForOrderRequest[pos].SalesQtyPerPackUnit
                                                      ))
                                                } U"*/

                                                AppConstants.saveCartListToPreferences(
                                                      this@ItemOneActivity,
                                                      mutableListOf()
                                                )
                                                AppConstants.saveCartListToPreferences(
                                                      this@ItemOneActivity,
                                                      AppConstants.cartListForOrderRequest
                                                )
                                          }
                                          //  textView.setText(AppConstants.cartListForOrderRequest.size.toString())
                                          return@forEachIndexed // use return@forEachIndexed instead of return@setOnClickListener
                                    }
                              }

// If we reach this point, the item was not found in the cart list, so add it
                              if (!itemFound) {
                                    /*var localDataForCart = LocalDataForCart()
                                    localDataForCart.ItemCode = currentDocLine.ItemCode
                                    localDataForCart.ItemDescription = currentDocLine.ItemName
                                    localDataForCart.UnitPrice = currentDocLine.UnitPrice
                                    localDataForCart.PriceType = currentDocLine.PriceType
                                    localDataForCart.SalesQtyPerPackUnit = currentDocLine.SalesQtyPerPackUnit
                                    localDataForCart.id = currentDocLine.id
                                    localDataForCart.apply {
                                          U_UTL_DD = Prefs.getString( Globals.DEALER_DISC)
                                          Currency = "INR"
                                          TaxCode = "IGST18"
                                          UnitPriceown = currentDocLine.UnitPrice // todo need to check
                                          U_UTL_TD = Prefs.getString( Globals.DISCOUNT_PERCENT)
                                          DiscountPercent = currentDocLine.Discount
                                          ProjectCode = ""
                                          FreeText = ""
                                          UomNo = currentDocLine.UnitPrice
                                          UoMCode = "Manual"
                                          Image = ""
                                          UnitWeight = currentDocLine.UnitPrice
                                          U_UTL_SD = Prefs.getString( Globals.SPECIAL_DISC)
                                    }

                                    localDataForCart.Quantity = 1*/

                                    val localDataForCart = LocalDataForCart(
                                          id = currentDocLine.id,
                                          OrderID = "",
//                                          SalesQtyPerPackUnit = currentDocLine.SalesQtyPerPackUnit,
                                          ItemCode = currentDocLine.ItemCode,
                                          ItemDescription = currentDocLine.Description,
                                          UnitPrice = stringToInt(currentDocLine.UnitPrice),
//                                          PriceType = currentDocLine.PriceType,
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
//                                          UnitWeight = currentDocLine.UnitWeight,
                                          U_UTL_DD = Prefs.getString( Globals.DEALER_DISC),
                                          Quantity = 1,
                                          TaxRate = currentDocLine.Tax
                                    )

                                    AppConstants.cartListForOrderRequest.add(localDataForCart)

                                    AppConstants.saveCartListToPreferences(
                                          this@ItemOneActivity,
                                          mutableListOf()
                                    )
                                    AppConstants.saveCartListToPreferences(
                                          this@ItemOneActivity,
                                          AppConstants.cartListForOrderRequest
                                    )

                                    binding.total.text = localDataForCart.Quantity.toString()
                                    /*binding.tvUValue.text = "${
                                          ( Globals.stringToInt(localDataForCart.Quantity.toString()) *  Globals.stringToInt(
                                                localDataForCart.SalesQtyPerPackUnit
                                          ))
                                    } U"*/

                                    /*  Log.e(
                                          TAG,
                                          "onBindViewHolder: ADDING CART SIZE- ${AppConstants.cartListForOrderRequest.size}"
                                      )*/
                                    //.setText(AppConstants.cartListForOrderRequest.size.toString())
                                    //notifyDataSetChanged()
                              }


                        }


                  }


            }
      }

}