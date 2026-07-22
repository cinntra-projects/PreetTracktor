package com.preetTractor.galaxyAndroid.ui.activity.customermodule.model

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Entity(tableName = "industry")
data class IndustryItem(
    @SerializedName("IndustryName")
    var industryName: String? = null,

    @PrimaryKey
    @NonNull
    @SerializedName("IndustryCode")
    var industryCode: String,

    @SerializedName("IndustryDescription")
    var industryDescription: String? = null
) : Serializable {
    // Getter and Setter methods are not required in Kotlin as the properties can be accessed directly
}
