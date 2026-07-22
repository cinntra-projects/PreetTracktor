package com.preetTractor.galaxyAndroid.data.model.customer

data class ResponseSecondaryCustomerList(
    val `data`: List<DataSecondaryCustomerList>,
    val message: String,
    val status: Int
)