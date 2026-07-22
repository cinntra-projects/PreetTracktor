package com.preetTractor.galaxyAndroid.data.expense.lising

data class ExpenseListModel(
    val `data`: List<AllExpenseData>,
    val message: String,
    val status: Int
)