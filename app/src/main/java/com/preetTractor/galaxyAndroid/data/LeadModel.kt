package com.preetTractor.galaxyAndroid.data

data class LeadModel(
    var leadList: ArrayList<LeadDataList>
){
    data class LeadDataList(
        var selectedLeadId:String,
        var leadName:String,
        var timing:String,
        var priority:String,
        var transport_mode:String= "",
        var remark:String
    )
}
