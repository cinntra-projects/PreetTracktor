package com.preetTractor.galaxyAndroid.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.preetTractor.galaxyAndroid.repos.MyRepository

class HomeViewModelProvider(
    val app: Application,
    private val repos: MyRepository,


    ): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MyViewModel(repos) as T
    }
}