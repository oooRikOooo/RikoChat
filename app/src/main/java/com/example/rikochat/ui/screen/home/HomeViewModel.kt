package com.example.rikochat.ui.screen.home

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rikochat.data.remote.api.chatSocket.WebSocketManager
import com.example.rikochat.domain.model.chatRoom.ChatRoomType
import com.example.rikochat.domain.usecase.createChatRoom.CreateChatRoomUseCase
import com.example.rikochat.domain.usecase.getCurrentUser.GetCurrentUserUseCase
import com.example.rikochat.domain.usecase.getUserMessages.GetUserChatRoomsUseCase
import com.example.rikochat.utils.DataState
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val getUserChatRoomsUseCase: GetUserChatRoomsUseCase,
    private val createChatRoomUseCase: CreateChatRoomUseCase,
    private val webSocketManager: WebSocketManager
) : ViewModel() {
    private val viewModelState = MutableStateFlow(HomeViewModelState())

    val uiState = viewModelState
        .map(HomeViewModelState::toUiState)
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value.toUiState()
        )

    var dialogChatRoomTitle by mutableStateOf("")
        private set

    var dialogChatRoomTitleError by mutableStateOf("")
        private set

    var isDialogChatRoomTitleError by mutableStateOf(false)

    fun onEvent(event: HomeUiEvent) {
        when (event) {

            HomeUiEvent.LoadInitialData -> {
                viewModelScope.launch(Dispatchers.IO) {
                    viewModelState.update {
                        it.copy(isLoading = true)
                    }

                    loadInitialDataAsync().await()

                    viewModelState.update {
                        it.copy(isLoading = false)
                    }
                }
            }

            HomeUiEvent.SignOut -> signOut()

            HomeUiEvent.CreateChatRoom -> {
                createChatRoom()
                dialogChatRoomTitle = ""
            }

            is HomeUiEvent.OnDialogTitleTextChanged -> {
                dialogChatRoomTitle = event.text
                if (event.text.isNotBlank()) {
                    isDialogChatRoomTitleError = false
                }
            }

            is HomeUiEvent.ShowDialogTitleError -> {
                dialogChatRoomTitleError = event.text
                isDialogChatRoomTitleError = true
            }
        }
    }

    fun connectToWebSocket() {

        Log.d("riko", "connectToWebSocket")
        if (webSocketManager.isWebSocketConnected()) {
            Log.d("riko", "webSocket connected")
            return
        }

        viewModelScope.launch(Dispatchers.IO) {

            when (val result = webSocketManager.initSession()) {
                is DataState.Error -> {
                    viewModelState.update {
                        it.copy(error = result.message)
                    }
                }

                is DataState.Success -> {
                    Log.d("riko", "connectToWebSocket Success")
                    webSocketManager.observeIncoming()
                }
            }

        }
    }


    fun disconnect() {
        viewModelScope.launch(Dispatchers.IO) {
            Log.d("riko", "disconnect")
            webSocketManager.closeSession()
        }
    }

    private fun createChatRoom() {
        viewModelScope.launch {

            val currentUser = viewModelState.value.currentUser!!

            when (
                val result = createChatRoomUseCase.invoke(
                    ownerId = currentUser.uid,
                    type = ChatRoomType.Group,
                    chatRoomTitle = dialogChatRoomTitle
                )
            ) {
                is DataState.Error -> {
                    viewModelState.update {
                        it.copy(error = result.message)
                    }
                }

                is DataState.Success -> {
                    viewModelState.update {
                        val newList = it.rooms.toMutableList().apply {
                            add(result.data)
                        }

                        it.copy(rooms = newList)
                    }
                }
            }
        }
    }

    private fun CoroutineScope.loadInitialDataAsync() = async {

        val currentUserResult = getCurrentUserUseCase.invoke()
        val userChatRoomsResult = getUserChatRoomsUseCase.invoke()

        viewModelState.update { currentState ->
            currentState.copy(
                currentUser = when (currentUserResult) {
                    is DataState.Error -> currentState.currentUser
                    is DataState.Success -> currentUserResult.data
                },
                rooms = when (userChatRoomsResult) {
                    is DataState.Error -> currentState.rooms
                    is DataState.Success -> userChatRoomsResult.data
                },
                error = when {
                    currentUserResult is DataState.Error -> currentUserResult.message
                    userChatRoomsResult is DataState.Error -> userChatRoomsResult.message
                    else -> null
                }
            )
        }
    }

    private fun signOut() {
        Firebase.auth.signOut()
    }

    override fun onCleared() {
        Log.d("riko", "onCleared")
        super.onCleared()
        disconnect()
    }

}