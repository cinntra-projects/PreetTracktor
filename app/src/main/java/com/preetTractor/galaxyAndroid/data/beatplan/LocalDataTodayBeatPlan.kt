package com.preetTractor.galaxyAndroid.data.beatplan

data class LocalDataTodayBeatPlan(
    var id:String,
    var approval_status:String="",
    var City:String="",
    var assined_to:String="",
    var assigned_name:String="",
    var isSelected:Boolean = false,
    var Type:String=""
)
