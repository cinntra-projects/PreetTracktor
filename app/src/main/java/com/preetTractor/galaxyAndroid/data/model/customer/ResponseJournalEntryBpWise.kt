package com.preetTractor.galaxyAndroid.data.model.customer

data class ResponseJournalEntryBpWise(
     var message: String,
    var status: Int,
     var data: ArrayList<DataCustomerLedger>
)