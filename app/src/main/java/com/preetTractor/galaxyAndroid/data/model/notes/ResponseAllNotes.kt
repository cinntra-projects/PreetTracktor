package com.preetTractor.galaxyAndroid.data.model.notes

data class ResponseAllNotes(
    val `data`: List<DataAllNotes>,
    val message: String,
    val status: Int
)