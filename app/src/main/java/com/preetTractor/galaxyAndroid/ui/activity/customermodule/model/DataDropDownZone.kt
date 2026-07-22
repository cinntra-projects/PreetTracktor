package com.preetTractor.galaxyAndroid.ui.activity.customermodule.model


import com.google.gson.annotations.SerializedName

data class DataDropDownZone(
    var id: String? = null,
    @SerializedName("Name") var name: String? = null,
    @SerializedName("Status") var status: String? = null
)