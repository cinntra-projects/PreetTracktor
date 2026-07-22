package com.preetTractor.galaxyAndroid.data.model.customer

import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose

data class ReceivableList(
    @SerializedName("InvoiceId")
    val invoiceId: String,

    @SerializedName("DocEntry")
    val docEntry: String,

    @SerializedName("CreateDate")
    val createDate: String,

    @SerializedName("DocTotal")
    val docTotal: String,

    @SerializedName("OverDueGroup")
    @Expose
    val overDueGroup: String
)

