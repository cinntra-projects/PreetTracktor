package com.preetTractor.galaxyAndroid.orderUi.model.local

data class LocalDataForBACart(
   var id: String="",
   var OrderID:String,
   var ItemCode: String,
   var ItemDescription: String,
   var UnitPrice: Int,
   var U_UTL_SD: String,
   var Currency: String,
   var TaxCode: String,
   var TaxRate: String,
   var UnitPriceown: Int,
   var U_UTL_TD: String,
   var DiscountPercent: Int,
   var ProjectCode: String,
   var FreeText: String,
   var UomNo: Int,
   var UoMCode: String,
   var Image: String,
   var U_UTL_DD: String,
   var Quantity: Int
)