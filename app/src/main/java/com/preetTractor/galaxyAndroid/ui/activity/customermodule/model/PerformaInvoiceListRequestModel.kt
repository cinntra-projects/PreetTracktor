package com.preetTractor.galaxyAndroid.ui.activity.customermodule.model

import com.google.gson.annotations.SerializedName

data class PerformaInvoiceListRequestModel(
    var salesPersonCode: String? = null,
    var PageNo: Int = 0,
    var maxItem: String? = null,
    var SearchText: String? = null,
    var order_by_field: String? = null,
    var order_by_value: String? = null,
    var field: Field = Field()
) {
    data class Field(
        @SerializedName("CreateDate__gte")
        var fromDate: String? = null,

        @SerializedName("CreateDate__lte")
        var toDate: String? = null,

        @SerializedName("is_draft")
        var isDraft: String? = null,

        var cardCode: String? = null,
        var cardName: String? = null,
        var department: String? = null,
        var status: String? = null,
        var oppoType: String? = null,
        var source: String? = null
    )
}
