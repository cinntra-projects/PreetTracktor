package com.preetTractor.galaxyAndroid.viewmodel

import androidx.lifecycle.ViewModel
import com.preetTractor.galaxyAndroid.repos.MyRepository
import com.preetTractor.galaxyAndroid.data.ResponseJsonDataItem
import com.preetTractor.galaxyAndroid.retrofit.Resource
import kotlinx.coroutines.flow.Flow

class MyViewModel(private val repository: MyRepository) : ViewModel() {


    val apiData: Flow<Resource<List<ResponseJsonDataItem>>> = repository.fetchData()

    val apiresponse:Flow<Resource<ResponseJsonDataItem>> =repository.sec()

}
