package com.preetTractor.galaxyAndroid.mvvmSetUp




inline fun <T> safeCall(action: () -> Resource<T>): Resource<T> {
    return try {
        action()
    } catch (e: Exception) {
        Resource.Error(e.message ?: "An unknown error occured")
    }
}