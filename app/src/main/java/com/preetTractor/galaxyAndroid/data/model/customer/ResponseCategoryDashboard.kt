package com.preetTractor.galaxyAndroid.data.model.customer

data class ResponseCategoryDashboard(
    val TotalCreditNote: Int,
    val TotalSales: Int,
    val `data`: List<DataCategoryDashboard>,
    val message: String,
    val status: Int
)