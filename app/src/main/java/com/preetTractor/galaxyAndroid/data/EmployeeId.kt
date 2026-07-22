package com.preetTractor.galaxyAndroid.data

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class EmployeeId : Parcelable, Serializable {
    @SerializedName("id")
    @Expose
    var id: Int? = null

    @SerializedName("companyID")
    @Expose
    var companyID: String? = null

    @SerializedName("SalesEmployeeCode")
    @Expose
    var salesEmployeeCode: String? = null

    @SerializedName("SalesEmployeeName")
    @Expose
    var salesEmployeeName: String? = null

    @SerializedName("EmployeeID")
    @Expose
    var employeeID: String? = null

    @SerializedName("userName")
    @Expose
    var userName: String? = null

    @SerializedName("password")
    @Expose
    var password: String? = null

    @SerializedName("firstName")
    @Expose
    var firstName: String? = null

    @SerializedName("middleName")
    @Expose
    var middleName: String? = null

    @SerializedName("lastName")
    @Expose
    var lastName: String? = null

    @SerializedName("Email")
    @Expose
    var email: String? = null

    @SerializedName("Mobile")
    @Expose
    var mobile: String? = null

    @SerializedName("role")
    @Expose
    var role: String? = null

    @SerializedName("position")
    @Expose
    var position: String? = null

    @SerializedName("branch")
    @Expose
    var branch: String? = null

    @SerializedName("Active")
    @Expose
    var active: String? = null

    @SerializedName("passwordUpdatedOn")
    @Expose
    var passwordUpdatedOn: String? = null

    @SerializedName("lastLoginOn")
    @Expose
    var lastLoginOn: String? = null

    @SerializedName("logedIn")
    @Expose
    var logedIn: String? = null

    @SerializedName("reportingTo")
    @Expose
    var reportingTo: String? = null

    @SerializedName("timestamp")
    @Expose
    var timestamp: String? = null

    @SerializedName("employeeId")
    @Expose
    var empId: String? = null

    protected constructor(`in`: Parcel) {
        this.id = (`in`.readValue((Int::class.java.getClassLoader())) as Int?)
        this.companyID = (`in`.readValue((String::class.java.getClassLoader())) as String?)
        this.salesEmployeeCode = (`in`.readValue((String::class.java.getClassLoader())) as String?)
        this.salesEmployeeName = (`in`.readValue((String::class.java.getClassLoader())) as String?)
        this.employeeID = (`in`.readValue((String::class.java.getClassLoader())) as String?)
        this.userName = (`in`.readValue((String::class.java.getClassLoader())) as String?)
        this.password = (`in`.readValue((String::class.java.getClassLoader())) as String?)
        this.firstName = (`in`.readValue((String::class.java.getClassLoader())) as String?)
        this.middleName = (`in`.readValue((String::class.java.getClassLoader())) as String?)
        this.lastName = (`in`.readValue((String::class.java.getClassLoader())) as String?)
        this.email = (`in`.readValue((String::class.java.getClassLoader())) as String?)
        this.mobile = (`in`.readValue((String::class.java.getClassLoader())) as String?)
        this.role = (`in`.readValue((String::class.java.getClassLoader())) as String?)
        this.position = (`in`.readValue((String::class.java.getClassLoader())) as String?)
        this.branch = (`in`.readValue((String::class.java.getClassLoader())) as String?)
        this.active = (`in`.readValue((String::class.java.getClassLoader())) as String?)
        this.passwordUpdatedOn = (`in`.readValue((String::class.java.getClassLoader())) as String?)
        this.lastLoginOn = (`in`.readValue((String::class.java.getClassLoader())) as String?)
        this.logedIn = (`in`.readValue((String::class.java.getClassLoader())) as String?)
        this.reportingTo = (`in`.readValue((String::class.java.getClassLoader())) as String?)
        this.timestamp = (`in`.readValue((String::class.java.getClassLoader())) as String?)
        this.empId = (`in`.readValue((String::class.java.getClassLoader())) as String?)
    }

    /**
     * No args constructor for use in serialization
     */
    constructor()

    /**
     * @param salesEmployeeName
     * @param lastName
     * @param role
     * @param salesEmployeeCode
     * @param lastLoginOn
     * @param mobile
     * @param active
     * @param employeeID
     * @param userName
     * @param branch
     * @param logedIn
     * @param firstName
     * @param companyID
     * @param password
     * @param middleName
     * @param id
     * @param position
     * @param passwordUpdatedOn
     * @param email
     * @param reportingTo
     * @param timestamp
     */
    constructor(
        id: Int?,
        companyID: String?,
        salesEmployeeCode: String?,
        salesEmployeeName: String?,
        employeeID: String?,
        userName: String?,
        password: String?,
        firstName: String?,
        middleName: String?,
        lastName: String?,
        email: String?,
        mobile: String?,
        role: String?,
        position: String?,
        branch: String?,
        active: String?,
        passwordUpdatedOn: String?,
        lastLoginOn: String?,
        logedIn: String?,
        reportingTo: String?,
        timestamp: String?
    ) : super() {
        this.id = id
        this.companyID = companyID
        this.salesEmployeeCode = salesEmployeeCode
        this.salesEmployeeName = salesEmployeeName
        this.employeeID = employeeID
        this.userName = userName
        this.password = password
        this.firstName = firstName
        this.middleName = middleName
        this.lastName = lastName
        this.email = email
        this.mobile = mobile
        this.role = role
        this.position = position
        this.branch = branch
        this.active = active
        this.passwordUpdatedOn = passwordUpdatedOn
        this.lastLoginOn = lastLoginOn
        this.logedIn = logedIn
        this.reportingTo = reportingTo
        this.timestamp = timestamp
        this.empId = timestamp
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeValue(id)
        dest.writeValue(companyID)
        dest.writeValue(salesEmployeeCode)
        dest.writeValue(salesEmployeeName)
        dest.writeValue(employeeID)
        dest.writeValue(userName)
        dest.writeValue(password)
        dest.writeValue(firstName)
        dest.writeValue(middleName)
        dest.writeValue(lastName)
        dest.writeValue(email)
        dest.writeValue(mobile)
        dest.writeValue(role)
        dest.writeValue(position)
        dest.writeValue(branch)
        dest.writeValue(active)
        dest.writeValue(passwordUpdatedOn)
        dest.writeValue(lastLoginOn)
        dest.writeValue(logedIn)
        dest.writeValue(reportingTo)
        dest.writeValue(timestamp)
        dest.writeValue(empId)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object {
        val CREATOR: Parcelable.Creator<EmployeeId?> = object : Parcelable.Creator<EmployeeId?> {
            override fun createFromParcel(`in`: Parcel): EmployeeId {
                return EmployeeId(`in`)
            }

            override fun newArray(size: Int): Array<EmployeeId?> {
                return (arrayOfNulls<EmployeeId>(size))
            }
        }
    }
}