package com.preetTractor.galaxyAndroid.data.model.order.model.response.ba

/**
{
"message": "Success",
"status": 200,
"data": [
{
"id": 1,
"ItemType": "Inventory",
"CodeType": "Manual",
"ItemName": "test items",
"ItemCode": "123",
"ItemImageURL": "/static/image/Item/image.png",
"Description": "",
"TaxCode": 0.0,
"Tax": 2.0,
"HSN": "",
"UnitPrice": 1000.0,
"NetPrice": 0.0,
"Discount": 0.0,
"Inventory": 89,
"UoS": "",
"Packing": "",
"Currency": "INR",
"Status": 1,
"client_id": "1",
"CreateDate": "2024-11-26",
"CreateTime": "18:39:04.230973",
"UpdateDate": "2024-11-26",
"UpdateTime": "18:39:04.231015",
"SKU": "12334",
"ROP": 0,
"as_Recurring": 0,
"Billing_Frequency": "",
"has_Specs": 0,
"Unit": "",
"Weight": "",
"Dimension": "",
"Location": "",
"Duration": "",
"has_add_info": 0,
"CatID": {
"id": 1,
"CategoryName": "Testing Inventory",
"CategoryImageURL": "/static/image/Item-Category/image.png",
"Status": 1,
"client_id": "1",
"CreateDate": "2024-11-26",
"CreateTime": "18:38:13.618732",
"UpdateDate": "2024-11-26",
"UpdateTime": "18:38:13.618790"
}
}
]
}
 */
data class ModelItemAllByCategory(
      var message: String = "",
      var status: Int = 0,
      var `data`: List<Data> = listOf()
) {
      data class Data(
            var id: Int = 0,
            var ItemType: String = "",
            var CodeType: String = "",
            var ItemName: String = "",
            var Quantity: Int = 0,
            var ItemCode: String = "",
            var ItemImageURL: String = "",
            var Description: String = "",
            var TaxCode: Double = 0.0,
            var Tax: Double = 0.0,
            var HSN: String = "",
            var UnitPrice: Double = 0.0,
            var NetPrice: Double = 0.0,
            var Discount: Double = 0.0,
            var Inventory: Int = 0,
            var UoS: String = "",
            var Packing: String = "",
            var Currency: String = "",
            var Status: Int = 0,
            var clientId: String = "",
            var CreateDate: String = "",
            var CreateTime: String = "",
            var UpdateDate: String = "",
            var UpdateTime: String = "",
            var SKU: String = "",
            var ROP: Int = 0,
            var asRecurring: Int = 0,
            var BillingFrequency: String = "",
            var hasSpecs: Int = 0,
            var Unit: String = "",
            var Weight: String = "",
            var Dimension: String = "",
            var Location: String = "",
            var Duration: String = "",
            var hasAddInfo: Int = 0,
            var CatID: CatIDS = CatIDS()
      ) {
            data class CatIDS(
                  var id: Int = 0,
                  var CategoryName: String = "",
                  var CategoryImageURL: String = "",
                  var Status: Int = 0,
                  var clientId: String = "",
                  var CreateDate: String = "",
                  var CreateTime: String = "",
                  var UpdateDate: String = "",
                  var UpdateTime: String = ""
            )
      }
}