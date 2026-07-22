package com.preetTractor.galaxyAndroid.data


data class FieldFilter (
    var assignedTo_id__in: ArrayList<String?>? = null,
    var source__in: ArrayList<String?>? = null,
    var CreateDate__gte: String? = null,
    var status: String? = null,
    var CreateDate__lte: String? = null,
    var StateCode: String? = null

)