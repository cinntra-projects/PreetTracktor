package com.preetTractor.galaxyAndroid.moreUi.model.documentsModel

data class DocumentListModel(
    val `data`: ArrayList<Data>,
    val message: String,
    val status: Int,
    val errors: String
) {
    data class Data(
        val create_date: String="",
        val create_time: String="",
        val doc_type: String="",
        val id: Int=-1,
        val image: String="",
        val name: String="",
        val status: String=""
    )
}