package com.preetTractor.galaxyAndroid.data.model.customer

data class ResponseOutletPicsFromCustomer(
    val `data`: List<DataOutletPicsFromCustomer>,
    val message: String,
    val status: Int
)