package com.preetTractor.galaxyAndroid.ui.activity.test

data class Itemlevel(
    val name: String,
    val subItems: List<Itemlevel>? = null,
    var isExpanded: Boolean = false // Track whether this item is expanded
)
