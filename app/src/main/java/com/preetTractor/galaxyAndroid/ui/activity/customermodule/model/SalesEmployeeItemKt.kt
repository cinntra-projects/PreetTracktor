package com.preetTractor.galaxyAndroid.ui.activity.customermodule.model
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class SalesEmployeeItemKt(
    @SerializedName("SalesEmployeeCode")
    @Expose
    var salesEmployeeCode: String? = null,

    @SerializedName("SalesEmployeeName")
    @Expose
    var salesEmployeeName: String? = null,

    @SerializedName("userName")
    @Expose
    var userName: String? = null,

    @SerializedName("role")
    @Expose
    var role: String? = null,

    @SerializedName("month")
    @Expose
    var month: String? = null,

    @SerializedName("Emp")
    @Expose
    var emp: String? = null,

    @SerializedName("date")
    @Expose
    var date: String? = null,

    @SerializedName("Type")
    @Expose
    var type: String? = null,

    var firstName: String? = null,

    @SerializedName("id")
    @Expose
    var id: Int? = null
) : Serializable {
    // Getters and setters are not required in Kotlin
}
