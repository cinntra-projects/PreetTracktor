package com.preetTractor.galaxyAndroid.data

data class FollowUpResponse(
    val `data`: List<FollowUpData>,
    val message: String,
    val meta: Meta,
    val status: Int
)