package com.preetTractor.galaxyAndroid.data

data class BdrcModel(
    val `data`: List<BdrcData>,
    val message: String,
    val meta: MetaX,
    val status: Int
)