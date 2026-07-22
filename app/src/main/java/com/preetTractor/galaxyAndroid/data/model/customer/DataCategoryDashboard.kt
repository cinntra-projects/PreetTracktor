package com.preetTractor.galaxyAndroid.data.model.customer

data class DataCategoryDashboard(
    val GroupCode: String,
    val GroupName: String,
    val SubGroup: List<Any>,
    val TotalPrice: String,
    val TotalQty: String
)