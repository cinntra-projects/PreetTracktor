package com.preetTractor.galaxyAndroid.ui.activity.test

data class Reportee(
    val Mobile: String,
    val SalesEmployeeCode: String,
    val SalesEmployeeName: String,
    val full_name: String,
    val id: Int,
    val reportees: List<Reportee>,
    val role: String
)