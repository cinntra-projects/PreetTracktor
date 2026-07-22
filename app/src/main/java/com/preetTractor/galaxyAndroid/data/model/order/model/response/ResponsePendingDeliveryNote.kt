package com.preetTractor.galaxyAndroid.orderUi.model.response

data class ResponsePendingDeliveryNote(
    val `data`: List<Data>,
    val message: String,
    val status: Int
):java.io.Serializable {
    data class Data(
        val CardCode: String,
        val ItemCode: String,
        val ItemDescription: String,
        val LineTotalSum: String,
        val Quantity: String
    ):java.io.Serializable
}