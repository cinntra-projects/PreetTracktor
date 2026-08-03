package com.preetTractor.galaxyAndroid.ui.orderUi.activity

import Event
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.preetTractor.galaxyAndroid.R
import com.preetTractor.galaxyAndroid.apiHelper.ApiClient
import com.preetTractor.galaxyAndroid.apiHelper.ApisInterface
import com.preetTractor.galaxyAndroid.apiHelper.AppConstants
import com.preetTractor.galaxyAndroid.apiHelper.AppConstants.getBaCartListFromPreferences
import com.preetTractor.galaxyAndroid.data.ResponseCategoryAllList
import com.preetTractor.galaxyAndroid.data.model.order.model.local.CartManager
import com.preetTractor.galaxyAndroid.data.model.order.model.request.ModelBACreateOrderRequest
import com.preetTractor.galaxyAndroid.data.model.order.model.response.ResponseBpOne
import com.preetTractor.galaxyAndroid.data.model.order.model.response.ba.ModelBpListStatic
import com.preetTractor.galaxyAndroid.data.model.order.model.response.ba.ModelBusinessPartnerAll
import com.preetTractor.galaxyAndroid.data.model.order.model.response.ba.ModelItemAllByCategory
import com.preetTractor.galaxyAndroid.databinding.ActivityAddOrderBinding
import com.preetTractor.galaxyAndroid.helper.APiPayloadKeys
import com.preetTractor.galaxyAndroid.helper.Globals
import com.preetTractor.galaxyAndroid.helper.Globals.calculateAndSetTotals
import com.preetTractor.galaxyAndroid.helper.Globals.calculateTotalAmount
import com.preetTractor.galaxyAndroid.helper.Globals.toPrettyJson
import com.preetTractor.galaxyAndroid.helper.PrefsByShubh
import com.preetTractor.galaxyAndroid.helper.PrefsByShubh.businessPartnerDetails
import com.preetTractor.galaxyAndroid.mvvmSetUp.DefaultMainRepositories
import com.preetTractor.galaxyAndroid.mvvmSetUp.MainRepos
import com.preetTractor.galaxyAndroid.mvvmSetUp.MainViewModel
import com.preetTractor.galaxyAndroid.mvvmSetUp.MainViewModelProvider
import com.preetTractor.galaxyAndroid.retrofit.RetrofitClient
import com.preetTractor.galaxyAndroid.ui.orderUi.adapter.ItemInBaOrderAdapter
import com.google.gson.JsonObject
import com.pixplicity.easyprefs.library.Prefs
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddOrderActivity : AppCompatActivity(), ItemInBaOrderAdapter.OnQuantityChangeListener {
      private lateinit var binding: ActivityAddOrderBinding
      private lateinit var mContext: Context
      private lateinit var itemAdapter: ItemInBaOrderAdapter
      private lateinit var viewModel: MainViewModel
      private var isVisible:Boolean = false
      var requestOrderCreate = ModelBACreateOrderRequest()
      var basePrice: Double = 0.0
      var dealerDiscount: Double = 0.0
      var specialDiscount: Double = 0.0
      var additionalDiscount: Double = 0.0
      var gstAmount: Double = 0.0
      var tradeAmount: Double = 0.0
      var invoiceAmount: Double = 0.0
      var getvalueForGstcalculation: Double = 0.0


      private lateinit var bpList: List<ModelBusinessPartnerAll.Data>
      private lateinit var catList: List<ResponseCategoryAllList.CategoryAllListData>

      private var selectedCardName: String? = null
      private var selectedCardCode: String? = null

      private var selectedCategoryName: String? = null
      private var selectedCategoryId: String? = null

      lateinit var builder: AlertDialog.Builder
      lateinit var alertDialog: AlertDialog


      private fun SetUPDialog(context: Context) {
            builder = AlertDialog.Builder(context)
            builder.setView(R.layout.progress_dialog).setCancelable(false)
            alertDialog = builder!!.create()
      }

      private fun setUpViewModel() {
            val dispatchers: CoroutineDispatcher = Dispatchers.Main
            val mainRepos = DefaultMainRepositories() as MainRepos
            val fanxApi: ApisInterface = ApiClient().service(this)
            val viewModelProviderfactory =
                  MainViewModelProvider(Application(), mainRepos, dispatchers, fanxApi)
            viewModel = ViewModelProvider(this, viewModelProviderfactory)[MainViewModel::class.java]

      }

      override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            binding = ActivityAddOrderBinding.inflate(layoutInflater)
            mContext = this
            initViews()
            clickListener()
            setContentView(binding.root)

      }

      private fun initViews() {
            setUpViewModel()
            SetUPDialog(this)
            if(getBaCartListFromPreferences().isNotEmpty()){
                  for (currentItem in getBaCartListFromPreferences()) {
                        basePrice += Globals.calculateBasePriceWithQuantityAndSPQ(currentItem)
                        /*if (currentItem.PriceType.equals("MRPRATE")) {
                              dealerDiscount +=  Globals.calculateDealerDiscount(currentItem)
                              specialDiscount +=  Globals.calculateSpecialDiscount(currentItem)

                        } else if (currentItem.PriceType.equals("FLATRATE")) {
                              additionalDiscount +=  Globals.calculateAdditionalDiscount(currentItem)
                        }*/
                  }
                  basePrice = basePrice - dealerDiscount - specialDiscount - additionalDiscount
            }else{
                  basePrice = 0.0
                  dealerDiscount = 0.0
                  specialDiscount = 0.0
                  additionalDiscount = 0.0
            }
            /*if (AppConstants.cartListForBaOrderRequest.isNotEmpty()) {
                  for (currentItem in AppConstants.cartListForBaOrderRequest) {
                        basePrice += Globals.calculateBasePriceWithQuantityAndSPQ(currentItem)
                        *//*if (currentItem.PriceType.equals("MRPRATE")) {
                              dealerDiscount +=  Globals.calculateDealerDiscount(currentItem)
                              specialDiscount +=  Globals.calculateSpecialDiscount(currentItem)

                        } else if (currentItem.PriceType.equals("FLATRATE")) {
                              additionalDiscount +=  Globals.calculateAdditionalDiscount(currentItem)
                        }*//*
                  }
                  basePrice = basePrice - dealerDiscount - specialDiscount - additionalDiscount
            } else {
                  basePrice = 0.0
                  dealerDiscount = 0.0
                  specialDiscount = 0.0
                  additionalDiscount = 0.0
            }*/
            //callBpListAllFilterApi(PrefsByShubh.getSalesEmployeeCode().toString())
            callBpBaListApi()
            //callBpOneApi("C1")
            callCategoryListApi()
            //callItemByCategoryApi("1")
            //setCustomerSpinner()

      }

      private fun callBpBaListApi() {
            val call = RetrofitClient.apiService.getBpBaList()
            call.enqueue(object : Callback<ModelBpListStatic> {
                  override fun onResponse(
                        call: Call<ModelBpListStatic>,
                        response: Response<ModelBpListStatic>
                  ) {

                        response.body()?.let {
                              if (it.status == 200) {
                                    selectedCardCode = response.body()?.data?.get(0)?.CardCode
                                    //selectedCardName= response.body()?.data?.get(0)?.CardName
                                    callBpOneApi(selectedCardCode.toString())

                              } else if (it.status == 201) {
                                    Globals.warningMessage(this@AddOrderActivity, it.message)
                              }
                        }
                  }

                  override fun onFailure(call: Call<ModelBpListStatic>, t: Throwable) {
                        Toast.makeText(this@AddOrderActivity, "Something went wrong", Toast.LENGTH_SHORT)
                              .show()
                  }
            })
      }

      private fun callBpOneApi(cardCode: String) {
            viewModel.bPOneApi(JsonObject().apply { addProperty(APiPayloadKeys.CardCode, cardCode) }, this)

            bindBpOneObserver()
      }

      private fun callBpListAllFilterApi(salesEmployeeCode: String) {
            val jsonObj = JsonObject().apply {
                  addProperty("SalesEmployeeCode", salesEmployeeCode)
            }
            viewModel.getBpListALlFilter(jsonObj, this)
            bindBpAllListObserver()
      }

      private fun bindBpOneObserver() {
            viewModel.bPOneDetailData.observe(this, Event.EventObserver(onError = {
                  Globals.warningMessage(this, it)
            }, onLoading = {

            }, { response ->

                  if (response.status == 200) {
                        //todo set dealer, special and additional discount
                        if (response.data.isNotEmpty()) {
                              response.data[0].apply {

                                    businessPartnerDetails = response
                                    requestAddressExtentionForOrderCreate(businessPartnerDetails)
                                    Log.i("BP_DETAILS", "$businessPartnerDetails")

                                    /* binding.apply {
                                           etEmailId.setText(response.data[0].EmailAddress)
                                           etContactNumber.setText(response.data[0].Phone1)
                                     }*/

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
                                          Globals.HEADER_DISCOUNT_PERCENT,
                                          DiscountPercent
                                    )

                                    Prefs.putString(
                                          Globals.FREIGHT_CHARGES_PERCENT,
                                          "0"
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
                        }


                  } else if (response.status == 201) {
                        Globals.warningMessage(this, response.message)
                  } else if (response.status == 401) {
                        //sessionManagement.ClearSession()
                      //  PrefsByShubh.ClearSession()
                        Globals.logoutScreen(this)

                  }
            }))
      }

      private fun requestAddressExtentionForOrderCreate(businessPartnerDetails: ResponseBpOne?) {
            businessPartnerDetails?.data?.get(0)?.apply {
                  if (BPAddresses.isNotEmpty()) {
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

                        //Log.i("BA_ORDER_CREATE", "AddressExtension: \n${toPrettyJson(requestOrderCreate.AddressExtension)}")
                  }
            }

      }

      private fun bindBpAllListObserver() {
            viewModel.bpListAllFilter.observe(
                  this,
                  Event.EventObserver(onError = {
                        //binding.spinKitLoader.visibility = View.GONE
                        Globals.warningMessage(this, it)
                  }, onLoading = {
                        //binding.spinKitLoader.visibility = View.VISIBLE
                  }, { response ->
                        if (response.status == 200) {
                              //binding.spinKitLoader.visibility = View.VISIBLE
                              if (response.data.isNotEmpty()) {
                                    response.data.let {
                                          bpList = it
                                    }

                                    val tempList: ArrayList<String> = ArrayList()
                                    for (item in bpList) {
                                          tempList.add(item.CardName)
                                    }

                                    if (tempList.isNotEmpty()) {
                                          val adapter: ArrayAdapter<String> = ArrayAdapter(this@AddOrderActivity, R.layout.drop_down_item_textview, tempList)
                                          binding.acCustomerAll.setAdapter(adapter)

                                    }

                                    binding.acCustomerAll.setOnItemClickListener { adapterView, view, pos, l ->
                                          //binding.addTicket.acSpinnerCustomer.setText(tempList[pos])
                                          binding.acCustomerAll.showDropDown()
                                          //selectedCardName = bpList[pos].CardName
                                          //selectedCardCode = bpList[pos].CardCode
                                          Log.i("FormData", "Selected BP: $selectedCardName  (Id: $selectedCardCode)")
                                          //callBpOneApi(selectedCardCode.toString())
                                    }

                              }


                        } else if (response.status == 401) {
                              //sessionManagement.ClearSession()
                       //       PrefsByShubh.ClearSession()
                              Globals.logoutScreen(this)

                        } else {
                              Globals.warningMessage(this, response.message)
                        }
                  })
            )
      }

      private fun callItemByCategoryApi(catId: String) {
            viewModel.getAllItemListByCategory(JsonObject().apply { addProperty("CatID", catId) }, this)
            bindItemListObserver()
      }

      private fun bindItemListObserver() {
            viewModel.allItemByCategory.observe(
                  this,
                  Event.EventObserver(onError = {
                        Globals.warningMessage(this, it)
                  }, onLoading = {

                  }, { response ->
                        if (response.status == 200) {
                              binding.apply {
                                    if (response.data.isNotEmpty()) {
                                          rvItemOrder.visibility = View.VISIBLE
                                          ivNoDataFound.visibility = View.GONE
                                          setItemsAdapter(response.data)
                                    } else {
                                          rvItemOrder.visibility = View.GONE
                                          ivNoDataFound.visibility = View.VISIBLE
                                    }
                              }


                        } else if (response.status == 401) {
                              //sessionManagement.ClearSession()
                          //    PrefsByShubh.ClearSession()
                              Globals.logoutScreen(this)

                        } else {
                              Globals.warningMessage(this, response.message)
                        }
                  })
            )
      }

      private fun callCategoryListApi() {
            viewModel.getItemAllCategoryListALlFilter(this)
            bindItemCategoryListObserver()
      }

      private fun bindItemCategoryListObserver() {
            viewModel.itemCategoryListAllFilter.observe(
                  this,
                  Event.EventObserver(onError = {
                        Globals.warningMessage(this, it)
                  }, onLoading = {

                  }, { response ->
                        if (response.status == 200) {

                              if (response.data.isNotEmpty()) {
                                    response.data.let {
                                          catList = it
                                    }

                                    val tempList: ArrayList<String> = ArrayList()
                                    for (item in catList) {
                                          tempList.add(item.CategoryName)
                                    }

                                    if (tempList.isNotEmpty()) {
                                          val adapter: ArrayAdapter<String> = ArrayAdapter(this@AddOrderActivity, R.layout.drop_down_item_textview, tempList)
                                          binding.acCategoryAll.setAdapter(adapter)

                                    }

                                    binding.acCategoryAll.setOnItemClickListener { adapterView, view, pos, l ->
                                          binding.acCategoryAll.showDropDown()
                                          selectedCategoryName = catList[pos].CategoryName
                                          selectedCategoryId = catList[pos].id.toString()
                                          Log.i("FormData", "Selected Category: $selectedCategoryName  (Id: $selectedCategoryId)")
                                          binding.layoutItems.visibility = if (selectedCategoryId?.isNotEmpty() == true) View.VISIBLE else View.GONE
                                          callItemByCategoryApi(selectedCategoryId.toString())
                                    }
                              }

                        } else if (response.status == 401) {
                              //sessionManagement.ClearSession()
                       //       PrefsByShubh.ClearSession()
                              Globals.logoutScreen(this)

                        } else {
                              Globals.warningMessage(this, response.message)
                        }
                  })
            )
      }

      private fun setItemsAdapter(data: List<ModelItemAllByCategory.Data>) {
            binding.rvItemOrder.run {
                  layoutManager = LinearLayoutManager(this@AddOrderActivity, LinearLayoutManager.VERTICAL, false)
                  itemAdapter = ItemInBaOrderAdapter(this@AddOrderActivity, data,this@AddOrderActivity)
                  adapter = itemAdapter
                  itemAdapter.notifyDataSetChanged()
            }
      }

      private fun clickListener() {
            binding.apply {
                  ivBackPress.setOnClickListener {
                        finish()
                        CartManager.instance.getCartItems().clear()
                  }

                  ivShowHideArrow.setOnClickListener {
                        isVisible = !isVisible // Toggle the state

                        if (isVisible) {
                              layoutShowHide.visibility = View.VISIBLE
                              ivShowHideArrow.setImageResource(R.drawable.ic_expand) // Change icon
                        } else {
                              layoutShowHide.visibility = View.GONE
                              ivShowHideArrow.setImageResource(R.drawable.ic_collapse) // Change icon
                        }
                  }

                  tvBtnPlaceOrder.setOnClickListener {
                        val docTotal = calculateTotalAmount(CartManager.instance.getCartItems(), Prefs.getString(Globals.FREIGHT_CHARGES_PERCENT).toDouble())
                        requestOrderCreate.CreatedByPerson = PrefsByShubh.getSalesEmployeeCode()?.toInt()!!
                        requestOrderCreate.isDraft = 0
                        requestOrderCreate.id = ""
                        requestOrderCreate.DocTotal = docTotal
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
                        businessPartnerDetails?.data?.get(0)?.let { requestOrderCreate.PaymentGroupCode = it.PayTermsGrpCode[0].GroupNumber.toInt() }
                        requestOrderCreate.PODate = Globals.getTodaysDatervrsfrmt().toString()
                        requestOrderCreate.PONumber = ""
                        businessPartnerDetails?.data?.get(0)?.let { requestOrderCreate.ContactPersonCode = it.ContactEmployees[0].InternalCode.toInt() }
                        businessPartnerDetails?.data?.get(0)?.let { requestOrderCreate.DiscountPercent = if (it.DiscountPercent.isNotEmpty()) it.DiscountPercent.toDouble() else 0.0 }
                        requestOrderCreate.DocDate = Globals.getTodaysDatervrsfrmt().toString()
                        requestOrderCreate.CardCode = selectedCardCode.toString() //PrefsByShubh.getCardCode().toString()
                        requestOrderCreate.CardName = binding.etCustomerName.text.toString().trim() //PrefsByShubh.getCardName().toString()
                        requestOrderCreate.C_Mobile = binding.etContactNumber.text.toString().trim()
                        requestOrderCreate.C_Email = binding.etEmailId.text.toString().trim()
                        requestOrderCreate.Comments = ""
                        requestOrderCreate.SalesPersonCode = PrefsByShubh.getSalesEmployeeCode()?.toInt()!!
                        //requestOrderCreate.AddressExtension: AddressExtension = AddressExtension()
                        //requestOrderCreate.DocumentLines: MutableList<LocalDataForCart> = mutableListOf()
                        requestOrderCreate.CreateDate = Globals.getTodaysDatervrsfrmt().toString()
                        requestOrderCreate.CreateTime = Globals.getfullformatCurrentTime()
                        requestOrderCreate.UpdateDate = Globals.getTodaysDatervrsfrmt().toString()
                        requestOrderCreate.UpdateTime = Globals.getfullformatCurrentTime()
                        requestOrderCreate.DocumentLines = CartManager.instance.getCartItems()

                        Log.i("BA_ORDER_CREATE", "Cart: DocTotal = $docTotal\n${toPrettyJson(requestOrderCreate)}")

                        viewModel.createBAOrderRequest(requestOrderCreate, this@AddOrderActivity)
                        subscribeToBAOrderObserver()
                  }

            }
      }

      override fun onBackPressed() {
            super.onBackPressed()
            CartManager.instance.getCartItems().clear()
      }

      private fun subscribeToBAOrderObserver() {
            viewModel.createBAOrderRequest.observe(this, Event.EventObserver(onError = {
                  alertDialog.dismiss()
                  Globals.warningMessage(this, it)
            }, onLoading = {
                  alertDialog.show()
            }, { response ->
                  alertDialog.dismiss()
                  when (response.status) {
                        200 -> {
                              AppConstants.saveBaCartListToPreferences(mutableListOf())
                              CartManager.instance.getCartItems().clear()
                              Globals.successMessage(this, "Order Requested SuccessFully")
                              finish()

                        }

                        201 -> {
                              Globals.warningMessage(this, response.message)
                        }

                        401 -> {
                              //sessionManagement.ClearSession()
                      //        PrefsByShubh.ClearSession()
                              Globals.logoutScreen(this)

                        }
                  }


            }))
      }

      override fun onPlusClicked(item: ModelItemAllByCategory.Data, newQuantity: Int) {
            binding.cvCartDetails.visibility = if (CartManager.instance.getCartItems().isNotEmpty()) View.VISIBLE else View.GONE
            calculateAndSetTotals(
                  CartManager.instance.getCartItems(),
                  freightCharge = 0.00, // Example freight charge
                  tvBasicAmount = binding.tvTotalBillingAmount,
                  tvItemDiscount = binding.tvItemDiscAmount,
                  tvHeaderDiscount = binding.tvHeaderDescAmount,
                  tvTaxAmount = binding.tvTaxRateAmount,
                  tvFreightCharge = binding.tvFreightChargeAmount,
                  tvGrandTotal = binding.tvTotalInvoiceAmount
            )
      }

      override fun onMinusClicked(item: ModelItemAllByCategory.Data, newQuantity: Int) {
            binding.cvCartDetails.visibility = if (CartManager.instance.getCartItems().isNotEmpty()) View.VISIBLE else View.GONE
            calculateAndSetTotals(
                  CartManager.instance.getCartItems(),
                  freightCharge = 0.00, // Example freight charge
                  tvBasicAmount = binding.tvTotalBillingAmount,
                  tvItemDiscount = binding.tvItemDiscAmount,
                  tvHeaderDiscount = binding.tvHeaderDescAmount,
                  tvTaxAmount = binding.tvTaxRateAmount,
                  tvFreightCharge = binding.tvFreightChargeAmount,
                  tvGrandTotal = binding.tvTotalInvoiceAmount
            )
      }


}