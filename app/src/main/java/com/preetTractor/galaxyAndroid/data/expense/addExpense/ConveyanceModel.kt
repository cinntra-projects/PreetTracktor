package com.preetTractor.galaxyAndroid.data.expense.addExpense

import com.preetTractor.galaxyAndroid.helper.Globals

data class ConveyanceModel(
    var id: Int = 1,
    var date: String = Globals.getTodaysDateINdd_mm_yyyy()!!,
    var toDate: String = Globals.getTodaysDateINdd_mm_yyyy()!!,
    var type: String = "",
    var remark: String = "",
    var location: String = "",
    var amount: Int = 0,
    var camera:ArrayList<String> = arrayListOf(),
    var toLocation: String = "",
    var mealStatus: String = "",
    var noOFPerson: String = "",
    var mode: String = ""
)
