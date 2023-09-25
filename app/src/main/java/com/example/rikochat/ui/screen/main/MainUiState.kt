package com.example.rikochat.ui.screen.main

sealed class MainUiState {

    object Idle : MainUiState()
    object Loading : MainUiState()

    data class SuccessTokenFetch(val token: String) : MainUiState()
}