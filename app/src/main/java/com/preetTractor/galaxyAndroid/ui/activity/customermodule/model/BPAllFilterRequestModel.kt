package com.preetTractor.galaxyAndroid.ui.activity.customermodule.model


data class BPAllFilterRequestModel(
    var SalesPersonCode: String? = null,
    var order_by_field: String? = null,
    var order_by_value: String? = null,
    var PageNo: Int = 0,
    var maxItem: Int = 0,
    var SearchText: String? = null,
    var field: Field? = null
) {
    data class Field(
        var CardType: String? = null,
        var Industry: String? = null,
        var SalesPersonPerson: String? = null,
        var U_TYPE: String? = null,
        var SalesPersonCode: String? = null,
        var PayTermsGrpCode: String? = null,
        var CreateDate__gte: String? = null,
        var CreateDate__lte: String? = null
    )
}

