package com.preetTractor.galaxyAndroid.data

import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class LeadTypeData : Serializable {
    @PrimaryKey
    @SerializedName("id")
    @Expose
    var id: Int? = null

    @SerializedName("Name")
    @Expose
    var name: String? = null

    @SerializedName("CreatedDate")
    @Expose
    var createdDate: String? = null

    @SerializedName("CreatedTime")
    @Expose
    var createdTime: String? = null

    var isIschecked: Boolean = false
        private set

    /**
     * No args constructor for use in serialization
     */
    constructor()

    /**
     * @param createdDate
     * @param name
     * @param createdTime
     * @param id
     */
    constructor(id: Int?, name: String?, createdDate: String?, createdTime: String?) : super() {
        this.id = id
        this.name = name
        this.createdDate = createdDate
        this.createdTime = createdTime
    }

    fun setIschecked(ischecked: Boolean) {
        this.isIschecked = ischecked
    }

    companion object {
        private const val serialVersionUID = 7065155881312542169L
    }

    override fun toString(): String {
        return name.toString()
    }
}