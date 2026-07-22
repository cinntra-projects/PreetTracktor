package com.preetTractor.galaxyAndroid.moreUi.model.mediaModel

data class AllMediaFileModel(
    val `data`: ArrayList<Data>,
    val message: String,
    val status: Int
){
    data class Data(
        val create_date: String,
        val create_time: String,
        val default_img_url: String,
        val description: String,
        val document_id: String,
        val `file`: String,
        val file_type: String,
        val id: Int,
        val tags: String,
        val title: String,
        val size: String,
        val format: String
    )
}