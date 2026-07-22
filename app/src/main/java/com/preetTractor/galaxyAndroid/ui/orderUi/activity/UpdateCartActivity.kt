package com.preetTractor.galaxyAndroid.ui.orderUi.activity

import Event
import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.JsonObject
import com.preetTractor.galaxyAndroid.R
import com.preetTractor.galaxyAndroid.apiHelper.ApiClient
import com.preetTractor.galaxyAndroid.apiHelper.ApisInterface
import com.preetTractor.galaxyAndroid.apiHelper.AppConstants
import com.preetTractor.galaxyAndroid.data.model.order.model.apibody.ModelSoCreateRequest
import com.preetTractor.galaxyAndroid.data.model.order.model.response.ResponseBpOne
import com.preetTractor.galaxyAndroid.databinding.ActivityUpdateCartBinding
import com.preetTractor.galaxyAndroid.helper.APiPayloadKeys
import com.preetTractor.galaxyAndroid.helper.Globals
import com.preetTractor.galaxyAndroid.helper.Globals.stringToInt
import com.preetTractor.galaxyAndroid.helper.PrefsByShubh
import com.preetTractor.galaxyAndroid.helper.PrefsByShubh.businessPartnerDetails
import com.preetTractor.galaxyAndroid.mvvmSetUp.DefaultMainRepositories
import com.preetTractor.galaxyAndroid.mvvmSetUp.MainRepos
import com.preetTractor.galaxyAndroid.mvvmSetUp.MainViewModel
import com.preetTractor.galaxyAndroid.mvvmSetUp.MainViewModelProvider
import com.preetTractor.galaxyAndroid.orderUi.model.local.LocalDataForCart
import com.preetTractor.galaxyAndroid.ui.orderUi.adapter.CartAdapter
import com.pixplicity.easyprefs.library.Prefs
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class UpdateCartActivity : AppCompatActivity() {
      lateinit var binding: ActivityUpdateCartBinding


      var cartAdapter = CartAdapter()
      var basePrice: Double = 0.0
      var dealerDiscount: Double = 0.0
      var specialDiscount: Double = 0.0
      var additionalDiscount: Double = 0.0
      var gstAmount: Double = 0.0
      var tradeAmount: Double = 0.0
      var invoiceAmount: Double = 0.0
      var getvalueForGstcalculation: Double = 0.0

      //var requestBodyForSoRequestCreate = RequestBodyForSoRequestCreate()
      var requestForSoCreate = ModelSoCreateRequest()


      lateinit var viewModel: MainViewModel
      //lateinit var sessionManagement: SessionManagement

      var orderId = ""


      private fun setUprecyclerview() = binding.rvDispatchOrder.apply {
            adapter = cartAdapter
            layoutManager = LinearLayoutManager(this@UpdateCartActivity)
            cartAdapter.notifyDataSetChanged()

      }

      override fun onBackPressed() {
            super.onBackPressed()
            AppConstants.cartListForOrderRequest.clear()
      }

      private fun setUpViewModel() {
            val dispatchers: CoroutineDispatcher = Dispatchers.Main
            val mainRepos = DefaultMainRepositories() as MainRepos
            val fanxApi: ApisInterface = ApiClient().service(this)
            val viewModelProviderfactory =
                  MainViewModelProvider(application, mainRepos, dispatchers, fanxApi)
            viewModel = ViewModelProvider(this, viewModelProviderfactory)[MainViewModel::class.java]

      }

      lateinit var builder: AlertDialog.Builder
      lateinit var alertDialog: AlertDialog


      private fun SetUPDialog(context: Context) {
            builder = AlertDialog.Builder(context)
            builder.setView(R.layout.progress_dialog).setCancelable(false)
            alertDialog = builder!!.create()
      }

      companion object {
            //  private const val TAG = "UpdateCartActivity"
      }

      override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            binding = ActivityUpdateCartBinding.inflate(layoutInflater)
            setContentView(binding.root)

            // supportActionBar?.hide()
            setUpViewModel()
            //sessionManagement = SessionManagement(this)
            SetUPDialog(this)
            orderId = intent.getStringExtra("ID").toString()

            binding.ibBack.setOnClickListener {
                  AppConstants.cartListForOrderRequest.clear()
                  finish()

            }

            //requestDataForSoCreate(businessPartnerDetails)

            binding.headingTradeDiscount.text =
                  "Trade Disc (${Prefs.getString( Globals.DISCOUNT_PERCENT)} %)"

            //todo bind default data---
            /*    var hashmap = HashMap<String, String>()
                hashmap["card_code"] = sessionManagement.getCardCode()!!
                hashmap["id"] = sessionManagement.getDistributorID()!!
                viewModel.distributorProfile(hashmap, this)
                bindObserverProfile()*/

            viewModel.requestOrderOneApi(JsonObject().apply {
                  addProperty(APiPayloadKeys.id, orderId)
            }, this)

            subscribeToUnderapproveObserver()


            cartAdapter.setOnItemRefreshCalculationClickListener { localDataForCart, i ->
                  basePrice = 0.0
                  dealerDiscount = 0.0
                  specialDiscount = 0.0
                  additionalDiscount = 0.0
                  tradeAmount = 0.0
                  getvalueForGstcalculation = 0.0
                  gstAmount = 0.0
                  if (AppConstants.cartListForOrderRequest.isNotEmpty()) {
                        for (currentItem in AppConstants.cartListForOrderRequest) {
                              basePrice +=  Globals.calculateBasePriceWithQuantityAndSPQ(currentItem)
                             /* if (currentItem.PriceType.equals("MRPRATE")) {
                                    dealerDiscount +=  Globals.calculateDealerDiscount(currentItem)
                                    specialDiscount +=  Globals.calculateSpecialDiscount(currentItem)

                              } else if (currentItem.PriceType.equals("FLATRATE")) {
                                    additionalDiscount +=  Globals.calculateAdditionalDiscount(currentItem)
                              }*/
                        }
                        basePrice = basePrice - dealerDiscount - specialDiscount - additionalDiscount

                  } else {
                        basePrice = 0.0
                        dealerDiscount = 0.0
                        specialDiscount = 0.0
                        additionalDiscount = 0.0
                        tradeAmount = 0.0
                        getvalueForGstcalculation = 0.0
                        gstAmount = 0.0
                  }

                  binding.tvTotalBillingAmount.text =
                        "₹ " +  Globals.numberToK( Globals.formatDoublevlauUpToTwoDecimal(basePrice).toString())

                  tradeAmount =  Globals.calculateTradeDiscount(basePrice)
                  binding.tvTradeDiscAmount.text =
                        "₹ " +  Globals.numberToK(
                               Globals.formatDoublevlauUpToTwoDecimal(tradeAmount).toString()
                        )

                  getvalueForGstcalculation = basePrice - tradeAmount
                  gstAmount =
                         Globals.calculateGstonBillingAfterMinusTradeDiscount(getvalueForGstcalculation)
                  binding.tvGst.text =
                        "₹ " +  Globals.numberToK( Globals.formatDoublevlauUpToTwoDecimal(gstAmount).toString())


                  binding.tvTotalInvoiceAmount.text =
                        "₹ " +  Globals.numberToK( Globals.formatDoublevlauUpToTwoDecimal((getvalueForGstcalculation + gstAmount)).toString())


                  /*   Log.e(TAG, "onCreate: Base Price= $basePrice ")
                     Log.e(TAG, "onCreate: dealerDiscount= $dealerDiscount ")
                     Log.e(TAG, "onCreate: specialDiscount= $specialDiscount ")
                     Log.e(TAG, "onCreate: additionalDiscount= $additionalDiscount ")*/


            }



            binding.btnProceedToBuy.setOnClickListener {

                  requestForSoCreate.UpdateDate = Globals.getTodaysDatervrsfrmt().toString()
                  requestForSoCreate.UpdateTime = Globals.getfullformatCurrentTime().toString()
                  requestForSoCreate.DocTotal = "${getvalueForGstcalculation + gstAmount}"
                  requestForSoCreate.DocumentLines = AppConstants.cartListForOrderRequest
                  viewModel.updateSoRequest(requestForSoCreate, this)
                  subscribeToObserver()


            }
      }

      private fun requestDataForSoCreate(businessPartnerDetails: ResponseBpOne?) {
            businessPartnerDetails?.data?.get(0)?.BPAddresses?.apply {
                  requestForSoCreate.AddressExtension.id = get(0).id
                  requestForSoCreate.AddressExtension.BillToCountry = "IN"
                  requestForSoCreate.AddressExtension.PlaceOfSupply = get(0).State
                  requestForSoCreate.AddressExtension.BillToDistrict = get(0).District
                  requestForSoCreate.AddressExtension.BillToCity = get(0).City
                  requestForSoCreate.AddressExtension.BillToZipCode = get(0).ZipCode
                  requestForSoCreate.AddressExtension.BillToBuilding = get(0).BuildingFloorRoom
                  requestForSoCreate.AddressExtension.BillToState = get(0).State
                  requestForSoCreate.AddressExtension.BillToStreet = get(0).Street

                  requestForSoCreate.AddressExtension.ShipToCountry = "IN"
                  requestForSoCreate.AddressExtension.ShipToDistrict = get(1).District
                  requestForSoCreate.AddressExtension.ShipToCity = get(1).City
                  requestForSoCreate.AddressExtension.ShipToZipCode = get(1).ZipCode
                  requestForSoCreate.AddressExtension.ShipToBuilding = get(1).BuildingFloorRoom
                  requestForSoCreate.AddressExtension.ShipToState = get(1).State
                  requestForSoCreate.AddressExtension.ShipToStreet = get(1).Street
                  requestForSoCreate.AddressExtension.U_SCOUNTRY = get(0).U_COUNTRY
                  requestForSoCreate.AddressExtension.U_SSTATE = get(0).U_STATE
                  requestForSoCreate.AddressExtension.U_SHPTYPS = get(0).U_SHPTYP
                  requestForSoCreate.AddressExtension.U_BCOUNTRY = get(1).U_COUNTRY
                  requestForSoCreate.AddressExtension.U_BSTATE = get(1).State
                  requestForSoCreate.AddressExtension.U_SHPTYPB = get(1).U_SHPTYP
            }

      }


      private fun subscribeToObserver() {
            viewModel.updateSoRequest.observe(this, Event.EventObserver(onError = {
                  alertDialog.dismiss()
                   Globals.warningMessage(this, it)
            }, onLoading = {
                  alertDialog.show()
            }, { response ->
                  alertDialog.dismiss()
                  if (response.status == 200) {
                        AppConstants.cartListForOrderRequest.clear()
                        AppConstants.saveCartListToPreferences(
                              this,
                              mutableListOf()
                        )
                         Globals.successMessage(this, "Order Updated SuccessFully")
                        finish()


                  } else if (response.status == 201) {
                         Globals.warningMessage(this, response.message)
                  } else if (response.status == 401) {
                        //sessionManagement.ClearSession()
                        PrefsByShubh.ClearSession()
                         Globals.logoutScreen(this)

                  }


            }))
      }

      @SuppressLint("SetTextI18n")
      private fun subscribeToUnderapproveObserver() {
            viewModel.requestOrderOneDetailData.observe(this, Event.EventObserver(onError = {

                  alertDialog.dismiss()
                  Globals.warningMessage(this, it)
            }, onLoading = {
                  alertDialog.show()
            }, { response ->
                  alertDialog.dismiss()
                  if (response.status == 200) {
                        if (response.data.isNotEmpty()) {
                              for (currentItem in response.data[0].DocumentLines) {
                                    /* var localDataForCart = LocalDataForCart()
                                     localDataForCart.id = currentItem.id
                                     localDataForCart.UomNo = currentItem.UomNo
                                     localDataForCart.UnitWeight = currentItem.UnitWeight
                                     localDataForCart.UnitPriceown = currentItem.UnitPriceown
                                     localDataForCart.UnitPrice = currentItem.UnitPrice
                                     localDataForCart.SalesQtyPerPackUnit = currentItem.SalesQtyPerPackUnit

                                     //todo need to add
                                     localDataForCart.OrderID = currentItem.OrderID


                                     localDataForCart.ItemDescription = currentItem.ItemDescription
                                     localDataForCart.ItemCode = currentItem.ItemCode
                                     localDataForCart.FreeText = currentItem.FreeText
                                     localDataForCart.Quantity = currentItem.Quantity
                                     localDataForCart.DiscountPercent = currentItem.DiscountPercent
                                     localDataForCart.PriceType = currentItem.PriceType*/

                                    val localDataForCart = LocalDataForCart(
                                          id = currentItem.id,
                                          OrderID = currentItem.OrderID,
//                                          SalesQtyPerPackUnit = currentItem.SalesQtyPerPackUnit?:"",
                                          ItemCode = currentItem.ItemCode,
                                          ItemDescription = currentItem.ItemDescription,
                                          UnitPrice = stringToInt(currentItem.UnitPrice),
//                                          PriceType = currentItem.PriceType,
                                          U_UTL_SD = Prefs.getString(Globals.SPECIAL_DISC),
                                          Currency = "INR",
                                          TaxCode = "IGST18",
                                          UnitPriceown = stringToInt(currentItem.UnitPrice),
                                          U_UTL_TD = Prefs.getString(Globals.DISCOUNT_PERCENT),
                                          DiscountPercent = stringToInt(currentItem.DiscountPercent),
                                          ProjectCode = "",
                                          FreeText = currentItem.FreeText,
                                          UomNo = stringToInt(currentItem.UnitPrice),
                                          UoMCode = "Manual",
                                          Image = "",
//                                          UnitWeight = currentItem.UnitWeight,
                                          U_UTL_DD = Prefs.getString(Globals.DEALER_DISC),
                                          Quantity = 1,
                                          TaxRate = currentItem.TaxRate.toDouble()
                                    )

                                    AppConstants.cartListForOrderRequest.add(localDataForCart)
                              }
                              Log.e("LOCAL_DATA", "localDataForCart from pref: \n${AppConstants.cartListForOrderRequest}")
                              AppConstants.saveCartListToPreferences(
                                    this,
                                    AppConstants.cartListForOrderRequest
                              )

                              cartAdapter.setItems(AppConstants.cartListForOrderRequest)
                              cartAdapter.notifyDataSetChanged()
                              setUprecyclerview()

                              if (AppConstants.cartListForOrderRequest.isNotEmpty()) {
                                    for (currentItem in AppConstants.cartListForOrderRequest) {
                                          basePrice +=  Globals.calculateBasePriceWithQuantityAndSPQ(currentItem)
                                         /* if (currentItem.PriceType.equals("MRPRATE")) {
                                                dealerDiscount +=  Globals.calculateDealerDiscount(currentItem)
                                                specialDiscount +=  Globals.calculateSpecialDiscount(currentItem)

                                          } else if (currentItem.PriceType.equals("FLATRATE")) {
                                                additionalDiscount +=  Globals.calculateAdditionalDiscount(currentItem)
                                          }*/
                                    }
                                    basePrice = basePrice - dealerDiscount - specialDiscount - additionalDiscount

                              } else {
                                    basePrice = 0.0
                                    dealerDiscount = 0.0
                                    specialDiscount = 0.0
                                    additionalDiscount = 0.0
                              }

                              binding.tvTotalBillingAmount.text = "₹ ${Globals.numberToK(Globals.formatDoublevlauUpToTwoDecimal(basePrice))}"
                              tradeAmount =  Globals.calculateTradeDiscount(basePrice)
                              binding.tvTradeDiscAmount.text = "₹ ${Globals.numberToK(Globals.formatDoublevlauUpToTwoDecimal(tradeAmount))}"

                              getvalueForGstcalculation = basePrice - tradeAmount
                              gstAmount = Globals.calculateGstonBillingAfterMinusTradeDiscount(getvalueForGstcalculation)
                              binding.tvGst.text = "₹ ${Globals.numberToK(Globals.formatDoublevlauUpToTwoDecimal(gstAmount))}"

                              binding.tvTotalInvoiceAmount.text = "₹ ${Globals.numberToK(Globals.formatDoublevlauUpToTwoDecimal((getvalueForGstcalculation + gstAmount)))}"


                              /*       Log.e(TAG, "onCreate: Base Price= $basePrice ")
                                     Log.e(TAG, "onCreate: dealerDiscount= $dealerDiscount ")
                                     Log.e(TAG, "onCreate: specialDiscount= $specialDiscount ")
                                     Log.e(TAG, "onCreate: additionalDiscount= $additionalDiscount ")*/


                              var updateObject = response.data[0]
                              Log.e("LOCAL_DATA", "Update Object Data: \n${updateObject}")

                              /* updateObject.apply {
                                     requestBodyForSoRequestCreate.id = updateObject.id
                                     requestBodyForSoRequestCreate.DocumentStatus = DocumentStatus
                                     requestBodyForSoRequestCreate.ApprovalStatus = ApprovalStatus
                                     requestBodyForSoRequestCreate.CancelStatus = CancelStatus
                                     requestBodyForSoRequestCreate.Unit = Unit
                                     requestBodyForSoRequestCreate.PayTermsGrpCode =
                                           Prefs.getString( Globals.PAYMENT_GROUP_CODE)
                                     requestBodyForSoRequestCreate.DocCurrency = DocCurrency
                                     requestBodyForSoRequestCreate.Series = "90"
                                     requestBodyForSoRequestCreate.CreateTime = CreateTime
                                     requestBodyForSoRequestCreate.TaxDate = TaxDate

                                     requestBodyForSoRequestCreate.CreationDate = CreationDate

                                     requestBodyForSoRequestCreate.CreateDate =
                                           CreateDate
                                     requestBodyForSoRequestCreate.DocDate =
                                           DocDate
                                     requestBodyForSoRequestCreate.DocDueDate =
                                           DocDueDate
                                     requestBodyForSoRequestCreate.DiscountPercent =
                                           Prefs.getString( Globals.DISCOUNT_PERCENT)
                                     requestBodyForSoRequestCreate.TaxDate = "0"
                                     requestBodyForSoRequestCreate.U_LAT = ""
                                     requestBodyForSoRequestCreate.U_LONG = ""
                                     requestBodyForSoRequestCreate.U_OPPID = ""
                                     requestBodyForSoRequestCreate.FreeDelivery = ""
                                     requestBodyForSoRequestCreate.TermCondition = ""
                                     requestBodyForSoRequestCreate.U_QUOTID = ""
                                     requestBodyForSoRequestCreate.DeliveryMode = ""
                                     requestBodyForSoRequestCreate.U_OPPRNM = ""
                                     requestBodyForSoRequestCreate.Link = ""
                                     requestBodyForSoRequestCreate.DeliveryTerm = ""
                                     requestBodyForSoRequestCreate.AdditionalCharges = ""
                                     requestBodyForSoRequestCreate.U_QUOTNM = ""
                                     requestBodyForSoRequestCreate.PaymentType = ""
                                     requestBodyForSoRequestCreate.Comments = ""
                                     requestBodyForSoRequestCreate.ContactPersonCode =
                                           Prefs.getString( Globals.CONTACT_PERSON_CODE)
                                     requestBodyForSoRequestCreate.CardName = CardName
                                     requestBodyForSoRequestCreate.CardCode = CardCode
                                     requestBodyForSoRequestCreate.CreatedBy =
                                           Prefs.getString( Globals.SALES_EMPLOYEE_CODE)!!
                                     requestBodyForSoRequestCreate.SalesPersonCode =
                                           Prefs.getString( Globals.SALES_EMPLOYEE_CODE)

                                     var addressExtensionLocal =
                                           RequestBodyForSoRequestCreate.AddressExtension(
                                                 id = AddressExtension.id.toString(),
                                                 BillToCountry = "IN",
                                                 BillToState = AddressExtension.ShipToState,
                                                 U_SHPTYPB = AddressExtension.U_SHPTYPS,
                                                 ShipToBuilding = "",
                                                 U_SCOUNTRY = AddressExtension.U_SCOUNTRY,
                                                 ShipToZipCode = AddressExtension.ShipToZipCode,
                                                 ShipToCountry = AddressExtension.ShipToCountry,
                                                 U_BSTATE = AddressExtension.U_BSTATE,
                                                 U_SSTATE = AddressExtension.U_SSTATE,
                                                 ShipToState = AddressExtension.ShipToState,
                                                 BillToZipCode = AddressExtension.BillToZipCode,
                                                 ShipToDistrict = AddressExtension.ShipToDistrict,
                                                 BillToDistrict = AddressExtension.BillToDistrict,
                                                 BillToCity = AddressExtension.BillToCity,
                                                 U_BCOUNTRY = AddressExtension.U_BCOUNTRY,
                                                 BillToStreet = AddressExtension.BillToStreet,
                                                 U_SHPTYPS = AddressExtension.U_SHPTYPS,
                                                 ShipToStreet = AddressExtension.ShipToStreet,
                                                 BillToBuilding = "",
                                                 ShipToCity = AddressExtension.ShipToCity,
                                                 PlaceOfSupply = AddressExtension.U_SSTATE,
                                           )

                                     requestBodyForSoRequestCreate.addressExtension =
                                           addressExtensionLocal

                               }*/

                              updateObject.apply {
                                    /*requestBodyForSoRequestCreate.id = updateObject.id
                                    requestBodyForSoRequestCreate.DocumentStatus = DocumentStatus
                                    requestBodyForSoRequestCreate.ApprovalStatus = ApprovalStatus
                                    requestBodyForSoRequestCreate.CancelStatus = CancelStatus
                                    requestBodyForSoRequestCreate.Unit = Unit
                                    requestBodyForSoRequestCreate.PayTermsGrpCode =
                                          Prefs.getString(Globals.PAYMENT_GROUP_CODE)
                                    requestBodyForSoRequestCreate.DocCurrency = DocCurrency
                                    requestBodyForSoRequestCreate.Series = "90"
                                    requestBodyForSoRequestCreate.CreateTime = CreateTime
                                    requestBodyForSoRequestCreate.TaxDate = TaxDate

                                    requestBodyForSoRequestCreate.CreationDate = CreationDate

                                    requestBodyForSoRequestCreate.CreateDate =
                                          CreateDate
                                    requestBodyForSoRequestCreate.DocDate =
                                          DocDate
                                    requestBodyForSoRequestCreate.DocDueDate =
                                          DocDueDate
                                    requestBodyForSoRequestCreate.DiscountPercent =
                                          Prefs.getString(Globals.DISCOUNT_PERCENT)
                                    requestBodyForSoRequestCreate.TaxDate = "0"
                                    requestBodyForSoRequestCreate.U_LAT = ""
                                    requestBodyForSoRequestCreate.U_LONG = ""
                                    requestBodyForSoRequestCreate.U_OPPID = ""
                                    requestBodyForSoRequestCreate.FreeDelivery = ""
                                    requestBodyForSoRequestCreate.TermCondition = ""
                                    requestBodyForSoRequestCreate.U_QUOTID = ""
                                    requestBodyForSoRequestCreate.DeliveryMode = ""
                                    requestBodyForSoRequestCreate.U_OPPRNM = ""
                                    requestBodyForSoRequestCreate.Link = ""
                                    requestBodyForSoRequestCreate.DeliveryTerm = ""
                                    requestBodyForSoRequestCreate.AdditionalCharges = ""
                                    requestBodyForSoRequestCreate.U_QUOTNM = ""
                                    requestBodyForSoRequestCreate.PaymentType = ""
                                    requestBodyForSoRequestCreate.Comments = ""
                                    requestBodyForSoRequestCreate.ContactPersonCode =
                                          Prefs.getString(Globals.CONTACT_PERSON_CODE)
                                    requestBodyForSoRequestCreate.CardName = CardName
                                    requestBodyForSoRequestCreate.CardCode = CardCode
                                    requestBodyForSoRequestCreate.CreatedBy =
                                          Prefs.getString(Globals.SALES_EMPLOYEE_CODE)!!
                                    requestBodyForSoRequestCreate.SalesPersonCode =
                                          Prefs.getString(Globals.SALES_EMPLOYEE_CODE)*/

                                    requestForSoCreate.id = id
                                    requestForSoCreate.DocumentStatus = DocumentStatus
                                    requestForSoCreate.ApprovalStatus = ApprovalStatus
                                    requestForSoCreate.CancelStatus = CancelStatus
                                    requestForSoCreate.Unit = Unit.toInt()
                                    requestForSoCreate.PayTermsGrpCode =
                                          Prefs.getString(Globals.PAYMENT_GROUP_CODE)
                                    requestForSoCreate.DocCurrency = DocCurrency
                                    requestForSoCreate.Series = 90
                                    requestForSoCreate.CreateTime = CreateTime
                                    requestForSoCreate.TaxDate = TaxDate

                                    requestForSoCreate.CreationDate = CreationDate

                                    requestForSoCreate.CreateDate =
                                          CreateDate
                                    requestForSoCreate.DocDate =
                                          DocDate
                                    requestForSoCreate.DocDueDate =
                                          DocDueDate
                                    requestForSoCreate.DiscountPercent =
                                          Prefs.getString(Globals.DISCOUNT_PERCENT)
                                    requestForSoCreate.TaxDate = "0"
                                    requestForSoCreate.U_LAT = ""
                                    requestForSoCreate.U_LONG = ""
                                    requestForSoCreate.U_OPPID = ""
                                    requestForSoCreate.FreeDelivery = ""
                                    requestForSoCreate.TermCondition = ""
                                    requestForSoCreate.U_QUOTID = ""
                                    requestForSoCreate.DeliveryMode = ""
                                    requestForSoCreate.U_OPPRNM = ""
                                    requestForSoCreate.Link = ""
                                    requestForSoCreate.DeliveryTerm = ""
                                    requestForSoCreate.AdditionalCharges = ""
                                    requestForSoCreate.U_QUOTNM = ""
                                    requestForSoCreate.PaymentType = ""
                                    requestForSoCreate.Comments = ""
                                    requestForSoCreate.ContactPersonCode =
                                          Prefs.getString(Globals.CONTACT_PERSON_CODE)
                                    requestForSoCreate.CardName = CardName
                                    requestForSoCreate.CardCode = CardCode
                                    requestForSoCreate.CreatedBy =
                                          Prefs.getString(Globals.SALES_EMPLOYEE_CODE)!!
                                    requestForSoCreate.SalesPersonCode =
                                          Prefs.getString(Globals.SALES_EMPLOYEE_CODE)


                                    /*AddressExtension.apply {
                                          requestForSoCreate.AddressExtension.BillToCountry = "IN"
                                          requestForSoCreate.AddressExtension.PlaceOfSupply = get(0).BillToBuilding
                                          requestForSoCreate.AddressExtension.BillToDistrict = get(0).BillToDistrict
                                          requestForSoCreate.AddressExtension.BillToCity = get(0).BillToCity
                                          requestForSoCreate.AddressExtension.BillToZipCode = get(0).BillToZipCode
                                          requestForSoCreate.AddressExtension.BillToBuilding = get(0).BillToBuilding
                                          requestForSoCreate.AddressExtension.BillToState = get(0).BillToState
                                          requestForSoCreate.AddressExtension.BillToStreet = get(0).BillToState

                                          requestForSoCreate.AddressExtension.ShipToCountry = "IN"
                                          requestForSoCreate.AddressExtension.ShipToDistrict = get(0).ShipToDistrict
                                          requestForSoCreate.AddressExtension.ShipToCity = get(0).ShipToCity
                                          requestForSoCreate.AddressExtension.ShipToZipCode = get(0).ShipToZipCode
                                          requestForSoCreate.AddressExtension.ShipToBuilding = get(0).ShipToBuilding
                                          requestForSoCreate.AddressExtension.ShipToState = get(0).ShipToState
                                          requestForSoCreate.AddressExtension.ShipToStreet = get(0).ShipToStreet
                                          requestForSoCreate.AddressExtension.U_SCOUNTRY = get(0).U_SCOUNTRY
                                          requestForSoCreate.AddressExtension.U_SSTATE = get(0).U_SSTATE
                                          requestForSoCreate.AddressExtension.U_SHPTYPS = get(0).U_SHPTYPS
                                          requestForSoCreate.AddressExtension.U_BCOUNTRY = get(0).U_BCOUNTRY
                                          requestForSoCreate.AddressExtension.U_BSTATE = get(0).U_BSTATE
                                          requestForSoCreate.AddressExtension.U_SHPTYPB = get(0).U_SHPTYPB
                                    }*/

                                    Log.e("LOCAL_DATA", "Address Extension: \n${requestForSoCreate.AddressExtension}")
                                    Log.e("LOCAL_DATA", "Completed Request: \n${requestForSoCreate}")
                                    /*var addressExtensionLocal =
                                          RequestBodyForSoRequestCreate.AddressExtension(
                                                id = AddressExtension.id.toString(),
                                                BillToCountry = "IN",
                                                BillToState = AddressExtension.ShipToState,
                                                U_SHPTYPB = AddressExtension.U_SHPTYPS,
                                                ShipToBuilding = "",
                                                U_SCOUNTRY = AddressExtension.U_SCOUNTRY,
                                                ShipToZipCode = AddressExtension.ShipToZipCode,
                                                ShipToCountry = AddressExtension.ShipToCountry,
                                                U_BSTATE = AddressExtension.U_BSTATE,
                                                U_SSTATE = AddressExtension.U_SSTATE,
                                                ShipToState = AddressExtension.ShipToState,
                                                BillToZipCode = AddressExtension.BillToZipCode,
                                                ShipToDistrict = AddressExtension.ShipToDistrict,
                                                BillToDistrict = AddressExtension.BillToDistrict,
                                                BillToCity = AddressExtension.BillToCity,
                                                U_BCOUNTRY = AddressExtension.U_BCOUNTRY,
                                                BillToStreet = AddressExtension.BillToStreet,
                                                U_SHPTYPS = AddressExtension.U_SHPTYPS,
                                                ShipToStreet = AddressExtension.ShipToStreet,
                                                BillToBuilding = "",
                                                ShipToCity = AddressExtension.ShipToCity,
                                                PlaceOfSupply = AddressExtension.U_SSTATE,
                                          )

                                    requestForSoCreate.addressExtension = addressExtensionLocal*/

                              }


                        }


                  } else if (response.status == 201) {
                        Globals.warningMessage(this, response.message)
                  } else if (response.status == 401) {
                        //sessionManagement.ClearSession()
                        PrefsByShubh.ClearSession()
                        Globals.logoutScreen(this)

                  } else {
                        Globals.warningMessage(this, response.message)
                  }


            }))
      }

      override fun onResume() {
            super.onResume()
            viewModel.bPOneApi(JsonObject().apply {
                  addProperty(APiPayloadKeys.CardCode, PrefsByShubh.getCardCode())
            }, this)

            bindBpOneObserver()
      }

      private fun bindBpOneObserver() {
            viewModel.bPOneDetailData.observe(this, Event.EventObserver(onError = {
                  alertDialog.dismiss()
                  Globals.warningMessage(this, it)
            }, onLoading = {
                  alertDialog.show()
            }, { response ->
                  alertDialog.dismiss()
                  if (response.status == 200) {
                        //todo set dealer, special and additional discount
                        response.data[0].apply {

                              businessPartnerDetails = response
                              Log.i("BP_DETAILS", "$businessPartnerDetails")
                              requestDataForSoCreate(businessPartnerDetails)

                              if (SalesPersonCode.isNotEmpty()) {
                                    Prefs.putString(
                                          Globals.SALES_EMPLOYEE_CODE,
                                          SalesPersonCode[0].SalesEmployeeCode
                                    )
                              }



                              Prefs.putString(
                                    Globals.CURRENCY,
                                    Currency
                              )

                              Prefs.putString(
                                    Globals.DEALER_DISC,
                                    U_UTL_DLRD
                              )
                              Prefs.putString(
                                    Globals.SPECIAL_DISC,
                                    U_UTL_SPCL
                              )
                              Prefs.putString(
                                    Globals.ADDITIONAL_DISC,
                                    U_CIS_AD
                              )

                              Prefs.putString(
                                    Globals.DISCOUNT_PERCENT,
                                    DiscountPercent
                              )
                              if (BPAddresses.isNotEmpty()) {
                                    Prefs.putString(
                                          Globals.BLOCK,
                                          BPAddresses[0].Block
                                    )
                                    Prefs.putString(
                                          Globals.CITY,
                                          BPAddresses[0].City
                                    )
                                    Prefs.putString(
                                          Globals.STATE,
                                          BPAddresses[0].State
                                    )
                              }

                              if (ContactEmployees.isNotEmpty()) {
                                    Prefs.putString(
                                          Globals.CONTACT_PERSON_CODE,
                                          ContactEmployees[0].InternalCode
                                    )

                              }

                              if (PayTermsGrpCode.isNotEmpty()) {
                                    Prefs.putString(
                                          Globals.PAYMENT_GROUP_CODE,
                                          PayTermsGrpCode[0].GroupNumber
                                    )

                              }

                        }

                  } else if (response.status == 201) {
                        Globals.warningMessage(this, response.message)
                  } else if (response.status == 401) {
                        //sessionManagement.ClearSession()
                        PrefsByShubh.ClearSession()
                        Globals.logoutScreen(this)

                  } else {
                        Globals.warningMessage(this, response.message)
                  }


            }))
      }


      /*    private fun bindObserverProfile() {
              viewModel.distributorProfileData.observe(this, Event.EventObserver(
                  onError = {
                      Log.e(TAG, "bindRemarkObserver: $it")
                  },
                  onLoading = {
                  },
                  onSuccess = { response ->
                      if (response.status == 200) {
                          if (response.data.isNotEmpty()) {
      
                              //todo set dealer, special and additional discount
                              if (response.data[0].bp_detail.isNotEmpty()) {
                                  if (response.data[0].bp_detail[0].BPAddresses.isNotEmpty()) {
                                      var addressExtensionLocal =
                                          RequestBodyForSoRequestCreate.AddressExtension(
                                              BillToCountry = "IN",
                                              BillToState = response.data[0].bp_detail[0].BPAddresses[0].U_STATE,
                                              U_SHPTYPB = response.data[0].bp_detail[0].BPAddresses[0].U_SHPTYP,
                                              ShipToBuilding = "",
                                              U_SCOUNTRY = response.data[0].bp_detail[0].BPAddresses[0].U_COUNTRY,
                                              ShipToZipCode = response.data[0].bp_detail[0].BPAddresses[0].ZipCode,
                                              ShipToCountry = response.data[0].bp_detail[0].BPAddresses[0].Country,
                                              U_BSTATE = response.data[0].bp_detail[0].BPAddresses[0].State,
                                              U_SSTATE = response.data[0].bp_detail[0].BPAddresses[0].State,
                                              ShipToState = response.data[0].bp_detail[0].BPAddresses[0].State,
                                              BillToZipCode = response.data[0].bp_detail[0].BPAddresses[0].ZipCode,
                                              ShipToDistrict = response.data[0].bp_detail[0].BPAddresses[0].District,
                                              BillToDistrict = response.data[0].bp_detail[0].BPAddresses[0].District,
                                              BillToCity = response.data[0].bp_detail[0].BPAddresses[0].City,
                                              U_BCOUNTRY = response.data[0].bp_detail[0].BPAddresses[0].U_COUNTRY,
                                              BillToStreet = response.data[0].bp_detail[0].BPAddresses[0].Street,
                                              U_SHPTYPS = response.data[0].bp_detail[0].BPAddresses[0].U_SHPTYP,
                                              ShipToStreet = response.data[0].bp_detail[0].BPAddresses[0].Street,
                                              BillToBuilding = "",
                                              ShipToCity = response.data[0].bp_detail[0].BPAddresses[0].City,
                                              PlaceOfSupply = response.data[0].bp_detail[0].BPAddresses[0].U_STATE,
      
                                              )
      
                                      requestBodyForSoRequestCreate.addressExtension =
                                          addressExtensionLocal
      
                                  }
      
      
                              }
      
      
                          } else {
      
                          }
      
      
                      } else if (response.status == 401) {
                          sessionManagement.ClearSession()
                           Globals.logoutScreen(this)
      
                      } else {
                           Globals.warningMessage(this, response.message)
                      }
                  }
              ))
          }*/

}