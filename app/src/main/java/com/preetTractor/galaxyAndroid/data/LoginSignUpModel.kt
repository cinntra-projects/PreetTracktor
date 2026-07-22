package com.preetTractor.galaxyAndroid.data

class LoginSignUpModel(
    val message: String,
    val status: Int,
    val data: ArrayList<Data>,
    val errors: String,
    val info: List<UserInfo>

    ) {

    data class Data(
        val otp: String,
        val distributor_id: String="",
        val card_code: String="",
        val card_name: String="",
        val token: String,
        val SalesEmployeeCode: String,
        val emp_code: String
    )

    data class UserInfo(
        val email: String,
        val password: String,
        val FCM: String = "",
        val app_id: String
    )
}