package com.preetTractor.galaxyAndroid.orderUi.model.response

data class DocumentLineSoRequest(
    val DiscountPercent: Double,
    val FreeText: String,
    val ItemCode: String,
    val ItemDescription: String,
    val LineNum: Int,
    val LineStatus: String,
    val LineTotal: String,
    val OpenAmount: String,
    val OrderID: String,
    val Price: String,
    val PriceAfterVAT: String,
    val PriceType: String,
    val Quantity: Int,
    val RemainingOpenQuantity: String,
    val TaxCode: String,
    val TaxRate: String,
    val U_UTL_DD: String,
    val U_UTL_SD: String,
    val U_UTL_TD: String,
    val UnitPrice: Double,
    val UnitPriceown: String,
    val UnitWeight: String,
    val UomNo: String,
    val id: Int
)