package com.preetTractor.galaxyAndroid.data

data class DataOuterAttendanceListing(
    val CheckIn_Address: String,
    val CheckIn_Image: String,
    val CheckIn_Lat: String,
    val CheckIn_Long: String,
    val CheckOut_Address: String,
    val CheckOut_Image: String,
    val CheckOut_Lat: String,
    val CheckOut_Long: String,
    val Create_Date: String,
    val Create_Time: String,
    val Total_Hour: Double,
    val Update_Date: String,
    val Update_Time: String,
    val id: String
) : java.io.Serializable