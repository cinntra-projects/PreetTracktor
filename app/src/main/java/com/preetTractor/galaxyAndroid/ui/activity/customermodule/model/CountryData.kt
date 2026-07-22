package com.preetTractor.galaxyAndroid.ui.activity.customermodule.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Entity(tableName = "data_country")
data class CountryData(
    @PrimaryKey
    @SerializedName("id")
    @Expose
    val id: Int,

    @SerializedName("Code")
    @Expose
    val code: String? = null,

    @SerializedName("Name")
    @Expose
    val name: String? = null
) : Serializable {
    companion object {
        private const val serialVersionUID = -2656043793485762717L
    }
}
