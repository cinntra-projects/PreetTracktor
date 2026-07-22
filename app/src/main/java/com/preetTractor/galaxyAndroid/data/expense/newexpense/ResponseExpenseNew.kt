package com.preetTractor.galaxyAndroid.data.expense.newexpense

data class ResponseExpenseNew(
    val `data`: ArrayList<DataExpenseNewList>,
    val message: String,
    val status: Int
)