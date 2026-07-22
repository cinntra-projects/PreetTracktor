package com.preetTractor.galaxyAndroid.data


import com.google.gson.annotations.SerializedName

data class ResponseBackGroundLocation(
    val message: String,
    val status: String,
    @SerializedName("total_distance") val totalDistance: String,
    val data: List<Datum>
) {
    data class Datum(
        val id: String,
        @SerializedName("SalesEmployeeCode") val salesEmployeeCode: String,
        @SerializedName("Latitude") val latitude: String,
        @SerializedName("Longitude") val longitude: String,
        @SerializedName("Address") val address: String,
        @SerializedName("Create_Date") val createDate: String,
        @SerializedName("Create_Time") val createTime: String
    )
}

