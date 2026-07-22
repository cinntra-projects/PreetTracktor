package com.preetTractor.galaxyAndroid.ui.activity.customermodule.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class BPAddress(
    @SerializedName("id")
    @Expose
    var id: Int? = null,

    @SerializedName("BPID")
    @Expose
    var bpid: String? = null,

    @SerializedName("BPCode")
    @Expose
    var bPCode: String? = null,

    @SerializedName("AddressName")
    @Expose
    var addressName: String? = null,

    @SerializedName("Street")
    @Expose
    var street: String? = null,

    @SerializedName("Block")
    @Expose
    var block: String? = null,

    @SerializedName("City")
    @Expose
    var city: String? = null,

    @SerializedName("State")
    @Expose
    var state: String? = null,

    @SerializedName("ZipCode")
    @Expose
    var zipCode: String? = null,

    @SerializedName("Country")
    @Expose
    var country: String? = null,

    @SerializedName("AddressType")
    @Expose
    var addressType: String? = null,

    @SerializedName("RowNum")
    @Expose
    var rowNum: String? = null,

    @SerializedName("U_SHPTYP")
    @Expose
    var uShptyp: String? = null,

    @SerializedName("U_COUNTRY")
    @Expose
    var uCountry: String? = null,

    @SerializedName("U_STATE")
    @Expose
    var uState: String? = null,

    @SerializedName("Default")
    @Expose
    var defaultValue: Int? = null
) : Serializable

