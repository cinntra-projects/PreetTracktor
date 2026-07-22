package com.preetTractor.galaxyAndroid.activity.signInScreen.model

import com.preetTractor.galaxyAndroid.data.model.DataTermsSignIn

data class ResponseTermsSignIn(
    val `data`: List<DataTermsSignIn>,
    val message: String,
    val status: Int
)