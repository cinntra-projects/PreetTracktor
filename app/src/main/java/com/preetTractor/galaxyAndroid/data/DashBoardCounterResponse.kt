package com.preetTractor.galaxyAndroid.data

data class DashBoardCounterResponse(
    val `data`: List<DashboardCounterModel>,
    val message: String,
    val status: Int
)