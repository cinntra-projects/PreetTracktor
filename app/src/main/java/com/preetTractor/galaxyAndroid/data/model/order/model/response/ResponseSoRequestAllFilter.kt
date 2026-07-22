package com.preetTractor.galaxyAndroid.orderUi.model.response

data class ResponseSoRequestAllFilter(
    val `data`: MutableList<DataSoRequestAllFilter>,
    val extra: Extra,
    val message: String,
    val status: Int
)