package com.preetTractor.galaxyAndroid.ui.activity.customermodule.model

import com.google.gson.annotations.SerializedName

data class DataBusinessType(
    var id: String? = null,

    @SerializedName("Type")
    var type: String? = null
)