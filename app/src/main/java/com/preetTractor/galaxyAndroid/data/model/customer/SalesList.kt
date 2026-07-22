package com.preetTractor.galaxyAndroid.data.model.customer

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class SalesList(
    @SerializedName("OrderId")
    var orderId: String? = null,

    @SerializedName("DocEntry")
    var docEntry: String? = null,

    @SerializedName("CreateDate")
    var createDate: String? = null,

    @SerializedName("DocTotal")
    var docTotal: String? = null
) : Serializable

