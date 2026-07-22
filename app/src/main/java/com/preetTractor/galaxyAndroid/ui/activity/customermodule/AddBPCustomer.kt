package com.preetTractor.galaxyAndroid.ui.activity.customermodule

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.pixplicity.easyprefs.library.Prefs
import com.preetTractor.galaxyAndroid.R
import com.preetTractor.galaxyAndroid.data.FieldFilter
import com.preetTractor.galaxyAndroid.data.FilterOverAll
import com.preetTractor.galaxyAndroid.data.LeadValue
import com.preetTractor.galaxyAndroid.data.model.CityData
import com.preetTractor.galaxyAndroid.data.model.CityResponse
import com.preetTractor.galaxyAndroid.databinding.FragmentAddPartner2Binding
import com.preetTractor.galaxyAndroid.helper.Globals
import com.preetTractor.galaxyAndroid.helper.Globals.showMessage
import com.preetTractor.galaxyAndroid.helper.PrefsByShubh
import com.preetTractor.galaxyAndroid.retrofit.RetrofitClient
import com.preetTractor.galaxyAndroid.ui.activity.BaseActivity
import com.preetTractor.galaxyAndroid.ui.activity.customermodule.adapter.CityAdapter
import com.preetTractor.galaxyAndroid.ui.activity.customermodule.adapter.StateAdapter
import com.preetTractor.galaxyAndroid.ui.activity.customermodule.model.CountryData
import com.preetTractor.galaxyAndroid.ui.activity.customermodule.model.CountryResponse
import com.preetTractor.galaxyAndroid.ui.activity.customermodule.model.CustomerBusinessRes
import com.preetTractor.galaxyAndroid.ui.activity.customermodule.model.DataBusinessType
import com.preetTractor.galaxyAndroid.ui.activity.customermodule.model.DataDropDownZone
import com.preetTractor.galaxyAndroid.ui.activity.customermodule.model.IndustryItem
import com.preetTractor.galaxyAndroid.ui.activity.customermodule.model.IndustryResponse
import com.preetTractor.galaxyAndroid.ui.activity.customermodule.model.PayMentTerm
import com.preetTractor.galaxyAndroid.ui.activity.customermodule.model.PayMentTermsDetail
import com.preetTractor.galaxyAndroid.ui.activity.customermodule.model.PerformaInvoiceListRequestModel
import com.preetTractor.galaxyAndroid.ui.activity.customermodule.model.ResponseBusinessType
import com.preetTractor.galaxyAndroid.ui.activity.customermodule.model.ResponseZoneDropDown
import com.preetTractor.galaxyAndroid.ui.activity.customermodule.model.SalesEmployee
import com.preetTractor.galaxyAndroid.ui.activity.customermodule.model.SalesEmployeeItemKt
import com.preetTractor.galaxyAndroid.ui.activity.customermodule.model.StateData
import com.preetTractor.galaxyAndroid.ui.activity.customermodule.model.StateRespose
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException


class AddBPCustomer : BaseActivity(), View.OnClickListener {

    private val TAG = "AddBPCustomer"
    private var TYPE = ""
    private var U_LeadNM = ""
    private var industryCode: String? = null
    private var shippingType: String? = null
    private var currencyCode = ""
    private var salesEmployeeCode = 0
    private var salesEmployeeName = ""

    // private val leadValueList: MutableList<LeadValue> = ArrayList()
    private var LeadID = "0"
    private var payment_term = ""
    private var parenT_account = ""
    private var zoneSelected = ""
    private var businessTypeSelected = ""
    private lateinit var shippinngType: Array<String>
    private var billshipType: String? = null
    private var ship_shiptype: String? = null
    private lateinit var act: AppCompatActivity
    private var billtoState: String? = null
    private var billtoStateCode: String? = null
    private var billtoCountrycode: String? = null
    private var billtoCountryName: String? = null
    private var billtoCityCode : String? = null
    private var shiptoState: String? = null
    private var shiptoCountrycode: String? = null
    private var shiptoCountryName: String? = null
    private var shiptoStateCode: String? = null
    private var shiptoCityCode : String? = null
    private var billtoCityId : Int = -1
    private var billtoStateId : Int = -1

    private lateinit var binding: FragmentAddPartner2Binding
    private var countryCode = ""
    private var countryname = ""
    private var token = ""
    var IS_CHECKED = false
    private lateinit var recyclerView: RecyclerView


    private lateinit var stateAdapter: StateAdapter
    private lateinit var shipStateAdapter: StateAdapter
    private val billStateList: ArrayList<StateData> = ArrayList()
    private val shipstateList: ArrayList<StateData> = ArrayList()
    private val billCityList: ArrayList<CityData> = ArrayList()
    private val shipCityList: ArrayList<CityData> = ArrayList()
    var IndustryItemItemList: List<IndustryItem> = java.util.ArrayList<IndustryItem>()
    var salesEmployeeItemList: List<SalesEmployeeItemKt> = java.util.ArrayList<SalesEmployeeItemKt>()
    var getPaymenterm: List<PayMentTerm> = java.util.ArrayList<PayMentTerm>()

    var businessTypeDataList = java.util.ArrayList<DataBusinessType>()
    var zoneDataList = java.util.ArrayList<DataDropDownZone>()
    var countyList = java.util.ArrayList<CountryData>()
    // private lateinit var leadValue: LeadValue

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        act = this
        binding = FragmentAddPartner2Binding.inflate(layoutInflater)
        setContentView(binding.root)
        val intent = getIntent()

        if (intent != null && Prefs.getString(Globals.AddBp, "") == ("Lead")) {
            val leadValue = intent.getParcelableExtra<LeadValue>(Globals.AddBp)
            binding.fragmentAddpartnergeneral.leadValue.setEnabled(false)
            setData(leadValue)
        }
        binding.loader.loader.visibility = View.GONE


        Log.d("checkToken", "token: ${Globals.GalaxyVistaToken}")
        token = "Token ${Globals.GalaxyVistaToken}"

        act = this
        shippinngType = resources.getStringArray(R.array.bpShippingType)
        ship_shiptype = shippinngType[0]
        billshipType = shippinngType[0]

        setDefaults()

        /* if (Globals.checkInternet(this)) {
             callCountryApi()
             callSalessApi()
         }*/

        eventManager()

        setUpBusinessTypeSpinner()
        setUpZoneSpinner()
        setUpParentAccountSpinner()
        setUpCurrencyList()

        callStagesApi()
        callSalessApi()
        callPaymentTermApi()
        callCountryApi()
    }

    private fun setData(leadValue: LeadValue?) {
        binding.fragmentAddpartnergeneral.nameValue.setText(leadValue?.companyName)
        binding.fragmentAddpartnercontact.contactOwnerValue.setText(leadValue?.contactPerson)
        binding.fragmentAddpartnercontact.emailValue.setText(leadValue?.email)
        //todo set email in general
        binding.fragmentAddpartnergeneral.companyEmailValue.setText(leadValue?.email)
        binding.fragmentAddpartnergeneral.etTurnover.setText(leadValue?.turnover)

        binding.fragmentAddpartnercontact.mobileValue.setText(leadValue?.phoneNumber)
        binding.fragmentAddpartnergeneral.companyNoValue.setText(leadValue?.phoneNumber)
        //        binding.fragmentAddpartnercontact.addressSection.billingNameValue.setText(leadValue.getLocation());
        binding.fragmentAddpartnergeneral.leadValue.setText(leadValue?.companyName)
        binding.fragmentAddpartnergeneral.acSalesEmployee.setText(
            leadValue?.assignedTo?.firstName
        )
        salesEmployeeCode = leadValue?.assignedTo?.SalesEmployeeCode?.toIntOrNull() ?: 0
    }
    private fun setDefaults() {
        frameManager(binding.generalFrame, binding.contactFrame, binding.general, binding.contact)
        binding.fragmentAddpartnercontact.addressSection.doneButton.visibility = View.GONE
        binding.headerBottomRounded.headTitle.text = getString(R.string.add_customer)
        binding.fragmentAddpartnercontact.createButton.setOnClickListener(this)
        binding.headerBottomRounded.backPress.setOnClickListener(this)
        // tab_2.setOnClickListener(this)
        binding.general.setOnClickListener(this)
        binding.tab2.setOnClickListener(this)
        binding.contact.setOnClickListener(this)
        binding.fragmentAddpartnergeneral.leadValue.setOnClickListener(this)
    }


    private fun setUpZoneSpinner() {
        binding.fragmentAddpartnergeneral.saerchableSpinnerZone.hint = "Zones"


        val opportunityAllListRequest = PerformaInvoiceListRequestModel().apply {
            field = PerformaInvoiceListRequestModel.Field()
            maxItem = "All"
            order_by_field = "id"
            order_by_value = "asc"
            PageNo = 1
            SearchText = ""
        }


        val call = RetrofitClient.apiService.getZoneDropDownApi(token, opportunityAllListRequest)

        call?.enqueue(object : Callback<ResponseZoneDropDown?> {
            override fun onResponse(
                call: Call<ResponseZoneDropDown?>, response: Response<ResponseZoneDropDown?>
            ) {
                if (response.code() == 200) {
                    response.body()?.data?.let { data ->
                        if (data.isNotEmpty()) {
                            // Add data to the zoneDataList
                            zoneDataList.clear()
                            zoneDataList.addAll(data)

                            // Create a list of zone names
                            val zoneNamesList = zoneDataList.map { it.name ?: "Unknown" }

                            // Set up the adapter for the spinner
                            val adapter = ArrayAdapter(
                                this@AddBPCustomer, R.layout.drop_down_textview, zoneNamesList
                            )
                            binding.fragmentAddpartnergeneral.saerchableSpinnerZone.setAdapter(
                                adapter
                            )
                            binding.fragmentAddpartnergeneral.saerchableSpinnerZone.threshold = 0

                            // Set the selected zone when an item is clicked
                            binding.fragmentAddpartnergeneral.saerchableSpinnerZone.setOnItemClickListener { _, _, position, _ ->
                                val selectedZone = zoneDataList[position]
                                zoneSelected = selectedZone.id ?: ""
                                binding.fragmentAddpartnergeneral.saerchableSpinnerZone.setText(
                                    selectedZone.name, false
                                ) // Set the name of the selected zone
                                Log.d(
                                    "Selected Zone", "ID: $zoneSelected, Name: ${selectedZone.name}"
                                )
                            }
                        } else {
                            Toast.makeText(
                                this@AddBPCustomer,
                                response.body()?.message ?: "No data available",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    } ?: run {
                        Toast.makeText(this@AddBPCustomer, "Response is null", Toast.LENGTH_LONG)
                            .show()
                    }

                } else {
                   /* Toast.makeText(this@AddBPCustomer, response.body()?.message, Toast.LENGTH_LONG)
                        .show()*/
                }
            }

            override fun onFailure(call: Call<ResponseZoneDropDown?>, t: Throwable) {
                Toast.makeText(this@AddBPCustomer, t.message, Toast.LENGTH_SHORT).show()
            }
        })


    }

    private fun callCountryApi() {
        val call = RetrofitClient.apiService.getCountryList()
        call?.enqueue(object : Callback<CountryResponse?> {
            override fun onResponse(
                call: Call<CountryResponse?>, response: Response<CountryResponse?>
            ) {
                if (response.body()?.status == 200) {
                    if (response.body()?.data?.isNotEmpty() == true) {
                        countyList.clear()
                        var itemsList = response.body()?.data ?: emptyList()
                        itemsList = filterList(itemsList)
                        countyList.addAll(itemsList)

                        val itemNames = mutableListOf<String>()
                        val cardCodeName = mutableListOf<String>()
                        for (item in countyList) {
                            itemNames.add(item.name ?: "")
                            cardCodeName.add(item.code ?: "")
                        }

                        val adapter =
                            ArrayAdapter(this@AddBPCustomer, R.layout.drop_down_textview, itemNames)
                        binding.fragmentAddpartnercontact.addressSection.acCountry.setAdapter(
                            adapter
                        )


                        // Set default to India
                        val defaultCountry = "India"
                        val defaultPos = itemNames.indexOf(defaultCountry)
                        if (defaultPos != -1) {
                            binding.fragmentAddpartnercontact.addressSection.acCountry.setText(
                                defaultCountry, false
                            )
                            billtoCountryName = defaultCountry
                            billtoCountrycode = countyList[defaultPos].code ?: ""

                            callBillToStateApi(billtoCountrycode!!)  // Call the API with India's code
                        }


                        val adapter1 =
                            ArrayAdapter(this@AddBPCustomer, R.layout.drop_down_textview, itemNames)
                        binding.fragmentAddpartnercontact.addressSection.acShipCountry.setAdapter(
                            adapter1
                        )


                    }
                } else {
                    Toast.makeText(this@AddBPCustomer, response.body()?.message, Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onFailure(call: Call<CountryResponse?>, t: Throwable) {
                Toast.makeText(this@AddBPCustomer, t.localizedMessage, Toast.LENGTH_SHORT).show()
            }
        })

    }

    //todo calling bill To state api
    private fun callBillToStateApi(countryCode: String) {
        val stateData = StateData().apply { country = countryCode }
        val call = RetrofitClient.apiService.getStateList(stateData)
        call!!.enqueue(object : Callback<StateRespose?> {
            override fun onResponse(call: Call<StateRespose?>, response: Response<StateRespose?>) {
                if (response.body()?.status == 200) {
                    billStateList.clear()
                    response.body()?.data?.let { data ->
                        if (data.isNotEmpty()) {
                            billStateList.addAll(data)
                            val stateAdapter = StateAdapter(
                                this@AddBPCustomer, R.layout.drop_down_textview, billStateList
                            )
                            //todo set bill state..
                            binding.fragmentAddpartnercontact.addressSection.acBillToState.setAdapter(
                                stateAdapter
                            )
                            binding.fragmentAddpartnercontact.addressSection.acBillToState.threshold =0
                            stateAdapter.notifyDataSetChanged()
                        }
                    }
                } else {
                    handleError(response)
                }
            }

            override fun onFailure(call: Call<StateRespose?>, t: Throwable) {
                Toast.makeText(this@AddBPCustomer, t.message, Toast.LENGTH_SHORT).show()
            }
        })

    }


    private fun callBillToCityApi(shiptoStateCode: String) {
        val stateData = FilterOverAll(
            field = FieldFilter(
                StateCode = shiptoStateCode
            ),
            maxItem = 1000,
            order_by_field = "id",
            order_by_value = "desc",
            SearchText = "",
            PageNo = 1
        )

        val call = RetrofitClient.apiService.getCityList(stateData)
        call.enqueue(object : Callback<CityResponse?> {
            override fun onResponse(call: Call<CityResponse?>, response: Response<CityResponse?>) {
                if (response.code() == 200) {
                    if (response.body()?.status == 200) {
                        if (response.body()?.data?.isNotEmpty() == true) {
                            billCityList.addAll(response.body()?.data ?: emptyList())
                            val cityAdapter = CityAdapter(
                                this@AddBPCustomer, R.layout.drop_down_textview, billCityList
                            )
                            //todo set bill state..
                            binding.fragmentAddpartnercontact.addressSection.cityValue.setAdapter(
                                cityAdapter
                            )
                            binding.fragmentAddpartnercontact.addressSection.cityValue.threshold = 0
                            cityAdapter.notifyDataSetChanged()
                        }

                    }
                } else {
                    Toast.makeText(this@AddBPCustomer, response.body()?.message, Toast.LENGTH_LONG)
                        .show()
                }
            }

            override fun onFailure(call: Call<CityResponse?>, t: Throwable) {
                Toast.makeText(this@AddBPCustomer, t.message, Toast.LENGTH_SHORT).show()
            }
        })


    }

    private fun handleError(response: Response<StateRespose?>) {
        when (response.body()?.status) {
            201, 500 -> {
                Toast.makeText(this@AddBPCustomer, response.body()?.message, Toast.LENGTH_SHORT)
                    .show()
            }

            else -> {
                try {
                    val gson = GsonBuilder().create()
                    val mError =
                        gson.fromJson(response.errorBody()?.string(), StateRespose::class.java)
                    Toast.makeText(this@AddBPCustomer, mError.message, Toast.LENGTH_LONG).show()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }


    private fun callShipToStateApi(shiptoCountrycode: String) {
        val stateData = StateData().apply {
            country = shiptoCountrycode
        }

        val call = RetrofitClient.apiService.getStateList(stateData)
        call!!.enqueue(object : Callback<StateRespose?> {
            override fun onResponse(call: Call<StateRespose?>, response: Response<StateRespose?>) {
                if (response.code() == 200) {
                    if (response.body()?.status == 200) {

                        shipstateList.clear()
                        if (response.body()?.data?.isNotEmpty() == true) {
                            shipstateList.addAll(response.body()?.data ?: emptyList())
                        } else {
                            val sta = StateData().apply {
                                name = "Select State"
                            }
                            shipstateList.add(sta)
                        }

                        stateAdapter = StateAdapter(
                            this@AddBPCustomer, R.layout.drop_down_textview, shipstateList
                        )
                        binding.fragmentAddpartnercontact.addressSection.acShipToState.setAdapter(
                            stateAdapter
                        )
                        binding.fragmentAddpartnercontact.addressSection.acShipToState.threshold = 0
                        stateAdapter.notifyDataSetChanged()

                    }
                } else {
                    Toast.makeText(this@AddBPCustomer, response.body()?.message, Toast.LENGTH_LONG)
                        .show()
                }
            }

            override fun onFailure(call: Call<StateRespose?>, t: Throwable) {
                Toast.makeText(this@AddBPCustomer, t.message, Toast.LENGTH_SHORT).show()
            }
        })


    }


    private fun callShipToCityApi(shiptoStateCode: String) {
        val stateData = FilterOverAll(
            field = FieldFilter(
                StateCode = shiptoStateCode
            ),
            maxItem = 1000,
            order_by_field = "id",
            order_by_value = "desc",
            SearchText = "",
            PageNo = 1
        )

        val call = RetrofitClient.apiService.getCityList(stateData)
        call.enqueue(object : Callback<CityResponse?> {
            override fun onResponse(call: Call<CityResponse?>, response: Response<CityResponse?>) {
                if (response.code() == 200) {
                    if (response.body()?.status == 200) {
                        if (response.body()?.data?.isNotEmpty() == true) {
                            shipCityList.addAll(response.body()?.data ?: emptyList())
                            val cityAdapter = CityAdapter(
                                this@AddBPCustomer, R.layout.drop_down_textview, shipCityList
                            )
                            //todo set bill state..
                            binding.fragmentAddpartnercontact.addressSection.shipcityValue.setAdapter(
                                cityAdapter
                            )
                            binding.fragmentAddpartnercontact.addressSection.shipcityValue.threshold = 0
                            cityAdapter.notifyDataSetChanged()
                        }

                    }
                } else {
                    Toast.makeText(this@AddBPCustomer, response.body()?.message, Toast.LENGTH_LONG)
                        .show()
                }
            }

            override fun onFailure(call: Call<CityResponse?>, t: Throwable) {
                Toast.makeText(this@AddBPCustomer, t.message, Toast.LENGTH_SHORT).show()
            }
        })


    }


    private fun filterList(value: List<CountryData>): List<CountryData> {
        return value.filter { it.name != "admin" }
    }


    private fun callPaymentTermApi() {
        val call = RetrofitClient.apiService.getPaymentTerm(token)
        call.enqueue(object : Callback<PayMentTermsDetail?> {
            override fun onResponse(
                call: Call<PayMentTermsDetail?>, response: Response<PayMentTermsDetail?>
            ) {
                if (!response.isSuccessful) {
                    Toast.makeText(
                        this@AddBPCustomer,
                        response.body()?.message ?: "Unable to load payment terms",
                        Toast.LENGTH_SHORT
                    ).show()
                    return
                }

                val paymentTerms = response.body()?.data.orEmpty()
                if (paymentTerms.isEmpty()) {
                    getPaymenterm = emptyList()
                    Toast.makeText(
                        this@AddBPCustomer, "No payment terms found", Toast.LENGTH_SHORT
                    ).show()
                    return
                }

                getPaymenterm = paymentTerms
                val paymentTermNames = paymentTerms.map { it.paymentTermsGroupName.orEmpty() }
                val adapter = ArrayAdapter(
                    this@AddBPCustomer, R.layout.drop_down_textview, paymentTermNames
                )
                binding.fragmentAddpartnergeneral.paymentTermValue.adapter = adapter

                binding.fragmentAddpartnergeneral.paymentTermValue.onItemSelectedListener =
                    object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(
                            parent: AdapterView<*>?, view: View?, position: Int, id: Long
                        ) {
                            val selectedTerm = paymentTerms.getOrNull(position)
                            Log.d(
                                "Selected Payment Term",
                                selectedTerm?.paymentTermsGroupName.orEmpty()
                            )
                            payment_term = selectedTerm?.groupNumber.orEmpty()
                        }

                        override fun onNothingSelected(parent: AdapterView<*>?) {
                            payment_term = paymentTerms.firstOrNull()?.groupNumber.orEmpty()
                        }
                    }

            }

            override fun onFailure(call: Call<PayMentTermsDetail?>, t: Throwable) {
                Log.e(TAG, "Failed to load payment terms", t)
                Toast.makeText(
                    this@AddBPCustomer,
                    t.localizedMessage ?: "Unable to load payment terms",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun callStagesApi() {
        val call: Call<IndustryResponse?>? = RetrofitClient.apiService.getIndustryList(token)
        call!!.enqueue(object : Callback<IndustryResponse?> {
            override fun onResponse(
                call: Call<IndustryResponse?>, response: Response<IndustryResponse?>
            ) {

                IndustryItemItemList = response.body()?.value ?: emptyList()

                val adapter = ArrayAdapter(
                    this@AddBPCustomer,
                    R.layout.drop_down_textview,
                    IndustryItemItemList.map { it.industryName })

                adapter.setDropDownViewResource(R.layout.drop_down_textview)

                binding.fragmentAddpartnergeneral.industrySpinner.adapter = adapter

                binding.fragmentAddpartnergeneral.industrySpinner.setSelection(0)

                binding.fragmentAddpartnergeneral.industrySpinner.onItemSelectedListener =
                    object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(
                            parent: AdapterView<*>?, view: View?, position: Int, id: Long
                        ) {
                            val selectedIndustryItem = IndustryItemItemList[position]
                            // Perform any further actions with selectedIndustryItem
                            industryCode = selectedIndustryItem.industryName
                        }

                        override fun onNothingSelected(parent: AdapterView<*>?) {
                            // Handle case when nothing is selected, if necessary
                        }
                    }

                industryCode = IndustryItemItemList[0].industryCode
            }

            override fun onFailure(call: Call<IndustryResponse?>, t: Throwable) {
                Log.e("", "onFailure: " + t.message)
            }
        })

    }


    private fun setUpBusinessTypeSpinner() {
        binding.fragmentAddpartnergeneral.saerchableSpinnerBusinessType.hint = "Business Type"

        binding.fragmentAddpartnergeneral.saerchableSpinnerBusinessType.onItemClickListener =
            AdapterView.OnItemClickListener { adapterView, view, position, id ->
                Log.e(
                    "SPINNER SEARCH", "onItemClick: " + businessTypeDataList[position].type
                )
                businessTypeSelected = businessTypeDataList[position].type.toString()
                TYPE = businessTypeDataList[position].id.toString()
            }


        val call: Call<ResponseBusinessType> = RetrofitClient.apiService.getBusinessType(token)
        call.enqueue(object : Callback<ResponseBusinessType> {
            override fun onResponse(
                call: Call<ResponseBusinessType>, response: Response<ResponseBusinessType>
            ) {
                if (response.code() == 200) {
                    if (response.body()?.data?.size!! > 0) {
                        businessTypeDataList.clear()
                        businessTypeDataList.addAll(response.body()!!.data!!)
                        businessTypeSelected = businessTypeDataList[0].type.toString()
                        // Convert businessTypeDataList to a list of 'type' values
                        val businessTypeNames = businessTypeDataList.map { it.type ?: "" }

                        val adapter = ArrayAdapter(
                            this@AddBPCustomer, R.layout.drop_down_textview, businessTypeNames
                        )

                        binding.fragmentAddpartnergeneral.saerchableSpinnerBusinessType.setAdapter(
                            adapter
                        )

                        binding.fragmentAddpartnergeneral.saerchableSpinnerBusinessType.setOnClickListener {
                            binding.fragmentAddpartnergeneral.saerchableSpinnerBusinessType.showDropDown()
                        }

                        binding.fragmentAddpartnergeneral.saerchableSpinnerBusinessType.setOnItemClickListener { _, _, position, _ ->
                            val selectedType = businessTypeNames[position]
                            binding.fragmentAddpartnergeneral.saerchableSpinnerBusinessType.setText(
                                selectedType, false
                            )
                        }

                    } else {
                        Toast.makeText(
                            this@AddBPCustomer, response.body()!!.message, Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }

            override fun onFailure(call: Call<ResponseBusinessType>, t: Throwable) {
                Toast.makeText(this@AddBPCustomer, t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun callSalessApi() {

        val employeeNames = mutableListOf<String>()

        val jsonObject = JsonObject()
        jsonObject.addProperty("SalesEmployeeCode", PrefsByShubh.getSalesEmployeeCode())

        val call = RetrofitClient.apiService.getSalesEmplyeeList(token, jsonObject)

        call?.enqueue(object : Callback<JsonObject?> {
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                if (response.isSuccessful) {
                    val responseObj = response.body()

                    // Assuming the response contains a "data" field that is a list of employees
                    val dataArray = responseObj?.getAsJsonArray("data")

                    // Convert JsonArray to List<SalesEmployeeItem>
                    salesEmployeeItemList = if (dataArray != null) {
                        val gson = Gson()
                        gson.fromJson(
                            dataArray, object : TypeToken<List<SalesEmployeeItemKt>>() {}.type
                        )
                    } else {
                        emptyList()
                    }


                    if (dataArray != null) {
                        for (employeeJson in dataArray) {
                            val employee = Gson().fromJson(employeeJson, SalesEmployee::class.java)

                            // Concatenate first name and last name
                            val fullName = "${employee.firstName} ${employee.lastName}"

                            // Add the full name to the list
                            employeeNames.add(fullName)
                        }
                    }

                    // Set the list in the AutoCompleteTextView
                    val adapter =
                        ArrayAdapter(this@AddBPCustomer, R.layout.drop_down_textview, employeeNames)
                    binding.fragmentAddpartnergeneral.acSalesEmployee.setAdapter(adapter)

//                    // Optionally, set a default value
//                    binding.fragmentAddpartnergeneral.acSalesEmployee.setText(
//                        employeeNames.firstOrNull(),
//                        false
//                    )

                    // Open the dropdown on click
                    binding.fragmentAddpartnergeneral.acSalesEmployee.setOnClickListener {
                        binding.fragmentAddpartnergeneral.acSalesEmployee.showDropDown()
                    }

                    // Handle item selection
                    binding.fragmentAddpartnergeneral.acSalesEmployee.setOnItemClickListener { parent, view, position, id ->
                        val selectedItem = parent.getItemAtPosition(position) as String

                        Log.d(
                            "salesEmployeeCode",
                            salesEmployeeItemList[position].salesEmployeeCode?.toIntOrNull()
                                .toString()
                        )

                        salesEmployeeCode =
                            salesEmployeeItemList[position].salesEmployeeCode?.toIntOrNull() ?: 0
                        salesEmployeeName = salesEmployeeItemList[position].salesEmployeeName ?: ""

                        // Set the selected item
                        binding.fragmentAddpartnergeneral.acSalesEmployee.setText(
                            selectedItem, false
                        )

                        // Optionally, you can store the selected item in a variable for further use
                        // val selectedEmployee = employeeNames[position]
                    }
                }
            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                Log.e("API Error", "onFailure: ${t.message}")
            }
        })


    }

    private fun eventManager() {


        binding.fragmentAddpartnergeneral.parentAccountValue.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>, view: View?, position: Int, id: Long
                ) {
                    // if (getPaymenterm.size > 0)
                    // parenT_account = addDatatoCategoryList(AllitemsList)[position]
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // parenT_account = addDatatoCategoryList(AllitemsList)[0]
                }
            }


        // Bill to address dropdown item selection
        binding.fragmentAddpartnercontact.addressSection.acCountry.setOnItemClickListener { parent, _, position, _ ->
            try {
                val countryName = parent.getItemAtPosition(position) as String
                billtoCountryName = countryName

                val pos = Globals.getCountryCodePos(countyList, countryName)
                billtoCountrycode = countyList[pos].code ?: ""

                if (countryName.isEmpty()) {
                    binding.fragmentAddpartnercontact.addressSection.rlRecyclerViewLayout.visibility =
                        View.GONE
                    binding.fragmentAddpartnercontact.addressSection.rvCountryList.visibility =
                        View.GONE
                } else {
                    binding.fragmentAddpartnercontact.addressSection.rlRecyclerViewLayout.visibility =
                        View.VISIBLE
                    binding.fragmentAddpartnercontact.addressSection.rvCountryList.visibility =
                        View.VISIBLE
                }

                if (countryName.isNotEmpty()) {
                    binding.fragmentAddpartnercontact.addressSection.acCountry.setText(
                        countryName
                    )
                    binding.fragmentAddpartnercontact.addressSection.acCountry.setSelection(
                        binding.fragmentAddpartnercontact.addressSection.acCountry.length()
                    )
                    callBillToStateApi(billtoCountrycode!!)
                } else {
                    billtoCountryName = ""
                    billtoCountrycode = ""
                    binding.fragmentAddpartnercontact.addressSection.acCountry.setText(
                        ""
                    )
                }
            } catch (e: Exception) {
                Log.e("catch", "onItemClick: ${e.message}")
                e.printStackTrace()
            }
        }
        // Ship to address dropdown item selection
        binding.fragmentAddpartnercontact.addressSection.acShipCountry.setOnItemClickListener { parent, _, position, _ ->
            try {
                val countryName = parent.getItemAtPosition(position) as String
                shiptoCountryName = countryName

                val pos = Globals.getCountryCodePos(countyList, countryName)
                shiptoCountrycode = countyList[pos].code ?: ""

                if (countryName.isEmpty()) {
                    binding.fragmentAddpartnercontact.addressSection.rlShipREcyclerLayout.visibility =
                        View.GONE
                    binding.fragmentAddpartnercontact.addressSection.rvShipCountryList.visibility =
                        View.GONE
                } else {
                    binding.fragmentAddpartnercontact.addressSection.rlShipREcyclerLayout.visibility =
                        View.VISIBLE
                    binding.fragmentAddpartnercontact.addressSection.rvShipCountryList.visibility =
                        View.VISIBLE
                }

                if (countryName.isNotEmpty()) {
                    binding.fragmentAddpartnercontact.addressSection.acShipCountry.setText(
                        countryName
                    )
                    binding.fragmentAddpartnercontact.addressSection.acShipCountry.setSelection(
                        binding.fragmentAddpartnercontact.addressSection.acShipCountry.length()
                    )
                    callShipToStateApi(shiptoCountrycode!!)
                } else {
                    shiptoCountryName = ""
                    shiptoCountrycode = ""
                    binding.fragmentAddpartnercontact.addressSection.acShipCountry.setText(
                        ""
                    )
                }
            } catch (e: Exception) {
                Log.e("catch", "onItemClick: ${e.message}")
                e.printStackTrace()
            }
        }

        // todo set bill to item click of autocomplete state
        binding.fragmentAddpartnercontact.addressSection.acBillToState.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                if (billStateList.isNotEmpty()) {
                    billtoState = billStateList[position].name
                    billtoStateCode = billStateList[position].code
                    billtoStateId = billStateList[position].id!!
                    binding.fragmentAddpartnercontact.addressSection.acBillToState.setText(
                        billStateList[position].name
                    )
                callBillToCityApi(billtoStateCode.toString())
                }
            }

        // todo ship to state item click
        binding.fragmentAddpartnercontact.addressSection.acShipToState.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                shiptoState = shipstateList[position].name
                shiptoStateCode = shipstateList[position].code
                binding.fragmentAddpartnercontact.addressSection.acShipToState.setText(shipstateList[position].name)
                callShipToCityApi(shiptoStateCode.toString())
            }

        binding.fragmentAddpartnercontact.addressSection.cityValue.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                billtoCityCode = billCityList[position].CityName
                billtoCityId = billCityList[position].id
                binding.fragmentAddpartnercontact.addressSection.cityValue.setText(billCityList[position].CityName)
            }

        binding.fragmentAddpartnercontact.addressSection.shipcityValue.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                shiptoCityCode = shipCityList[position].CityName
                binding.fragmentAddpartnercontact.addressSection.shipcityValue.setText(shipCityList[position].CityName)
            }

        binding.fragmentAddpartnergeneral.acSalesEmployee.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->
                val selectedEmployee = salesEmployeeItemList.getOrNull(position)

                if (selectedEmployee != null) {
                    Log.d(
                        "salesEmployeeCode",
                        selectedEmployee.salesEmployeeCode?.toIntOrNull().toString()
                    )

                    salesEmployeeCode = selectedEmployee.salesEmployeeCode?.toIntOrNull() ?: 0
                    salesEmployeeName = selectedEmployee.salesEmployeeName ?: ""

                    binding.fragmentAddpartnergeneral.acSalesEmployee.setText(
                        salesEmployeeName, false
                    )
                } else {
                    salesEmployeeCode = 0
                    salesEmployeeName = ""
                    binding.fragmentAddpartnergeneral.acSalesEmployee.setText("", false)
                }
            }


        binding.fragmentAddpartnercontact.addressSection.shippingSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>, view: View?, position: Int, id: Long
                ) {
                    billshipType = shippinngType[position]
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    billshipType = shippinngType[0]
                }
            }

        binding.fragmentAddpartnercontact.addressSection.shippingSpinner2.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>, view: View?, position: Int, id: Long
                ) {
                    ship_shiptype = shippinngType[position]
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    ship_shiptype = shippinngType[0]
                }
            }

        binding.fragmentAddpartnergeneral.industrySpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>, view: View?, position: Int, id: Long
                ) {
                    if (IndustryItemItemList.isNotEmpty()) {
                        industryCode = IndustryItemItemList[position].industryCode
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    if (IndustryItemItemList.isNotEmpty()) {
                        industryCode = IndustryItemItemList[0].industryCode
                    }
                }
            }


        binding.fragmentAddpartnercontact.addressSection.checkbox1.setOnCheckedChangeListener { buttonView, isChecked ->
            Log.d("checking1", isChecked.toString())
            if (isChecked) {
                IS_CHECKED = true
                binding.fragmentAddpartnercontact.addressSection.shipBlock.visibility = View.VISIBLE
            } else {
                IS_CHECKED = false
                binding.fragmentAddpartnercontact.addressSection.shipBlock.visibility = View.GONE
            }
        }

        binding.fragmentAddpartnergeneral.paymentTermValue.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>, view: View?, position: Int, id: Long
                ) {
                    if (getPaymenterm.isNotEmpty()) {
                        payment_term = getPaymenterm[position].groupNumber.toString()
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    payment_term = getPaymenterm[0].groupNumber.toString()
                }
            }
    }


    private fun setUpParentAccountSpinner() {
        // Set up parent account spinner
    }

    private fun setUpCurrencyList() {
        // Set up currency list
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.back_press -> finish()
            R.id.tab_1, R.id.general -> frameManager(
                binding.generalFrame, binding.contactFrame, binding.general, binding.contact
            )

            R.id.tab_2, R.id.contact -> frameManager(
                binding.contactFrame, binding.generalFrame, binding.contact, binding.general
            )

            R.id.create_button -> {
                val name = binding.fragmentAddpartnergeneral.nameValue.text.toString().trim()
                val comp_no =
                    binding.fragmentAddpartnergeneral.companyNoValue.text.toString().trim()
                val contactName =
                    binding.fragmentAddpartnercontact.contactOwnerValue.text.toString().trim()
                val mobile = binding.fragmentAddpartnercontact.mobileValue.text.toString().trim()
                val email = binding.fragmentAddpartnercontact.emailValue.text.toString().trim()
                val website = binding.fragmentAddpartnergeneral.websiteValue.text.toString().trim()
                val comp_email =
                    binding.fragmentAddpartnergeneral.companyEmailValue.text.toString().trim()
                val gst = binding.fragmentAddpartnergeneral.invoiceNoValue.text.toString().trim()
                U_LeadNM = binding.fragmentAddpartnergeneral.leadValue.text.toString().trim()

                val billName =
                    binding.fragmentAddpartnercontact.addressSection.billingNameValue.text.toString()
                        .trim()
                val billZipcode =
                    binding.fragmentAddpartnercontact.addressSection.zipCodeValue.text.toString()
                        .trim()
                val billCity =
                    binding.fragmentAddpartnercontact.addressSection.cityValue.text.toString()
                        .trim()
                val billAddressValue =
                    binding.fragmentAddpartnercontact.addressSection.billingAddressValue.text.toString()
                        .trim()

                val shipName =
                    binding.fragmentAddpartnercontact.addressSection.shippingNameValue.text.toString()
                        .trim()
                val shipZipcode =
                    binding.fragmentAddpartnercontact.addressSection.zipcodeValue2.text.toString()
                        .trim()
                val shipCity =
                    binding.fragmentAddpartnercontact.addressSection.shipcityValue.text.toString()
                        .trim()
                val shipAddressValue =
                    binding.fragmentAddpartnercontact.addressSection.shippingAddressValue.text.toString()
                        .trim()
                val shipping_spinner =
                    binding.fragmentAddpartnercontact.addressSection.shippingSpinner.selectedItem.toString()


                parenT_account =
                    binding.fragmentAddpartnergeneral.saerchableSpinnerParentAccount.text.toString()
                        .trim()

                val billShippingCountry1: String =
                    binding.fragmentAddpartnercontact.addressSection.acCountry.text.toString()
                        .trim()

                val shipShippingCountry1: String =
                    binding.fragmentAddpartnercontact.addressSection.acShipCountry.text.toString()
                        .trim()


                val billtoState1: String =
                    binding.fragmentAddpartnercontact.addressSection.acBillToState.text.toString()
                        .trim()

                val shiptoState1: String =
                    binding.fragmentAddpartnercontact.addressSection.acShipToState.text.toString()
                        .trim()


                /*Log.d("checking", "UnCheckShipValidation: billtoState = $billtoState")

                if (billtoState.isEmpty()) {
                    Log.d("checking", "Bill To State is empty")
                    showMessage(act, "Select Bill To State")
                    return false
                }*/


                Log.d("checking", IS_CHECKED.toString())
                // TODO: Add code by Tarun Sharma for dynamic fields
                if (IS_CHECKED) {
                    if (validation(
                            name,
                            comp_email,
                            comp_no,
                            mobile,
                            email,
                            industryCode!!,
                            salesEmployeeCode,
                            contactName,
                            billName,
                            billZipcode,
                            billCity,
                            billAddressValue,
                            billShippingCountry1,
                            billtoState1,
                            shipName,
                            shipZipcode,
                            shipCity,
                            shipAddressValue,
                            shipShippingCountry1,
                            shiptoState1,
                            parenT_account,
                            zoneSelected,
                            gst,
                            shipping_spinner
                        )
                    ) {

                        // Create main payload JSON object
                        val payload = JsonObject().apply {
                            addProperty("U_LEADID", LeadID)
                            addProperty("U_LEADNM", U_LeadNM)
                            addProperty("CardCode", "")
                            addProperty("CardName", name)
                            addProperty("CardType", "cCustomer")
                            addProperty("Industry", industryCode)
                            addProperty("Website", website)
                            addProperty("StateID", billtoStateId)
                            addProperty("CityID", billtoCityId)
                            addProperty("EmailAddress", comp_email)
                            addProperty("Phone1", comp_no)
                            addProperty("DiscountPercent", "")
                            addProperty("Currency", currencyCode)
                            addProperty("IntrestRatePercent", "")
                            addProperty("CommissionPercent", "")
                            addProperty(
                                "Notes",
                                binding.fragmentAddpartnercontact.remarksValue.text.toString()
                            )

                            addProperty("PayTermsGrpCode", payment_term)
                            addProperty("CreditLimit", "")
                            addProperty("AttachmentEntry", "")
                            addProperty("SalesPersonCode", salesEmployeeCode.toString())
                            addProperty("U_PARENTACC", parenT_account)
                            addProperty("U_BPGRP", "")
                            addProperty("U_CONTOWNR", contactName)
                            addProperty("U_RATING", "")
                            addProperty("U_TYPE", TYPE)
                            addProperty(
                                "U_ANLRVN",
                                binding.fragmentAddpartnergeneral.etTurnover.text.toString()
                            )
                            addProperty("U_CURBAL", "")
                            addProperty("U_ACCNT", "")
                            addProperty(
                                "U_INVNO",
                                binding.fragmentAddpartnergeneral.invoiceNoValue.text.toString()
                                    .trim()
                            )
                            addProperty("CreateDate", Globals.getTodaysDatervrsfrmt())
                            addProperty("CreateTime", Globals.getTCurrentTime())
                            addProperty("UpdateDate", Globals.getTodaysDatervrsfrmt())
//                            addProperty("uLat", Globals.currentlattitude.toString())
//                            addProperty("uLong", Globals.currentlongitude.toString())
//
                            addProperty("U_LAT", "")
                            addProperty("U_LONG", "")
                            addProperty("UpdateTime", Globals.getTCurrentTime())
                            addProperty("zone", zoneSelected)

                        }

                        // BP Addresses
                        val bpAddressesArray = JsonArray()

                        val billToAddress = JsonObject().apply {
                            addProperty("BPCode", "")
                            addProperty("AddressName", billName)
                            addProperty("AddressType", "bo_BillTo")
                            addProperty("Block", "")
                            addProperty("City", billCity)
                            addProperty("Country", billtoCountrycode)
                            addProperty("RowNum", "0")
                            addProperty("State", billtoStateCode)
                            addProperty("Street", billAddressValue)
                            addProperty("U_COUNTRY", billtoCountryName)
                            addProperty("U_STATE", billtoState)
                            addProperty("U_SHPTYP", billshipType)
                            addProperty("ZipCode", billZipcode)


                        }
                        bpAddressesArray.add(billToAddress)

                        val shipToAddress = JsonObject().apply {
                            addProperty("BPCode", "")
                            addProperty("AddressType", "bo_ShipTo")
                            addProperty("RowNum", "1")
                            addProperty("Block", "")

                            if (binding.fragmentAddpartnercontact.addressSection.checkbox1.isChecked) {
                                addProperty("AddressName", shipName)
                                addProperty("ZipCode", shipZipcode)
                                addProperty("Street", shipAddressValue)
                                addProperty("U_STATE", shiptoState)
                                addProperty("U_COUNTRY", shiptoCountryName)
                                addProperty("U_SHPTYP", ship_shiptype)
                                addProperty("State", shiptoStateCode)
                                addProperty("Country", shiptoCountrycode)
                                addProperty("City", shipCity)
                            } else {
                                addProperty("AddressName", billName)
                                addProperty("Street", billAddressValue)
                                addProperty("ZipCode", billZipcode)
                                addProperty("U_COUNTRY", billtoCountryName)
                                addProperty("U_STATE", billtoState)
                                addProperty("U_SHPTYP", billshipType)
                                addProperty("State", billtoStateCode)
                                addProperty("Country", billtoCountrycode)
                                addProperty("City", billCity)
                            }
                        }
                        bpAddressesArray.add(shipToAddress)

                        payload.add("BPAddresses", bpAddressesArray)

                        // Contact Employees
                        val contactEmployeesArray = JsonArray()
                        val contactEmployee = JsonObject().apply {
                            addProperty("Name", contactName)
                            addProperty("E_Mail", email)
                            addProperty("MobilePhone", mobile)
                        }
                        contactEmployeesArray.add(contactEmployee)

                        payload.add("ContactEmployees", contactEmployeesArray)


                        // Log payload for debugging
                        println(payload.toString())
                        Globals.checkInternet(this) { isConnected, isFast ->
                            if (isConnected) {

                                if (isFast) {
                                    // ✅ good internet → continue API call
                                    binding.loader.loader.visibility = View.VISIBLE
                                    binding.fragmentAddpartnercontact.createButton.isEnabled = false

                                    createBP(payload)
                                } else {
                                    // ⚠️ slow internet → still allow but show warning
                                    binding.loader.loader.visibility = View.VISIBLE
                                    binding.fragmentAddpartnercontact.createButton.isEnabled = false

                                    createBP(payload)
                                }

                            }
                        }

                    }
                } else {
                    if (unCheckShipValidation(
                            name,
                            comp_email,
                            comp_no,
                            mobile,
                            email,
                            industryCode.toString(),
                            salesEmployeeCode,
                            contactName,
                            billName,
                            billZipcode,
                            billCity,
                            billAddressValue,
                            billShippingCountry1,
                            billtoState1,
                            parenT_account,
                            zoneSelected,
                            gst,
                            shipping_spinner
                        )
                    ) {

                        val payload = JsonObject().apply {
                            addProperty("U_LEADID", LeadID)
                            addProperty("U_LEADNM", U_LeadNM)
                            addProperty("CardCode", "")
                            addProperty("CardName", name)
                            addProperty("CardType", "cCustomer") // Value from Spinner
                            addProperty("Industry", industryCode)
                            addProperty("Website", website)
                            addProperty("EmailAddress", comp_email)
                            addProperty("Phone1", comp_no)
                            addProperty("DiscountPercent", "")
                            addProperty("Currency", currencyCode)
                            addProperty("IntrestRatePercent", "")
                            addProperty("CommissionPercent", "")
                            addProperty(
                                "Notes",
                                binding.fragmentAddpartnercontact.remarksValue.text.toString()
                            )
                            addProperty("PayTermsGrpCode", payment_term)
                            addProperty("CreditLimit", "")
                            addProperty("AttachmentEntry", "")
                            addProperty("SalesPersonCode", salesEmployeeCode.toString())
                            addProperty("U_PARENTACC", parenT_account)
                            addProperty("U_BPGRP", "")
                            addProperty("U_CONTOWNR", contactName)
                            addProperty("U_RATING", "")
                            addProperty("U_TYPE", TYPE)
                            addProperty(
                                "U_ANLRVN",
                                binding.fragmentAddpartnergeneral.etTurnover.text.toString()
                            )
                            addProperty("U_CURBAL", "")
                            addProperty("U_ACCNT", "")
                            addProperty(
                                "U_INVNO",
                                binding.fragmentAddpartnergeneral.invoiceNoValue.text.toString()
                                    .trim()
                            )
                            addProperty("CreateDate", Globals.getTodaysDatervrsfrmt())
                            addProperty("CreateTime", Globals.getTCurrentTime())
                            addProperty("UpdateDate", Globals.getTodaysDatervrsfrmt())
                            addProperty("U_LAT", "")
                            addProperty("U_LONG", "")
//                        addProperty("uLat", Globals.currentlattitude.toString())
//                        addProperty("uLong", Globals.currentlongitude.toString())
                            addProperty("UpdateTime", Globals.getTCurrentTime())
                            addProperty("zone", zoneSelected)


                        }

                        // Add BP Addresses
                        val bpAddressesArray = JsonArray()

                        val billToAddress = JsonObject().apply {
                            addProperty("BPCode", "")
                            addProperty("AddressName", billName)
                            addProperty("AddressType", "bo_BillTo")
                            addProperty("Block", "")
                            addProperty("City", billCity)
                            addProperty("Country", billtoCountrycode)
                            addProperty("RowNum", "0")
                            addProperty("State", billtoStateCode)
                            addProperty("Street", billAddressValue)
                            addProperty("U_COUNTRY", billtoCountryName)
                            addProperty("U_STATE", billtoState)
                            addProperty("U_SHPTYP", billshipType)
                            addProperty("ZipCode", billZipcode)
                        }
                        bpAddressesArray.add(billToAddress)

                        val shipToAddress = JsonObject().apply {
                            addProperty("AddressType", "bo_ShipTo")
                            addProperty("RowNum", "1")
                            addProperty("BPCode", "")
                            addProperty("Block", "")
                            if (binding.fragmentAddpartnercontact.addressSection.checkbox1.isChecked) {
                                addProperty("AddressName", shipName)
                                addProperty("ZipCode", shipZipcode)
                                addProperty("Street", shipAddressValue)
                                addProperty("U_STATE", shiptoState)
                                addProperty("U_COUNTRY", shiptoCountryName)
                                addProperty("U_SHPTYP", ship_shiptype)
                                addProperty("State", shiptoStateCode)
                                addProperty("Country", shiptoCountrycode)
                                addProperty("City", shipCity)
                            } else {
                                addProperty("AddressName", billName)
                                addProperty("Street", billAddressValue)
                                addProperty("ZipCode", billZipcode)
                                addProperty("U_COUNTRY", billtoCountryName)
                                addProperty("U_STATE", billtoState)
                                addProperty("U_SHPTYP", billshipType)
                                addProperty("State", billtoStateCode)
                                addProperty("Country", billtoCountrycode)
                                addProperty("City", billCity)
                            }
                        }
                        bpAddressesArray.add(shipToAddress)

                        payload.add("BPAddresses", bpAddressesArray)

                        // Add Contact Employees
                        val contactEmployeesArray = JsonArray()
                        val contactEmployee = JsonObject().apply {
                            addProperty("Name", contactName)
                            addProperty("E_Mail", email)
                            addProperty("MobilePhone", mobile)
                        }
                        contactEmployeesArray.add(contactEmployee)

                        payload.add("ContactEmployees", contactEmployeesArray)

                        binding.loader.loader.visibility = View.VISIBLE
                        binding.fragmentAddpartnercontact.createButton.isEnabled = false

                        createBP(payload)

                    }
                }

            }

        }
    }

    private fun frameManager(
        visiblleFrame: FrameLayout, f1: FrameLayout, selected: TextView, t1: TextView
    ) {
        selected.setTextColor(resources.getColor(R.color.colorPrimary))
        t1.setTextColor(resources.getColor(R.color.black))
        visiblleFrame.visibility = View.VISIBLE
        f1.visibility = View.GONE
    }


    private fun validation(
        cowner: String,
        comp_email: String,
        comp_no: String,
        mobile: String,
        email: String,
        industryCode: String,
        salesEmployeeCode: Int,
        contactName: String,
        billName: String,
        billZipcode: String,
        billCity: String,
        billAddressValue: String,
        countryname: String,
        billtoState: String,
        shipName: String,
        shipZipcode: String,
        shipCity: String,
        shipAddressValue: String,
        shiptoCountryName: String,
        shiptoState: String,
        parentAccount: String,
        selectedZone: String,
        gst: String,
        shipping_spinner: String
    ): Boolean {


        if (cowner.isEmpty()) {
            showMessage(act, "Enter Company name")
            return false
        }
        if (comp_email.isEmpty()) {
            showMessage(act, "Enter Email")
            return false
        }
        if (comp_email.isNotEmpty() && Globals.isvalidateemail(binding.fragmentAddpartnergeneral.companyEmailValue)) {
            binding.fragmentAddpartnergeneral.companyEmailValue.requestFocus()
            return false
        }
        if (selectedZone.isEmpty() || selectedZone.equals("Zones", ignoreCase = true)) {
            showMessage(act, "Select Zone")
            return false
        }
        if (gst.isEmpty()) {
            showMessage(act, "GST is Required")
            return false
        }
        if (email.isEmpty()) {
            showMessage(act, "Enter Contact Email")
            return false
        }
        if (email.isNotEmpty() && Globals.isvalidateemail(binding.fragmentAddpartnercontact.emailValue)) {
            binding.fragmentAddpartnercontact.emailValue.requestFocus()
            return false
        }
        if (comp_no.isEmpty()) {
            showMessage(act, "Enter Company Contact No.")
            return false
        }
        if (mobile.isEmpty()) {
            showMessage(act, "Enter Contact person mobile number")
            return false
        }
        if (salesEmployeeCode == 0) {
            showMessage(act, "Select Sales Employee")
            return false
        }
        if (contactName.isEmpty()) {
            showMessage(act, "Enter Contact Name")
            return false
        }
        if (billName.isEmpty()) {
            showMessage(act, "Enter Billing Name")
            return false
        }
        if (billZipcode.isEmpty()) {
            showMessage(act, "Enter Billing Zipcode")
            return false
        }
        if (billCity.isEmpty()) {
            showMessage(act, "Enter Bill To City")
            return false
        }
        if (countryname.isEmpty()) {
            showMessage(act, "Select Bill To Country")
            return false
        }
        if (billtoState.isEmpty()) {
            showMessage(act, "Select Bill To State")
            return false
        }
        if (billAddressValue.isEmpty()) {
            showMessage(act, "Enter Billing Address")
            return false
        }
        if (shipName.isEmpty()) {
            showMessage(act, "Enter Shipping Name")
            return false
        }
        if (shipZipcode.isEmpty()) {
            showMessage(act, "Enter Ship To Zipcode")
            return false
        }
        if (shiptoCountryName.isEmpty()) {
            showMessage(act, "Select Ship To Country")
            return false
        }
        if (shiptoState.isEmpty()) {
            showMessage(act, "Select Ship To State")
            return false
        }
        if (shipCity.isEmpty()) {
            showMessage(act, "Enter Ship To City")
            return false
        }
        if (shipAddressValue.isEmpty()) {
            showMessage(act, "Enter Ship Address")
            return false
        }
        if (shipping_spinner.isEmpty()) {
            showMessage(act, "Select Shipping Type")
            return false
        }

        return true
    }

    private fun unCheckShipValidation(
        cowner: String,
        comp_email: String,
        comp_no: String,
        mobile: String,
        email: String,
        industryCode: String,
        salesEmployeeCode: Int,
        contactName: String,
        billName: String,
        billZipcode: String,
        billCity: String,
        billAddressValue: String,
        countryname: String,
        billtoState: String,
        parentAccount: String,
        selectedZone: String,
        gst: String,
        shipping_spinner: String
    ): Boolean {

        if (cowner.isEmpty()) {
            showMessage(act, "Enter Company name")
            return false
        }
        if (comp_email.isEmpty()) {
            showMessage(act, "Enter Email")
            return false
        }
        if (comp_email.isNotEmpty() && Globals.isvalidateemail(binding.fragmentAddpartnergeneral.companyEmailValue)) {
            binding.fragmentAddpartnergeneral.companyEmailValue.requestFocus()
            return false
        }
        if (salesEmployeeCode == 0) {
            showMessage(act, "Select Sales Employee")
            return false
        }
        if (selectedZone.isEmpty() || selectedZone.equals("Zones", ignoreCase = true)) {
            showMessage(act, "Select Zone")
            return false
        }
        if (gst.isEmpty()) {
            showMessage(act, "GST is Required")
            return false
        }
        if (comp_no.isEmpty()) {
            showMessage(act, "Enter Company Contact No.")
            return false
        }
        if (contactName.isEmpty()) {
            showMessage(act, "Enter Contact Name")
            return false
        }
        if (mobile.isEmpty()) {
            showMessage(act, "Enter Contact person mobile number")
            return false
        }
        if (email.isEmpty()) {
            showMessage(act, "Enter Contact Email")
            return false
        }

        if (email.isNotEmpty() && Globals.isvalidateemail(binding.fragmentAddpartnercontact.emailValue)) {
            binding.fragmentAddpartnercontact.emailValue.requestFocus()
            return false
        }

        if (billName.isEmpty()) {
            showMessage(act, "Enter Billing Name")
            return false
        }
        if (billZipcode.isEmpty()) {
            showMessage(act, "Enter Billing Zipcode")
            return false
        }
        if (countryname.isEmpty()) {
            showMessage(act, "Select Bill To Country")
            return false
        }

        if (billtoState.trim().isEmpty()) {
            Log.d("checking", "Bill To State is null or empty")
            showMessage(act, "Select Bill To State")
            return false
        }

        if (billCity.isEmpty()) {
            showMessage(act, "Enter Bill To City")
            return false
        }
        if (billAddressValue.isEmpty()) {
            showMessage(act, "Enter Billing Address")
            return false
        }
        if (shipping_spinner.isEmpty()) {
            showMessage(act, "Select Shipping Type")
            return false
        }

        return true
    }


    private fun createBP(input: JsonObject) {
        val call = RetrofitClient.apiService.addNewCustomer(token, input)
        call.enqueue(object : Callback<CustomerBusinessRes> {
            override fun onResponse(
                call: Call<CustomerBusinessRes>, response: Response<CustomerBusinessRes>
            ) {
                binding.loader.loader.visibility = View.GONE
                if (response.body()?.status == 200) {
                    binding.fragmentAddpartnercontact.createButton.isEnabled = true

                    // fetchBusinessPartnertDataFromApi(this@AddBPCustomer)

                    Toast.makeText(this@AddBPCustomer, "Add Successfully", Toast.LENGTH_LONG).show()
                    finish()
                } else {
                    binding.loader.loader.visibility = View.GONE
                    binding.fragmentAddpartnercontact.createButton.isEnabled = true
                    Toast.makeText(
                        this@AddBPCustomer,
                        response.body()?.message ?: "Unknown error",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onFailure(call: Call<CustomerBusinessRes>, t: Throwable) {
                binding.fragmentAddpartnercontact.createButton.isEnabled = true
                binding.loader.loader.visibility = View.GONE
                Toast.makeText(
                    this@AddBPCustomer, t.message ?: "An error occurred", Toast.LENGTH_SHORT
                ).show()
            }
        })
    }


}
