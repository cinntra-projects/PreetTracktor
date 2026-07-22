package com.preetTractor.galaxyAndroid.data

data class BeatPlanCustomerDropDownModel(
    val `data`: ArrayList<Data> = arrayListOf(),
    val message: String = "",
    val status: Int = 0
) {
    data class Data(
        var CardCode: String = "",
        var CardName: String = "",
        var id: Int = 0,
        var timing: String,
        var priority: String,
        var transport_mode: String = "",
        var remark: String,
        var CheckinTime: String="",
        var CheckoutTime: String="",
        var selectedDate : String = "",
        var bdrcList: MutableList<BdrcData> = mutableListOf()
    )
}