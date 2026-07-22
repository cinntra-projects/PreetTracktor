package com.preetTractor.galaxyAndroid.helper

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.util.Patterns
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.ImageViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.pixplicity.easyprefs.library.Prefs
import com.preetTractor.galaxyAndroid.BuildConfig
import com.preetTractor.galaxyAndroid.R
import com.preetTractor.galaxyAndroid.data.BeatPlanCustomerDropDownModel
import com.preetTractor.galaxyAndroid.data.DocumentLines
import com.preetTractor.galaxyAndroid.data.LeadTypeData
import com.preetTractor.galaxyAndroid.data.beatplan.DataStateAll
import com.preetTractor.galaxyAndroid.data.beatplan.LocalDataTodayBeatPlan
import com.preetTractor.galaxyAndroid.data.localdata.LocalGalaxyTracking
import com.preetTractor.galaxyAndroid.data.model.order.model.response.ModelOrderListing
import com.preetTractor.galaxyAndroid.orderUi.model.local.LocalDataForBACart
import com.preetTractor.galaxyAndroid.orderUi.model.local.LocalDataForCart
import com.preetTractor.galaxyAndroid.ui.activity.ActivitySignIn
import com.preetTractor.galaxyAndroid.ui.activity.customermodule.ConnectivityReceiver
import com.preetTractor.galaxyAndroid.ui.activity.customermodule.model.CountryData
import kotlinx.coroutines.launch
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.sql.Timestamp
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.temporal.ChronoUnit
import java.util.Calendar
import java.util.Date
import java.util.Locale

object Globals {
    // val BASE_URL="https://jsonplaceholder.typicode.com/"
    var CURRENT_CLASS: String? = null
    const val DEALER_DISC: String = "_DEALER_DISC"
    const val CURRENCY: String = "_CURRENCY"
    const val SPECIAL_DISC: String = "_SPECIAL_DISC"
    const val ADDITIONAL_DISC: String = "_ADDITIONAL_DISC"
    const val DISCOUNT_PERCENT: String = "_DISCOUNT_PERCENT"
    const val BLOCK: String = "_BLOCK"
    const val CONTACT_PERSON_CODE: String = "_CONTACT_PERSON_CODE"
    const val PAYMENT_GROUP_CODE: String = "_PAYMENT_GROUP_CODE"
    const val CITY: String = "_CITY"
    const val STATE: String = "_STATE"
    const val MOBILE_NO = "_mobile_no"
    const val OTP = "_otp"
    const val CARDCODE = "_cardCode"
    const val DISTRIBUTOR_ID = "_distributor_id"
    const val MPIN_DIALOG = "_mpinDialog"
    const val SALES_EMPLOYEE_CODE = "_SALES_EMPLOYEE_CODE"
    const val CARD_CODE_BP_ONE = "CardCode"
    const val role_name = "role_name"
    const val REDIRECT_TO_MPIN_DIALOG = "_redirectToMpinDialog"
    val isCheckingStart = "_isCheckingStart"
    const val DEVICE_ID = "_DEVICE_ID"

    @JvmField
    var SalesEmployeeCode = PrefsByShubh.getSalesEmployeeCode() ?: "-1"
    var globalLatitude = 0.0
    var globalLongitude = 0.0
    var globalAddress = ""
    var globalCity = ""
    @JvmField
    var empCode = PrefsByShubh.getEmpCode() ?: "0"
    @JvmField
    var EMP_NAME = "_EMP_NAME"


    @JvmField
    var LeadDetails = "_LeadDetails"

    //for ba order
    const val HEADER_DISCOUNT_PERCENT: String = "_HEADER_DISCOUNT_PERCENT"
    const val FREIGHT_CHARGES_PERCENT: String = "_FREIGHT_CHARGES_PERCENT"


    @JvmField
    var AddBp: String = "_AddBp"
    @JvmField
    var Lead_Data: String = "_Lead_Data"

    lateinit var assignedTo: ArrayList<String>

    var todayBeatPlanList = ArrayList<LocalDataTodayBeatPlan>()

    var QUERY_SALEPERSON_CODE_PDF = "&SalesEmployeeCode="

    const val QUERY_PAGE_SIZE = 20
    const val PAGE_NO_STRING = "PageNo="
    const val QUERY_MAX_PAGE_PDF = "&MaxSize="

    const val FROM_DATE = "_FromDate_"
    const val TO_DATE = "_ToDate_"


    const val Sale_Purchse_Diff = "_Sale_Purchse_Diff"

    //todo for purchase
    const val ISPURCHASE = "_ISPURCHASE"
    const val TOKEN = "_TOKEN"

    public var enquireList = arrayListOf<String>("Enquiry", "Complain")

    const val particularBpSales = "" + BuildConfig.PDF_URL + "PartList.html?"
    const val LedgerUrl = "" + BuildConfig.PDF_URL + "Saleledger.html?"
    const val journalVoucher = "" + BuildConfig.PDF_URL + "journalvoucher.html?id="
    const val debitNoteUrl = "" + BuildConfig.PDF_URL + "ar_debit_note.html?"

    const val invoiceUrl = "" + BuildConfig.PDF_URL + "CompanyInvoice.html?"
    const val apInvoiceUrl = "" + BuildConfig.PDF_URL + "ap_Invoice.html?"
    const val receiptVoucherPdf = "" + BuildConfig.PDF_URL + "ReceiptVoucher.html?"
    const val Ap_creditNoteUrl = "" + BuildConfig.PDF_URL + "ap_debit_note.html?"
    const val creditNoteUrl_Service = "" + BuildConfig.PDF_URL + "ar_credit_note_service.html?"
    const val creditNoteUrl = "" + BuildConfig.PDF_URL + "ar_credit_note.html?"

    var isBeatPlanWorking = false

    @JvmField
    var productInterestList_gl: Array<String?> =
        arrayOf<String?>("Aluminium", "UPVC", "Internal", "Combo/Mix", "Other")

    @JvmField
     val  SelectedItems = java.util.ArrayList<DocumentLines?>()
    var GalaxyEmail: String? = null
    var GalaxyPassword: String? = null
    var GalaxyFcm: String? = null
    var GalaxyAppId: String? = null
    var loginIntoAnotherDevice = false

    @JvmField
    var GalaxyVistaToken: String? = null


    @JvmStatic
    fun convertTimeInHHMMSSA(timeString: String?): String {
        if (timeString == null || timeString.isEmpty()) {
            return "Invalid time"
        }

        val inputFormat = SimpleDateFormat("HH:mm:ss.SSSSSS", Locale.getDefault())
        val outputFormat = SimpleDateFormat("hh:mm aa", Locale.getDefault())

        try {
            val date = inputFormat.parse(timeString)
            return outputFormat.format(date).uppercase(Locale.getDefault())
        } catch (e: ParseException) {
            e.printStackTrace()
            return "Invalid time format"
        }
    }

    fun changeDecemal(input: String): String? {
        val df = DecimalFormat("#.##")
        return df.format(input.toDouble())
    }

    fun getMonthFirstLastDateOfSelectedMonthYear(selectedYear: Int, selectedMonth: Int): Pair<String, String> {
        //val selectedYear = 2025  // Replace with the selected year
        // val selectedMonth = Calendar.JANUARY  // Replace with the selected month (0-based index)

// Get the first date of the selected month
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, selectedYear)
        calendar.set(Calendar.MONTH, selectedMonth)
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        val firstDate = calendar.time // First day of the month

// Get the last date of the selected month
        val lastDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        calendar.set(Calendar.DAY_OF_MONTH, lastDay)
        val lastDate = calendar.time // Last day of the month

// Format the dates if needed
        val dateFormat = java.text.SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val firstDateString = dateFormat.format(firstDate)
        val lastDateString = dateFormat.format(lastDate)

// Output the results
        println("First date: $firstDateString")
        println("Last date: $lastDateString")
        return Pair(firstDateString, lastDateString)
    }
    @JvmStatic
    fun getTimestamp(): String {
        val sdf3 = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val timestamp = Timestamp(System.currentTimeMillis())
        return sdf3.format(timestamp)
    }

    @JvmStatic
    fun getleadType(data: MutableList<LeadTypeData?>, contactPerson: String?): Int {
        var index = -1
        //        +  " " +data.get(i).getLastName()
        for (i in data.indices) {
            val cp: String = data.get(i)?.name.toString()
            if (cp == contactPerson) {
                index = i
                break
            }
        }
        return index
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun getCurrentFinancialYear(): Pair<String, String> {
        val currentDate = LocalDate.now()

        // Determine the financial year start and end
        val financialStartYear = if (currentDate.monthValue >= 4) currentDate.year else currentDate.year - 1
        val financialEndYear = financialStartYear + 1

        val startDate = LocalDate.of(financialStartYear, 4, 1) // April 1st
        val endDate = LocalDate.of(financialEndYear, 3, 31)   // March 31st

        // Format the dates as "yyyy-MM-dd"
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        return Pair(startDate.format(formatter), endDate.format(formatter))
    }


    fun openLocationInGoogleMaps(
        context: Context,
        latitude: String,
        longitude: String,
        label: String = "Location"
    ) {
        val uri = Uri.parse("geo:0,0?q=$latitude,$longitude($label)")
        val intent = Intent(Intent.ACTION_VIEW, uri).apply {
            setPackage("com.google.android.apps.maps")
        }
        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        } else {
            // If Google Maps is not installed, open in a browser
            val browserIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://www.google.com/maps/search/?api=1&query=$latitude,$longitude")
            )
            context.startActivity(browserIntent)
        }
    }

    fun showAlertDialog(
        context: Context,
        title: String,
        message: String,
        positiveButtonText: String,
        negativeButtonText: String,
        iconImg: Int,
        onDelete: () -> Unit,
        onCancel: () -> Unit
    ) {
        MaterialAlertDialogBuilder(context)
            .setMessage(message)
            .setTitle(title)
            .setIcon(iconImg)

            .setPositiveButton(positiveButtonText) { dialogInterface, _ ->
                onDelete.invoke()
                dialogInterface.dismiss()
            }
            .setNegativeButton(negativeButtonText) { dialogInterface, _ ->
                onCancel.invoke()
                dialogInterface.dismiss()
            }
            .create()
            .show()
    }


    //todo opendialer
    fun openDialerWithNumber(context: Context, phoneNumber: String) {
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:$phoneNumber")
        }

        context.startActivity(intent)

    }

    fun convert_dd_MM_yyyy_into_yyyy_MM_dd(inputDate: String): String {
        val inputFormat = SimpleDateFormat("dd-MM-yyyy")
        val outputFormat = SimpleDateFormat("yyyy-MM-dd")

        val date = inputFormat.parse(inputDate)
        return outputFormat.format(date)
    }

    fun convert_yyyy_MM_dd_into_dd_MM_yyyy(inputDate: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd")
        val outputFormat = SimpleDateFormat("dd-MM-yyyy")

        val date = inputFormat.parse(inputDate)
        return outputFormat.format(date)
    }


    fun convert_dd_MM_yyyy_into_yyyy_MM_ddDASH(inputDate: String): String {
        val inputFormat = SimpleDateFormat("dd/MM/yyyy")
        val outputFormat = SimpleDateFormat("yyyy-MM-dd")

        val date = inputFormat.parse(inputDate)
        return outputFormat.format(date)
    }


    fun enableAllCalenderDateSelect(context: Context, textView: TextView) {
        val c = Calendar.getInstance()
        val mYear = c.get(Calendar.YEAR)
        val mMonth = c.get(Calendar.MONTH)
        val mDay = c.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            context, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                /* val selectedDate = "$year-${monthOfYear + 1}-$dayOfMonth"
                 val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.US)*/
                val selectedDate = "$dayOfMonth-${monthOfYear + 1}-$year"
                val dateFormatter = SimpleDateFormat("dd-MM-yyyy", Locale.US)
                try {
                    val strDate = dateFormatter.parse(selectedDate)
                    textView.setText(dateFormatter.format(strDate))
                } catch (e: ParseException) {
                    e.printStackTrace()
                }
            }, mYear, mMonth, mDay
        )

        datePickerDialog.datePicker // setMinDate(System.currentTimeMillis() - 1000)
        // datePickerDialog.setMessage(textView.hint.toString())
        datePickerDialog.show()

    }


    private val calendar = Calendar.getInstance()


    var offlineLatLong = ArrayList<LocalGalaxyTracking>()

    fun convertSecondsToHMS(totalSeconds: Double): String {
        val hours = (totalSeconds / 3600).toInt()
        val minutes = ((totalSeconds % 3600) / 60).toInt()
        val seconds = (totalSeconds % 60).toInt()

        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }

    fun convertDateFormat(dateString: String?): String? {
        var convertedDate = ""
        try {
            val inputFormat = SimpleDateFormat("yyyy-mm-dd")
            val date = inputFormat.parse(dateString)
            val outputFormat = SimpleDateFormat("dd-mm-yyyy")
            convertedDate = outputFormat.format(date)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return convertedDate
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun convertDateToDayMonth(dateString: String): String {
        if (dateString.isEmpty()) {
            return "Invalid date: Empty string"
        }

        return try {
            val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val outputFormatter = DateTimeFormatter.ofPattern("dd MMM")

            val date = LocalDate.parse(dateString, inputFormatter)
            date.format(outputFormatter)
        } catch (e: DateTimeParseException) {
            "Invalid date format"
        }
    }

    fun ImageView.setTint(@ColorRes colorRes: Int) {
        ImageViewCompat.setImageTintList(
            this,
            ColorStateList.valueOf(ContextCompat.getColor(context, colorRes))
        )
    }

    /*  open fun secondsBetween(dateStr: String): Long {
          // Define the date format
          val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss a")

          // Parse the given date string into a LocalDateTime object
          val givenDate = LocalDateTime.parse(dateStr, formatter)

          // Get the current date and time
          val currentDate = LocalDateTime.now()

          // Calculate the difference in seconds between the given date and current date
          return ChronoUnit.SECONDS.between(givenDate, currentDate)
      }*/

    /*  open fun secondsBetween(dateStr: String): Long {
          // Define the date format
          val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss a")

          // Parse the given date string into a LocalDateTime object
          val givenDate = LocalDateTime.parse(dateStr, formatter)

          // Get the current date and time
          val currentDate = LocalDateTime.now()

          // Calculate the difference in seconds between the given date and current date
          return ChronoUnit.SECONDS.between(givenDate, currentDate)
      }*/


    @RequiresApi(Build.VERSION_CODES.O)
    fun secondsBetween(dateStr: String): Long {
        // Define the date format with Locale.US to handle AM/PM consistently across versions
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss a", Locale.US)

        // Parse the given date string into a LocalDateTime object
        val givenDate = LocalDateTime.parse(dateStr, formatter)

        // Get the current date and time
        val currentDate = LocalDateTime.now()

        // Calculate the difference in seconds between the given date and current date
        return ChronoUnit.SECONDS.between(givenDate, currentDate)
    }


    fun getDateConvertToDesiredFormat(date: Date, outputFormat: String): String? {
        return try {
            val outputFormat = SimpleDateFormat(outputFormat, Locale.US)
            date?.let { outputFormat.format(it) }
        } catch (e: ParseException) {
            e.printStackTrace()
            "Invalid date: Empty string"
        }
    }

    fun Context.setDynamicValueWithStringXml(resourceId: Int, vararg formatArgs: Any): String {
        return getString(resourceId, *formatArgs)
    }

    fun checkForInternet(context: Context): Boolean {

        // register activity with the connectivity manager service
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        // if the android version is equal to M
        // or greater we need to use the
        // NetworkCapabilities to check what type of
        // network has the internet connection
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            // Returns a Network object corresponding to
            // the currently active default data network.
            val network = connectivityManager.activeNetwork ?: return false

            // Representation of the capabilities of an active network.
            val activeNetwork =
                connectivityManager.getNetworkCapabilities(network) ?: return false

            return when {
                // Indicates this network uses a Wi-Fi transport,
                // or WiFi has network connectivity
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true

                // Indicates this network uses a Cellular transport. or
                // Cellular has network connectivity
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true

                // else return false
                else -> false
            }
        } else {
            // if the android version is below M
            @Suppress("DEPRECATION") val networkInfo =
                connectivityManager.activeNetworkInfo ?: return false
            @Suppress("DEPRECATION")
            return networkInfo.isConnected
        }
    }


    fun getUriFromPath(context: Context, filePath: String): Uri? {
        val file = File(filePath)
        return FileProvider.getUriForFile(context, "${context.packageName}.FileProvider", file)
    }


    fun compressImageFile(context:Context,imageFile: File): File {

        val bitmap = BitmapFactory.decodeFile(imageFile.absolutePath)
            ?: throw IOException("Unable to decode image")

        val compressedFile = File(
            context.cacheDir,
            "compressed_${System.currentTimeMillis()}.jpg"
        )

        FileOutputStream(compressedFile).use {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 40, it)
        }

        return compressedFile

    }

    fun dateStringConvertToDesiredFormat(
        dateStr: String,
        inputFormat: String,
        outputFormat: String
    ): String? {

        return try {
            val date = SimpleDateFormat(inputFormat).parse(dateStr)
            val outputFormat = SimpleDateFormat(outputFormat, Locale.US)
            date?.let { outputFormat.format(it) }
        } catch (e: ParseException) {
            e.printStackTrace()
            "" //"Invalid date: Empty string"
        }
    }

    fun stringToInt(string: String?): Int {
        try {
            val discountPercent = string
            val intValue: Int

            if (discountPercent!!.contains(".")) {
                // Parse as double and convert to integer
                val doubleValue = discountPercent.toDouble()
                intValue = doubleValue.toInt()
            } else {
                // Parse as integer directly
                intValue = discountPercent.toInt()
            }

            return intValue
        } catch (e: NumberFormatException) {
            e.printStackTrace()
            return 0
        }
    }

    fun dateStringConvertToFormatDisablePastDates(
        dateStr: String,
        inputFormat: String,
        outputFormat: String
    ): String? {
        return try {
            val date = SimpleDateFormat(inputFormat, Locale.US).parse(dateStr)
            val currentDate = Date() // Get the current date

            if (date != null && !date.before(currentDate)) {
                val outputFormat = SimpleDateFormat(outputFormat, Locale.US)
                outputFormat.format(date) // Format the date if it's not in the past
            } else {
                null // Return null if the date is in the past
            }
        } catch (e: ParseException) {
            e.printStackTrace()
            null // Return null in case of a parsing error
        }
    }
    fun TextView.showDatePicker(
        format: String = "dd-MM-yyyy",
        disablePastDates: Boolean = true
    ) {
        setOnClickListener {

            val calendar = Calendar.getInstance()

            val dialog = DatePickerDialog(
                context,
                { _, year, month, day ->

                    val selected = Calendar.getInstance()
                    selected.set(year, month, day)

                    text = SimpleDateFormat(
                        format,
                        Locale.getDefault()
                    ).format(selected.time)

                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )

            if (disablePastDates) {
                dialog.datePicker.minDate = System.currentTimeMillis() - 1000
            }

            dialog.show()
        }
    }
    fun openDatePicker(editText: TextView, onDateSelected: (formattedDate: String) -> Unit) {
        // Get the current date
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        // Create a DatePickerDialog
        val datePickerDialog = DatePickerDialog(
            editText.context,
            { _, selectedYear, selectedMonth, selectedDay ->
                // Format the selected date as yyyy-MM-dd
                val formattedDate =
                    String.format("%d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay)
                // Call the onDateSelected lambda with the formatted date
                onDateSelected(formattedDate)

            },
            year,
            month,
            day
        )

        // Show the date picker dialog
        datePickerDialog.show()
    }


    fun EditText.transformIntoDatePicker(context: Context, format: String, maxDate: Date? = null) {
        isFocusableInTouchMode = false
        isClickable = true
        isFocusable = false

        val myCalendar = Calendar.getInstance()
        val datePickerOnDataSetListener =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                myCalendar.set(Calendar.YEAR, year)
                myCalendar.set(Calendar.MONTH, monthOfYear)
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                val sdf = SimpleDateFormat(format, Locale.UK)
                setText(sdf.format(myCalendar.time))
            }

        setOnClickListener {
            DatePickerDialog(
                context,
                datePickerOnDataSetListener,
                myCalendar.get(Calendar.YEAR),
                myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)
            ).run {
                maxDate?.time?.also { datePicker.maxDate = it }
                show()
            }
        }
    }


    fun EditText.transformIntoDatePickerWithLast90Days(
        context: Context,
        format: String,
        maxDate: Date? = null
    ) {
        isFocusableInTouchMode = false
        isClickable = true
        isFocusable = false

        val myCalendar = Calendar.getInstance()
        val datePickerOnDataSetListener =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                myCalendar.set(Calendar.YEAR, year)
                myCalendar.set(Calendar.MONTH, monthOfYear)
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                val sdf = SimpleDateFormat(format, Locale.UK)
                setText(sdf.format(myCalendar.time))
            }

        setOnClickListener {
            val currentDate = Calendar.getInstance()

            // Create a Calendar instance for the minimum date (90 days ago)
            val minDateCalendar = Calendar.getInstance()
            minDateCalendar.add(Calendar.DAY_OF_YEAR, -90)

            DatePickerDialog(
                context,
                datePickerOnDataSetListener,
                myCalendar.get(Calendar.YEAR),
                myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)
            ).run {
                // Set max date to today
                datePicker.maxDate = currentDate.timeInMillis

                // Set min date to 90 days before today
                datePicker.minDate = minDateCalendar.timeInMillis

                show()
            }

        }
    }


    fun EditText.transformIntoDatePickerWithDisablePastDates(
        context: Context,
        format: String,
        maxDate: Date? = null
    ) {
        isFocusableInTouchMode = false
        isClickable = true
        isFocusable = false

        val myCalendar = Calendar.getInstance()
        val datePickerOnDataSetListener =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                myCalendar.set(Calendar.YEAR, year)
                myCalendar.set(Calendar.MONTH, monthOfYear)
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                val sdf = SimpleDateFormat(format, Locale.UK)
                setText(sdf.format(myCalendar.time))
            }

        setOnClickListener {
            DatePickerDialog(
                context,
                datePickerOnDataSetListener,
                myCalendar.get(Calendar.YEAR),
                myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)
            ).run {
                // Disable past dates by setting the minimum date to today
                datePicker.minDate = System.currentTimeMillis()

                // Optionally set a maximum date if provided
                maxDate?.time?.also { datePicker.maxDate = it }

                show()
            }
        }
    }

    fun EditText.transformIntoTimePicker(context: Context, format: String) {
        isFocusableInTouchMode = false
        isClickable = true
        isFocusable = false

        val myCalendar = Calendar.getInstance()
        val sdf = SimpleDateFormat(format, Locale.UK)

        val timePickerDialogListener = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
            myCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
            myCalendar.set(Calendar.MINUTE, minute)
            setText(sdf.format(myCalendar.time)) // Update the EditText with the selected time
        }

        setOnClickListener {
            // Parse the time from the EditText, if available
            if (!text.isNullOrEmpty()) {
                try {
                    val parsedDate = sdf.parse(text.toString())
                    parsedDate?.let {
                        myCalendar.time = it
                    }
                } catch (e: Exception) {
                    e.printStackTrace() // Log or handle the parse exception
                }
            }

            TimePickerDialog(
                context,
                timePickerDialogListener,
                myCalendar.get(Calendar.HOUR_OF_DAY),
                myCalendar.get(Calendar.MINUTE),
                true // Use true for 24-hour format; change to false for 12-hour format
            ).show()
        }
    }

    fun EditText.transformIntoTimePicker1(context: Context, format: String) : String{
        isFocusableInTouchMode = false
        isClickable = true
        isFocusable = false

        val myCalendar = Calendar.getInstance()
        val sdf = SimpleDateFormat(format, Locale.UK)

        val timePickerDialogListener = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
            myCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
            myCalendar.set(Calendar.MINUTE, minute)
            setText(sdf.format(myCalendar.time)) // Update the EditText with the selected time
        }

        setOnClickListener {
            // Parse the time from the EditText, if available
            if (!text.isNullOrEmpty()) {
                try {
                    val parsedDate = sdf.parse(text.toString())
                    parsedDate?.let {
                        myCalendar.time = it
                    }
                } catch (e: Exception) {
                    e.printStackTrace() // Log or handle the parse exception
                }
            }

            TimePickerDialog(
                context,
                timePickerDialogListener,
                myCalendar.get(Calendar.HOUR_OF_DAY),
                myCalendar.get(Calendar.MINUTE),
                true // Use true for 24-hour format; change to false for 12-hour format
            ).show()
        }

        return sdf.format(myCalendar.time)
    }


    fun TextView.transformIntoDatePicker(context: Context, format: String, maxDate: Date? = null) {
        isFocusableInTouchMode = false
        isClickable = true
        isFocusable = false

        val myCalendar = Calendar.getInstance()
        val datePickerOnDataSetListener =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                myCalendar.set(Calendar.YEAR, year)
                myCalendar.set(Calendar.MONTH, monthOfYear)
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                val sdf = SimpleDateFormat(format, Locale.UK)
                text = sdf.format(myCalendar.time)
            }

        setOnClickListener {
            DatePickerDialog(
                context, datePickerOnDataSetListener, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)
            ).run {
                maxDate?.time?.also { datePicker.maxDate = it }
                show()
            }
        }
    }

    fun <T> toPrettyJson(data: T): String {
        val gsonPretty: Gson = GsonBuilder().setPrettyPrinting().create()
        return gsonPretty.toJson(data)
    }

    fun TextView.transformIntoDatePicker(
        context: Context,
        format: String,
        maxDate: Date? = null,
        onDateSelected: ((String) -> Unit)? = null // Optional lambda to handle custom actions
    ) {
        isFocusableInTouchMode = false
        isClickable = true
        isFocusable = false

        val myCalendar = Calendar.getInstance()
        val datePickerOnDataSetListener =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                myCalendar.set(Calendar.YEAR, year)
                myCalendar.set(Calendar.MONTH, monthOfYear)
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                val sdf = SimpleDateFormat(format, Locale.UK)
                val selectedDate = sdf.format(myCalendar.time)

                // Set the selected date to the TextView
                text = selectedDate

                // Trigger custom action when date is selected
                onDateSelected?.invoke(selectedDate)
            }

        setOnClickListener {
            DatePickerDialog(
                context, datePickerOnDataSetListener, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)
            ).run {
                maxDate?.time?.also { datePicker.maxDate = it }
                show()
            }
        }
    }


    fun TextView.transformIntoDatePicker(
        context: Context,
        displayFormat: String, // Format for showing in TextView
        apiFormat: String,     // Format for API request
        maxDate: Date? = null,
        onDateSelected: ((String, String) -> Unit)? = null // Optional lambda for both formats
    ) {
        isFocusableInTouchMode = false
        isClickable = true
        isFocusable = false

        val myCalendar = Calendar.getInstance()
        val datePickerOnDataSetListener =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                myCalendar.set(Calendar.YEAR, year)
                myCalendar.set(Calendar.MONTH, monthOfYear)
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                // Format the date in both display and API formats
                val displayDate = SimpleDateFormat(displayFormat, Locale.US).format(myCalendar.time)
                val apiDate = SimpleDateFormat(apiFormat, Locale.US).format(myCalendar.time)

                // Set the display date to the TextView
                setText(displayDate)

                // Trigger custom action with both formats
                onDateSelected?.invoke(displayDate, apiDate)
            }

        setOnClickListener {
            DatePickerDialog(
                context, datePickerOnDataSetListener, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)
            ).run {
                maxDate?.time?.also { datePicker.maxDate = it }
                show()
            }
        }
    }



    fun logoutScreen(context: Context) {
        val mainIntent = Intent(context, ActivitySignIn::class.java)
        mainIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        context?.applicationContext?.startActivity(mainIntent)
        (context as Activity).finish()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun convertDateToDDMMYYYY(dateString: String): String {
        if (dateString.isEmpty()) {
            return "Invalid date: Empty string"
        }

        return try {
            val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val outputFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")

            val date = LocalDate.parse(dateString, inputFormatter)
            date.format(outputFormatter)
        } catch (e: DateTimeParseException) {
            "Invalid date format"
        }
    }

    fun stringDateToDate(stringDate: String): Date {
//        return   Date.parse(stringDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        return SimpleDateFormat("yyyy-MM-dd").parse(stringDate)
    }

    fun errorMessage(context: Context, message: String) {
        MotionToast.createColorToast(
            context as Activity, "Error", message, MotionToastStyle.ERROR,
            MotionToast.GRAVITY_BOTTOM,
            MotionToast.LONG_DURATION,
            ResourcesCompat.getFont(context, www.sanju.motiontoast.R.font.helvetica_regular)
        )

    }

    fun successMessage(context: Context, message: String) {
        MotionToast.createColorToast(
            context as Activity, "Success", message, MotionToastStyle.SUCCESS,
            MotionToast.GRAVITY_BOTTOM,
            MotionToast.LONG_DURATION,
            ResourcesCompat.getFont(context, www.sanju.motiontoast.R.font.helvetica_regular)
        )

    }

    fun warningMessage(context: Context, message: String) {
        MotionToast.createColorToast(
            context as Activity, "Warning", message, MotionToastStyle.WARNING,
            MotionToast.GRAVITY_BOTTOM,
            MotionToast.LONG_DURATION,
            ResourcesCompat.getFont(context, www.sanju.motiontoast.R.font.helvetica_regular)
        )

    }

    fun getCurrentDateTimeFormatted(): String? {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm a")
        val currentDate = Date()
        return dateFormat.format(currentDate)
    }


    fun getCurrentDateTimeFormatted_hh_mm_ss(): String? {

        // val dateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm:ss a")
        val dateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm:ss a", Locale.US)
        val currentDate = Date()
        return dateFormat.format(currentDate)
    }

    fun isPalindrome(input: String): Boolean {
        var i = 0
        var j = input.length - 1
        var result = true


        while (i < j) {
            if (input[i] != input[j]) {
                result = false
                break
            }

            i++
            j--
        }

        return result
    }

    fun Fragment.hideKeyboard() {
        view?.let { activity?.hideKeyboard(it) }
    }

    fun Activity.hideKeyboard() {
        hideKeyboard(currentFocus ?: View(this))
    }

    fun Context.hideKeyboard(view: View) {
        val inputMethodManager =
            getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun getTodaysDate(): String? {
        return SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
    }

    fun getTodaysDatePlusOne(): String? {
        val calendar = Calendar.getInstance() // Get current date
        calendar.add(Calendar.DAY_OF_YEAR, 1) // Add one day
        return SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(calendar.time)
    }

    fun getTodayDatePlusOneReverseFormat(): String? {
        val calendar = Calendar.getInstance() // Get current date
        calendar.add(Calendar.DAY_OF_YEAR, 1) // Add one day
        return SimpleDateFormat("yyyy-mm-dd", Locale.getDefault()).format(calendar.time)
    }

    fun getFirstDateofMonth(): String? {
        val c = Calendar.getInstance()   // this takes current date
        c.set(Calendar.DAY_OF_MONTH, 1);
        System.out.println(c.getTime())
        return SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(c.getTime())
    }


    fun getLastDateOfMonth(): String? {
        val calendar = Calendar.getInstance()  // This takes the current date
        calendar.set(
            Calendar.DAY_OF_MONTH,
            calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        ) // Set to the last day of the month
        return SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(calendar.time)
    }

    fun getTodaysDateINdd_mm_yyyy(): String? {
        return SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
    }

    @JvmStatic
    fun getTodaysDatervrsfrmt(): String? {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }


    @JvmStatic
    fun getTCurrentTime(): String? {
        return SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
    }

    fun getTCurrentTime_hh_mm_ss_aa(): String? {
        return SimpleDateFormat("hh:mm:ss a", Locale.getDefault()).format(Date())
    }

    fun getRoundOffUpTOTwo(v: String): String? {
        Log.e("TAG", "getRoundOffUpTOTwo: $v")
        val roundOffSales = Math.round(java.lang.Double.valueOf(v) * 100.0) / 100.0
        return roundOffSales.toString()
    }

    fun foo(value: Double): String? //Got here 6.743240136E7 or something..
    {
        val formatter: DecimalFormat
        formatter =
            if (value - value.toInt() > 0.0) DecimalFormat("0.00") //Here you can also deal with rounding if you wish..
            else DecimalFormat("0.00")
        return formatter.format(value)
    }

    fun numberToK(number: String?): String? {
        var number = number
        if (number == null || number.equals("null", ignoreCase = true)) number =
            "00" else if (number.isEmpty()) number = "00"
        // DecimalFormat df = new DecimalFormat("0.00");
        val df = DecimalFormat("0")
        val amount = df.format(number.toDouble()).toDouble()
        val format =
            NumberFormat.getInstance(Locale("en", "IN"))
        return format.format(amount)
    }


    fun firstDateOfFinancialYear(): String? {
        val calendar = Calendar.getInstance()
        //  calendar.set(Calendar.YEAR,-1);
        val year = calendar[Calendar.YEAR]
        val month = calendar[Calendar.MONTH]

        // Calculate financial year starting and ending dates
        val startDate: String
        val endDate: String
        if (month >= Calendar.APRIL) {
            // If the current month is April or later, the financial year starts from the current year
            startDate = "$year-04-01"
            endDate = (year + 1).toString() + "-03-31"
        } else {
            // If the current month is before April, the financial year starts from the previous year
            startDate = (year - 1).toString() + "-04-01"
            endDate = "$year-03-31"
        }

        // Format dates in "yyyy-MM-dd" format
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        val formattedStartDate = dateFormat.format(calendar.time)
        val formattedEndDate = dateFormat.format(calendar.time)
        // return "2023-03-31";
        return startDate
    }

    fun lastDateOfFinancialYear(): String? {
        val calendar = Calendar.getInstance()
        val year = calendar[Calendar.YEAR]
        val month = calendar[Calendar.MONTH]

        // Calculate financial year starting and ending dates
        val startDate: String
        val endDate: String
        if (month >= Calendar.APRIL) {
            // If the current month is April or later, the financial year starts from the current year
            startDate = "$year-04-01"
            endDate = (year + 1).toString() + "-03-31"
        } else {
            // If the current month is before April, the financial year starts from the previous year
            startDate = (year - 1).toString() + "-04-01"
            endDate = "$year-03-31"
        }

        // Format dates in "yyyy-MM-dd" format
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        val formattedStartDate = dateFormat.format(calendar.time)
        val formattedEndDate = dateFormat.format(calendar.time)
        return endDate
    }


    fun numberTokOnlyForStd(number: String?): String? {
        var number = number
        if (number == null || number.equals("null", ignoreCase = true)) number =
            "00" else if (number.isEmpty()) number = "00"
        val df = DecimalFormat("0.00")
        //        DecimalFormat df = new DecimalFormat("0");
        val amount = df.format(number.toDouble()).toDouble()
        val format =
            NumberFormat.getInstance(Locale("en", "IN"))
        return format.format(amount)
    }

    /*fun loadFragmentWithFrameLayout(
        fragment: Fragment,
        id: Int,
        supportFragmentManager: FragmentManager
    ) {
        supportFragmentManager
            .beginTransaction()
            .replace(id, fragment)
            .commit()
    }*/

    fun loadFragmentWithFrameLayout(
        fragment: Fragment,
        id: Int,
        fragmentManager: FragmentManager
    ) {
        Log.d(
            "FragmentLoad",
            "Replacing fragment with ID: $id and fragment: ${fragment::class.java.simpleName}"
        )
        fragmentManager
            .beginTransaction()
            .replace(id, fragment)
            .addToBackStack(null)
            .commitAllowingStateLoss()
    }

    fun setRupeesDrawable(context:Context,textView:TextView,isDrawableStart:Boolean,drawableSize:Int){
        val drawable = ContextCompat.getDrawable(context, R.drawable.ic_rupee_symbol)
        // Set the desired size
        drawable?.setBounds(0, 0, drawableSize, drawableSize) // Width and height in pixels
        // Apply the drawable
        if(isDrawableStart){
            textView.setCompoundDrawables(
                drawable, // Left
                null,     // Top
                null,     // Right
                null      // Bottom
            )
        }else{
            textView.setCompoundDrawables(
                null, // Left
                null,     // Top
                drawable,     // Right
                null      // Bottom
            )
        }


    }

    fun calculateTotalAmount(itemList: MutableList<LocalDataForBACart>, freightCharge: Double): Double {
        var totalAmount = 0.0

        for (item in itemList) {
            val basicAmount = item.Quantity * item.UnitPrice
            val itemDiscount = basicAmount * (item.DiscountPercent.toDouble() / 100)
            val amountAfterItemDiscount = basicAmount - itemDiscount

            val headerDiscount = amountAfterItemDiscount * (Prefs.getString(HEADER_DISCOUNT_PERCENT, "").trimEnd('%').toDoubleOrNull() ?: 0.0) / 100
            val amountAfterHeaderDiscount = amountAfterItemDiscount - headerDiscount

            val taxRate = item.TaxRate.replace("%", "").toDoubleOrNull() ?: 0.0
            val taxAmount = amountAfterHeaderDiscount * (taxRate / 100)

            val total = amountAfterHeaderDiscount + taxAmount
            totalAmount += total
        }

        // Add the freight charge
        totalAmount += freightCharge

        return totalAmount.roundToTwoDecimalPlaces()
    }

    fun Double.roundToTwoDecimalPlaces(): Double {
        return String.format("%.2f", this).toDouble()
    }

    fun convertDoubleInto2DecimalPlaces(number: Double): Double {
        val decimalFormat = DecimalFormat("#.##")
        val formattedNumber = decimalFormat.format(number).toDouble()
        return formattedNumber
    }

    fun calculateItemTotal(
        item: ModelOrderListing.Data.DocumentLine,
        headerDiscountPercent: Double
    ): Map<String, Double> {
        // Calculate basic amount for the item
        val itemBasicAmount = item.Quantity * item.UnitPrice

        // Calculate item discount
        val itemDiscountValue = itemBasicAmount * (item.DiscountPercent.toDouble() / 100)

        // Calculate amount after item discount
        val amountAfterItemDiscount = itemBasicAmount - itemDiscountValue

        // Calculate header discount
        val headerDiscountValue = amountAfterItemDiscount * (headerDiscountPercent / 100)

        // Calculate amount after header discount
        val amountAfterHeaderDiscount = amountAfterItemDiscount - headerDiscountValue

        // Calculate tax amount
        val taxRate = (item.TaxRate.trimEnd('%').toDoubleOrNull() ?: 0.0) / 100
        val taxAmountValue = amountAfterHeaderDiscount * taxRate

        // Calculate total for this item
        val itemTotal = amountAfterHeaderDiscount + taxAmountValue

        // Return all calculations as a map
        return mapOf(
            "BasicAmount" to itemBasicAmount,
            "ItemDiscount" to itemDiscountValue,
            "HeaderDiscount" to headerDiscountValue,
            "TaxAmount" to taxAmountValue,
            "TotalAmount" to itemTotal
        )
    }


    @SuppressLint("DefaultLocale")
    fun calculateAndSetTotalsForAdapter(
        itemList: List<ModelOrderListing.Data.DocumentLine>,
        freightCharge: Double,
        tvBasicAmount: TextView,
        tvItemDiscount: TextView,
        tvHeaderDiscount: TextView,
        tvTaxAmount: TextView,
        tvFreightCharge: TextView,
        tvGrandTotal: TextView
    ) {
        var basicAmount = 0.0
        var itemDiscount = 0.0
        var headerDiscount = 0.0
        var taxAmount = 0.0
        var totalAmount = 0.0

        for (item in itemList) {
            // Calculate basic amount for each item
            val itemBasicAmount = item.Quantity * item.UnitPrice
            basicAmount += itemBasicAmount

            // Calculate item discount
            val itemDiscountValue = itemBasicAmount * (item.DiscountPercent.toDouble() / 100)
            itemDiscount += itemDiscountValue

            // Calculate amount after item discount
            val amountAfterItemDiscount = itemBasicAmount - itemDiscountValue

            // Calculate header discount
            val headerDiscountValue = amountAfterItemDiscount * (Prefs.getString(HEADER_DISCOUNT_PERCENT, "").trimEnd('%').toDoubleOrNull() ?: 0.0) / 100
            headerDiscount += headerDiscountValue

            // Calculate amount after header discount
            val amountAfterHeaderDiscount = amountAfterItemDiscount - headerDiscountValue

            // Calculate tax amount
            val taxRate = (item.TaxRate.trimEnd('%').toDoubleOrNull() ?: 0.0) / 100
            val taxAmountValue = amountAfterHeaderDiscount * taxRate
            taxAmount += taxAmountValue

            // Calculate total for this item
            val itemTotal = amountAfterHeaderDiscount + taxAmountValue
            totalAmount += itemTotal
        }

        // Add freight charge to the total amount
        totalAmount += freightCharge

        // Update UI TextViews
        tvBasicAmount.text = String.format("₹ %.2f", basicAmount)
        tvItemDiscount.text = String.format("₹ %.2f", itemDiscount)
        tvHeaderDiscount.text = String.format("₹ %.2f", headerDiscount)
        tvTaxAmount.text = String.format("₹ %.2f", taxAmount)
        tvFreightCharge.text = String.format("₹ %.2f", freightCharge)
        tvGrandTotal.text = String.format("₹ %.2f", totalAmount)
    }

    @SuppressLint("DefaultLocale")
    fun calculateAndSetTotals(
        itemList: MutableList<LocalDataForBACart>,
        freightCharge: Double,
        tvBasicAmount: TextView,
        tvItemDiscount: TextView,
        tvHeaderDiscount: TextView,
        tvTaxAmount: TextView,
        tvFreightCharge: TextView,
        tvGrandTotal: TextView
    ) {
        var basicAmount = 0.0
        var itemDiscount = 0.0
        var headerDiscount = 0.0
        var taxAmount = 0.0
        var totalAmount = 0.0

        for (item in itemList) {
            // Calculate basic amount for each item
            val itemBasicAmount = item.Quantity * item.UnitPrice
            basicAmount += itemBasicAmount

            // Calculate item discount
            val itemDiscountValue = itemBasicAmount * (item.DiscountPercent.toDouble() / 100)
            itemDiscount += itemDiscountValue

            // Calculate amount after item discount
            val amountAfterItemDiscount = itemBasicAmount - itemDiscountValue

            // Calculate header discount
            val headerDiscountValue = amountAfterItemDiscount * (Prefs.getString(HEADER_DISCOUNT_PERCENT, "").trimEnd('%').toDoubleOrNull() ?: 0.0) / 100
            headerDiscount += headerDiscountValue

            // Calculate amount after header discount
            val amountAfterHeaderDiscount = amountAfterItemDiscount - headerDiscountValue

            // Calculate tax amount
            val taxRate = (item.TaxRate.trimEnd('%').toDoubleOrNull() ?: 0.0) / 100
            val taxAmountValue = amountAfterHeaderDiscount * taxRate
            taxAmount += taxAmountValue

            // Calculate total for this item
            val itemTotal = amountAfterHeaderDiscount + taxAmountValue
            totalAmount += itemTotal
        }

        // Add freight charge to the total amount
        totalAmount += freightCharge

        // Update UI TextViews
        tvBasicAmount.text = String.format("₹ %.2f", basicAmount)
        tvItemDiscount.text = String.format("₹ %.2f", itemDiscount)
        tvHeaderDiscount.text = String.format("₹ %.2f", headerDiscount)
        tvTaxAmount.text = String.format("₹ %.2f", taxAmount)
        tvFreightCharge.text = String.format("₹ %.2f", freightCharge)
        tvGrandTotal.text = String.format("₹ %.2f", totalAmount)
    }

    fun calculateBasePriceWithQtyAndUnitPrice(item: LocalDataForBACart): Double {
        return (item.UnitPrice * item.Quantity).toDouble()
    }

    fun calculateAmountAfterItemDiscountPercent(item: LocalDataForBACart): Double {
        val basePrice = calculateBasePriceWithQtyAndUnitPrice(item)
        return basePrice - (basePrice * item.DiscountPercent / 100)
    }

    fun calculateAmountAfterHeaderDiscountPercent(item: LocalDataForBACart): Double {
        val amountAfterItemDiscount = calculateAmountAfterItemDiscountPercent(item)
        return amountAfterItemDiscount - (amountAfterItemDiscount * HEADER_DISCOUNT_PERCENT.toDouble() / 100)
    }

    fun calculateAmountAfterIncludeGstTaxRate(item: LocalDataForBACart): Double {
        val calculateAmountAfterHeaderDiscountPercent = calculateAmountAfterHeaderDiscountPercent(item)
        return calculateAmountAfterHeaderDiscountPercent + (calculateAmountAfterHeaderDiscountPercent * item.TaxRate.toDouble() / 100)
    }

    fun calculateAmountAfterIncludeFreightCharges(item: LocalDataForBACart): Double {
        val calculateAmountAfterIncludeGstTaxRate = calculateAmountAfterIncludeGstTaxRate(item)
        return calculateAmountAfterIncludeGstTaxRate + (calculateAmountAfterIncludeGstTaxRate * FREIGHT_CHARGES_PERCENT.toDouble() / 100)
    }

    fun calculateBasePriceWithQuantityAndSPQ(
        currentDocLine: LocalDataForCart
    ): Double {
        var basePrice = currentDocLine.UnitPrice * currentDocLine.Quantity
        /** stringToInt(currentDocLine.SalesQtyPerPackUnit)*/

        return basePrice.toDouble()
    }

    fun calculateBasePriceWithQuantityAndSPQ(
        currentDocLine: LocalDataForBACart
    ): Double {
        var basePrice = currentDocLine.UnitPrice * currentDocLine.Quantity
        /** stringToInt(currentDocLine.SalesQtyPerPackUnit)*/

        return basePrice.toDouble()
    }

    fun calculateDealerDiscount(currentDocLine: LocalDataForCart): Double {
        var basePrice =
            currentDocLine.UnitPrice * currentDocLine.Quantity

        /** stringToInt(
        currentDocLine.SalesQtyPerPackUnit
        ).toDouble()*/
        var dealerDiscount =
            stringToInt(Prefs.getString(Globals.DEALER_DISC)) * basePrice / 100.0
        return dealerDiscount.toDouble()
    }

    fun getStatePosForCode(stateName: String, stateList: ArrayList<DataStateAll>): Int {
        for (index in stateList.indices) {
            if (stateList[index].Name == stateName) {
                return index
            }
        }
        return -1
    }

    fun isValidBeatPlanAdditionList(dataList: List<BeatPlanCustomerDropDownModel.Data>): Boolean {
        return dataList.all {
            it.CardCode.isNotEmpty() &&
                      it.CardName.isNotEmpty() &&
                      it.timing.isNotEmpty() &&
                      it.priority.isNotEmpty() &&
                      it.remark.isNotEmpty()
        }
    }



    fun calculateSpecialDiscount(currentDocLine: LocalDataForCart): Double {
        var basePrice =
            currentDocLine.UnitPrice * currentDocLine.Quantity

        /** stringToInt(
        currentDocLine.SalesQtyPerPackUnit
        )*/
        var dealerDiscount =
            stringToInt(Prefs.getString(Globals.SPECIAL_DISC)) * basePrice / 100.0
        return dealerDiscount.toDouble()
    }


    fun calculateAdditionalDiscount(currentDocLine: LocalDataForCart): Double {
        var basePrice =
            currentDocLine.UnitPrice * currentDocLine.Quantity

        /** stringToInt(currentDocLine.SalesQtyPerPackUnit)*/

        var dealerDiscount =
            stringToInt(Prefs.getString(Globals.ADDITIONAL_DISC)) * basePrice / 100.0
        return dealerDiscount.toDouble()
    }

    fun formatDoublevlauUpToTwoDecimal(value: Double): String {
        val decimalFormat = DecimalFormat("#.00")
        return decimalFormat.format(value)
    }

    fun calculateTradeDiscount(billingAmount: Double): Double {
        var dealerDiscount =
            stringToInt(Prefs.getString(Globals.DISCOUNT_PERCENT)) * billingAmount / 100.0
        return dealerDiscount.toDouble()
    }

    fun calculateGstonBillingAfterMinusTradeDiscount(billingAmount: Double): Double {
        var dealerDiscount =
            18 * billingAmount / 100.0
        return dealerDiscount.toDouble()
    }

    fun getfullformatCurrentTime(): String {
        return SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
    }



    @JvmStatic
    open fun convert_yyyy_mm_dd_to_dd_mm_yyyy(str: String?): String? {
        var convertedDate = ""
        val inputDateFormat = SimpleDateFormat("yyyy-MM-dd")
        val outputDateFormat = SimpleDateFormat("dd-MM-yyyy")
        try {
            val date = inputDateFormat.parse(str)
            convertedDate = outputDateFormat.format(date)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return convertedDate
    }


    fun getCountryCodePos(list: java.util.ArrayList<CountryData>, code: String): Int {
        return list.indexOfFirst { it.name.equals(code, ignoreCase = true) }.takeIf { it != -1 } ?: -1
    }

    fun showMessage(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun showSuccessMessage(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun showErrorMessage(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun isvalidateemail(email_value: EditText): Boolean {
        val checkEmail = email_value.text.toString()
        val hasSpecialEmail = Patterns.EMAIL_ADDRESS.matcher(checkEmail).matches()
        if (!hasSpecialEmail) {
            email_value.error = "This E-Mail address is not valid"
            return true
        }
        return false
    }

    fun showNoInternetDialog(context: Context) {

            val dialog = Dialog(context, R.style.DialogTheme)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(false)
            dialog.setContentView(R.layout.no_internet_connection)
            dialog.window?.setBackgroundDrawable(ContextCompat.getDrawable(context, R.color.transparent))
            dialog.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)

            val tryAgain: Button = dialog.findViewById(R.id.try_again)
            tryAgain.setOnClickListener { dialog.cancel() }

            dialog.show()


    }
    fun showSlowInternetWarning(context: Context) {
        val dialog = Dialog(context, R.style.DialogTheme)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.no_internet_connection)

        dialog.window?.setBackgroundDrawable(
            ContextCompat.getDrawable(context, R.color.transparent)
        )

        val tryAgain: Button = dialog.findViewById(R.id.try_again)
        val no_internet_text: TextView = dialog.findViewById(R.id.no_internet_text)
        no_internet_text.text = "Slow Internet"
        tryAgain.setOnClickListener { dialog.dismiss() }

        dialog.show()
    }

    fun checkInternet(context: Context, onResult: (Boolean, Boolean) -> Unit) {
        val isConnected = ConnectivityReceiver.isConnected()

        if (!isConnected) {
            showNoInternetDialog(context)
            onResult(false, false)
            return
        }

        // check speed in background
        kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Main).launch {
            val isFast = NetworkQualityChecker.isInternetFast()

            if (!isFast) {
                showSlowInternetWarning(context)
            }

            onResult(true, isFast)
        }
    }
}