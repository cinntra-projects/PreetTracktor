package com.preetTractor.galaxyAndroid.data

data class BdrcData(
    val Advances: Int,
    val BillingTarget: Int= 0,
    val CardCode: String,
    val City: String,
    val CollectionTarget: Int=0,
    val CustomerName: String,
    val DeliveryTarget: Int=0,
    val EmpCode: String,
    val EmpName: String,
    val FinancialYear: String,
    val Month: String,
    val PhysicalSTK: Int,
    val RetailTarget: Int=0,
    val State: String,
    val id: Int
)