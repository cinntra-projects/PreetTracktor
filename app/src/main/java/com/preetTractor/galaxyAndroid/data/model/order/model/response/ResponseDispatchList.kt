package com.preetTractor.galaxyAndroid.orderUi.model.response

data class ResponseDispatchList(
    val `data`: List<Data>,
    val message: String,
    val status: Int
){
    data class Data(
        val DocDate: String,
        val DocEntry: String,
        val NetTotal: String,
        val U_TransporterName: String,
        val id: Int
    )
}