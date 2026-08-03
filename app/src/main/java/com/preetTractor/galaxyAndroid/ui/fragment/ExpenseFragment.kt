package com.preetTractor.galaxyAndroid.ui.fragment

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.forEach
import androidx.core.view.isNotEmpty
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.chip.Chip
import com.google.gson.JsonObject
import com.preetTractor.galaxyAndroid.apiHelper.ApiClient
import com.preetTractor.galaxyAndroid.apiHelper.AppConstants
import com.preetTractor.galaxyAndroid.mvvmSetUp.MainViewModel
import com.preetTractor.galaxyAndroid.BuildConfig
import com.preetTractor.galaxyAndroid.R
import com.preetTractor.galaxyAndroid.data.ResponseGlobal
import com.preetTractor.galaxyAndroid.data.expense.newexpense.DataExpenseNewList
import com.preetTractor.galaxyAndroid.data.expense.newexpense.ResponseExpenseNew
import com.preetTractor.galaxyAndroid.data.expense.type.ExpenseTypeData
import com.preetTractor.galaxyAndroid.databinding.FragmentExpenseBinding
import com.preetTractor.galaxyAndroid.databinding.ItemConveyanceBinding
import com.preetTractor.galaxyAndroid.helper.Globals
import com.preetTractor.galaxyAndroid.helper.Globals.hideKeyboard
import com.preetTractor.galaxyAndroid.helper.Globals.setTint
import com.preetTractor.galaxyAndroid.helper.Globals.stringDateToDate
import com.preetTractor.galaxyAndroid.helper.Globals.transformIntoDatePickerWithLast90Days
import com.preetTractor.galaxyAndroid.helper.LocationPermissionHelper
import com.preetTractor.galaxyAndroid.helper.PrefsByShubh
import com.preetTractor.galaxyAndroid.ui.activity.MainActivity
import com.preetTractor.galaxyAndroid.ui.activity.addExpenses.AddExpenseActivity
import com.preetTractor.galaxyAndroid.ui.recyclerview.ExpenseAdapter
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date


class ExpenseFragment : Fragment() {
    val expenseTypeBoolean = MutableLiveData<String>()
    lateinit var binding: FragmentExpenseBinding
    lateinit var viewModel: MainViewModel
    private lateinit var adapter: ExpenseAdapter
    private var allExpenseList = ArrayList<DataExpenseNewList>()
    private var expenseTypeList = arrayListOf<ExpenseTypeData>()
    var expenseType = ""
    lateinit var dialog: Dialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentExpenseBinding.inflate(layoutInflater)
        return binding.root
    }

    companion object {
        private const val TAG = "ExpenseFragment"
    }

    // If you want to store the date in a string variable
    var dateString: String? = Globals.getTodaysDatervrsfrmt()
    var fromDateString: String? = Globals.getTodaysDatervrsfrmt()
    var toDateString: String? = Globals.getTodaysDatervrsfrmt()

    var fileString = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as MainActivity).viewModel
        dialog = Dialog(requireContext())

        binding.tvDate.text = Globals.getFirstDateofMonth()
        fromDateString=Globals.dateStringConvertToDesiredFormat(Globals.getFirstDateofMonth().toString(), "dd-MM-yyyy", "yyyy-MM-dd")
        binding.tvDate.setOnClickListener {
            Globals.openDatePicker(binding.tvDate) { formattedDate ->
                fromDateString = formattedDate
                binding.tvDate.text = Globals.dateStringConvertToDesiredFormat(formattedDate, "yyyy-MM-dd", "dd-MM-yyyy")
                callAllExpenseListTypeApi()
            }
        }
        binding.tvToDate.text = Globals.getTodaysDate()
        toDateString=binding.tvToDate.text.toString()
        toDateString=Globals.dateStringConvertToDesiredFormat(Globals.getTodaysDate().toString(), "dd-MM-yyyy", "yyyy-MM-dd")
        binding.tvToDate.setOnClickListener {
            Globals.openDatePicker(binding.tvToDate)
            { formattedDate ->
                // Set the formatted date (yyyy-MM-dd) in the EditText
                toDateString = formattedDate
                binding.tvToDate.text = Globals.dateStringConvertToDesiredFormat(
                    formattedDate,
                    "yyyy-MM-dd",
                    "dd-MM-yyyy"
                )
                callAllExpenseListTypeApi()
            }
        }
//        callAllExpenseListTypeApi()
        binding.fabAddExpense.setOnClickListener {
            if (expenseType.isNotEmpty()) {
                var intent = Intent(requireActivity(), AddExpenseActivity::class.java)
                intent.putExtra("expenseType", expenseType)
                startActivity(intent)
            } else {
                Toast.makeText(requireContext(), "Please Select Expense Type", Toast.LENGTH_SHORT)
                    .show()
            }


        }
        adapter = ExpenseAdapter(allExpenseList, requireContext())
        binding.rvLeave.adapter = adapter


        adapter.setonItemClickListener { dataExpenseNewList, i ->
            isMiscellaneous =
                dataExpenseNewList.expenseName.equals("Miscellaneous", ignoreCase = true)
            if(dataExpenseNewList.approvalStatus == "Pending")
                    openAlert(dataExpenseNewList)
        }
        expenseFilter()
    }

    var isMiscellaneous = false

    override fun onResume() {
        super.onResume()
        Log.e(TAG, "onResume: ")
        callExpenseTypeApi()
    }

    private fun setupChipGroup() {
        binding.chipGroup.forEach { child ->
            (child as? Chip)?.setOnCheckedChangeListener { _, _ ->
                updatePersonCheckStatus(child, child.id)
            }
        }


    }


    private fun createTagChip(context: Context, chipName: String, idChip: Int): Chip {
        return Chip(context).apply {
            text = chipName
            id = idChip
            isChecked = idChip == 1
            setChipBackgroundColorResource(R.color.purple_700)
            isCloseIconVisible = false
            tag = chipName
            isClickable = true
            isCheckable = true
            checkedIcon = null
            isChipIconVisible = false
            setTextAppearance(R.style.ChipTextAppearance)
            setTextColor(ContextCompat.getColor(context, R.color.white))

        }

    }

    var mode = "Km"




    private fun expenseFilter() {
        binding.rgLeave.setOnCheckedChangeListener { group, checkedId ->
            val radio: RadioButton = requireActivity().findViewById(checkedId)
            when (checkedId) {
                R.id.allRB -> {
                    Log.e(TAG, "intialStatus: ${allExpenseList.toString()}")
                    adapter.updateEmployeeListItems(allExpenseList)
                    binding.tvNoData.visibility=if (adapter.itemCount>0) View.GONE else View.VISIBLE

                }

                R.id.approvedRB -> {

                    var filterList = allExpenseList.filter { it.approvalStatus == "Approved" }
                    adapter.updateEmployeeListItems(filterList)
                    binding.tvNoData.visibility=if (adapter.itemCount>0) View.GONE else View.VISIBLE

                }

                R.id.pendingRB -> {
                    var filterList =
                        allExpenseList.filter { it.approvalStatus == "Pending" }
                    adapter.updateEmployeeListItems(filterList)
                    binding.tvNoData.visibility=if (adapter.itemCount>0) View.GONE else View.VISIBLE

                }

                R.id.rejecedRB -> {
                    var filterList =
                        allExpenseList.filter { it.approvalStatus == "Rejected" }
                    adapter.updateEmployeeListItems(filterList)
                    binding.tvNoData.visibility=if (adapter.itemCount>0) View.GONE else View.VISIBLE
                }
            }


        }
    }

    private fun addExpenseApi(mAlert: AlertDialog, jsonObject: JsonObject) {

        if (Globals.checkForInternet(requireActivity())) {
            viewModel.createExpenseApi(jsonObject, requireContext())
            bindAddExpenseObserver(mAlert)
        }

    }

    var payRequestExpenseType: String = "";

    fun callAllExpenseListTypeApi() {
        if (Globals.checkForInternet(requireActivity())) {
            val jsonObject = JsonObject().apply {
                addProperty("ToDate", toDateString)
                addProperty("FromDate", fromDateString)
                addProperty("Datefilter", "")
                addProperty("Status", "")
                addProperty("TypeOfExpense", payRequestExpenseType)
                addProperty("SalesEmployeeCode", PrefsByShubh.getSalesEmployeeCode())
                addProperty("PageNo", "1")
                addProperty("MaxSize", "All")

            }
            Log.e(TAG, "jsonObject : ${jsonObject}")
            viewModel.getAllExpenseListApi(jsonObject, requireContext())
            bindAllExpenseListObserver()
        }
    }

    private fun bindAllExpenseListObserver() {
        viewModel.allExpenseListData.observe(viewLifecycleOwner, Event.EventObserver(
            onError = {
                binding.spinKitLoader.visibility = View.GONE
            },
            onSuccess = {
                binding.spinKitLoader.visibility = View.GONE
                Log.e(TAG, "bindAllExpenseListObserver: ${it.data}")
                if (it.status == 200) {

                    if (it.data.isEmpty()){
                        allExpenseList.clear()
                        allExpenseList.addAll(it.data)
                        binding.noDataFoundLayout.ivNoDataFound.visibility = View.GONE
                        adapter.updateEmployeeListItems(listOf())
                        binding.tvNoData.visibility=if (adapter.itemCount>0) View.GONE else View.VISIBLE
                        adapter.notifyDataSetChanged()

                    }else{
                        binding.noDataFoundLayout.ivNoDataFound.visibility = View.GONE
                        binding.rvLeave.layoutManager = LinearLayoutManager(requireContext())

                        allExpenseList = it.data

                        var filterList = allExpenseList!!.filter {
                            stringDateToDate(it.fromDate).after(fromDateString?.let { it1 ->
                                stringDateToDate(
                                    it1
                                )
                            })|| stringDateToDate(it.fromDate) == fromDateString?.let { it1 ->
                                stringDateToDate(
                                    it1
                                )
                            } && stringDateToDate(it.toDate).before(toDateString?.let { it1 ->
                                stringDateToDate(
                                    it1
                                )
                            })|| stringDateToDate(it.toDate) == toDateString?.let { it1 ->
                                stringDateToDate(
                                    it1
                                )
                            }
                        }

                        binding.allRB.isChecked = true
                        adapter.updateEmployeeListItems(filterList)
                        binding.tvNoData.visibility=if (adapter.itemCount>0) View.GONE else View.VISIBLE
                        adapter.notifyDataSetChanged()


                    }

                }


            },
            onLoading = {
                binding.spinKitLoader.visibility = View.VISIBLE

            }
        ))
    }

    fun callExpenseTypeApi() {
        if (Globals.checkForInternet(requireActivity())) {
            viewModel.getExpenseTypeApi(requireContext())
            bindListObserver()
        }
    }

    private fun bindAddExpenseObserver(mAlert: AlertDialog) {
        viewModel.createExpenseTypeData.observe(viewLifecycleOwner, Event.EventObserver(
            onError = {
                binding.spinKitLoader.visibility = View.GONE
                mAlert.dismiss()

            },
            onSuccess = {
                binding.spinKitLoader.visibility = View.GONE
                mAlert.dismiss()


            },
            onLoading = {
                binding.spinKitLoader.visibility = View.VISIBLE

            }
        ))
    }

    private fun bindListObserver() {
        viewModel.expenseTypeData.observe(viewLifecycleOwner, Event.EventObserver(
            onError = {
                binding.spinKitLoader.visibility = View.GONE
            },
            onSuccess = {
                binding.spinKitLoader.visibility = View.GONE
                if (it.status == 200) {
                    if (it.data.isNotEmpty()) {

                        expenseTypeList = (it.data)
                        val firstChipId = it.data[0].id
                        binding.chipGroup.removeAllViews()
                        it.data.forEach { data ->
                            val chip = createTagChip(requireContext(), data.name, data.id)
                            binding.chipGroup.addView(chip)
                            updatePersonCheckStatus(chip, data.id)
                        }
                        setupChipGroup()
                        if (binding.chipGroup.isNotEmpty()) {
                            binding.chipGroup.check(firstChipId)
                        }
                    }


                }

            },
            onLoading = {
                binding.spinKitLoader.visibility = View.VISIBLE
            }
        ))
    }

    private fun setFilterAppearance(chip: Chip) {
        if (chip.isChecked) {
            chip.setTextColor(
                (ContextCompat.getColorStateList(
                    requireContext(),
                    R.color.white
                ))
            )
            chip.setChipStrokeColorResource(R.color.purple_700)
            chip.chipBackgroundColor = ColorStateList.valueOf(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.purple_700
                )
            )
        } else {
            chip.setTextColor(
                (ContextCompat.getColorStateList(
                    requireContext(),
                    R.color.black_text_color
                ))
            )
            chip.chipBackgroundColor = ColorStateList.valueOf(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.dividerColor
                )
            )

            chip.setChipStrokeColorResource(R.color.dividerColor)
        }
    }

    private fun updatePersonCheckStatus(chip: Chip, filter: Int) {
        when (filter) {

            1 -> if (chip.isChecked) {
                Log.d(TAG, "updatePersonCheckStatus: ${chip.text},${chip.isChecked}")
                expenseType = chip.text.toString()
                payRequestExpenseType = chip.text.toString()
                callAllExpenseListTypeApi()
            } else {
                Log.d(TAG, "updatePersonCheckStatus: ${chip.text},${chip.isChecked}")
            }

            2 -> if (chip.isChecked) {
                Log.d(TAG, "updatePersonCheckStatus: ${chip.text},${chip.isChecked}")
                expenseType = chip.text.toString()
                payRequestExpenseType = chip.text.toString()
                callAllExpenseListTypeApi()
            } else {
                Log.d(TAG, "updatePersonCheckStatus: ${chip.text},${chip.isChecked}")
            }

            3 -> if (chip.isChecked) {
                expenseType = chip.text.toString()
                payRequestExpenseType = chip.text.toString()
                callAllExpenseListTypeApi()
            } else {
            }

            4 -> if (chip.isChecked) {

                expenseType = "Misc"
                payRequestExpenseType = chip.text.toString()
                callAllExpenseListTypeApi()

            } else {
            }


        }
        setFilterAppearance(chip)
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
    //todo new key for Amount calculation at Km
    var basePriceOnRoll = PrefsByShubh.getString("expenseRate","0")
    private fun openAlert(dataExpenseNewList: DataExpenseNewList)
    {

        if (mAlert?.isShowing == true) {
            return // Exit if the dialog is already open
        }
        val alertBinding =
            ItemConveyanceBinding.inflate(LayoutInflater.from(requireContext()))

        val builder = AlertDialog.Builder(requireContext())
       /* // Check if the dialog is already showing
        if (mAlert?.isShowing == true) {
            return // Exit if the dialog is already open
        }*/

        mAlert = builder.setView(alertBinding.root)
            .setCancelable(true)
            .create()

        mAlert?.window?.setBackgroundDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                R.drawable.alert_bg
            )
        )


        setAlertTitle(alertBinding.tvTitle, dataExpenseNewList)

        setViews(alertBinding, dataExpenseNewList)

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
        alertBinding.btnSave.setOnClickListener {
            hideKeyboard()
            if (Globals.checkForInternet(requireActivity())) {
                if (isMiscellaneous) {
                    addExpense(alertBinding, dataExpenseNewList)
                } else {
                    if (alertBinding.etAmount.text.toString().isNotEmpty()) {
                        if (alertBinding.etLocation.text.toString().isNotEmpty()) {
                            if (spinnerModeSelectedItem.equals("Km", ignoreCase = true)) {
                                if (alertBinding.etKm.text.toString().trim().isEmpty()) {
                                    Globals.errorMessage(
                                        requireContext(),
                                        "Please add Km"
                                    )

                                }else{
                                    addExpense(alertBinding, dataExpenseNewList)
                                }
                            }else{
                                addExpense(alertBinding, dataExpenseNewList)
                            }
                            // addExpense(alertBinding) 123

                        } else {
                            Globals.errorMessage(requireContext(), "Please enter location")
                        }
                    } else {
                        Globals.errorMessage(requireContext(), "Please enter amount")
                    }
                }


            }


        }

        alertBinding.tvDate.setText(
            Globals.dateStringConvertToDesiredFormat(
                dataExpenseNewList.fromDate ?: "", "yyyy-MM-dd", "dd/MM/yyyy"
            )
        )

        alertBinding.tvToDate.setText(
            Globals.dateStringConvertToDesiredFormat(
                dataExpenseNewList.toDate ?: "", "yyyy-MM-dd", "dd/MM/yyyy"
            )
        )
        alertBinding.tvToDate.transformIntoDatePickerWithLast90Days(
            alertBinding.tvToDate.context,
            "dd/MM/yyyy",
            null
        )
        alertBinding.tvDate.transformIntoDatePickerWithLast90Days(alertBinding.tvDate.context, "dd/MM/yyyy", null)

        alertBinding.etAmount.setText(dataExpenseNewList.expenseAmount)
        alertBinding.etLocation.setText(dataExpenseNewList.address)
        alertBinding.etToLocation.setText(dataExpenseNewList.address2)
        alertBinding.etNoOfPeople.setText(dataExpenseNewList.numPerson)
        alertBinding.edNameOfPersons.setText(dataExpenseNewList.personsName)
        alertBinding.etHotelName.setText(dataExpenseNewList.hotelName)
        alertBinding.etKm.setText(dataExpenseNewList.km)

        if (dataExpenseNewList.expenseName.equals("Food")) {
            // alertBinding.etRemark.setText(dataExpenseNewList.mealStatus)
        } else {
            alertBinding.etRemark.setText(dataExpenseNewList.remarks)
        }

        alertBinding.ivViewAttachment.setOnClickListener {
            showPopup(fileString, "IMAGE")
        }



        if (!dataExpenseNewList.approvalStatus.equals("Pending",ignoreCase = true)){
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
                tvCamera.visibility=View.INVISIBLE
               btnSave.visibility=View.INVISIBLE
               btnCancel.visibility=View.INVISIBLE
            }
        }


        alertBinding.etKm.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                var calcAMount = "0"
                calcAMount = if (p0.toString().isNotEmpty()) {
                    p0.toString()
                } else {
                    "0"
                }

                if (spinnerModeSelectedItem.equals("Km", ignoreCase = true)) {
                    alertBinding.etAmount.setText("${basePriceOnRoll!!.toInt() * calcAMount.toInt()}")
                } else {

                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })

        mAlert?.show()
    }


    private fun callExpenseOneApi(
        dataExpenseNewList: DataExpenseNewList,
        alertBinding: ItemConveyanceBinding
    ) {
        val jsonObject = JsonObject().apply {
            addProperty("id", dataExpenseNewList.id)
        }
        val call: Call<ResponseExpenseNew> =
            ApiClient().service(requireActivity())
                .getExpenseOne(jsonObject)
        call.enqueue(object : Callback<ResponseExpenseNew> {
            override fun onResponse(
                call: Call<ResponseExpenseNew>,
                response: Response<ResponseExpenseNew>
            ) {
                if (response != null) {
                    binding.spinKitLoader.visibility = View.GONE
                    if (response.body()!!.status == 200
                    ) {
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
                            requireActivity(),
                            response.body()!!.message.toString()
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
                binding.spinKitLoader.visibility = View.GONE
                Globals.errorMessage(requireActivity(), t.message.toString())
//                Toast.makeText(this@AddExpenseActivity, "" + t.message, Toast.LENGTH_SHORT).show()
                /* loader.setVisibility(View.GONE);
                alertDialog.dismiss();*/
            }
        })
    }


    private fun showPopup(_url: String, _msg: String) {
        val dialog = Dialog(requireContext())
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



        Glide.with(requireContext()).load(BuildConfig.IMAGE_URL + _url)
            .placeholder(R.drawable.ic_user)
            .error(R.drawable.ic_user)
            .listener(object : RequestListener<Drawable> {
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
            })
            .into(ivUser)

        closeButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun setViews(
        alertBinding: ItemConveyanceBinding,
        dataExpenseNewList: DataExpenseNewList
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
        }
        alertBinding.tvCamera.setOnClickListener {
            if (!LocationPermissionHelper.hasLocationPermission(requireContext())) {
                Toast.makeText(requireActivity(), "Please Grant All Permissions", Toast.LENGTH_SHORT).show()
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.data = Uri.parse("package:${requireActivity().packageName}")
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            } else {

                //startService()
                dispatchMakeModelPictureIntent()
            }


         //   dispatchMakeModelPictureIntent()
        }

    }


    private fun setSpinner(
        alertBinding: ItemConveyanceBinding,
        dataExpenseNewList: DataExpenseNewList
    ) {
        if (dataExpenseNewList.expenseName == "Food") {
            alertBinding.headingBillCopy.visibility = View.VISIBLE
        } else {
            alertBinding.headingBillCopy.visibility = View.GONE
        }

        var mealType = arrayOf(
            "Breakfast",
            "Lunch",
            "Dinner"
        )
        val mealSpinner = ArrayAdapter<String>(
            requireActivity(),
            android.R.layout.simple_spinner_item,
            mealType
        )
        mealSpinner.setDropDownViewResource(R.layout.custom_spinner)
        alertBinding.spinnerMeal.adapter = mealSpinner
        alertBinding.spinnerMeal.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
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
            if (dataExpenseNewList.expenseName == "Food") arrayOf("Yes", "No") else arrayOf(
                "Km",
                "Petrol",
                "Train/cab/Bus",
                "Tolls"
            )
        val modeSpinner = ArrayAdapter<String>(
            requireActivity(),
            android.R.layout.simple_spinner_item,
            modes
        )
        modeSpinner.setDropDownViewResource(R.layout.custom_spinner)
        alertBinding.spinnerMode.adapter = modeSpinner

        alertBinding.spinnerMode.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    // Get the selected item text from the Spinner
                    val selectedItem = parent.getItemAtPosition(position).toString()
                    (parent.getChildAt(0) as TextView).setTextColor(Color.BLACK)
                    spinnerModeSelectedItem = selectedItem

                    if (spinnerModeSelectedItem.equals("Km", ignoreCase = true)) {
                        setUpExpenseViews(alertBinding, true)

                    } else {
                        setUpExpenseViews(alertBinding, false)
                        /*alertBinding.apply {
                            etKm.visibility=View.GONE
                        }*/

                    }
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
            if (modeToSelect == "Yes")
                View.VISIBLE
            else
                View.INVISIBLE
        } else {
            View.VISIBLE
        }).also { alertBinding.tvCamera.visibility = it }
        alertBinding.spinnerMeal.setSelection(
            if (dataExpenseNewList.remarks.equals(
                    "Breakfast",
                    ignoreCase = true
                )
            ) 0 else if (dataExpenseNewList.remarks.equals("Lunch", ignoreCase = true)) 1 else 2
        )
    }


    private fun setUpExpenseViews(alertBinding: ItemConveyanceBinding, isKmSelected: Boolean) {
        if (isKmSelected) {
            alertBinding.apply {
                etKm.visibility = View.VISIBLE

                etAmount.isEnabled = false
            }
        } else {
            alertBinding.apply {
                etKm.visibility = View.GONE
                etAmount.isEnabled = true

            }
        }

    }

    private fun addExpense(
        alertBinding: ItemConveyanceBinding,
        dataExpenseNewList: DataExpenseNewList
    ) {
        val builder: MultipartBody.Builder = MultipartBody.Builder()
        builder.setType(MultipartBody.FORM)

        builder.addFormDataPart("id", dataExpenseNewList.id)
        builder.addFormDataPart("type_of_expense", dataExpenseNewList.typeOfExpense)
        builder.addFormDataPart("createDate", dataExpenseNewList.createDate)
        builder.addFormDataPart("updateDate", Globals.getTodaysDatervrsfrmt()!!)
        builder.addFormDataPart("createTime", dataExpenseNewList.createTime)
        builder.addFormDataPart("updateTime", Globals.getTCurrentTime()!!)
        builder.addFormDataPart("updatedBy", PrefsByShubh.getSalesEmployeeCode()!!)
        builder.addFormDataPart("salesemployeecode", dataExpenseNewList.salesemployeecode)
        if (dataExpenseNewList.expenseName.equals("Conveyance")) {
            builder.addFormDataPart("expense_name", "Conveyance")
        }
        if (dataExpenseNewList.expenseName.equals("Lodging")) {
            builder.addFormDataPart("expense_name", "Lodging")
        }
        if (dataExpenseNewList.expenseName.equals("Food")) {
            builder.addFormDataPart("expense_name", "Food")
        }


        if (dataExpenseNewList.expenseName.equals("Miscellaneous")) {
            builder.addFormDataPart("expense_name", "Miscellaneous")
        }

        builder.addFormDataPart(
            "from_date",
            Globals.convert_dd_MM_yyyy_into_yyyy_MM_ddDASH(alertBinding.tvDate.text.toString())
        )

        builder.addFormDataPart(
            "to_date",
            Globals.convert_dd_MM_yyyy_into_yyyy_MM_ddDASH(alertBinding.tvToDate.text.toString())
        )

        builder.addFormDataPart(
            "num_person",
            alertBinding.etNoOfPeople.text.toString()
        )

        builder.addFormDataPart(
            "persons_name",
            alertBinding.edNameOfPersons.text.toString()
        )

        builder.addFormDataPart(
            "meal_status",
            spinnerModeSelectedItem
        )

        builder.addFormDataPart(
            "hotel_name",
            alertBinding.etHotelName.text.toString()
        )

        builder.addFormDataPart(
            "mode",
            spinnerModeSelectedItem
        )
        builder.addFormDataPart("latitude", Globals.globalLatitude.toString())
        builder.addFormDataPart("latitude2", Globals.globalLatitude.toString())
        builder.addFormDataPart("longitude", Globals.globalLongitude.toString())
        builder.addFormDataPart("longitude2", Globals.globalLongitude.toString())
        builder.addFormDataPart("address", alertBinding.etLocation.text.toString())
        builder.addFormDataPart("address2", alertBinding.etToLocation.text.toString())
        builder.addFormDataPart("expense_amount", alertBinding.etAmount.text.toString())
        builder.addFormDataPart(
            "remarks",
            if (dataExpenseNewList.expenseName.equals("Food")) spinnerMealSelectedItem else alertBinding.etRemark.text.toString()
        )
        val emptyArray = mutableListOf<String>()
        /*   builder.addFormDataPart("attach", "")*/
        if (currentPhotoPath.isNotEmpty()) {
            val compressedImageFile = Globals.compressImageFile(requireContext(),File(currentPhotoPath))
            try {
                val file: File = File(currentPhotoPath)
                builder.addFormDataPart(
                    "attach", compressedImageFile.name,

                )
            } catch (e: java.lang.Exception) {
                builder.addFormDataPart(
                    "attach", "",
                    "".toRequestBody("multipart/form-data".toMediaTypeOrNull())
                )
                e.printStackTrace()
            }
        } else {
            builder.addFormDataPart("attach", "")
        }
        val requestBody: MultipartBody = builder.build()
        Log.e("payload--->", requestBody.toString())
        // photoList.add(requestBody.toString())
        callExpenseCreateMultipart(requestBody, alertBinding)
    }

    private fun callExpenseCreateMultipart(
        requestBody: MultipartBody,
        alertBinding: ItemConveyanceBinding
    ) {

        alertBinding.btnSave.isEnabled=false
        alertBinding.btnSave.alpha=0.3f
        binding.spinKitLoader.visibility = View.VISIBLE
        val call: Call<ResponseGlobal> =
            ApiClient().service(requireActivity()).updateExpenseApiMultipart(requestBody)
        call.enqueue(object : Callback<ResponseGlobal> {
            override fun onResponse(
                call: Call<ResponseGlobal>,
                response: Response<ResponseGlobal>
            ) {
                if (response != null) {
                    binding.spinKitLoader.visibility = View.GONE
                    if (response.body()!!.status == 200
                    ) {
                        alertBinding.btnSave.isEnabled=true
                        alertBinding.btnSave.alpha=1f
                        Globals.successMessage(requireActivity(), "Updated SuccessFully")
                        currentPhotoPath = ""
                        mAlert?.dismiss()
                        isMiscellaneous = false
                        callAllExpenseListTypeApi()

                    } else if (response.body()!!.status == 201) {
                        alertBinding.btnSave.isEnabled=true
                        alertBinding.btnSave.alpha=1f
                        Globals.errorMessage(
                            requireActivity(),
                            response.body()!!.message.toString()
                        )

                    }
                }
            }

            override fun onFailure(call: Call<ResponseGlobal>, t: Throwable) {
                alertBinding.btnSave.isEnabled=true
                alertBinding.btnSave.alpha=1f
                binding.spinKitLoader.visibility = View.GONE
                Globals.errorMessage(requireActivity(), t.message.toString())

            }
        })
    }

    val REQUEST_IMAGE_MAKE_MODEL_PHOTO = 123
    var makeModelPhoto = ""
    lateinit var fileMakeModelPhotoUri: Uri
    var currentPhotoPath = ""
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_IMAGE_MAKE_MODEL_PHOTO && resultCode == AppCompatActivity.RESULT_OK) {
            val imgFile = File(makeModelPhoto)
            if (imgFile.exists()) {
                fileMakeModelPhotoUri = Uri.fromFile(imgFile)
                currentPhotoPath = fileMakeModelPhotoUri.path!!
                Log.e("fileUri---", fileMakeModelPhotoUri.toString())
            }


            Log.e(TAG, "onActivityResultCUSTOMERPHOTO>>>>>>>>>: $currentPhotoPath")

        }
    }

    //todo make and model camera intent--
    private fun dispatchMakeModelPictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(requireActivity().packageManager)?.also {
                var photoFile: File? = try {
                    createImageFile()
                } catch (ex: java.io.IOException) {
                    Toast.makeText(
                        requireContext(),
                        "Error occurred while creating the file",
                        Toast.LENGTH_SHORT
                    ).show()
                    null
                }
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        requireActivity(),
                        "${BuildConfig.APPLICATION_ID}.FileProvider",
                        it
                    )
                    //                    val photoURI: Uri = Uri.fromFile(it) //todo ==> using Uri.fromFile to create the URI for the photo file, which leads to a FileUriExposedException on Android 7.0 and above
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_MAKE_MODEL_PHOTO)
                }
            }
        }
    }

    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_${timeStamp}_"
        val storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            ?: requireContext().cacheDir
        if (!storageDir.exists()) {
            storageDir.mkdirs()
        }
        val image = File.createTempFile(imageFileName, ".png", storageDir)

        currentPhotoPath = image.absolutePath


        return image
    }



}