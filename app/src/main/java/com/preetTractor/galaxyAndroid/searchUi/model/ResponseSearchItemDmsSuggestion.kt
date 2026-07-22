package com.preetTractor.galaxyAndroid.searchUi.model

data class ResponseSearchItemDmsSuggestion(
    val `data`: List<DataSearchItemDmsSuggestion>,
    val message: String,
    val status: Int
)