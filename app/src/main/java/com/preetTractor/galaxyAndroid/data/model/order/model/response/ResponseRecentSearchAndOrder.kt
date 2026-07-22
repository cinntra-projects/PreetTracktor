package com.preetTractor.galaxyAndroid.orderUi.model.response

data class ResponseRecentSearchAndOrder(
    val `data`: List<DataRecentSearchAndOrder>,
    val message: String,
    val status: Int
)