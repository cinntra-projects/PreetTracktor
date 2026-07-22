package com.preetTractor.galaxyAndroid.data

data class ResponseCategoryAllList(
    val data: List<CategoryAllListData>,
    val message: String,
    val status: Int
){

    data class CategoryAllListData(
        val CategoryImageURL: String,
        val CategoryName: String,
        val CreateDate: Any,
        val CreateTime: Any,
        val UpdateDate: Any,
        val UpdateTime: Any,
        val id: Int
    )
}
