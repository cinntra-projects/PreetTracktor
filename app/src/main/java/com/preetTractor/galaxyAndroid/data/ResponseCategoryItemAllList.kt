package com.preetTractor.galaxyAndroid.data

data class ResponseCategoryItemAllList(
    val `data`: List<Data>,
    val message: String,
    val status: Int
){
    data class Data(
        val Billing_Frequency: String,
        val CatID: CatID1,
        val CodeType: String,
        val CreateDate: String,
        val CreateTime: String,
        val Currency: String,
        val Description: String,
        val Dimension: String,
        val Discount: Double,
        val Duration: String,
        val HSN: String,
        val Inventory: Int,
        val ItemCode: String,
        val ItemImageURL: String,
        val ItemName: String,
        val ItemType: String,
        val Location: String,
        val NetPrice: Double,
        val Packing: String,
        val ROP: Int,
        val SKU: String,
        val Status: Int,
        val Tax: Double,
        val TaxCode: Double,
        val Unit: String,
        val UnitPrice: Double,
        val UoS: String,
        val UpdateDate: String,
        val UpdateTime: String,
        val Weight: String,
        val as_Recurring: Int,
        val client_id: String,
        val has_Specs: Int,
        val has_add_info: Int,
        val id: Int
    ){
        data class CatID1(
            val CategoryImageURL: String,
            val CategoryName: String,
            val CreateDate: String,
            val CreateTime: String,
            val Status: Int,
            val UpdateDate: String,
            val UpdateTime: String,
            val client_id: String,
            val id: Int
        )
    }
}




