package com.preetTractor.galaxyAndroid.ui.activity.customermodule.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class IndustryResponse(
    @SerializedName("data") val value: ArrayList<IndustryItem>? = null,
    val status: Int = 0,
    val message: String? = null
) : Serializable
