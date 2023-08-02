package com.example.rikochat.ui.screen.registration

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rikochat.domain.usecase.createAccount.CreateAccountUseCase
import com.example.rikochat.utils.DataState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RegistrationViewModel(
    private val createAccountUseCase: CreateAccountUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<RegistrationUiState>(RegistrationUiState.Idle)

    val uiState = _uiState.asStateFlow()

    var username by mutableStateOf("")
        private set

    var email by mutableStateOf("")
        private set

    var password by mutableStateOf("")
        private set

    var showSnackBar by mutableStateOf(false)
        private set

    var snackBarText by mutableStateOf("")
        private set

    fun onEvent(event: RegistrationUiEvent) {
        when (event) {
            is RegistrationUiEvent.OnEmailTextChanged -> email = event.text
            is RegistrationUiEvent.OnPasswordTextChanged -> password = event.text
            RegistrationUiEvent.Register -> {
                register()
            }

            is RegistrationUiEvent.OnUserNameTextChanged -> username = event.text
            RegistrationUiEvent.HideSnackBar -> {
                showSnackBar = false
                snackBarText = ""
                viewModelScope.launch {
                    _uiState.emit(RegistrationUiState.Idle)
                }
            }

            is RegistrationUiEvent.ShowSnackBar -> {
                showSnackBar = true
                snackBarText = event.errorText
                viewModelScope.launch {
                    _uiState.emit(RegistrationUiState.Idle)
                }
            }
        }
    }

    private fun register() {
        viewModelScope.launch(Dispatchers.IO) {

            _uiState.emit(RegistrationUiState.Loading)

            when (val result =
                createAccountUseCase.invoke(username.trim(), email.trim(), password.trim())
            ) {
                is DataState.Error -> {
                    _uiState.emit(RegistrationUiState.FailedRegistration(result.message))
                }

                is DataState.Success -> {
                    _uiState.emit(RegistrationUiState.SuccessfulRegister)
                }
            }

        }
    }
}