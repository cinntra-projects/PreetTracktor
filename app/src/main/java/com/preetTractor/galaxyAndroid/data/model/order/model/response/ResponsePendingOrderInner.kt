package com.preetTractor.galaxyAndroid.orderUi.model.response

data class ResponsePendingOrderInner(
    val `data`: List<Data>,
    val message: String,
    val status: Int
){
    data class Data(
        val DocDueDate: String,
        val DocNum: String,
        val LineTotalSum: String,
        val Quantity: String,
        val id: String
    )
}