package com.preetTractor.galaxyAndroid.data

data class LeadResponse(
    val `data`: List<LeadValue>,
    val message: String,
    val meta: Meta,
    val status: Int
)