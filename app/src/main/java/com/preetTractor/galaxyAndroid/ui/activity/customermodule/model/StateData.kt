package com.preetTractor.galaxyAndroid.ui.activity.customermodule.model
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class StateData(
    @SerializedName("Code")
    var code: String? = null,

    @SerializedName("Country")
    var country: String? = null,

    @SerializedName("Name")
    var name: String? = null,

    @SerializedName("id")
    @Expose
    var id: Int? = null


):Serializable{
    override fun toString(): String {
        return name.toString()
    }
}