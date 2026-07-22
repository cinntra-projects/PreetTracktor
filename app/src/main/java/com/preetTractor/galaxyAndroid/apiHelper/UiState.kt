package com.preetTractor.galaxyAndroid.apiHelper

import com.preetTractor.galaxyAndroid.data.LeadValue

sealed class UiState {

    object Idle : UiState()

    object Loading : UiState()

    data class Success(
        val leads: List<LeadValue>
    ) : UiState()

    data class Empty(
        val message: String
    ) : UiState()

    data class Error(
        val message: String
    ) : UiState()
}