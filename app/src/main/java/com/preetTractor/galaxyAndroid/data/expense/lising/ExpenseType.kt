package com.preetTractor.galaxyAndroid.data.expense.lising

data class ExpenseType(
    val attach: List<Attach>,
    val createDate: String,
    val createTime: String,
    val expense_amount: String,
    val expense_id: String,
    val expense_type_id: String,
    val expense_type_name: String,
    val id: Int,
    val remarks: String,
    val updateDate: String,
    val updateTime: String
)