package com.preetTractor.galaxyAndroid.orderUi.model.response

data class ResponseItemAllCategoryList(
    val `data`: List<Data>,
    val message: String,
    val status: Int
){
    data class Data(
        val CategoryImage: String,
        val U_UTL_ITMCT: String
    )
}