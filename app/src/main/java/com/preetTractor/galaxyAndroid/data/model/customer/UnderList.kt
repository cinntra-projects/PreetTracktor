package com.preetTractor.galaxyAndroid.data.model.customer

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class UnderList(
    @SerializedName("InvoiceId")
    @Expose
    var invoiceId: String? = null,

    @SerializedName("DocEntry")
    @Expose
    var docEntry: String? = null,

    @SerializedName("CreateDate")
    @Expose
    var createDate: String? = null,

    @SerializedName("DocDueDate")
    @Expose
    var docDueDate: String? = null,

    @SerializedName("DocTotal")
    @Expose
    var docTotal: String? = null,

    @SerializedName("Month")
    @Expose
    var month: String? = null,

    @SerializedName("OverDueGroup")
    @Expose
    var overDueGroup: String? = null,

    @SerializedName("OverDueDays")
    @Expose
    var overDueDays: Int? = null
) : Serializable

