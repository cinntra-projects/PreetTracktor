package com.preetTractor.galaxyAndroid.data.model.customer

import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose
import java.io.Serializable

data class MonthGroupSalesList(
    @SerializedName("Month")
    @Expose
    var month: String="",

    @SerializedName("DocTotal")
    @Expose
    var docTotal: String=""
) : Serializable

