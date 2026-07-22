package com.preetTractor.galaxyAndroid.data.model

data class CityResponse(
    val `data`: ArrayList<CityData>,
    val message: String,
    val status: Int
)