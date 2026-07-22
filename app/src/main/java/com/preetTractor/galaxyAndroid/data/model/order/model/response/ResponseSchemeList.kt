package com.preetTractor.galaxyAndroid.orderUi.model.response

data class ResponseSchemeList(
    val `data`: List<Data>,
    val message: String,
    val status: Int
){
    data class Data(
        val attachment: List<Attachment>,
        val cat_name: String,
        val create_date: String,
        val create_time: String,
        val created_by: String,
        val discount_percent: String,
        val end_date: String,
        val id: String,
        val item_code: String,
        val item_list: List<Item>,
        val start_date: String,
        val status: String,
        val update_date: String,
        val update_time: String
    ){
        data class Attachment(
            val File: String
        )
        data class Item(
            val ItemCode: String,
            val ItemName: String
        )
    }
}