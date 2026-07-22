package com.preetTractor.galaxyAndroid.mvvmSetUp

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.preetTractor.galaxyAndroid.apiHelper.ApisInterface

import kotlinx.coroutines.CoroutineDispatcher

class MainViewModelProvider(
    val app: Application,
    private val repos: MainRepos,
    private val dispatchers: CoroutineDispatcher,
    private val fanxApi: ApisInterface
    ) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        return MainViewModel(app, repos, dispatchers, fanxApi) as T

    }
}