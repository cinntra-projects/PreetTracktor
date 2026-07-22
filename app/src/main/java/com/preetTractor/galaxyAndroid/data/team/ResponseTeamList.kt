package com.preetTractor.galaxyAndroid.data.team

data class ResponseTeamList(
    val `data`: List<DataTeamList>,
    val message: String,
    val status: Int
)