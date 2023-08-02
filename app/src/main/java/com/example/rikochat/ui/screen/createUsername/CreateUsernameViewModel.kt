package com.example.rikochat.ui.screen.createUsername

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rikochat.data.remote.api.NO_USER_FOUND
import com.example.rikochat.domain.model.user.User
import com.example.rikochat.domain.usecase.getUser.GetUserUseCase
import com.example.rikochat.domain.usecase.saveUser.SaveUserUseCase
import com.example.rikochat.utils.DataState
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class CreateUsernameViewModel(
    private val saveUserUseCase: SaveUserUseCase,
    private val getUserUseCase: GetUserUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow<CreateUsernameUiState>(CreateUsernameUiState.Idle)

    val uiState = _uiState.asStateFlow()

    var username by mutableStateOf("")
        private set

    var showSnackBar by mutableStateOf(false)
        private set

    var snackBarText by mutableStateOf("")
        private set

    fun onEvent(event: CreateUsernameUiEvent) {
        when (event) {
            CreateUsernameUiEvent.CreateUserName -> {
                createUsername()
            }

            CreateUsernameUiEvent.CreateUsernameScreenIsClosed -> {
                viewModelScope.launch {
                    _uiState.emit(CreateUsernameUiState.Idle)
                }
            }

            is CreateUsernameUiEvent.OnUsernameTextChanged -> username = event.text

            CreateUsernameUiEvent.HideSnackBar -> {
                showSnackBar = false
                snackBarText = ""
                viewModelScope.launch {
                    _uiState.emit(CreateUsernameUiState.Idle)
                }
            }

            is CreateUsernameUiEvent.ShowSnackBar -> {
                showSnackBar = true
                snackBarText = event.errorText
                viewModelScope.launch {
                    _uiState.emit(CreateUsernameUiState.Idle)
                }
            }
        }
    }

    private fun createUsername() {
        viewModelScope.launch(Dispatchers.IO) {
            val user = Firebase.auth.currentUser!!

            val profileUpdates = userProfileChangeRequest {
                displayName = username
            }

            user.updateProfile(profileUpdates).await()

            when (val result = getUserUseCase.invoke(username.trim())) {
                is DataState.Error -> {
                    if (result.message == NO_USER_FOUND) {
                        when (val saveUserResult =
                            saveUserUseCase.invoke(User(username.trim(), user.email!!.trim()))) {
                            is DataState.Error -> {
                                _uiState.emit(CreateUsernameUiState.FailedCreate(saveUserResult.message))
                                return@launch
                            }

                            is DataState.Success -> {
                                _uiState.emit(CreateUsernameUiState.SuccessfulCreate)
                                return@launch
                            }
                        }
                    } else {
                        _uiState.emit(CreateUsernameUiState.FailedCreate(result.message))
                        return@launch
                    }
                }

                is DataState.Success -> {
                    _uiState.emit(CreateUsernameUiState.SuccessfulCreate)
                }
            }

        }
    }
}