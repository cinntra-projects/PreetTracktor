package com.preetTractor.galaxyAndroid.repos

import com.preetTractor.galaxyAndroid.data.ResponseJsonDataItem
import com.preetTractor.galaxyAndroid.retrofit.Resource
import com.preetTractor.galaxyAndroid.retrofit.RetrofitClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class MyRepository() {


    fun fetchData(): Flow<Resource<List<ResponseJsonDataItem>>> = flow {
        emit(Resource.Loading())
        try {
            val response = RetrofitClient.apiService.getData()
            if (response.isSuccessful) {
                emit(Resource.Success(response.body() ?: emptyList()))
            } else {
                emit(Resource.Error("Failed to fetch data"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("An error occurred"))
        }
    }


    fun sec():Flow<Resource<ResponseJsonDataItem>> = flow {
        emit(Resource.Loading())
        try {
            val response = RetrofitClient.apiService.getDataResponse()
            if (response.isSuccessful) {
                emit(Resource.Success(response.body()!!))
            } else {
                emit(Resource.Error("Failed to fetch data"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("An error occurred"))
        }
    }

    fun fetchDataSecond(): Flow<Resource<List<ResponseJsonDataItem>>> = flow {
        emit(Resource.Loading())
        try {


        }catch (e:Exception){
            emit(Resource.Error("An error occurred"))
        }
    }
}
