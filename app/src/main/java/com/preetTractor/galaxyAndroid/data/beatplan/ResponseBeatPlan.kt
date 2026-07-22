package com.preetTractor.galaxyAndroid.data.beatplan

data class ResponseBeatPlan(
    val `data`: List<DataBeatPlan>,
    val message: String,
    val status: String
)