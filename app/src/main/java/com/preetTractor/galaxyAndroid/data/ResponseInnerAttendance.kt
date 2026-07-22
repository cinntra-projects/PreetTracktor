package com.preetTractor.galaxyAndroid.data

data class ResponseInnerAttendance(
    val `data`: List<DataInnerAttendance>,
    val message: String,
    val status: String
)