package com.preetTractor.galaxyAndroid.data

data class ModeOfTravelResponse(
    val `data`: List<ModeOfTravelData>,
    val message: String,
    val status: Int
)