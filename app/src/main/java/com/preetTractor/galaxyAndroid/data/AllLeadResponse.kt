package com.preetTractor.galaxyAndroid.data

data class AllLeadResponse(
    val `data`: List<Data>,
    val message: String,
    val status: Int
){
    data class Data(
        var companyName: String,
        val id: Int,
        val status: String
    )
}