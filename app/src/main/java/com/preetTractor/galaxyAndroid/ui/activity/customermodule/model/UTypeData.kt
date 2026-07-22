package com.preetTractor.galaxyAndroid.ui.activity.customermodule.model


import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Entity(tableName = "table_BpTypeData")
data class UTypeData(
    @PrimaryKey
    @SerializedName("id")
    @Expose
    var id: Int? = null,

    @SerializedName("Type")
    @Expose
    var type: String? = null
) : Serializable
