package com.preetTractor.galaxyAndroid.ui.activity.test

data class DataHeirarchYList(
    val Mobile: String,
    val SalesEmployeeCode: String,
    val SalesEmployeeName: String,
    val EmployeeID: String,
    val firstName: String,
    val full_name: String,
    val id: Int,
    val lastName: String,
    val reportees: List<DataHeirarchYList>,
    val role: String,
    var isExpanded: Boolean,
)