package com.preetTractor.galaxyAndroid.orderUi.model

data class PendingByOrderModel(
    val `data`: List<Data>,
    val message: String,
    val status: Int
){
    data class Data(
        val DocDueDate: String,
        val ItemCode: String,
        val ItemDescription: String,
        val OrderDocEntry: String,
        val OrderID: String,
        val PendingAmount: String,
        val PendingQty: String,
        val Quantity: String,
        val UnitPrice: String
    )
}