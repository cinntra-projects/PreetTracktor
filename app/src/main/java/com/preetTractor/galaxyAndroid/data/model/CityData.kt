package com.preetTractor.galaxyAndroid.data.model

data class CityData(
    val CityName: String,
    val CountryCode: String,
    val StateCode: String,
    val StateName: String,
    val cluster: Any,
    val countryName: String,
    val id: Int
){
    override fun toString(): String {
        return CityName.toString()
    }
}

