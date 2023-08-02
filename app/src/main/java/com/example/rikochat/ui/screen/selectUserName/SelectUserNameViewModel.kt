package com.example.rikochat.ui.screen.selectUserName

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SelectUserNameViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<SelectUserNameUiState>(SelectUserNameUiState.Idle)
    val uiState = _uiState.asStateFlow()

    val usernameText = mutableStateOf("")

    fun onUsernameChanged(username: String) {
        usernameText.value = username
    }

    fun onEvent(event: SelectUserNameUiEvent) {
        when (event) {
            SelectUserNameUiEvent.OnJoinClick -> {
                viewModelScope.launch {
                    if (usernameText.value.isNotBlank())
                        _uiState.emit(SelectUserNameUiState.SuccessLogin(usernameText.value))
                    else
                        _uiState.emit(SelectUserNameUiState.FailureLogin("Field is empty"))
                }
            }

            is SelectUserNameUiEvent.OnUsernameChanged -> onUsernameChanged(event.username)
            SelectUserNameUiEvent.ScreenClosed -> {
                viewModelScope.launch {
                    _uiState.emit(SelectUserNameUiState.Idle)
                }
            }
        }
    }
}