package com.preetTractor.galaxyAndroid.data

import com.preetTractor.galaxyAndroid.data.beatplan.DataBeatPlan


data class AttachmentModel(
    val message: String,
    val status: Int,
    val data: List<Data>,
    val info: List<UserInfo>
) {
    data class Data(
        val id: String,
        val file: String,
        val profileImage: String,
        val deviceId: String,
        val token: String,
        val email: String,
        val phone: String,
        val linkType: String,
        val caption: String,
        val linkID: String,
        val createDate: String,
        val createTime: String,
        val updateDate: String,
        val updateTime: String,
        val size: String,
        val attendance_timestamp: String,
        val attendance_status: String,
        val employee_detail: EmployeeDetails,
        // val beatplan_detail:ArrayList<DataBeatPlan>
        val today_beatplan_detail:ArrayList<DataBeatPlan>
    )

    data class UserInfo(
        val email: String,
        val password: String,
        val FCM: String = "",
        val app_id: String
    )
}



