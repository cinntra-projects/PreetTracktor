package com.preetTractor.galaxyAndroid.data

data class ResponseOuterAttendanceListing(
    val `data`: List<DataOuterAttendanceListing>,
    val message: String,
    val status: String
)