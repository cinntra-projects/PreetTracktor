package com.preetTractor.galaxyAndroid.ui.activity.customermodule.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class BusinessPartnerAllResponse(
    @SerializedName("message")
    @Expose
    var message: String? = null,

    @SerializedName("status")
    @Expose
    var status: Int = 0,

    @SerializedName("data")
    @Expose
    var data: ArrayList<BPListResponse> = ArrayList()
) : Serializable


