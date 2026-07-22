package com.preetTractor.galaxyAndroid.orderUi.model

data class PendingOrderWiseListModel(
    val `data`: Data,
    val message: String,
    val status: Int
){
    data class Data(
        val orderwise: List<Orderwise>
    )


    data class Orderwise(
        val CardCode: String,
        val CardName: String,
        val DocDueDate: String,
        val DocNum: String,
        val OrderDocEntry: String,
        val OrderID: Int,
        val PendingAmount: Double,
        val PendingQty: Int
    )
}