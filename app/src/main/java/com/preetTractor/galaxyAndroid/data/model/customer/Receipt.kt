package com.preetTractor.galaxyAndroid.data.model.customer
import com.google.gson.annotations.SerializedName

data class Receipt(
    @SerializedName("ReceiptId")
    val receiptId: String,

    @SerializedName("DocEntry")
    val docEntry: String,

    @SerializedName("CreateDate")
    val createDate: String,

    @SerializedName("DocTotal")
    val docTotal: String
)

