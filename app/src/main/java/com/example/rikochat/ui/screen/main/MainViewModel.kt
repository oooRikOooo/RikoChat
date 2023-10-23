package com.example.rikochat.ui.screen.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rikochat.data.remote.api.chatSocket.WebSocketManager
import com.example.rikochat.domain.repository.TokenRepository
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class MainViewModel(
    private val tokenRepository: TokenRepository,
    private val webSocketManager: WebSocketManager
) : ViewModel() {

    private val _state = MutableStateFlow<MainUiState>(MainUiState.Idle)
    val state = _state.asStateFlow()


    init {
        observeToken()
    }

//    fun openConnection() {
//        viewModelScope.launch(Dispatchers.IO) {
//            if (!webSocketManager.isWebSocketConnected())
//                webSocketManager.initSession()
//        }
//
//    }
//
//    fun closeConnection() {
//        viewModelScope.launch(Dispatchers.IO) {
//            if (webSocketManager.isWebSocketConnected())
//                webSocketManager.closeSession()
//        }
//    }

    private fun observeToken() {
        viewModelScope.launch(Dispatchers.Main) {

            _state.emit(MainUiState.Loading)

            updateToken().await()

            tokenRepository.observeAuthToken().collect {
                _state.emit(MainUiState.SuccessTokenFetch(it))

//                if (it.isEmpty()) {
//                    closeConnection()
//                } else {
//                    openConnection()
//                }
            }
        }
    }

    private fun CoroutineScope.updateToken() = async {
        val currentUser = Firebase.auth.currentUser

        if (currentUser != null) {
            val refreshTokenResult = currentUser.getIdToken(true).await()

            refreshTokenResult.token?.let {
                tokenRepository.updateAuthToken(it)
            }
        }

    }
}