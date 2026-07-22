package com.preetTractor.galaxyAndroid.data

data class NonCustomerFormModel(
    var nonCustomerList: ArrayList<NonCustomerData>
){
    data class NonCustomerData(
        var prospectName:String,
        var prospectNumber:String,
        var source:String,
        var selectedSourceId:String,
        var industry:String,
        var zone:String,
        var timing:String,
        var priority:String,
        var transport_mode:String="",
        var remark:String,
        var createLeadCheck:Boolean = false
    )
}
