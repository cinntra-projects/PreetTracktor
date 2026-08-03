package com.preetTractor.galaxyAndroid.ui.orderUi.activity

import Event
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.preetTractor.galaxyAndroid.R
import com.preetTractor.galaxyAndroid.apiHelper.ApiClient
import com.preetTractor.galaxyAndroid.apiHelper.ApisInterface
import com.preetTractor.galaxyAndroid.apiHelper.AppConstants
import com.preetTractor.galaxyAndroid.data.model.order.model.request.ModelCreateOrderRequest
import com.preetTractor.galaxyAndroid.data.model.order.model.response.ResponseBpOne
import com.preetTractor.galaxyAndroid.databinding.ActivityCartBinding
import com.preetTractor.galaxyAndroid.helper.Globals
import com.preetTractor.galaxyAndroid.helper.Globals.toPrettyJson
import com.preetTractor.galaxyAndroid.helper.PrefsByShubh
import com.preetTractor.galaxyAndroid.helper.PrefsByShubh.businessPartnerDetails
import com.preetTractor.galaxyAndroid.mvvmSetUp.DefaultMainRepositories
import com.preetTractor.galaxyAndroid.mvvmSetUp.MainRepos
import com.preetTractor.galaxyAndroid.mvvmSetUp.MainViewModel
import com.preetTractor.galaxyAndroid.mvvmSetUp.MainViewModelProvider
import com.preetTractor.galaxyAndroid.ui.orderUi.adapter.CartAdapter
import com.pixplicity.easyprefs.library.Prefs
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class CartActivity : AppCompatActivity() {
      lateinit var binding: ActivityCartBinding
      var cartAdapter = CartAdapter()
      var basePrice: Double = 0.0
      var dealerDiscount: Double = 0.0
      var specialDiscount: Double = 0.0
      var additionalDiscount: Double = 0.0
      var gstAmount: Double = 0.0
      var tradeAmount: Double = 0.0
      var invoiceAmount: Double = 0.0
      var getvalueForGstcalculation: Double = 0.0

      //var requestForSoCreate = ModelSoCreateRequest()
      var requestOrderCreate = ModelCreateOrderRequest()

      lateinit var viewModel: MainViewModel
      //lateinit var sessionManagement: SessionManagement


      var where = ""

      var totalUnitPrice = 0.0
      var totalDiscount = 0
      var FinalItemPrice = 0.0
      var totalGST = 0.0

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
            //  private const val TAG = "CartActivity"
      }

      override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)

            binding = ActivityCartBinding.inflate(layoutInflater)
            setContentView(binding.root)
            setUpViewModel()
            //sessionManagement = SessionManagement(this)
            SetUPDialog(this)

            //todo bind default data---
            /* var hashmap = HashMap<String, String>()
             hashmap["card_code"] = PrefsByShubh.getCardCode().toString() //sessionManagement.getCardCode()!!
             hashmap["id"] = PrefsByShubh.getSalesEmployeeCode().toString()  //sessionManagement.getDistributorID()!!
             hashmap["SalesEmployeeCode"] = PrefsByShubh.getSalesEmployeeCode().toString()
             viewModel.distributorProfile(hashmap, this)
             Log.e("PREF_SHUBH","${hashmap}")
             bindObserverProfile()*/
            
            requestDataForSoCreate(businessPartnerDetails)
           /* viewModel.bPOneApi(JsonObject().apply {
                  addProperty(APiPayloadKeys.CardCode, PrefsByShubh.getCardCode())
            }, this)

            bindBpOneObserver()*/

            binding.headingTradeDiscount.text =
                  "Trade Disc (${Prefs.getString( Globals.DISCOUNT_PERCENT)} %)"

            binding.ibBack.setOnClickListener {
                  finish()
            }

            binding.tvAddressTitle.text =
                  "Deliver To : ${Prefs.getString( Globals.BLOCK)}, ${Prefs.getString( Globals.CITY)}, ${
                        Prefs.getString( Globals.STATE)
                  }"

            cartAdapter.setItems(AppConstants.cartListForOrderRequest)
            cartAdapter.notifyDataSetChanged()


            setUprecyclerview()
            //    supportActionBar?.hide()


            if (AppConstants.cartListForOrderRequest.isNotEmpty()) {
                  for (currentItem in AppConstants.cartListForOrderRequest) {
                        basePrice +=  Globals.calculateBasePriceWithQuantityAndSPQ(currentItem)
                        /*if (currentItem.PriceType.equals("MRPRATE")) {
                              dealerDiscount +=  Globals.calculateDealerDiscount(currentItem)
                              specialDiscount +=  Globals.calculateSpecialDiscount(currentItem)

                        } else if (currentItem.PriceType.equals("FLATRATE")) {
                              additionalDiscount +=  Globals.calculateAdditionalDiscount(currentItem)
                        }*/

                        val itemQuantity = currentItem.UnitPrice * currentItem.Quantity
                        val itemDiscount = (currentItem.DiscountPercent * itemQuantity)/100

                        val valuesMinusDiscount = itemQuantity - itemDiscount
                        totalUnitPrice +=  valuesMinusDiscount

                        val taxAmount = (valuesMinusDiscount * currentItem.TaxRate)/100
                        totalGST += taxAmount


                        FinalItemPrice +=  (valuesMinusDiscount + taxAmount)

                  }
                  basePrice=basePrice-dealerDiscount-specialDiscount-additionalDiscount
            } else {
                  basePrice = 0.0
                  dealerDiscount = 0.0
                  specialDiscount = 0.0
                  additionalDiscount = 0.0

                  totalUnitPrice = 0.0
                  totalGST = 0.0
                  FinalItemPrice = 0.0
            }

            binding.tvTotalBillingAmount.text =
                  "₹ "+ totalUnitPrice.toString()
            binding.tvTotalGST.text = "₹ "+ totalGST.toString()
            binding.tvTotalInvoiceAmt.text = "₹ "+ FinalItemPrice.toString()

//            binding.tvTotalBillingAmount.text =
//                  "₹ "+ Globals.numberToK( Globals.formatDoublevlauUpToTwoDecimal(basePrice).toString())

            tradeAmount =  Globals.calculateTradeDiscount(basePrice)
            binding.tvTradeDiscAmount.text =
                  "₹ "+  Globals.numberToK( Globals.formatDoublevlauUpToTwoDecimal(tradeAmount).toString())

            getvalueForGstcalculation = basePrice - tradeAmount
            gstAmount =  Globals.calculateGstonBillingAfterMinusTradeDiscount(getvalueForGstcalculation)
            binding.tvGst.text = "₹ "+ Globals.numberToK( Globals.formatDoublevlauUpToTwoDecimal(gstAmount).toString())


            binding.tvTotalInvoiceAmount.text ="₹ "+  Globals.numberToK( Globals.formatDoublevlauUpToTwoDecimal((getvalueForGstcalculation + gstAmount)).toString())


            /*Log.e(TAG, "onCreate: Base Price= $basePrice ")
            Log.e(TAG, "onCreate: dealerDiscount= $dealerDiscount ")
            Log.e(TAG, "onCreate: specialDiscount= $specialDiscount ")
            Log.e(TAG, "onCreate: additionalDiscount= $additionalDiscount ")*/

            cartAdapter.setOnItemRefreshCalculationClickListener { localDataForCart, i ->
                  basePrice = 0.0
                  dealerDiscount = 0.0
                  specialDiscount = 0.0
                  additionalDiscount = 0.0
                  tradeAmount = 0.0
                  getvalueForGstcalculation = 0.0
                  gstAmount = 0.0

                  totalUnitPrice = 0.0
                  totalGST = 0.0
                  FinalItemPrice = 0.0

                  if (AppConstants.cartListForOrderRequest.isNotEmpty()) {
                        for (currentItem in AppConstants.cartListForOrderRequest) {
                              basePrice +=  Globals.calculateBasePriceWithQuantityAndSPQ(currentItem)
                              /*if (currentItem.PriceType.equals("MRPRATE")) {
                                    dealerDiscount +=  Globals.calculateDealerDiscount(currentItem)
                                    specialDiscount +=  Globals.calculateSpecialDiscount(currentItem)

                              } else if (currentItem.PriceType.equals("FLATRATE")) {
                                    additionalDiscount +=  Globals.calculateAdditionalDiscount(currentItem)
                              }*/

                              val itemQuantity = currentItem.UnitPrice * currentItem.Quantity
                              val itemDiscount = (currentItem.DiscountPercent * itemQuantity)/100

                              val valuesMinusDiscount = itemQuantity - itemDiscount
                              totalUnitPrice +=  valuesMinusDiscount

                              val taxAmount = (valuesMinusDiscount * currentItem.TaxRate)/100
                              totalGST += taxAmount


                              FinalItemPrice +=  (valuesMinusDiscount + taxAmount)
                        }
                        basePrice=basePrice-dealerDiscount-specialDiscount-additionalDiscount
                  } else {
                        basePrice = 0.0
                        dealerDiscount = 0.0
                        specialDiscount = 0.0
                        additionalDiscount = 0.0
                        tradeAmount = 0.0
                        getvalueForGstcalculation = 0.0
                        gstAmount = 0.0

                        totalUnitPrice = 0.0
                        totalGST = 0.0
                        FinalItemPrice = 0.0
                  }


                  binding.tvTotalBillingAmount.text = "₹ "+ totalUnitPrice.toString()
                  binding.tvTotalGST.text = "₹ "+ totalGST.toString()
                  binding.tvTotalInvoiceAmt.text = "₹ "+ FinalItemPrice.toString()

//                  binding.tvTotalBillingAmount.text =
//                        "₹ "+  Globals.numberToK( Globals.formatDoublevlauUpToTwoDecimal(basePrice).toString())

                  tradeAmount =  Globals.calculateTradeDiscount(basePrice)
                  binding.tvTradeDiscAmount.text =
                        "₹ "+  Globals.numberToK( Globals.formatDoublevlauUpToTwoDecimal(tradeAmount).toString())

                  getvalueForGstcalculation = basePrice - tradeAmount
                  gstAmount =
                         Globals.calculateGstonBillingAfterMinusTradeDiscount(getvalueForGstcalculation)
                  binding.tvGst.text = "₹ "+ Globals.numberToK( Globals.formatDoublevlauUpToTwoDecimal(gstAmount).toString())


                  binding.tvTotalInvoiceAmount.text =
                        "₹ "+ Globals.numberToK( Globals.formatDoublevlauUpToTwoDecimal((getvalueForGstcalculation + gstAmount)).toString())


                  /*  Log.e(TAG, "onCreate: Base Price= $basePrice ")
                    Log.e(TAG, "onCreate: dealerDiscount= $dealerDiscount ")
                    Log.e(TAG, "onCreate: specialDiscount= $specialDiscount ")
                    Log.e(TAG, "onCreate: additionalDiscount= $additionalDiscount ")*/


            }



            binding.btnProceedToBuy.setOnClickListener {

                  /*requestBodyForSoRequestCreate.DocumentStatus = "bost_Open"
                  requestBodyForSoRequestCreate.ApprovalStatus = "Pending"
                  requestBodyForSoRequestCreate.CancelStatus = "csNo"
                  requestBodyForSoRequestCreate.Unit = "1"
                  requestBodyForSoRequestCreate.PayTermsGrpCode =
                        Prefs.getString( Globals.PAYMENT_GROUP_CODE)
                  requestBodyForSoRequestCreate.DocCurrency = Prefs.getString( Globals.CURRENCY)
                  requestBodyForSoRequestCreate.Series = "90"
                  requestBodyForSoRequestCreate.CreateTime =  Globals.getfullformatCurrentTime()
                  requestBodyForSoRequestCreate.TaxDate =  Globals.getTodaysDatervrsfrmt().toString()
                  requestBodyForSoRequestCreate.CreationDate =  Globals.getTodaysDatervrsfrmt().toString()
                  requestBodyForSoRequestCreate.CreateDate =  Globals.getTodaysDatervrsfrmt().toString()
                  requestBodyForSoRequestCreate.DocDate =  Globals.getTodaysDatervrsfrmt().toString()
                  requestBodyForSoRequestCreate.DocDueDate =  Globals.getTodaysDatervrsfrmt().toString()
                  requestBodyForSoRequestCreate.DiscountPercent = Prefs.getString( Globals.DISCOUNT_PERCENT)
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
                  requestBodyForSoRequestCreate.CardName = PrefsByShubh.getCardName()!! //sessionManagement.getCardName()!!
                  requestBodyForSoRequestCreate.CardCode = PrefsByShubh.getCardCode()!! //sessionManagement.getCardCode()!!
                  requestBodyForSoRequestCreate.CreatedBy = Prefs.getString( Globals.SALES_EMPLOYEE_CODE)!!
                  requestBodyForSoRequestCreate.SalesPersonCode =
                        Prefs.getString( Globals.SALES_EMPLOYEE_CODE)
                  //requestBodyForSoRequestCreate.DocTotal = binding.tvTotalInvoiceAmount.text.toString()
                  requestBodyForSoRequestCreate.DocTotal = "${getvalueForGstcalculation + gstAmount}"
                  requestBodyForSoRequestCreate.DocumentLines = AppConstants.cartListForOrderRequest*/

                  /*requestForSoCreate.DocumentStatus = "bost_Open"  //comment by vinod
                  requestForSoCreate.ApprovalStatus = "Pending"
                  requestForSoCreate.CancelStatus = "csNo"
                  requestForSoCreate.Unit = 1
                  requestForSoCreate.PayTermsGrpCode =
                        Prefs.getString(Globals.PAYMENT_GROUP_CODE)
                  requestForSoCreate.DocCurrency = businessPartnerDetails?.data?.get(0)?.Currency ?: ""// Prefs.getString(Globals.CURRENCY)
                  requestForSoCreate.Series = 90
                  requestForSoCreate.CreateTime = Globals.getfullformatCurrentTime()
                  requestForSoCreate.TaxDate = Globals.getTodaysDatervrsfrmt().toString()
                  requestForSoCreate.CreationDate = Globals.getTodaysDatervrsfrmt().toString()
                  requestForSoCreate.CreateDate = Globals.getTodaysDatervrsfrmt().toString()
                  requestForSoCreate.DocDate = Globals.getTodaysDatervrsfrmt().toString()
                  requestForSoCreate.DocDueDate = Globals.getTodaysDatervrsfrmt().toString()
                  requestForSoCreate.DiscountPercent = Prefs.getString(Globals.DISCOUNT_PERCENT)
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
                  requestForSoCreate.CardName = PrefsByShubh.getCardName()!! //sessionManagement.getCardName()!!
                  requestForSoCreate.CardCode = PrefsByShubh.getCardCode()!! //sessionManagement.getCardCode()!!
                  requestForSoCreate.CreatedBy = Prefs.getString(Globals.SALES_EMPLOYEE_CODE)!!
                  requestForSoCreate.SalesPersonCode = businessPartnerDetails?.data?.get(0)?.SalesPersonCode?.get(0)?.SalesEmployeeCode.toString()

                  //requestForSoCreate.DocTotal = binding.tvTotalInvoiceAmount.text.toString()
                  requestForSoCreate.DocTotal = "${getvalueForGstcalculation + gstAmount}"

                  requestForSoCreate.DocumentLines = AppConstants.cartListForOrderRequest*/  //comment by vinod

                  requestOrderCreate.CreatedByPerson = PrefsByShubh.getSalesEmployeeCode()!!.toInt()
                  requestOrderCreate.isDraft = 0
                  requestOrderCreate.id = ""

                  val priceText = binding.tvTotalInvoiceAmt.text.toString()
                  val numericValue = priceText.replace(Regex("[^0-9.]"), "").toDoubleOrNull() ?: 0.0
                  requestOrderCreate.DocTotal = numericValue

                  requestOrderCreate.TaxDate = Globals.getTodaysDatervrsfrmt().toString()
                  requestOrderCreate.DocDueDate = Globals.getTodaysDatervrsfrmt().toString()
                  requestOrderCreate.UQuotnm = ""
                  requestOrderCreate.UQuotid = 0
                  requestOrderCreate.UOppid = 0
                  requestOrderCreate.UOpprnm = ""
                  requestOrderCreate.departement = 2
                  requestOrderCreate.PRID = ""
                  requestOrderCreate.BPLID = 5
                  requestOrderCreate.QuotationID = 0
                  requestOrderCreate.FreightCharge = 0
                  businessPartnerDetails?.data?.getOrNull(0)?.let { dataItem ->
                        if (!dataItem.PayTermsGrpCode.isNullOrEmpty()) {
                              requestOrderCreate.PaymentGroupCode = dataItem.PayTermsGrpCode[0].GroupNumber.toInt()
                        } else {
                              Log.e("CartActivity", "PayTermsGrpCode list is empty")
//                              Toast.makeText(this, "Payment terms not available", Toast.LENGTH_SHORT).show()
                        }
                  }

                  requestOrderCreate.PODate = Globals.getTodaysDatervrsfrmt().toString()
                  requestOrderCreate.PONumber = ""
                  businessPartnerDetails?.data?.get(0)?.let { requestOrderCreate.ContactPersonCode = it.ContactEmployees[0].InternalCode.toInt() }
                  businessPartnerDetails?.data?.get(0)?.let { requestOrderCreate.DiscountPercent = if(it.DiscountPercent.isNotEmpty()) it.DiscountPercent.toDouble() else 0.0 }
                  requestOrderCreate.DocDate = Globals.getTodaysDatervrsfrmt().toString()
                  requestOrderCreate.CardCode = PrefsByShubh.getCardCode().toString()
                  requestOrderCreate.CardName = PrefsByShubh.getCardName().toString()
                  requestOrderCreate.Comments = ""
                  requestOrderCreate.SalesPersonCode = PrefsByShubh.getSalesEmployeeCode()!!.toInt()
                  //requestOrderCreate.AddressExtension: AddressExtension = AddressExtension()
                  //requestOrderCreate.DocumentLines: MutableList<LocalDataForCart> = mutableListOf()
                  requestOrderCreate.CreateDate = Globals.getTodaysDatervrsfrmt().toString()
                  requestOrderCreate.CreateTime = Globals.getfullformatCurrentTime()
                  requestOrderCreate.UpdateDate = Globals.getTodaysDatervrsfrmt().toString()
                  requestOrderCreate.UpdateTime = Globals.getfullformatCurrentTime()
                  requestOrderCreate.DocumentLines = AppConstants.cartListForOrderRequest

                  viewModel.createSoRequest(requestOrderCreate, this)
                  subscribeToObserver()

                  Log.i("PLACE_ORDER_REQUEST", "Place Order Request Json: \n${toPrettyJson(requestOrderCreate)}")
            }

      }

      private fun requestDataForSoCreate(businessPartnerDetails: ResponseBpOne?) {
            businessPartnerDetails?.data?.get(0)?.apply {
                  if (BPAddresses.isNotEmpty()){
                        requestOrderCreate.AddressExtension.id = BPAddresses[0].id
                        requestOrderCreate.AddressExtension.QuotationID = "0"
                        requestOrderCreate.AddressExtension.ShipToId = BPAddresses[1].id.toString()
                        requestOrderCreate.AddressExtension.ShipToStreet = BPAddresses[1].Street
                        requestOrderCreate.AddressExtension.ShipToBlock = BPAddresses[1].Block
                        requestOrderCreate.AddressExtension.ShipToBuilding = BPAddresses[1].AddressName
                        requestOrderCreate.AddressExtension.ShipToCity = BPAddresses[1].City
                        requestOrderCreate.AddressExtension.ShipToZipCode = BPAddresses[1].ZipCode
                        requestOrderCreate.AddressExtension.ShipToCounty = BPAddresses[1].County
                        requestOrderCreate.AddressExtension.ShipToState = BPAddresses[1].State
                        requestOrderCreate.AddressExtension.ShipToCountry = BPAddresses[1].Country
                        requestOrderCreate.AddressExtension.ShipToAddress2 = ""
                        requestOrderCreate.AddressExtension.UScountry = BPAddresses[1].Country
                        requestOrderCreate.AddressExtension.UShptyps = BPAddresses[1].U_SHPTYP
                        requestOrderCreate.AddressExtension.USstate = BPAddresses[1].U_STATE
                        requestOrderCreate.AddressExtension.BillToId = ""
                        requestOrderCreate.AddressExtension.BillToStreet = BPAddresses[0].Street
                        requestOrderCreate.AddressExtension.BillToBlock = BPAddresses[0].Block
                        requestOrderCreate.AddressExtension.BillToBuilding = BPAddresses[0].AddressName
                        requestOrderCreate.AddressExtension.BillToCity = BPAddresses[0].City
                        requestOrderCreate.AddressExtension.BillToZipCode = BPAddresses[0].ZipCode
                        requestOrderCreate.AddressExtension.BillToCounty = BPAddresses[0].County
                        requestOrderCreate.AddressExtension.BillToState = BPAddresses[0].State
                        requestOrderCreate.AddressExtension.BillToCountry = BPAddresses[0].Country
                        requestOrderCreate.AddressExtension.BillToAddress2 = ""
                        requestOrderCreate.AddressExtension.PlaceOfSupply = ""
                        requestOrderCreate.AddressExtension.UBcountry = BPAddresses[0].U_COUNTRY
                        requestOrderCreate.AddressExtension.UShptypb = BPAddresses[0].U_SHPTYP
                        requestOrderCreate.AddressExtension.UBstate = BPAddresses[0].U_STATE
                        requestOrderCreate.AddressExtension.GSTIN = ""
                  }
                  /*if (BPAddresses.isNotEmpty()){
                        requestOrderCreate.AddressExtension.id = BPAddresses[0].id
                        requestForSoCreate.AddressExtension.BillToCountry = "IN"
                        requestForSoCreate.AddressExtension.PlaceOfSupply = BPAddresses[0].State
                        requestForSoCreate.AddressExtension.BillToDistrict =BPAddresses[0].District
                        requestForSoCreate.AddressExtension.BillToCity = BPAddresses[0].City
                        requestForSoCreate.AddressExtension.BillToZipCode =  BPAddresses[0].ZipCode
                        requestForSoCreate.AddressExtension.BillToBuilding = BPAddresses[0].BuildingFloorRoom
                        requestForSoCreate.AddressExtension.BillToState =  BPAddresses[0].State
                        requestForSoCreate.AddressExtension.BillToStreet = BPAddresses[0].Street
                  }
                  requestForSoCreate.AddressExtension.ShipToCountry = "IN"
                  requestForSoCreate.AddressExtension.ShipToDistrict =  get(1).District
                  requestForSoCreate.AddressExtension.ShipToCity =  get(1).City
                  requestForSoCreate.AddressExtension.ShipToZipCode =  get(1).ZipCode
                  requestForSoCreate.AddressExtension.ShipToBuilding =  get(1).BuildingFloorRoom
                  requestForSoCreate.AddressExtension.ShipToState =  get(1).State
                  requestForSoCreate.AddressExtension.ShipToStreet =  get(1).Street
                  requestForSoCreate.AddressExtension.U_SCOUNTRY =  get(0).U_COUNTRY
                  requestForSoCreate.AddressExtension.U_SSTATE =  get(0).U_STATE
                  requestForSoCreate.AddressExtension.U_SHPTYPS =  get(0).U_SHPTYP
                  requestForSoCreate.AddressExtension.U_BCOUNTRY =  get(1).U_COUNTRY
                  requestForSoCreate.AddressExtension.U_BSTATE =  get(1).State
                  requestForSoCreate.AddressExtension.U_SHPTYPB =  get(1).U_SHPTYP */
            }
            
      }

      /* private fun bindBpOneObserver() {
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

                               requestForSoCreate.AddressExtension.BillToCountry = "IN"
                               requestForSoCreate.AddressExtension.PlaceOfSupply = BPAddresses[0].State
                               requestForSoCreate.AddressExtension.BillToDistrict = BPAddresses[0].District
                               requestForSoCreate.AddressExtension.BillToCity = BPAddresses[0].City
                               requestForSoCreate.AddressExtension.BillToZipCode = BPAddresses[0].ZipCode
                               requestForSoCreate.AddressExtension.BillToBuilding = BPAddresses[0].BuildingFloorRoom
                               requestForSoCreate.AddressExtension.BillToState = BPAddresses[0].State
                               requestForSoCreate.AddressExtension.BillToStreet = BPAddresses[0].Street

                               requestForSoCreate.AddressExtension.ShipToCountry = "IN"
                               requestForSoCreate.AddressExtension.ShipToDistrict = BPAddresses[1].District
                               requestForSoCreate.AddressExtension.ShipToCity = BPAddresses[1].City
                               requestForSoCreate.AddressExtension.ShipToZipCode = BPAddresses[1].ZipCode
                               requestForSoCreate.AddressExtension.ShipToBuilding = BPAddresses[1].BuildingFloorRoom
                               requestForSoCreate.AddressExtension.ShipToState = BPAddresses[1].State
                               requestForSoCreate.AddressExtension.ShipToStreet = BPAddresses[1].Street
                               requestForSoCreate.AddressExtension.U_SCOUNTRY = BPAddresses[0].U_COUNTRY
                               requestForSoCreate.AddressExtension.U_SSTATE = BPAddresses[0].U_STATE
                               requestForSoCreate.AddressExtension.U_SHPTYPS = BPAddresses[0].U_SHPTYP
                               requestForSoCreate.AddressExtension.U_BCOUNTRY = BPAddresses[1].U_COUNTRY
                               requestForSoCreate.AddressExtension.U_BSTATE = BPAddresses[1].State
                               requestForSoCreate.AddressExtension.U_SHPTYPB = BPAddresses[1].U_SHPTYP
                               Log.i("SO_REQUEST", "Request For Order: " + requestForSoCreate.toString())
                         }

                   } else if (response.status == 201) {
                         Globals.warningMessage(this, response.message)
                   } else if (response.status == 401) {
                         //sessionManagement.ClearSession()
                         PrefsByShubh.ClearSession()
                         Globals.logoutScreen(this)

                   }


             }))
       }*/

      private fun subscribeToObserver() {
            viewModel.createSoRequest.observe(this, Event.EventObserver(onError = {
                  alertDialog.dismiss()
                   Globals.warningMessage(this, it)
            }, onLoading = {
                  alertDialog.show()
            }, { response ->
                  alertDialog.dismiss()
                  if (response.status == 200) {
                        AppConstants.cartListForOrderRequest.clear()
                        AppConstants.saveCartListToPreferences(this, mutableListOf())
                         Globals.successMessage(this, "Order Requested SuccessFully")
                        finish()


                  } else if (response.status == 201) {
                         Globals.warningMessage(this, response.message)
                  } else if (response.status == 401) {
                        //sessionManagement.ClearSession()
                  //      PrefsByShubh.ClearSession()
                         Globals.logoutScreen(this)

                  }


            }))
      }

      private fun setUprecyclerview() = binding.rvDispatchOrder.apply {
            adapter = cartAdapter
            layoutManager = LinearLayoutManager(this@CartActivity)

      }


      /* private fun bindObserverProfile() {
             viewModel.distributorProfileData.observe(this, Event.EventObserver(
                   onError = {
                         // Log.e(TAG, "bindRemarkObserver: $it")
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
                               //sessionManagement.ClearSession()
                               PrefsByShubh.ClearSession()
                                Globals.logoutScreen(this)

                         } else {
                                Globals.warningMessage(this, response.message)
                         }
                   }
             ))
       }*/
}