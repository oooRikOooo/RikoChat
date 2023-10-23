package com.example.rikochat.ui.screen.userDetails

sealed class UserDetailsUiState {

    object Idle : UserDetailsUiState()

    object Loading : UserDetailsUiState()

    data class Error(val message: String) : UserDetailsUiState()

    data class SuccessLoad(val data: String) : UserDetailsUiState()

}