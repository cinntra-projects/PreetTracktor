package com.preetTractor.galaxyAndroid.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class LeadValue(
    val CreateDate: String = "",
    val CreateTime: String= "",
    val CreatedBy: EmployeeValue? = null,
    val UpdateDate: String= "",
    val UpdateTime: String= "",
    val UpdatedBy: EmployeeValue? = null,
    val assignedTo: EmployeeValue? = null,
    val campaign: String= "",
    val client_id: String= "",
    val companyName: String= "",
    val contactPerson: String= "",
    val date: String= "",
    val designation: String= "",
    val email: String= "",
    val employeeId: EmployeeValue? = null,
    val hasBP: Boolean = false,
    var id: Int = 0,
    val industry_type: String?= null,
    val junk: Int = 0,
    val leadType: String= "",
    val lead_id: String= "",
    val lead_remarks: String= "",
    val location: String= "",
    val message: String= "",
    val new_interest: String= "",
    val new_product_interest: String= "",
    val next_follow_up_date: String= "",
    val numOfEmployee: Int = 0,
    val organization_rating: String= "",
    val phoneNumber: String= "",
    val productInterest: String= "",
    val source: String= "",
    val source_id: Int= 0,
    val status: String= "",
    val turnover: String= "",
    val valuation: String= "",
    val zone: String?= null,
    val state: String= ""
): Parcelable