package com.preetTractor.galaxyAndroid.data

data class LeadSourceAllResponseModel(
    val `data`: List<Data>,
    val message: String,
    val status: Int
){
    data class Data(
        val CreatedDate: String,
        val CreatedTime: String,
        val Name: String,
        val client_id: String,
        val id: Int
    )
}