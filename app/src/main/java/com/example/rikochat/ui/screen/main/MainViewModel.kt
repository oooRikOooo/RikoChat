package com.example.rikochat.ui.screen.main

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rikochat.data.remote.api.NO_USER_FOUND
import com.example.rikochat.domain.api.chatSocket.ChatSocketService
import com.example.rikochat.domain.model.chatRoom.ChatRoomType
import com.example.rikochat.domain.usecase.createChatRoom.CreateChatRoomUseCase
import com.example.rikochat.domain.usecase.getUser.GetUserUseCase
import com.example.rikochat.domain.usecase.getUserMessages.GetUserChatRoomsUseCase
import com.example.rikochat.utils.DataState
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainViewModel(
    private val getUserUseCase: GetUserUseCase,
    private val getUserChatRoomsUseCase: GetUserChatRoomsUseCase,
    private val createChatRoomUseCase: CreateChatRoomUseCase,
    private val chatSocketService: ChatSocketService
) : ViewModel() {
    private val viewModelState = MutableStateFlow(MainViewModelState())

    private val _user = MutableStateFlow<MainUserUiState>(MainUserUiState.Idle)
    val user: StateFlow<MainUserUiState> = _user.asStateFlow()

    val uiState = viewModelState
        .map(MainViewModelState::toUiState)
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

    fun onEvent(event: MainUiEvent) {
        when (event) {
            MainUiEvent.LoadUserChatRooms -> loadUserChatRooms()

            MainUiEvent.LoadUser -> loadUser()

            MainUiEvent.SignOut -> signOut()

            MainUiEvent.CreateChatRoom -> {
                createChatRoom()
                dialogChatRoomTitle = ""
            }

            is MainUiEvent.OnDialogTitleTextChanged -> {
                dialogChatRoomTitle = event.text
                if (event.text.isNotBlank()) {
                    isDialogChatRoomTitleError = false
                }
            }

            is MainUiEvent.ShowDialogTitleError -> {
                dialogChatRoomTitleError = event.text
                isDialogChatRoomTitleError = true
            }
        }
    }

    fun connectToWebSocket() {

        if (chatSocketService.isWebSocketConnected()) return

        viewModelScope.launch(Dispatchers.IO) {

            val username = Firebase.auth.currentUser!!.displayName!!

            when (val result = chatSocketService.initSession(username)) {
                is DataState.Error -> {
                    viewModelState.update {
                        it.copy(error = result.message)
                    }
                }

                is DataState.Success -> {
//                    chatSocketService.observeMessages(viewModelScope)
//                        .collect { message ->
//                            val list = viewModelState.value.rooms
//                            Log.d("riko", "adwad")
//                            list.find {
//                                it.id == message.roomId
//                            }?.lastMessage = message.text
//
//                            viewModelState.update {
//                                it.copy(rooms = list, error = null)
//                            }
//                        }
                }
            }

        }
    }


    fun disconnect() {
        viewModelScope.launch(Dispatchers.IO) {
            chatSocketService.closeSession()
        }
    }

//    private fun observeChatRooms() {
//        chatSocketService.observeRooms()
//            .onEach { chatRooms ->
//                viewModelState.update {
//                    it.copy(rooms = chatRooms, error = null)
//                }
//            }.launchIn(viewModelScope)
//    }

    private fun createChatRoom() {
        viewModelScope.launch {
            val username = Firebase.auth.currentUser!!.displayName!!

            when (
                val result = createChatRoomUseCase.invoke(
                    username = username,
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

    private fun loadUserChatRooms() {
        viewModelScope.launch {

            viewModelState.update {
                it.copy(isLoading = true)
            }

            val username = Firebase.auth.currentUser!!.displayName!!

            when (val result = getUserChatRoomsUseCase.invoke(username)) {
                is DataState.Error -> {
                    viewModelState.update {
                        it.copy(isLoading = false, error = result.message)
                    }
                }

                is DataState.Success -> {
                    viewModelState.update {
                        it.copy(isLoading = false, rooms = result.data, error = null)
                    }
                }
            }

//            observeChatRooms()
        }
    }

    private fun loadUser() {
        viewModelScope.launch {
            _user.emit(MainUserUiState.Loading)

            val username = Firebase.auth.currentUser?.displayName

            if (username == null) {
                _user.emit(MainUserUiState.UserNotFound)
                return@launch
            }

            when (val result = getUserUseCase.invoke(username)) {
                is DataState.Error -> {
                    if (result.message == NO_USER_FOUND) {
                        _user.emit(MainUserUiState.UserNotFound)
                    } else {
                        _user.emit(MainUserUiState.Error(result.message))
                    }
                }

                is DataState.Success -> {
                    _user.emit(MainUserUiState.SuccessfulLoad(result.data))
                }
            }
        }


    }

    private fun signOut() {
        Firebase.auth.signOut()
    }

    override fun onCleared() {
        super.onCleared()
        disconnect()
    }

}