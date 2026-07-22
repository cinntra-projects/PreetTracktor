package com.preetTractor.galaxyAndroid.ui.activity.customermodule.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.preetTractor.galaxyAndroid.ui.activity.customermodule.BpAddressConverter
import com.preetTractor.galaxyAndroid.ui.activity.customermodule.ListConverter
import com.preetTractor.galaxyAndroid.ui.activity.customermodule.SaleEmployeeItemConverter
import com.preetTractor.galaxyAndroid.ui.activity.customermodule.UTypeDataConverter
import java.io.Serializable

@Entity(tableName = "table_bussiness_partner_data")
data class BusinessPartnerData(
    @PrimaryKey
    @SerializedName("id")
    @Expose
    var id: Int? = null,

    @SerializedName("CardCode")
    @Expose
    var cardCode: String? = null,

    @SerializedName("CardName")
    @Expose
    var cardName: String? = null,

    @SerializedName("Industry")
    @Expose
    var industry: String? = null,

    @SerializedName("CardType")
    @Expose
    var cardType: String? = null,

    @SerializedName("Website")
    @Expose
    var website: String? = null,

    @SerializedName("EmailAddress")
    @Expose
    var emailAddress: String? = null,

    @SerializedName("Phone1")
    @Expose
    var phone1: String? = null,

    @SerializedName("DiscountPercent")
    @Expose
    var discountPercent: String? = null,

    @SerializedName("Currency")
    @Expose
    var currency: String? = null,

    @SerializedName("IntrestRatePercent")
    @Expose
    var intrestRatePercent: String? = null,

    @SerializedName("CommissionPercent")
    @Expose
    var commissionPercent: String? = null,

    @SerializedName("Notes")
    @Expose
    var notes: String? = null,

    @TypeConverters(ListConverter::class)
    @SerializedName("PayTermsGrpCode")
    @Expose
    var payTermsGrpCode: List<PayMentTerm>? = null,

    @SerializedName("CreditLimit")
    @Expose
    var creditLimit: String? = null,

    @SerializedName("AttachmentEntry")
    @Expose
    var attachmentEntry: String? = null,

    @TypeConverters(SaleEmployeeItemConverter::class)
    @SerializedName("SalesPersonCode")
    @Expose
    var salesPersonCode: List<SalesEmployeeItemKt>? = null,

    @SerializedName("ContactPerson")
    @Expose
    var contactPerson: String? = null,

    @SerializedName("U_PARENTACC")
    @Expose
    var uParentacc: String? = null,

    @SerializedName("U_BPGRP")
    @Expose
    var uBpgrp: String? = null,

    @SerializedName("U_CONTOWNR")
    @Expose
    var uContownr: String? = null,

    @SerializedName("U_RATING")
    @Expose
    var uRating: String? = null,

    @TypeConverters(UTypeDataConverter::class)
    @SerializedName("U_TYPE")
    @Expose
    var uType: List<UTypeData>? = null,

    @SerializedName("U_ANLRVN")
    @Expose
    var uAnlrvn: String? = null,

    @SerializedName("U_CURBAL")
    @Expose
    var uCurbal: String? = null,

    @SerializedName("U_ACCNT")
    @Expose
    var uAccnt: String? = null,

    @SerializedName("U_INVNO")
    @Expose
    var uInvno: String? = null,

    @SerializedName("CreateDate")
    @Expose
    var createDate: String? = null,

    @SerializedName("CreateTime")
    @Expose
    var createTime: String? = null,

    @SerializedName("UpdateDate")
    @Expose
    var updateDate: String? = null,

    @SerializedName("UpdateTime")
    @Expose
    var updateTime: String? = null,

    @TypeConverters(BpAddressConverter::class)
    @SerializedName("BPAddresses")
    @Expose
    var bPAddresses: List<BPAddress>? = null,

    @TypeConverters(ContactEmployeesModel::class)
    @SerializedName("ContactEmployees")
    @Expose
    var contactEmployees: List<ContactEmployeesModel>? = null
) : Serializable

