package com.example.rikochat.ui.screen.userDetails

import com.example.rikochat.domain.model.user.User

sealed class UserDetailsUiState {

    object Idle : UserDetailsUiState()

    object Loading : UserDetailsUiState()

    data class Error(val message: String) : UserDetailsUiState()

    data class SuccessLoad(val data: User) : UserDetailsUiState()

}