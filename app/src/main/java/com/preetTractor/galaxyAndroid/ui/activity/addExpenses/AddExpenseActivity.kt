package com.preetTractor.galaxyAndroid.ui.activity.addExpenses

import android.app.AlertDialog
import android.content.Intent

import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.util.Base64
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import com.preetTractor.galaxyAndroid.BuildConfig
import com.preetTractor.galaxyAndroid.R
import com.preetTractor.galaxyAndroid.data.ResponseGlobal
import com.preetTractor.galaxyAndroid.data.expense.addExpense.ConveyanceModel
import com.preetTractor.galaxyAndroid.data.expense.type.ExpenseTypeData
import com.preetTractor.galaxyAndroid.databinding.ActivityAddExpenseBinding
import com.preetTractor.galaxyAndroid.databinding.ItemConveyanceBinding
import com.preetTractor.galaxyAndroid.helper.Globals
import com.preetTractor.galaxyAndroid.helper.Globals.hideKeyboard
import com.preetTractor.galaxyAndroid.helper.Globals.transformIntoDatePicker
import com.preetTractor.galaxyAndroid.helper.PrefsByShubh
import com.preetTractor.galaxyAndroid.ui.recyclerview.AddConveyanceAdapter
import com.google.android.material.snackbar.Snackbar
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.preetTractor.galaxyAndroid.apiHelper.ApiClient
import com.preetTractor.galaxyAndroid.apiHelper.ApisInterface
import com.preetTractor.galaxyAndroid.mvvmSetUp.DefaultMainRepositories
import com.preetTractor.galaxyAndroid.mvvmSetUp.MainRepos
import com.preetTractor.galaxyAndroid.mvvmSetUp.MainViewModel
import com.preetTractor.galaxyAndroid.mvvmSetUp.MainViewModelProvider
import com.preetTractor.galaxyAndroid.helper.Globals.transformIntoDatePickerWithLast90Days
import com.preetTractor.galaxyAndroid.helper.LocationPermissionHelper
import com.preetTractor.galaxyAndroid.ui.activity.BaseActivity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date

class AddExpenseActivity : BaseActivity() {
    lateinit var viewModel: MainViewModel
    lateinit var binding: ActivityAddExpenseBinding
    lateinit var conveynanceAdapter: AddConveyanceAdapter
    var conveynanceList = arrayListOf<ConveyanceModel>()
    var expenseTypePreviouse = ""
    override var currentPhotoPath = ""
    override val REQUEST_IMAGE_MAKE_MODEL_PHOTO = 123
    override var makeModelPhoto = ""
    override lateinit var fileMakeModelPhotoUri: Uri
    var photoList: ArrayList<String> = arrayListOf()
    var listPosition = -1
    lateinit var mAlert: AlertDialog
    var checkedIDExpense: Int = -1
    var expenseType: Int = -1//Conveyance=1,Lodging=2,Food=3
    var spinnerModeSelectedItem: String = ""
    var spinnerMealSelectedItem: String = ""

    //todo new key for Amount calculation at Km
    var basePriceOnRoll = PrefsByShubh.getString("expenseRate", "0")

    companion object {
        private const val TAG = "AddExpenseActivity"
    }

    private fun setUpViewModel() {
        val dispatchers: CoroutineDispatcher = Dispatchers.Main
        val mainRepos = DefaultMainRepositories() as MainRepos
        val fanxApi: ApisInterface = ApiClient().service(this)
        val viewModelProviderfactory =
            MainViewModelProvider(application, mainRepos, dispatchers, fanxApi)
        viewModel = ViewModelProvider(this, viewModelProviderfactory)[MainViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(/* savedInstanceState = */ savedInstanceState)
        binding = ActivityAddExpenseBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpViewModel()
        binding.ivBackPress.setOnClickListener {
            finish()
        }
        expenseTypePreviouse = intent?.getStringExtra("expenseType").toString()
        callExpenseTypeApi()
        clickListener()


    }

    var isMiscellaneous = false
    private fun clickListener() {
        binding.tvAddConveyance.setOnClickListener {
            expenseType = 1
            isMiscellaneous = false
            openAlert()
        }
        binding.tvAddLodging.setOnClickListener {
            isMiscellaneous = false
            expenseType = 2
            openAlert()
        }
        binding.tvAddFood.setOnClickListener {
            isMiscellaneous = false
            expenseType = 3
            openAlert()
        }

        binding.tvAddMiscellaneous.setOnClickListener {
            isMiscellaneous = true
            expenseType = 4
            openAlert()
        }
    }

    private fun setAlertTitle(tvTitle: TextView) {
        val title = when (expenseType) {
            1 -> "Add Conveyance"
            2 -> "Add Lodging"
            3 -> "Add food"
            else -> ""
        }
        val checkedExpenseType = when (checkedIDExpense) {
            1 -> "Local"
            2 -> "Travelling"
            3 -> "Outstation"
            4 -> "Misc"
            else -> ""

        }
        tvTitle.text = "${title} (${checkedExpenseType})"
    }


    lateinit var alertBinding: ItemConveyanceBinding
    private fun openAlert() {
        alertBinding = ItemConveyanceBinding.inflate(LayoutInflater.from(this@AddExpenseActivity))

        val builder = AlertDialog.Builder(this@AddExpenseActivity)

        mAlert = builder.setView(alertBinding.root)
            .setCancelable(true)
            .create()
        mAlert.window?.setBackgroundDrawable(
            ContextCompat.getDrawable(
                this@AddExpenseActivity,
                R.drawable.alert_bg
            )
        )
        setAlertTitle(alertBinding.tvTitle)



        setViews(alertBinding)
        currentPhotoPath = ""
        if (isMiscellaneous) {
            alertBinding.apply {
                layoutAmount.visibility = View.GONE
                etLocation.visibility = View.GONE
                etToLocation.visibility = View.GONE
                spinnerMeal.visibility = View.GONE
                etRemark.visibility = View.VISIBLE
                etRemark.hint = getString(R.string.remarks_mand)
                spinnerMode.visibility = View.GONE
                alertBinding.tvTitle.text = "Add Miscellaneous"
            }
        }


        alertBinding.btnCancel.setOnClickListener {
            hideKeyboard()
            expenseType = -1
            mAlert.dismiss()
        }
        alertBinding.btnClose.setOnClickListener {
            hideKeyboard()
            expenseType = -1
            mAlert.dismiss()
        }
        alertBinding.btnSave.setOnClickListener {
            hideKeyboard()
            if (Globals.checkForInternet(this@AddExpenseActivity)) {
                if (isMiscellaneous) {
                    if (alertBinding.etRemark.text.toString().isNotEmpty()) {
                        if (currentPhotoPath.isNotEmpty()) {
                            addExpense(alertBinding)
                        } else
                            Globals.errorMessage(
                                this@AddExpenseActivity,
                                "Please attach image"
                            )
                    } else
                        Globals.errorMessage(
                            this@AddExpenseActivity,
                            "Please enter remark"
                        )
                } else {
                    if (expenseType == 1) {
                        if (alertBinding.etAmount.text.toString().isNotEmpty()) {
                            if (alertBinding.etLocation.text.toString().isNotEmpty()) {
                                if (alertBinding.etRemark.text.toString().isNotEmpty()) {

                                    // New condition for spinnerModeSelectedItem
                                    if (spinnerModeSelectedItem.equals("Km", ignoreCase = true)) {
                                        if (alertBinding.etKm.text.toString().trim().isEmpty()) {
                                            Globals.errorMessage(
                                                this@AddExpenseActivity,
                                                "Please add Km"
                                            )
                                            return@setOnClickListener
                                        }
                                    }

                                    if (checkedIDExpense == 2) {
                                        if (alertBinding.etToLocation.text.toString()
                                                .isNotEmpty()
                                        ) {
                                            if (currentPhotoPath.isNotEmpty()) {
                                                addExpense(alertBinding)
                                            } else {
                                                Globals.errorMessage(
                                                    this@AddExpenseActivity,
                                                    "Please attach image"
                                                )
                                            }
                                        } else {
                                            Globals.errorMessage(
                                                this@AddExpenseActivity,
                                                "Please enter to location"
                                            )
                                        }
                                    } else {
                                        if (currentPhotoPath.isNotEmpty()) {
                                            addExpense(alertBinding)
                                        } else {
                                            Globals.errorMessage(
                                                this@AddExpenseActivity,
                                                "Please attach image"
                                            )
                                        }
                                    }

                                } else {
                                    Globals.errorMessage(
                                        this@AddExpenseActivity,
                                        "Please enter remark"
                                    )
                                }
                            } else {
                                Globals.errorMessage(
                                    this@AddExpenseActivity,
                                    "Please enter location"
                                )
                            }
                        } else {
                            Globals.errorMessage(this@AddExpenseActivity, "Please enter amount")
                        }
                    } else if (expenseType == 2) {
                        if (alertBinding.etAmount.text.toString().isNotEmpty()) {
                            if (alertBinding.etLocation.text.toString().isNotEmpty()) {
                                if (alertBinding.etHotelName.text.toString().isNotEmpty()) {
                                    if (alertBinding.etNoOfPeople.text.toString().isNotEmpty()) {
                                        if (currentPhotoPath.isNotEmpty()) {
                                            addExpense(alertBinding)
                                        } else
                                            Globals.errorMessage(
                                                this@AddExpenseActivity,
                                                "Please attach image"
                                            )
                                    } else {
                                        Globals.errorMessage(
                                            this@AddExpenseActivity,
                                            "Please enter no. of people"
                                        )
                                    }
                                } else {
                                    Globals.errorMessage(
                                        this@AddExpenseActivity,
                                        "Please enter hotel name"
                                    )
                                }
                            } else {
                                Globals.errorMessage(
                                    this@AddExpenseActivity,
                                    "Please enter location"
                                )
                            }
                        } else {
                            Globals.errorMessage(this@AddExpenseActivity, "Please enter amount")
                        }
                    } else if (expenseType == 3) {
                        if (alertBinding.etAmount.text.toString().isNotEmpty()) {
                            if (alertBinding.etLocation.text.toString().isNotEmpty()) {
                                if (currentPhotoPath.isNotEmpty() || spinnerModeSelectedItem == "No") {
                                    addExpense(alertBinding)
                                } else
                                    Globals.errorMessage(
                                        this@AddExpenseActivity,
                                        "Please attach image"
                                    )
                            } else {
                                Globals.errorMessage(
                                    this@AddExpenseActivity,
                                    "Please enter location"
                                )
                            }
                        } else {
                            Globals.errorMessage(this@AddExpenseActivity, "Please enter amount")
                        }
                    }
                }
            }

        }

        alertBinding.tvDate.setText(
            Globals.dateStringConvertToDesiredFormat(
                Globals.getTodaysDate() ?: "",
                "dd-MM-yyyy",
                "dd/MM/yyyy"
            )
        )

        alertBinding.tvToDate.setText(
            Globals.dateStringConvertToDesiredFormat(
                Globals.getTodaysDate() ?: "",
                "dd-MM-yyyy",
                "dd/MM/yyyy"
            )
        )
        alertBinding.tvToDate.transformIntoDatePicker(
            alertBinding.tvToDate.context,
            "dd/MM/yyyy",
            null
        )
        alertBinding.tvDate.transformIntoDatePickerWithLast90Days(
            alertBinding.tvDate.context,
            "dd/MM/yyyy",
            null
        )

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
                    Log.d("bdbbabk", "onTextChanged: $basePriceOnRoll")
                    alertBinding.etAmount.setText("${basePriceOnRoll!!.toInt() * calcAMount.toInt()}")
                } else {

                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })


        mAlert.show()
    }

    private fun setViews(alertBinding: ItemConveyanceBinding) {
        if (expenseType == 1) {
            if (checkedIDExpense == 2) {
                alertBinding.etToLocation.visibility = View.VISIBLE
            } else {
                alertBinding.etToLocation.visibility = View.GONE
            }
            setSpinner(alertBinding)

            alertBinding.groupLodge.visibility = View.GONE
            alertBinding.spinnerMode.visibility = View.VISIBLE
            alertBinding.etRemark.visibility = View.VISIBLE
            alertBinding.spinnerMeal.visibility = View.GONE
            alertBinding.etKm.visibility = View.VISIBLE
        } else if (expenseType == 2) {
            alertBinding.etToLocation.visibility = View.GONE
            alertBinding.etRemark.visibility = View.INVISIBLE
            alertBinding.spinnerMeal.visibility = View.GONE
            alertBinding.spinnerMode.visibility = View.GONE
            alertBinding.groupLodge.visibility = View.VISIBLE
            setSpinner(alertBinding)
            alertBinding.etKm.visibility = View.GONE
        } else if (expenseType == 3) {
            alertBinding.etToLocation.visibility = View.GONE
            alertBinding.etRemark.visibility = View.INVISIBLE
            alertBinding.spinnerMeal.visibility = View.VISIBLE
            alertBinding.etRemark.hint = "Meal"
            alertBinding.spinnerMode.visibility = View.VISIBLE
            alertBinding.groupLodge.visibility = View.GONE
            setSpinner(alertBinding)
            alertBinding.etKm.visibility = View.GONE
        } else {
            alertBinding.etRemark.hint = "Remark"
        }
//                if (expenseType == 3 && spinnerModeSelectedItem == "Yes") View.VISIBLE else View.INVISIBLE
        alertBinding.tvCamera.setOnClickListener {

            if (!LocationPermissionHelper.hasLocationPermission(this)) {
                Toast.makeText(this, "Please Grant All Permissions", Toast.LENGTH_SHORT).show()
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.data = Uri.parse("package:$packageName")
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            } else {

                //startService()
                dispatchMakeModelPictureIntent()
            }

        }

    }


    private fun setSpinner(alertBinding: ItemConveyanceBinding) {

        if (expenseType == 3) {
            alertBinding.headingBillCopy.visibility = View.VISIBLE
        } else {
            alertBinding.headingBillCopy.visibility = View.GONE
        }

        var modes = if (expenseType == 3) arrayOf("Yes", "No") else arrayOf(
            "Km",
            "Petrol",
            "Train/cab/Bus",
            "Tolls"
        )
        var mealType = arrayOf(
            "Breakfast",
            "Lunch",
            "Dinner"
        )
        val modeSpinner = ArrayAdapter<String>(
            this@AddExpenseActivity,
            android.R.layout.simple_spinner_item,
            modes
        )
        val mealSpinner = ArrayAdapter<String>(
            this@AddExpenseActivity,
            android.R.layout.simple_spinner_item,
            mealType
        )
        mealSpinner.setDropDownViewResource(R.layout.custom_spinner)
        modeSpinner.setDropDownViewResource(R.layout.custom_spinner)

        alertBinding.spinnerMode.adapter = modeSpinner
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

                    (if (expenseType == 3) {
                        if (spinnerModeSelectedItem == "Yes")
                            View.VISIBLE
                        else
                            View.INVISIBLE
                    } else {
                        View.VISIBLE
                    }).also { alertBinding.tvCamera.visibility = it
                    alertBinding.ivViewAttachment.visibility = it}

                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // Handle case where no item is selected (optional)
                }
            }
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


    private fun onCheckedListener() {
        // initial state of radio buttons
/*        binding.linearMiscellaneous.visibility = View.GONE
        binding.constraintMoreLayout.visibility = View.VISIBLE

        binding.tvConveyance.visibility = View.VISIBLE
        binding.ivConveyance.visibility = View.VISIBLE

        binding.tvLodging.visibility = View.GONE
        binding.view2.visibility = View.GONE
        binding.view3.visibility = View.GONE
        binding.tvAddLodging.visibility = View.GONE
        binding.tvFood.visibility = View.GONE
        binding.tvAddFood.visibility = View.GONE
        binding.ivFood.visibility = View.GONE
        binding.ivLodging.visibility = View.GONE*/



        binding.chipGroup.check(checkedIDExpense)

        if (checkedIDExpense == 3) {
            //todo new views
            binding.linearMiscellaneous.visibility = View.GONE
            binding.constraintMoreLayout.visibility = View.VISIBLE

            binding.tvConveyance.visibility = View.VISIBLE
            binding.tvLodging.visibility = View.VISIBLE

            binding.view2.visibility = View.VISIBLE
            binding.tvAddLodging.visibility = View.VISIBLE

            binding.tvFood.visibility = View.VISIBLE
            binding.tvAddFood.visibility = View.VISIBLE
            binding.view3.visibility = View.VISIBLE

            binding.ivConveyance.visibility = View.VISIBLE
            binding.ivLodging.visibility = View.VISIBLE
            binding.ivFood.visibility = View.VISIBLE
        }

        if (checkedIDExpense == 1) {
            //todo new views
            binding.linearMiscellaneous.visibility = View.GONE
            binding.constraintMoreLayout.visibility = View.VISIBLE

            binding.tvConveyance.visibility = View.VISIBLE
            binding.ivConveyance.visibility = View.VISIBLE

            binding.tvLodging.visibility = View.GONE
            binding.view2.visibility = View.GONE
            binding.view3.visibility = View.GONE
            binding.tvAddLodging.visibility = View.GONE
            binding.tvFood.visibility = View.GONE
            binding.tvAddFood.visibility = View.GONE
            binding.ivFood.visibility = View.GONE
            binding.ivLodging.visibility = View.GONE
        }

        if (checkedIDExpense == 2) {
            //todo new views
            binding.linearMiscellaneous.visibility = View.GONE
            binding.constraintMoreLayout.visibility = View.VISIBLE

            binding.tvConveyance.visibility = View.VISIBLE
            binding.tvLodging.visibility = View.GONE
            binding.view2.visibility = View.GONE
            binding.view3.visibility = View.VISIBLE
            binding.tvAddLodging.visibility = View.GONE
            binding.tvFood.visibility = View.VISIBLE
            binding.tvAddFood.visibility = View.VISIBLE
            binding.ivConveyance.visibility = View.VISIBLE
            binding.ivLodging.visibility = View.GONE
            binding.ivFood.visibility = View.VISIBLE
        }


        if (checkedIDExpense == 4) {
            //todo new views
            binding.linearMiscellaneous.visibility = View.VISIBLE
            binding.constraintMoreLayout.visibility = View.GONE
            binding.tvConveyance.visibility = View.VISIBLE
            binding.tvLodging.visibility = View.GONE
            binding.view2.visibility = View.GONE

            binding.tvAddLodging.visibility = View.GONE
            binding.tvFood.visibility = View.GONE

            binding.view3.visibility = View.GONE
            binding.tvAddFood.visibility = View.GONE

            binding.ivConveyance.visibility = View.VISIBLE
            binding.ivLodging.visibility = View.GONE
            binding.ivFood.visibility = View.GONE

        }




        binding.chipGroup.setOnCheckedChangeListener { radioGroup, checkedId ->
            when (checkedId) {

                1 -> {

                    checkedIDExpense = checkedId
                    val chip =
                        binding.chipGroup.findViewById<RadioButton>(checkedId) // Get the checked chip by ID
                    val chipText = chip.text.toString() // Get the chip text

                    //todo new views
                    binding.linearMiscellaneous.visibility = View.GONE
                    binding.constraintMoreLayout.visibility = View.VISIBLE

                    binding.tvConveyance.visibility = View.VISIBLE
                    binding.ivConveyance.visibility = View.VISIBLE

                    binding.tvLodging.visibility = View.GONE
                    binding.view2.visibility = View.GONE
                    binding.view3.visibility = View.GONE
                    binding.tvAddLodging.visibility = View.GONE
                    binding.tvFood.visibility = View.GONE
                    binding.tvAddFood.visibility = View.GONE
                    binding.ivFood.visibility = View.GONE
                    binding.ivLodging.visibility = View.GONE
                    expenseTypePreviouse = chipText
                }

                2 -> {

                    checkedIDExpense = checkedId
                    val chip =
                        binding.chipGroup.findViewById<RadioButton>(checkedId) // Get the checked chip by ID
                    val chipText = chip.text.toString() // Get the chip text
                    //todo new views
                    binding.linearMiscellaneous.visibility = View.GONE
                    binding.constraintMoreLayout.visibility = View.VISIBLE

                    binding.tvConveyance.visibility = View.VISIBLE
                    binding.tvLodging.visibility = View.GONE
                    binding.view2.visibility = View.GONE
                    binding.view3.visibility = View.VISIBLE
                    binding.tvAddLodging.visibility = View.GONE
                    binding.tvFood.visibility = View.VISIBLE
                    binding.tvAddFood.visibility = View.VISIBLE
                    binding.ivConveyance.visibility = View.VISIBLE
                    binding.ivLodging.visibility = View.GONE
                    binding.ivFood.visibility = View.VISIBLE

                    expenseTypePreviouse = chipText

                }

                3 -> {

                    checkedIDExpense = checkedId
                    val chip =
                        binding.chipGroup.findViewById<RadioButton>(checkedId) // Get the checked chip by ID
                    val chipText = chip.text.toString() // Get the chip text
                    expenseTypePreviouse = chipText

                    //todo new views
                    binding.linearMiscellaneous.visibility = View.GONE
                    binding.constraintMoreLayout.visibility = View.VISIBLE

                    binding.tvConveyance.visibility = View.VISIBLE
                    binding.tvLodging.visibility = View.VISIBLE

                    binding.view2.visibility = View.VISIBLE
                    binding.tvAddLodging.visibility = View.VISIBLE

                    binding.tvFood.visibility = View.VISIBLE
                    binding.tvAddFood.visibility = View.VISIBLE
                    binding.view3.visibility = View.VISIBLE

                    binding.ivConveyance.visibility = View.VISIBLE
                    binding.ivLodging.visibility = View.VISIBLE
                    binding.ivFood.visibility = View.VISIBLE

                }

                4 -> {
                    checkedIDExpense = checkedId
                    val chip =
                        binding.chipGroup.findViewById<RadioButton>(checkedId) // Get the checked chip by ID
                    val chipText = chip.text.toString() // Get the chip text
                    expenseTypePreviouse = chipText

                    //todo new views
                    binding.linearMiscellaneous.visibility = View.VISIBLE
                    binding.constraintMoreLayout.visibility = View.GONE
                    binding.tvConveyance.visibility = View.VISIBLE
                    binding.tvLodging.visibility = View.GONE
                    binding.view2.visibility = View.GONE

                    binding.tvAddLodging.visibility = View.GONE
                    binding.tvFood.visibility = View.GONE

                    binding.view3.visibility = View.GONE
                    binding.tvAddFood.visibility = View.GONE

                    binding.ivConveyance.visibility = View.VISIBLE
                    binding.ivLodging.visibility = View.GONE
                    binding.ivFood.visibility = View.GONE


                }

            }

        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_IMAGE_MAKE_MODEL_PHOTO && resultCode == RESULT_OK) {
            val imgFile = File(makeModelPhoto)

            if (imgFile.exists()) {
                fileMakeModelPhotoUri = Uri.fromFile(imgFile)
                currentPhotoPath = fileMakeModelPhotoUri.path!!
                Log.e("fileUri---", fileMakeModelPhotoUri.toString())
            }


            Log.e(TAG, "onActivityResultCUSTOMERPHOTO>>>>>>>>>: $currentPhotoPath")


            //  Globals.successMessage(this, "Attachement Added")

            alertBinding.ivViewAttachment.visibility = View.VISIBLE
            alertBinding.ivViewAttachment.setImageURI(
                Globals.getUriFromPath(
                    this,
                    currentPhotoPath
                )
            )

            Snackbar.make(binding.viewBottom, "Attachment Added", Snackbar.LENGTH_SHORT).show()

        }

        if (requestCode == REQUEST_IMAGE_MAKE_MODEL_PHOTO) {
            if (resultCode == RESULT_CANCELED) {
                // Clear currentPhotoPath if the user canceled the camera
                currentPhotoPath = ""
//                Toast.makeText(this, "Camera operation canceled", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun addExpense(alertBinding: ItemConveyanceBinding) {
        val builder: MultipartBody.Builder = MultipartBody.Builder()
        builder.setType(MultipartBody.FORM)

        if (expenseType == 4) {
            builder.addFormDataPart("type_of_expense", "Miscellaneous")
        } else {
            builder.addFormDataPart("type_of_expense", expenseTypePreviouse)
        }


        builder.addFormDataPart("createDate", Globals.getTodaysDatervrsfrmt()!!)
        builder.addFormDataPart("createTime", Globals.getTCurrentTime()!!)
        builder.addFormDataPart("createdBy", PrefsByShubh.getSalesEmployeeCode()!!)
        builder.addFormDataPart("salesemployeecode", PrefsByShubh.getSalesEmployeeCode()!!)
        if (expenseType == 1) {
            builder.addFormDataPart("expense_name", "Conveyance")
        }
        if (expenseType == 2) {
            builder.addFormDataPart("expense_name", "Lodging")
        }
        if (expenseType == 3) {
            builder.addFormDataPart("expense_name", "Food")
        }


        if (expenseType == 4) {
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

        builder.addFormDataPart("num_person", alertBinding.etNoOfPeople.text.toString())
        builder.addFormDataPart("persons_name", alertBinding.edNameOfPersons.text.toString())

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
        if (alertBinding.etKm.text.toString().trim().isNotEmpty()) {
            builder.addFormDataPart("KM", alertBinding.etKm.text.toString())
        } else {
            builder.addFormDataPart("KM", "0")
        }

        builder.addFormDataPart(
            "remarks",
            if (expenseType == 3) spinnerMealSelectedItem else alertBinding.etRemark.text.toString()
        )
        val emptyArray = mutableListOf<String>()

        if (currentPhotoPath.isNotEmpty()) {
            try {
                if (currentPhotoPath.isNotEmpty() && File(currentPhotoPath).exists()) {
                    val compressedImageFile = Globals.compressImageFile(this,File(currentPhotoPath))

                    if (compressedImageFile.exists()) {
                        builder.addFormDataPart(
                            "attach", compressedImageFile.name,
                            compressedImageFile.asRequestBody("multipart/form-data".toMediaTypeOrNull())
                        )
                    } else {
                        builder.addFormDataPart(
                            "attach", "",
                            "".toRequestBody("multipart/form-data".toMediaTypeOrNull())
                        )
                    }
                } else {
                    builder.addFormDataPart("attach", "")
                }
            } catch (e: Exception) {
                builder.addFormDataPart(
                    "attach", "",
                    "".toRequestBody("multipart/form-data".toMediaTypeOrNull())
                )
                e.printStackTrace()
            }

        }
        else {
            builder.addFormDataPart("attach", "")
        }
        val requestBody: MultipartBody = builder.build()
        createExpenseJson()
        Log.e("payload--->", requestBody.toString())
        photoList.add(requestBody.toString())

        callExpenseCreateMultipart(requestBody, alertBinding)
    }

    private fun createExpenseJson() {
        val jsonObject = JSONObject()

        try {
            jsonObject.put("type_of_expense", if (expenseType == 4) "Miscellaneous" else expenseTypePreviouse)
            jsonObject.put("createDate", Globals.getTodaysDatervrsfrmt())
            jsonObject.put("createTime", Globals.getTCurrentTime())
            jsonObject.put("createdBy", PrefsByShubh.getSalesEmployeeCode())
            jsonObject.put("salesemployeecode", PrefsByShubh.getSalesEmployeeCode())

            when (expenseType) {
                1 -> jsonObject.put("expense_name", "Conveyance")
                2 -> jsonObject.put("expense_name", "Lodging")
                3 -> jsonObject.put("expense_name", "Food")
                4 -> jsonObject.put("expense_name", "Miscellaneous")
            }

            jsonObject.put("from_date", Globals.convert_dd_MM_yyyy_into_yyyy_MM_ddDASH(alertBinding.tvDate.text.toString()))
            jsonObject.put("to_date", Globals.convert_dd_MM_yyyy_into_yyyy_MM_ddDASH(alertBinding.tvToDate.text.toString()))
            jsonObject.put("num_person", alertBinding.etNoOfPeople.text.toString())
            jsonObject.put("persons_name", alertBinding.edNameOfPersons.text.toString())
            jsonObject.put("meal_status", spinnerModeSelectedItem)
            jsonObject.put("hotel_name", alertBinding.etHotelName.text.toString())
            jsonObject.put("mode", spinnerModeSelectedItem)
            jsonObject.put("latitude", Globals.globalLatitude.toString())
            jsonObject.put("latitude2", Globals.globalLatitude.toString())
            jsonObject.put("longitude", Globals.globalLongitude.toString())
            jsonObject.put("longitude2", Globals.globalLongitude.toString())
            jsonObject.put("address", alertBinding.etLocation.text.toString())
            jsonObject.put("address2", alertBinding.etToLocation.text.toString())
            jsonObject.put("expense_amount", alertBinding.etAmount.text.toString())
            jsonObject.put("KM", if (alertBinding.etKm.text.toString().trim().isNotEmpty()) alertBinding.etKm.text.toString() else "0")
            jsonObject.put("remarks", if (expenseType == 3) spinnerMealSelectedItem else alertBinding.etRemark.text.toString())

            // Handling the attachment
            if (currentPhotoPath.isNotEmpty()) {
                try {
                    val file = File(currentPhotoPath)
                    if (file.exists()) {
                        val compressedImageFile = Globals.compressImageFile(this,file)
                        if (compressedImageFile.exists()) {
                            val base64File = Base64.encodeToString(compressedImageFile.readBytes(), Base64.DEFAULT)
                            jsonObject.put("attach", base64File)
                        } else {
                            jsonObject.put("attach", "")
                        }
                    } else {
                        jsonObject.put("attach", "")
                    }
                } catch (e: Exception) {
                    jsonObject.put("attach", "")
                    e.printStackTrace()
                }
            } else {
                jsonObject.put("attach", "")
            }

            // Log the JSON payload
            Log.e("JSON Payload--->", jsonObject.toString())

        } catch (e: JSONException) {
            e.printStackTrace()
        }

    }


    fun buildJsonString(): String {
        val jsonObject = JSONObject()

        val typeOfExpense = if (expenseType == 4) "Miscellaneous" else expenseTypePreviouse
        jsonObject.put("type_of_expense", typeOfExpense)

        jsonObject.put("createDate", Globals.getTodaysDatervrsfrmt() ?: "")
        jsonObject.put("createTime", Globals.getTCurrentTime() ?: "")
        jsonObject.put("createdBy", PrefsByShubh.getSalesEmployeeCode() ?: "")
        jsonObject.put("salesemployeecode", PrefsByShubh.getSalesEmployeeCode() ?: "")

        val expenseName = when (expenseType) {
            1 -> "Conveyance"
            2 -> "Lodging"
            3 -> "Food"
            4 -> "Miscellaneous"
            else -> ""
        }
        jsonObject.put("expense_name", expenseName)

        jsonObject.put(
            "from_date",
            Globals.convert_dd_MM_yyyy_into_yyyy_MM_ddDASH(alertBinding.tvDate.text.toString())
        )
        jsonObject.put(
            "to_date",
            Globals.convert_dd_MM_yyyy_into_yyyy_MM_ddDASH(alertBinding.tvToDate.text.toString())
        )
        jsonObject.put("num_person", alertBinding.etNoOfPeople.text.toString())
        jsonObject.put("persons_name", alertBinding.edNameOfPersons.text.toString())
        jsonObject.put("meal_status", spinnerModeSelectedItem)
        jsonObject.put("hotel_name", alertBinding.etHotelName.text.toString())
        jsonObject.put("mode", spinnerModeSelectedItem)
        jsonObject.put("latitude", Globals.globalLatitude.toString())
        jsonObject.put("latitude2", Globals.globalLatitude.toString())
        jsonObject.put("longitude", Globals.globalLongitude.toString())
        jsonObject.put("longitude2", Globals.globalLongitude.toString())
        jsonObject.put("address", alertBinding.etLocation.text.toString())
        jsonObject.put("address2", alertBinding.etToLocation.text.toString())
        jsonObject.put("expense_amount", alertBinding.etAmount.text.toString())
        jsonObject.put("KM", alertBinding.etKm.text.toString())
        jsonObject.put(
            "remarks",
            if (expenseType == 3) spinnerMealSelectedItem else alertBinding.etRemark.text.toString()
        )

        // Handle the attachment
        val attach = if (currentPhotoPath.isNotEmpty()) {
            File(currentPhotoPath).name
        } else {
            ""
        }
        jsonObject.put("attach", attach)

        Log.d("Payload---->", "buildJsonString: ${jsonObject.toString()}")
        return jsonObject.toString()
    }


    private fun addConveyance() {
        conveynanceAdapter = AddConveyanceAdapter(conveynanceList, this)
        binding.rvConveyance.adapter = conveynanceAdapter.apply {
            conveynanceAdapter
        }
        binding.rvConveyance.setHasFixedSize(true)
        binding.tvAddConveyance.setOnClickListener {
            conveynanceList.add(ConveyanceModel())
            if (conveynanceList.size == 1)
                conveynanceAdapter.notifyDataSetChanged()
            else
                conveynanceAdapter.notifyItemInserted(conveynanceList.size - 1)
        }
        binding.rvConveyance.setItemViewCacheSize(conveynanceList.size)
        conveynanceAdapter.setOnDateClickListener { str, i ->
            conveynanceList[i].date = str
        }
        conveynanceAdapter.setOnDeleteBtnClickListener { str, i ->
            conveynanceList.removeAt(i)
            conveynanceAdapter.notifyDataSetChanged()
        }
        conveynanceAdapter.setOnRemarkBtnClickListener { str, i ->
            conveynanceList[i].remark = str
        }
        conveynanceAdapter.setOnCameraBtnClickListener { str, i ->
            //            conveynanceList[i].camera[0] = str
            if (listPosition != i) {
                listPosition = i
            } else {
                photoList.clear()
            }
            //            dispatchMakeModelPictureIntent(i)
        }
        conveynanceAdapter.setOnLocationBtnClickListener { str, i ->
            conveynanceList[i].location = str
        }
        conveynanceAdapter.setOnToLocationBtnClickListener { str, i ->
            conveynanceList[i].toLocation = str
        }
        conveynanceAdapter.setOnAmountClickListener { str, i ->
            conveynanceList[i].amount = str
        }
        binding.tvSave.setOnClickListener {
            if (Globals.checkForInternet(this)) {
                Log.e(TAG, "${(conveynanceList.toString())}")
                /* viewModel.createExpenseApi(expenseJsonObject(), this)
                 bindAddExpenseObserver()*/
                // callExpenseCreateMultipart()
            }
        }
    }


    private fun callExpenseCreateMultipart(
        requestBody: MultipartBody,
        alertBinding: ItemConveyanceBinding
    ) {
        alertBinding.btnSave.isEnabled = false
        alertBinding.btnSave.alpha = 0.3F
        binding.spinKitLoader.visibility = View.VISIBLE
        val call: Call<ResponseGlobal> =
            ApiClient().service(this).createExpenseApiMultipart(requestBody)
        call.enqueue(object : Callback<ResponseGlobal> {
            override fun onResponse(
                call: Call<ResponseGlobal>,
                response: Response<ResponseGlobal>
            ) {
                if (response != null) {
                    binding.spinKitLoader.visibility = View.GONE
                    if (response.body()!!.status == 200
                    ) {
                        Globals.successMessage(this@AddExpenseActivity, "Added SuccessFully")
                        currentPhotoPath = ""
                        mAlert.dismiss()
                        alertBinding.btnSave.isEnabled = true
                        alertBinding.btnSave.alpha = 1F
                        Log.e(TAG, "addExpense: ${buildJsonString()}")

                    } else if (response.body()!!.status == 201) {
                        Globals.errorMessage(
                            this@AddExpenseActivity,
                            response.body()!!.message.toString()
                        )
                        alertBinding.btnSave.isEnabled = true
                        alertBinding.btnSave.alpha = 1F
//                        Toast.makeText(
//                            this@AddExpenseActivity,
//                            response.body()!!.message,
//                            Toast.LENGTH_SHORT
//                        ).show()
                    }
                }
            }

            override fun onFailure(call: Call<ResponseGlobal>, t: Throwable) {
                binding.spinKitLoader.visibility = View.GONE
                Globals.errorMessage(this@AddExpenseActivity, t.message.toString())
//                Toast.makeText(this@AddExpenseActivity, "" + t.message, Toast.LENGTH_SHORT).show()
                /* loader.setVisibility(View.GONE);
                alertDialog.dismiss();*/
            }
        })
    }

    fun callExpenseTypeApi() {
        if (Globals.checkForInternet(this@AddExpenseActivity)) {
            viewModel.getExpenseTypeApi(this@AddExpenseActivity)
            bindListObserver()
        }
    }

    fun addRadioButton(data: ExpenseTypeData) {
        val rb = RadioButton(this)
        val params = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        params.height = resources.getDimension(R.dimen.dimen_30).toInt()
        params.setMargins(15, 5, 15, 5)
        params.weight = 1f
        rb.layoutParams = params
        rb.text = data.name
        rb.id = data.id
        rb.setTextColor(
            ContextCompat.getColorStateList(
                this@AddExpenseActivity,
                R.color.chip_text_seletor
            )
        )
        rb.buttonDrawable = getResources().getDrawable(android.R.color.transparent)
        rb.background = getResources().getDrawable(R.drawable.selector_expenses_chips)
        rb.gravity = Gravity.CENTER
        rb.textSize = 12f
        rb.setTypeface(null, Typeface.BOLD)
        rb.setPadding(10, 10, 10, 10)
        rb.isChecked = data.id == 1
        binding.chipGroup.addView(rb)
    }

    private fun bindListObserver() {
        viewModel.expenseTypeData.observe(this@AddExpenseActivity, Event.EventObserver(
            onError = {
                binding.spinKitLoader.visibility = View.GONE

            },
            onSuccess = {
                binding.spinKitLoader.visibility = View.GONE
                if (it.status == 200) {
                    if (it.data.isNotEmpty()) {
                        val firstChipId = it.data[0].id
                        binding.chipGroup.removeAllViews()
                        binding.chipGroup.weightSum = it.data.size.toFloat()

                        it.data.forEach { data ->
                            addRadioButton(data)
//                            checkedIDExpense = 1


                        }
                        when (expenseTypePreviouse) {

                            "Local" -> {
                                checkedIDExpense = 1
                            }
                            "Travelling" -> {
                                checkedIDExpense = 2
                            }
                            "Outstation" -> {
                                checkedIDExpense = 3
                            }
                            "Misc" -> {
                                checkedIDExpense = 4
                            }
                            else -> {
                                checkedIDExpense = -1
                            }

                        }


                    }


                }
                onCheckedListener()
            },
            onLoading = {
                binding.spinKitLoader.visibility = View.VISIBLE
            }
        ))
    }


    fun photoJsonArray(photoList: ArrayList<String>): JsonArray {
        val jsonArray = JsonArray()
        photoList.forEach {
            jsonArray.add(it)
        }
        return jsonArray
    }

    private fun bindAddExpenseObserver() {
        viewModel.createExpenseTypeData.observe(this, Event.EventObserver(
            onError = {
                binding.spinKitLoader.visibility = View.GONE
                Globals.errorMessage(this@AddExpenseActivity, "Something went wrong.")

            },
            onSuccess = {
                binding.spinKitLoader.visibility = View.GONE
                if (it.status == 200) {
                    Globals.successMessage(this@AddExpenseActivity, it.message)
                    finish()
                }


            },
            onLoading = {
                binding.spinKitLoader.visibility = View.VISIBLE

            }
        ))
    }

    fun expenseJsonObject(): JsonObject {
        var totalAmmount = 0
        var expenseTypeJsonObject = JsonObject()
        conveynanceList.forEach {
            totalAmmount += it.amount.toInt()
            expenseTypeJsonObject.apply {
                addProperty("expense_name", it.type)
                addProperty(
                    "from_date",
                    Globals.dateStringConvertToDesiredFormat(
                        it.date,
                        "dd/MM/YYYY",
                        "yyyy-MM-dd"
                    )
                )
                addProperty(
                    "to_date",
                    Globals.dateStringConvertToDesiredFormat(
                        it.toDate,
                        "dd/MM/YYYY",
                        "yyyy-MM-dd"
                    )
                )
                addProperty("num_person", it.noOFPerson)
                addProperty("meal_status", it.mealStatus)
                addProperty("mode", it.mode) // assuming mode is a property in alertBinding
                addProperty("latitude", Globals.globalLatitude.toString())
                addProperty("longitude", Globals.globalLongitude.toString())
                addProperty("address", Globals.globalAddress)
                addProperty("latitude2", Globals.globalLatitude.toString())
                addProperty("longitude2", Globals.globalLongitude.toString())
                addProperty("address2", Globals.globalAddress)
                addProperty("expense_amount", it.amount)
                addProperty("remarks", it.remark)
                add("attach", photoJsonArray(it.camera))
                // attaching an empty JSONArray
            }
        }


        val jsonObject = JsonObject().apply {
            addProperty(
                "expense_date",
                Globals.convert_dd_MM_yyyy_into_yyyy_MM_dd(Globals.getTodaysDate().toString())
            )
            addProperty("type_of_expense", expenseTypePreviouse)
            addProperty("totalAmount", totalAmmount.toString())
            addProperty("createDate", Globals.getTodaysDatervrsfrmt())
            addProperty("createTime", Globals.getTCurrentTime())
            addProperty("createdBy", PrefsByShubh.getSalesEmployeeCode())
            addProperty("salesemployeecode", PrefsByShubh.getSalesEmployeeCode())
            add("expense_type", JsonArray().apply {
                add(expenseTypeJsonObject)
            })
        }

        return jsonObject
    }


    //todo make and model camera intent--
    override fun dispatchMakeModelPictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                var photoFile: File? = try {
                    createImageFile()
                } catch (ex: java.io.IOException) {
                    Toast.makeText(
                        this,
                        "Error occurred while creating the file",
                        Toast.LENGTH_SHORT
                    ).show()
                    null
                }
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        this,
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

    override fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_${timeStamp}_"
        val storageDir = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                .toString() + "/AnGService"
        )
        if (!storageDir.exists()) {
            storageDir.mkdir()
        }
        val image = File.createTempFile(imageFileName, ".png", storageDir)

        currentPhotoPath = image.absolutePath


        return image
    }


}

