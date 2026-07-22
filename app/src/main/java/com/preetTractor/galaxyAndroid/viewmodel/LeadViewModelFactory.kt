package com.preetTractor.galaxyAndroid.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.preetTractor.galaxyAndroid.mvvmSetUp.DefaultMainRepositories

class LeadViewModelFactory(
    private val repository: DefaultMainRepositories
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        if (modelClass.isAssignableFrom(LeadViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LeadViewModel(repository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel")
    }
}