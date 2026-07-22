package com.preetTractor.galaxyAndroid.ui.activity

import Event
import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.google.gson.JsonObject
import com.preetTractor.galaxyAndroid.BuildConfig
import com.preetTractor.galaxyAndroid.R
import com.preetTractor.galaxyAndroid.apiHelper.ApiClient
import com.preetTractor.galaxyAndroid.apiHelper.ApisInterface
import com.preetTractor.galaxyAndroid.data.ResponseGlobal
import com.preetTractor.galaxyAndroid.data.expense.newexpense.DataExpenseNewList
import com.preetTractor.galaxyAndroid.data.expense.newexpense.ResponseExpenseNew
import com.preetTractor.galaxyAndroid.databinding.ActivityExpenseRequestBinding
import com.preetTractor.galaxyAndroid.databinding.DialogLeaveStatusBinding
import com.preetTractor.galaxyAndroid.databinding.ItemConveyanceBinding
import com.preetTractor.galaxyAndroid.helper.Constant
import com.preetTractor.galaxyAndroid.helper.Globals
import com.preetTractor.galaxyAndroid.helper.Globals.hideKeyboard
import com.preetTractor.galaxyAndroid.helper.Globals.setTint
import com.preetTractor.galaxyAndroid.helper.Globals.transformIntoDatePicker
import com.preetTractor.galaxyAndroid.helper.PrefsByShubh
import com.preetTractor.galaxyAndroid.mvvmSetUp.DefaultMainRepositories
import com.preetTractor.galaxyAndroid.mvvmSetUp.MainRepos
import com.preetTractor.galaxyAndroid.mvvmSetUp.MainViewModel
import com.preetTractor.galaxyAndroid.mvvmSetUp.MainViewModelProvider
import com.preetTractor.galaxyAndroid.retrofit.RetrofitClient
import com.preetTractor.galaxyAndroid.ui.recyclerview.ExpenseAdapter
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ExpenseRequestActivity : BaseActivity() {
    lateinit var binding: ActivityExpenseRequestBinding
    private lateinit var expenseAdapter: ExpenseAdapter
    private var allExpenseStatusItemList = ArrayList<DataExpenseNewList>()
    var salesEmpoyeeCode = ""
    var salesEmpoyeeName = ""

    companion object {
        private const val TAG = "ExpenseRequestActivity"
    }

    lateinit var dialog: Dialog
    lateinit var viewModel: MainViewModel
    var fromDateString: String? =
        Globals.convert_dd_MM_yyyy_into_yyyy_MM_dd(Globals.getFirstDateofMonth()!!)
    var toDateString: String? = Globals.getTodaysDatervrsfrmt()

    private fun setUpViewModel() {
        val dispatchers: CoroutineDispatcher = Dispatchers.Main
        val mainRepos = DefaultMainRepositories() as MainRepos
        val fanxApi: ApisInterface = ApiClient().service(this)
        val viewModelProviderfactory =
            MainViewModelProvider(application, mainRepos, dispatchers, fanxApi)
        viewModel = ViewModelProvider(this, viewModelProviderfactory)[MainViewModel::class.java]

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExpenseRequestBinding.inflate(layoutInflater)

        setContentView(binding.root)

        dialog = Dialog(this)
        setUpViewModel()
        intialStatus()
//        apiCalling()

        salesEmpoyeeCode = intent.getStringExtra(Constant.WHERE_INTENT_VALUE_SALES).toString()
        salesEmpoyeeName = intent.getStringExtra(Constant.WHERE_INTENT_VALUE_SALES_NAME).toString()

        supportActionBar?.apply {
            title = salesEmpoyeeName
            setDisplayHomeAsUpEnabled(true)
            setHomeButtonEnabled(true)
        }


        callAllExpenseListTypeApi()


        binding.tvDate.text = Globals.getFirstDateofMonth()
        fromDateString = Globals.dateStringConvertToDesiredFormat(
            Globals.getFirstDateofMonth().toString(), "dd-MM-yyyy", "yyyy-MM-dd"
        )
        binding.tvDate.setOnClickListener {
            Globals.openDatePicker(binding.tvDate) { formattedDate ->
                fromDateString = formattedDate
                binding.tvDate.text = Globals.dateStringConvertToDesiredFormat(
                    formattedDate, "yyyy-MM-dd", "dd-MM-yyyy"
                )
                callAllExpenseListTypeApi()
            }
        }
        binding.tvToDate.text = Globals.getTodaysDate()
        toDateString = binding.tvToDate.text.toString()
        toDateString = Globals.dateStringConvertToDesiredFormat(
            Globals.getTodaysDate().toString(), "dd-MM-yyyy", "yyyy-MM-dd"
        )
        binding.tvToDate.setOnClickListener {
            Globals.openDatePicker(binding.tvToDate) { formattedDate ->
                // Set the formatted date (yyyy-MM-dd) in the EditText
                toDateString = formattedDate
                binding.tvToDate.text = Globals.dateStringConvertToDesiredFormat(
                    formattedDate, "yyyy-MM-dd", "dd-MM-yyyy"
                )
                callAllExpenseListTypeApi()
            }
        }


    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showStatusDialog(leaveStatusData: DataExpenseNewList) {

        val binding = DialogLeaveStatusBinding.inflate(LayoutInflater.from(this))

        dialog.setContentView(binding.root)

        // todo Set the dialog window to be larger
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )

        // Set up the spinner with an adapter
        binding.spinnerStatus.apply {
            adapter = ArrayAdapter(
                context,
                android.R.layout.simple_spinner_dropdown_item,
                resources.getStringArray(R.array.status_array)
            )
        }

        // Set the button click listener
        binding.btnSave.setOnClickListener {
            val selectedStatus = binding.spinnerStatus.selectedItem.toString()
            // Trigger your desired function here
            handleStatusSave(selectedStatus, leaveStatusData)
            dialog.dismiss()
        }

        // Show the dialog
        dialog.show()
    }

    private fun handleStatusSave(status: String, leaveStatusData: DataExpenseNewList) {
        // Handle the save action based on the selected status
        when (status) {
            "Approved" -> {
                changeLeaveStatusAPi(status, leaveStatusData)
            }

            "Rejected" -> {
                // Handle cancelled action
                changeLeaveStatusAPi(status, leaveStatusData)
            }

            "Pending" -> {
                changeLeaveStatusAPi(status, leaveStatusData)
                // Handle pending action
            }
        }
    }


    var isMiscellaneous = false
    private fun intialStatus() {
        expenseAdapter = ExpenseAdapter(
            allExpenseStatusItemList, this
        )
        expenseAdapter.setonStatusBtnClickListener { leaveStatusData, i ->
            if (leaveStatusData.salesemployeecode == PrefsByShubh.getSalesEmployeeCode()) {
                Globals.warningMessage(this, "Not Authorized")
            } else {
                showStatusDialog(leaveStatusData)
            }

        }

        expenseAdapter.setonItemClickListener { dataExpenseNewList, i ->
            isMiscellaneous =
                dataExpenseNewList.expenseName.equals("Miscellaneous", ignoreCase = true)

            openAlert(dataExpenseNewList)

        }

        binding.rgLeave.setOnCheckedChangeListener { group, checkedId ->
            val radio: RadioButton = findViewById(checkedId)
            when (checkedId) {
                R.id.allRB -> {
                    Log.e(TAG, "intialStatus: $allExpenseStatusItemList")
                    expenseAdapter.updateEmployeeListItems(allExpenseStatusItemList)
                }

                R.id.approvedRB -> {

                    var filterList =
                        allExpenseStatusItemList.filter { it.approvalStatus == "Approved" }
                    expenseAdapter.updateEmployeeListItems(filterList)
                }

                R.id.pendingRB -> {
                    var filterList =
                        allExpenseStatusItemList.filter { it.approvalStatus == "Pending" }
                    expenseAdapter.updateEmployeeListItems(filterList)
                }

                R.id.rejecedRB -> {
                    var filterList =
                        allExpenseStatusItemList.filter { it.approvalStatus == "Rejected" }
                    expenseAdapter.updateEmployeeListItems(filterList)
                }
            }


        }
    }

    private fun setAlertTitle(tvTitle: TextView, dataExpenseNewList: DataExpenseNewList) {
        tvTitle.text = "${dataExpenseNewList.expenseName} (${dataExpenseNewList.typeOfExpense})"
    }

    //todo mAlert
    private var mAlert: AlertDialog? = null

    // lateinit var mAlert: AlertDialog
    var expenseTypeInteger = -1
    var spinnerModeSelectedItem = ""
    var spinnerMealSelectedItem: String = ""
    private fun openAlert(dataExpenseNewList: DataExpenseNewList) {
        if (mAlert?.isShowing == true) {
            return // Exit if the dialog is already open
        }
        val alertBinding = ItemConveyanceBinding.inflate(LayoutInflater.from(this))

        val builder = AlertDialog.Builder(this)

        mAlert = builder.setView(alertBinding.root).setCancelable(true).create()
        mAlert?.window?.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.alert_bg))
        setAlertTitle(alertBinding.tvTitle, dataExpenseNewList)

        setViews(alertBinding, dataExpenseNewList)

        alertBinding.apply {
            tvDate.isEnabled = false
            tvDate.isClickable = false
            etAmount.isEnabled = false
            etAmount.isClickable = false
            etLocation.isEnabled = false
            etLocation.isClickable = false
            etToLocation.isEnabled = false
            etToLocation.isClickable = false
            spinnerMode.isEnabled = false
            spinnerMode.isClickable = false
            etRemark.isEnabled = false
            etRemark.isClickable = false
            etKm.isEnabled = false
            etKm.isClickable = false
        }

        if (isMiscellaneous) {
            alertBinding.apply {


                layoutAmount.visibility = View.GONE
                etLocation.visibility = View.GONE
                etToLocation.visibility = View.GONE
                spinnerMeal.visibility = View.GONE
                etRemark.visibility = View.VISIBLE
                etRemark.hint = "Remark"
                spinnerMode.visibility = View.GONE
                alertBinding.tvTitle.text = "Miscellaneous"
            }
        }
        callExpenseOneApi(dataExpenseNewList, alertBinding)


        alertBinding.btnCancel.setOnClickListener {
            hideKeyboard()
            expenseTypeInteger = -1
            mAlert?.dismiss()
        }
        alertBinding.btnClose.setOnClickListener {
            hideKeyboard()
            expenseTypeInteger = -1
            mAlert?.dismiss()
        }

        alertBinding.btnSave.visibility = View.INVISIBLE
        alertBinding.btnCancel.visibility = View.INVISIBLE


        alertBinding.tvDate.setText(
            Globals.dateStringConvertToDesiredFormat(
                dataExpenseNewList.fromDate, "yyyy-MM-dd", "dd/MM/yyyy"
            )
        )

        alertBinding.tvToDate.setText(
            Globals.dateStringConvertToDesiredFormat(
                dataExpenseNewList.toDate, "yyyy-MM-dd", "dd/MM/yyyy"
            )
        )
        alertBinding.tvToDate.transformIntoDatePicker(
            alertBinding.tvToDate.context, "dd/MM/yyyy", null
        )
        alertBinding.tvDate.transformIntoDatePicker(alertBinding.tvDate.context, "dd/MM/yyyy", null)

        alertBinding.etAmount.setText(dataExpenseNewList.expenseAmount)
        alertBinding.etLocation.setText(dataExpenseNewList.address)
        alertBinding.etToLocation.setText(dataExpenseNewList.address2)
        alertBinding.etNoOfPeople.setText(dataExpenseNewList.numPerson)
        alertBinding.etHotelName.setText(dataExpenseNewList.hotelName)
        alertBinding.edNameOfPersons.setText(dataExpenseNewList.personsName)

        if (dataExpenseNewList.expenseName.equals("Food")) {
            // alertBinding.etRemark.setText(dataExpenseNewList.mealStatus)
        } else {
            alertBinding.etRemark.setText(dataExpenseNewList.remarks)
        }

        alertBinding.ivViewAttachment.setOnClickListener {
            showPopup(fileString, "IMAGE")
        }




        mAlert?.show()
    }

    var fileString = ""

    private fun callExpenseOneApi(
        dataExpenseNewList: DataExpenseNewList, alertBinding: ItemConveyanceBinding
    ) {
        val jsonObject = JsonObject().apply {
            addProperty("id", dataExpenseNewList.id)
        }
        val call: Call<ResponseExpenseNew> = ApiClient().service(this).getExpenseOne(jsonObject)
        call.enqueue(object : Callback<ResponseExpenseNew> {
            override fun onResponse(
                call: Call<ResponseExpenseNew>, response: Response<ResponseExpenseNew>
            ) {
                if (response != null) {
                    //  binding.spinKitLoader.visibility = View.GONE
                    if (response.body()!!.status == 200) {
                        if (response.body()!!.data.isNotEmpty()) {
                            if (response.body()!!.data[0].attach.isNotEmpty()) {
                                alertBinding.ivViewAttachment.visibility = View.VISIBLE
                                fileString = response.body()!!.data[0].attach[0].File

                            } else {
                                alertBinding.ivViewAttachment.visibility = View.GONE
                            }
                        }

                    } else if (response.body()!!.status == 201) {
                        Globals.errorMessage(
                            this@ExpenseRequestActivity, response.body()!!.message.toString()
                        )
//                        Toast.makeText(
//                            this@AddExpenseActivity,
//                            response.body()!!.message,
//                            Toast.LENGTH_SHORT
//                        ).show()
                    }
                }
            }

            override fun onFailure(call: Call<ResponseExpenseNew>, t: Throwable) {
                //  binding.spinKitLoader.visibility = View.GONE
                Globals.errorMessage(this@ExpenseRequestActivity, t.message.toString())
//                Toast.makeText(this@AddExpenseActivity, "" + t.message, Toast.LENGTH_SHORT).show()
                /* loader.setVisibility(View.GONE);
                alertDialog.dismiss();*/
            }
        })
    }


    private fun showPopup(_url: String, _msg: String) {
        val dialog = Dialog(this)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setContentView(R.layout.custom_dialogs)

        val closeButton = dialog.findViewById<ImageButton>(R.id.ivClose)
        val tvLocation = dialog.findViewById<TextView>(R.id.tvLocation)
        val ivLocation = dialog.findViewById<ImageView>(R.id.iv_location)
        val ivUser = dialog.findViewById<ImageView>(R.id.iv_User)
        val progressBar = dialog.findViewById<ProgressBar>(R.id.progressBar2)

        // Show progress bar initially
        progressBar.visibility = View.VISIBLE

        tvLocation.text = _url
        ivLocation.setTint(R.color.red)

        if (_msg.contains("ADDRESS", ignoreCase = true)) {
            tvLocation.visibility = View.VISIBLE
            ivLocation.visibility = View.VISIBLE
            ivUser.visibility = View.GONE
        } else {
            tvLocation.visibility = View.GONE
            ivLocation.visibility = View.GONE
            ivUser.visibility = View.VISIBLE
        }


        Glide.with(this).load(BuildConfig.IMAGE_URL + _url).placeholder(R.drawable.ic_user)
            .error(R.drawable.ic_user).listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: com.bumptech.glide.request.target.Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    // Hide progress bar when loading fails
                    progressBar.visibility = View.GONE
                    return false // Allow Glide to handle the error drawable
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: com.bumptech.glide.request.target.Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    // Hide progress bar when the image is loaded successfully
                    progressBar.visibility = View.GONE
                    return false // Allow Glide to display the image
                }
            }).into(ivUser)

        closeButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun setViews(
        alertBinding: ItemConveyanceBinding, dataExpenseNewList: DataExpenseNewList
    ) {
        if (dataExpenseNewList.expenseName == "Conveyance") {
            if (dataExpenseNewList.typeOfExpense.equals("Travelling", ignoreCase = true)) {
                alertBinding.etToLocation.visibility = View.VISIBLE
            } else {
                alertBinding.etToLocation.visibility = View.GONE
            }
            setSpinner(alertBinding, dataExpenseNewList)
            alertBinding.groupLodge.visibility = View.GONE
            alertBinding.spinnerMeal.visibility = View.GONE
            alertBinding.spinnerMode.visibility = View.VISIBLE
            alertBinding.etRemark.visibility = View.VISIBLE
        } else if (dataExpenseNewList.expenseName == "Lodging") {
            alertBinding.etToLocation.visibility = View.GONE
            alertBinding.etRemark.visibility = View.GONE
            alertBinding.spinnerMeal.visibility = View.GONE
            alertBinding.spinnerMode.visibility = View.GONE
            alertBinding.groupLodge.visibility = View.VISIBLE
            setSpinner(alertBinding, dataExpenseNewList)
        } else if (dataExpenseNewList.expenseName == "Food") {
            alertBinding.etToLocation.visibility = View.GONE
            alertBinding.etRemark.visibility = View.INVISIBLE
            alertBinding.spinnerMeal.visibility = View.VISIBLE
            alertBinding.etRemark.hint = "Meal"
            alertBinding.spinnerMode.visibility = View.VISIBLE
            alertBinding.groupLodge.visibility = View.GONE


            setSpinner(alertBinding, dataExpenseNewList)
        } else {
            alertBinding.etRemark.hint = "Remark"
        }/* alertBinding.tvCamera.setOnClickListener {
             dispatchMakeModelPictureIntent()
         }*/
        alertBinding.tvCamera.visibility = View.INVISIBLE

    }


    private fun setSpinner(
        alertBinding: ItemConveyanceBinding, dataExpenseNewList: DataExpenseNewList
    ) {
        if (dataExpenseNewList.expenseName.equals("Food")) {
            alertBinding.headingBillCopy.visibility = View.VISIBLE
        } else {
            alertBinding.headingBillCopy.visibility = View.GONE
        }

        var mealType = arrayOf(
            "Breakfast", "Lunch", "Dinner"
        )
        val mealSpinner = ArrayAdapter<String>(
            this, android.R.layout.simple_spinner_item, mealType
        )
        mealSpinner.setDropDownViewResource(R.layout.custom_spinner)
        alertBinding.spinnerMeal.adapter = mealSpinner
        alertBinding.spinnerMeal.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>, view: View?, position: Int, id: Long
                ) {
                    // Get the selected item text from the Spinner
                    val selectedItem = parent.getItemAtPosition(position).toString()
                    (parent.getChildAt(0) as TextView).setTextColor(Color.BLACK)
                    spinnerMealSelectedItem = selectedItem
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // Handle case where no item is selected (optional)
                }
            }
        var modes =
            if (dataExpenseNewList.expenseName.equals("Food")) arrayOf("Yes", "No") else arrayOf(
                "Km", "Petrol", "Train/cab/Bus", "Tolls"
            )
        val modeSpinner = ArrayAdapter<String>(
            this, android.R.layout.simple_spinner_item, modes
        )
        modeSpinner.setDropDownViewResource(R.layout.custom_spinner)
        alertBinding.spinnerMode.adapter = modeSpinner

        if (dataExpenseNewList.mode.equals("Km", ignoreCase = true)) {
            alertBinding.etKm.visibility = View.VISIBLE
        } else {
            alertBinding.etKm.visibility = View.GONE
        }

        alertBinding.spinnerMode.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>, view: View?, position: Int, id: Long
                ) {
                    // Get the selected item text from the Spinner
                    val selectedItem = parent.getItemAtPosition(position).toString()
                    (parent.getChildAt(0) as TextView).setTextColor(Color.BLACK)
                    spinnerModeSelectedItem = selectedItem
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // Handle case where no item is selected (optional)
                }
            }

        // Auto-select the item based on the mode key
        var modeToSelect = ""
        if (dataExpenseNewList.expenseName == "Food") {
            modeToSelect = dataExpenseNewList.mealStatus
        } else {
            modeToSelect = dataExpenseNewList.mode
        }

        val indexToSelect = modes.indexOf(modeToSelect)

        // Set the spinner selection if the mode exists in the array
        if (indexToSelect >= 0) {
            alertBinding.spinnerMode.setSelection(indexToSelect)
        }
        (if (dataExpenseNewList.expenseName == "Food") {
            if (modeToSelect == "Yes") View.VISIBLE
            else View.INVISIBLE
        } else {
            View.VISIBLE
        }).also { alertBinding.tvCamera.visibility = it }
        alertBinding.spinnerMeal.setSelection(
            if (dataExpenseNewList.remarks.equals(
                    "Breakfast", ignoreCase = true
                )
            ) 0 else if (dataExpenseNewList.remarks.equals("Lunch", ignoreCase = true)) 1 else 2
        )
    }

    fun callAllExpenseListTypeApi() {
        if (Globals.checkForInternet(this)) {
            val jsonObject = JsonObject().apply {
                addProperty("ToDate", toDateString)
                addProperty("FromDate", fromDateString)
                addProperty("Datefilter", "")
                addProperty("TypeOfExpense", "")
                addProperty("Status", "")
                addProperty("SalesEmployeeCode", salesEmpoyeeCode)
                addProperty("PageNo", "1")
                addProperty("MaxSize", "All")

            }
            Log.e(TAG, "jsonObject : ${jsonObject}")
            viewModel.getAllExpenseListApi(jsonObject, this)
            bindAllExpenseListObserver()
        }
    }

    private fun bindAllExpenseListObserver() {
        viewModel.allExpenseListData.observe(this, Event.EventObserver(onError = {
            binding.progressBar.visibility = View.GONE
        }, onSuccess = {
            binding.progressBar.visibility = View.GONE
            Log.e(TAG, "bindAllExpenseListObserver: ${it.data}")
            if (it.status == 200) {
                if (it.data.isEmpty()) {
                    allExpenseStatusItemList.clear()
                    expenseAdapter.updateEmployeeListItems(allExpenseStatusItemList)
                    binding.noDataFoundLayout.ivNoDataFound.visibility = View.VISIBLE
                    expenseAdapter.notifyDataSetChanged()
                } else {
                    binding.noDataFoundLayout.ivNoDataFound.visibility = View.GONE
                    binding.rvLeave.layoutManager = LinearLayoutManager(this)
                    allExpenseStatusItemList = it.data
                    binding.allRB.isChecked = true
                    binding.rvLeave.adapter = expenseAdapter
                    expenseAdapter.updateEmployeeListItems(allExpenseStatusItemList)
                    expenseAdapter.notifyDataSetChanged()

                }

            }

        }, onLoading = {
            binding.progressBar.visibility = View.VISIBLE

        }))
    }


    private fun changeLeaveStatusAPi(status: String, leaveStatusData: DataExpenseNewList) {
        binding.progressBar.visibility = View.VISIBLE

        val hde = JsonObject().apply {
            addProperty("id", leaveStatusData.id)
            addProperty("status", status)
            addProperty("salesemployeecode", PrefsByShubh.getSalesEmployeeCode())
            addProperty("updateDate", Globals.getTodaysDatervrsfrmt())
            addProperty("updateTime", Globals.getTCurrentTime())
        }
        val call = RetrofitClient.apiService.galaxyApproveExpenseApi(hde)

        call.enqueue(object : Callback<ResponseGlobal> {
            override fun onResponse(
                call: Call<ResponseGlobal>, response: Response<ResponseGlobal>
            ) {
                binding.progressBar.visibility = View.GONE
                response.body()?.let {
                    dialog.dismiss()
                    Log.e(TAG, "onResponse: ${it.message}")
                    when (it.status) {
                        200 -> {
                            Globals.successMessage(
                                this@ExpenseRequestActivity, "Updated SuccessFully"
                            )
                            callAllExpenseListTypeApi()
                        }

                        201 -> {
                            Globals.successMessage(this@ExpenseRequestActivity, it.message)
                        }

                        else -> {
                            Globals.successMessage(this@ExpenseRequestActivity, response.message())
                        }
                    }
                }
            }

            override fun onFailure(call: Call<ResponseGlobal>, t: Throwable) {
                Log.e(TAG, "onFailure: ${t.message}")
                Toast.makeText(
                    this@ExpenseRequestActivity, "Something went wrong", Toast.LENGTH_SHORT
                ).show()
                binding.progressBar.visibility = View.GONE
            }
        })
    }


}