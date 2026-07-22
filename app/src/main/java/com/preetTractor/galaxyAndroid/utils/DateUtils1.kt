package com.preetTractor.galaxyAndroid.utils

import java.text.SimpleDateFormat
import java.util.*

object DateUtils1 {

    // Get the current month name
    fun getCurrentMonth(): String {
        val calendar = Calendar.getInstance()
        val monthFormat = SimpleDateFormat("MMMM", Locale.getDefault())
        return monthFormat.format(calendar.time)
    }

    // Get the total number of days in the current month
    fun getTotalDaysInCurrentMonth(): Int {
        val calendar = Calendar.getInstance()
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    }

    // Get today's date (day of the month only)
    fun getTodayDate(): String {
        val calendar = Calendar.getInstance()
        return calendar.get(Calendar.DAY_OF_MONTH).toString()
    }

}