package com.preetTractor.galaxyAndroid.data.beatplan

data class ResponseCityAll(
    val `data`: List<DataCityAll>,
    val message: String,
    val meta: Meta,
    val status: Int
)