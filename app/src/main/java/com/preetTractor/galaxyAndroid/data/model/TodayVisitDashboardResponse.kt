package com.preetTractor.galaxyAndroid.data.model

data class TodayVisitDashboardResponse(
    val `data`: List<Data>,
    val message: String,
    val status: String
){
    data class Data(
        val completed_visit: Int,
        val total_visit: Int
    )
}
