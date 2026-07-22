package com.preetTractor.galaxyAndroid.orderUi.model

data class OrderOneDetailModel(
    val `data`: List<Data>,
    val message: String,
    val status: Int
){
    data class Data(
        val AdditionalCharges: String,
        val AddressExtension: List<AddressExtension>,
        val ApprovalStatus: String,
        val ApproverId: String,
        val Attach: List<Any>,
        val CancelStatus: String,
        val CardCode: String,
        val CardName: String,
        val Comments: String,
       // val ContactPersonCode: List<ContactPersonCode>,
        val ContactPersonCode:String,
        val CreateDate: String,
        val CreateTime: String,
        //val CreatedBy: List<CreatedBy>,
        val CreatedBy: String,
        val CreationDate: String,
        val DeliveryCharge: String,
        val DeliveryMode: String,
        val DeliveryTerm: String,
        val DiscountPercent: String,
        val DocCurrency: String,
        val DocDate: String,
        val DocDueDate: String,
        val DocEntry: String,
        val DocNum: String,
        val DocTotal: String,
        val GrossTotal: String,
        val DocumentLines: List<DocumentLine>,
        val DocumentStatus: String,
        val FreeDelivery: String,
        val Link: String,
        val NetTotal: String,
        //val PayTermsGrpCode: List<PayTermsGrpCode>,
        val PayTermsGrpCode: String,
        val PaymentType: String,
        //val SalesPersonCode: List<SalesPersonCode>,
        val SalesPersonCode: String,
        val TaxDate: String,
        val TermCondition: String,
        val U_LAT: String,
        val U_LONG: String,
        val U_OPPID: String,
        val U_OPPRNM: String,
        val U_QUOTID: String,
        val U_QUOTNM: String,
        val Unit: String,
        val UpdateDate: String,
        val UpdateTime: String,
        val VatSum: String,
        val id: String
    )

    data class AddressExtension(
        val BillToBuilding: String,
        val BillToCity: String,
        val BillToCountry: String,
        val BillToDistrict: String,
        val BillToState: String,
        val BillToStreet: String,
        val BillToZipCode: String,
        val OrderID: String,
        val ShipToBuilding: String,
        val ShipToCity: String,
        val ShipToCountry: String,
        val ShipToDistrict: String,
        val ShipToState: String,
        val ShipToStreet: String,
        val ShipToZipCode: String,
        val U_BCOUNTRY: String,
        val U_BSTATE: String,
        val U_SCOUNTRY: String,
        val U_SHPTYPB: String,
        val U_SHPTYPS: String,
        val U_SSTATE: String,
        val id: Int
    )

    data class DocumentLine(
        val DiscountPercent: String,
        val FreeText: String,
        val ItemCode: String,
        val ItemDescription: String,
        val LineNum: String,
        val LineStatus: String,
        val PriceType: String,
        val LineTotal: String,
        val OpenAmount: String,
        val OrderID: String,
        val Price: String,
        val PriceAfterVAT: String,
        val Quantity: Int,
        val RemainingOpenQuantity: String,
        val TaxCode: String,
        val TaxRate: String,
        val UnitPrice: String,
        val SalesQtyPerPackUnit: String?,
        val UnitPriceown: String,
        val UnitWeight: String,
        val UomNo: String,
        val id: String
    )

    data class PayTermsGrpCode(
        val GroupNumber: String,
        val PaymentTermsGroupName: String,
        val id: Int
    )

    data class SalesPersonCode(
        val Email: String,
        val Mobile: String,
        val SalesEmployeeCode: String,
        val SalesEmployeeName: String,
        val id: Int
    )


    data class CreatedBy(
        val Email: String,
        val Mobile: String,
        val SalesEmployeeCode: String,
        val SalesEmployeeName: String,
        val id: Int
    )


    data class ContactPersonCode(
        val E_Mail: String,
        val FirstName: String,
        val MobilePhone: String,
        val id: Int
    )
}