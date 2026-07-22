package com.preetTractor.galaxyAndroid.data.model.customer

data class ResponseItemListCustomerDashboard(
    val `data`: List<DataItemDashboard>,
    val extra: Extra,
    val message: String,
    val status: Int
)