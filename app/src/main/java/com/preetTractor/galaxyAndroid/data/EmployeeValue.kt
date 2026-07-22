package com.preetTractor.galaxyAndroid.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class EmployeeValue(
    val Active: String,
    val Email: String,
    val EmployeeID: String,
    val Ext: String,
    val FCM: String,
    val Is_galaxy_user: String,
    val Mobile: String,
    val OTP: String? = null,
    val SalesEmployeeCode: String,
    val SalesEmployeeName: String,
    val Vehicle: String,
    val branch: String,
    val client_id: String,
    val companyID: String,
    val departement: Int,
    val firstName: String,
    val id: Int,
    val is_deleted: Boolean,
    val lastLoginOn: String,
    val lastName: String,
    val logedIn: String,
    val middleName: String,
    val password: String,
    val passwordUpdatedOn: String,
    val position: String,
    val reportingTo: String,
    val role: Int,
    val timestamp: String,
    val userName: String,
    val user_id: String,
    val zone: List<Int>
): Parcelable