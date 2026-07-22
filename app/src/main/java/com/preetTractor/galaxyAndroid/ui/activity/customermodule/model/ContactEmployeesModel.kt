package com.preetTractor.galaxyAndroid.ui.activity.customermodule.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ContactEmployeesModel(
    var countryCode: String? = null,

    @SerializedName("CardCode")
    @Expose
    var cardCode: String? = null,

    @SerializedName("Name")
    @Expose
    var name: String? = null,

    @SerializedName("Position")
    @Expose
    var position: String? = null,

    @SerializedName("Address")
    @Expose
    var address: String? = null,

    @SerializedName("Phone1")
    @Expose
    var phone1: String? = null,

    @SerializedName("Phone2")
    @Expose
    var phone2: String? = null,

    @SerializedName("MobilePhone")
    @Expose
    var mobilePhone: String? = null,

    @SerializedName("Fax")
    @Expose
    var fax: String? = null,

    @SerializedName("E_Mail")
    @Expose
    var email: String? = null,

    @SerializedName("Pager")
    @Expose
    var pager: String? = null,

    @SerializedName("Remarks1")
    @Expose
    var remarks1: String? = null,

    @SerializedName("Remarks2")
    @Expose
    var remarks2: String? = null,

    @SerializedName("Password")
    @Expose
    var password: String? = null,

    @SerializedName("Gender")
    @Expose
    var gender: String? = null,

    @SerializedName("Title")
    @Expose
    var title: String? = null,

    @SerializedName("FirstName")
    @Expose
    var firstName: String? = null,

    @SerializedName("MiddleName")
    @Expose
    var middleName: String? = null,

    @SerializedName("LastName")
    @Expose
    var lastName: String? = null,

    @SerializedName("InternalCode")
    @Expose
    var internalCode: String? = null,

    @SerializedName("id")
    @Expose
    var id: Int = 0
) : Serializable

