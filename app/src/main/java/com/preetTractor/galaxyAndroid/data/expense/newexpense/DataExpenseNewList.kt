package com.preetTractor.galaxyAndroid.data.expense.newexpense

import com.google.gson.annotations.SerializedName
import com.preetTractor.galaxyAndroid.data.expense.lising.Attach

data class DataExpenseNewList(
    val id: String,
    val salesemployeecode: String,
    @SerializedName("type_of_expense")
    val typeOfExpense: String,
    @SerializedName("expense_name")
    val expenseName: String,
    @SerializedName("from_date")
    val fromDate: String,
    @SerializedName("to_date")
    val toDate: String,
    @SerializedName("num_person")
    val numPerson: String,

    @SerializedName("persons_name")
    val personsName: String,

    @SerializedName("hotel_name")
    val hotelName: String,

    @SerializedName("KM")
    val km: String,


    @SerializedName("meal_status")
    val mealStatus: String,
    val mode: String,
    @SerializedName("expense_amount")
    val expenseAmount: String,
    val latitude: String,
    val longitude: String,
    val address: String,
    val latitude2: String,
    val longitude2: String,
    val address2: String,
    val remarks: String,
    val createDate: String,
    val createTime: String,
    val createdBy: String,
    val updateDate: String,
    val updateTime: String,
    val updatedBy: String,
    val approvedBy: String,
    @SerializedName("approval_status")
    val approvalStatus: String,

    val attach:MutableList<Attach> = mutableListOf()
)