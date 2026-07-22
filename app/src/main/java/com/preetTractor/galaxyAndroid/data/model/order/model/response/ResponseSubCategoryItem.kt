package com.preetTractor.galaxyAndroid.orderUi.model.response

data class ResponseSubCategoryItem(
    val `data`: List<Data>,
    val message: String,
    val status: Int
){
    data class Data(
        val SubCategoryImage: String,
        val U_UTL_ITSBG: String
    )

}