package com.example.rikochat.ui.screen.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rikochat.data.remote.api.NO_USER_FOUND
import com.example.rikochat.domain.usecase.getUser.GetUserUseCase
import com.example.rikochat.domain.usecase.loginUser.LoginUserUseCase
import com.example.rikochat.utils.DataState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val loginUserUseCase: LoginUserUseCase,
    private val getUserUseCase: GetUserUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)

    val uiState = _uiState.asStateFlow()

    var email by mutableStateOf("")
        private set

    var password by mutableStateOf("")
        private set

    var showSnackBar by mutableStateOf(false)
        private set

    var snackBarText by mutableStateOf("")
        private set

    fun onEvent(event: LoginUiEvent) {
        when (event) {
            LoginUiEvent.Login -> {
                login()
            }

            LoginUiEvent.LoginScreenIsClosed -> {
                viewModelScope.launch {
                    _uiState.emit(LoginUiState.Idle)
                }
            }

            is LoginUiEvent.OnEmailTextChanged -> email = event.text

            is LoginUiEvent.OnPasswordTextChanged -> password = event.text
            LoginUiEvent.HideSnackBar -> {
                showSnackBar = false
                snackBarText = ""
                viewModelScope.launch {
                    _uiState.emit(LoginUiState.Idle)
                }
            }

            is LoginUiEvent.ShowSnackBar -> {
                showSnackBar = true
                snackBarText = event.errorText
                viewModelScope.launch {
                    _uiState.emit(LoginUiState.Idle)
                }
            }
        }
    }

    private fun login() {
        viewModelScope.launch {

            _uiState.emit(LoginUiState.Loading)

            when (val result = loginUserUseCase.invoke(email.trim(), password.trim())) {
                is DataState.Error -> {
                    _uiState.emit(LoginUiState.FailedLogin(result.message))
                }

                is DataState.Success -> {
                    _uiState.emit(LoginUiState.SuccessLogin)
                }
            }

        }
    }
}