package com.preetTractor.galaxyAndroid.moreUi.model.enquiryModel

data class EnquiryListModel(
    val `data`: ArrayList<Data>,
    val errors: String,
    val message: String,
    val status: Int
){
    data class Data(
        val card_code: String,
        val card_name: String,
        val create_date: String,
        val create_time: String,
        val enquiry_type: String,
        val id: Int,
        val message: String,
        var status: String,
        val ticket_number: String,
        val update_date: String,
        val update_time: String
    )
}