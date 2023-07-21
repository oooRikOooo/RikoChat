package com.example.rikochat.ui.screen.selectUser

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SelectUserViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<SelectUserUiState>(SelectUserUiState.Idle)
    val uiState = _uiState.asStateFlow()

    val usernameText = mutableStateOf("")

    fun onUsernameChange(username: String) {
        usernameText.value = username
    }

    fun onEvent(event: SelectUserUiEvent) {
        when (event) {
            SelectUserUiEvent.OnJoinClick -> {
                viewModelScope.launch {
                    if (usernameText.value.isNotBlank())
                        _uiState.emit(SelectUserUiState.SuccessLogin(usernameText.value))
                    else
                        _uiState.emit(SelectUserUiState.FailureLogin("Field is empty"))
                }
            }

            is SelectUserUiEvent.OnUsernameChanged -> usernameText.value = event.username
        }
    }
}