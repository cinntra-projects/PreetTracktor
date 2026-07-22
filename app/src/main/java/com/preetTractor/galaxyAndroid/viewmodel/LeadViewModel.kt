package com.preetTractor.galaxyAndroid.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.preetTractor.galaxyAndroid.apiHelper.UiState
import com.preetTractor.galaxyAndroid.data.FilterOverAll
import com.preetTractor.galaxyAndroid.data.FollowUpData
import com.preetTractor.galaxyAndroid.mvvmSetUp.DefaultMainRepositories
import com.preetTractor.galaxyAndroid.mvvmSetUp.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LeadViewModel(
    private val repository: DefaultMainRepositories
) : ViewModel() {

    private val _leadState =
        MutableStateFlow<UiState>(UiState.Idle)

    val leadState: StateFlow<UiState> =
        _leadState.asStateFlow()



    fun getAllLeads(
        filter: FilterOverAll,
        context: Context
    ) {

        viewModelScope.launch {

            _leadState.value = UiState.Loading

            when (
                val result = repository.getAllLead(
                    filter,
                    context
                )
            ) {

                is Resource.Success -> {

                    val list =
                        result.data ?: emptyList()

                    if (list.isEmpty()) {

                        _leadState.value =
                            UiState.Empty(
                                "No leads found"
                            )

                    } else {

                        _leadState.value =
                            UiState.Success(list)
                    }
                }

                is Resource.Error -> {

                    _leadState.value =
                        UiState.Error(
                            result.message ?: "Unknown Error"
                        )
                }

                is Resource.Loading -> {
                    _leadState.value =
                        UiState.Loading
                }
            }
        }
    }

    fun callFollowUpApi(
        context: Context,
        filter: FollowUpData,
    ) {
        viewModelScope.launch {
            val response = repository.callFollowUpApirepos(context,filter)
            if(response.status == 200){
                Toast.makeText(context,"Follow Up Created Successfully",Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(context,response.message.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }

}