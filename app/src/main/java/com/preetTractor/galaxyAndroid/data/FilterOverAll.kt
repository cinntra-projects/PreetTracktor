package com.preetTractor.galaxyAndroid.data


data class FilterOverAll (
    var field: FieldFilter? = null,
    var leadType: String? = null,
    var maxItem:Int = 30 ,
    var order_by_field: String? = null,
    var order_by_value: String? = null,
    var SalesPersonCode: String? = null,
    var SearchText: String? = null,
    var PageNo: Int = 30

)
