package com.preetTractor.galaxyAndroid.data.model.notes

data class ResponseBpOverview(
    val `data`: List<DataBpOverview>,
    val message: String,
    val status: Int
)