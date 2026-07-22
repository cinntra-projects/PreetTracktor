package com.preetTractor.galaxyAndroid.data.model.notes

import com.preetTractor.galaxyAndroid.orderUi.model.response.ResponseSchemeList

data class DataAllNotes(
    val BeatPlan_id: String,
    val CardCode: String,
    val CardName: String,
    val Create_Date: String,
    val Create_Time: String,
    val CreatedBy: String,
    val Remark: String,
    val Title: String,
    val Update_Date: String,
    val Update_Time: String,
    val id: Int,
    val Attach:ArrayList<ResponseSchemeList.Data.Attachment>
)