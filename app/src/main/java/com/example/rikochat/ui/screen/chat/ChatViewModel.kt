package com.example.rikochat.ui.screen.chat

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rikochat.data.remote.api.chatSocket.WebSocketManager
import com.example.rikochat.data.remote.model.rest.message.MessageDto
import com.example.rikochat.domain.api.message.RoomService
import com.example.rikochat.domain.model.message.Message
import com.example.rikochat.domain.usecase.addUserToGroupChat.AddUserToGroupChatUseCase
import com.example.rikochat.domain.usecase.getChatRoom.GetChatRoomUseCase
import com.example.rikochat.domain.usecase.getChatRoomMembers.GetChatRoomMembersUseCase
import com.example.rikochat.utils.DataState
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ChatViewModel(
    private val roomService: RoomService,
    private val webSocketManager: WebSocketManager,
    private val getChatRoomUseCase: GetChatRoomUseCase,
    private val getChatRoomMembersUseCase: GetChatRoomMembersUseCase,
    private val addUserToGroupChatUseCase: AddUserToGroupChatUseCase
) : ViewModel() {

    private val viewModelState = MutableStateFlow(ChatViewModelState())

    val uiState = viewModelState
        .map(ChatViewModelState::toUiState)
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value.toUiState()
        )

    val messageText = mutableStateOf("")

    var dialogAddUserToGroupChatRoomUsername by mutableStateOf("")
        private set

    var dialogAddUserToGroupChatRoomUsernameError by mutableStateOf("")
        private set

    var isDialogAddUserToGroupChatRoomUsernameError by mutableStateOf(false)

    var clickedMessage by mutableStateOf<Message?>(null)

    fun onEvent(event: ChatUiEvent) {
        when (event) {
            is ChatUiEvent.OnMessageChanged -> {
                messageText.value = event.message
            }

            is ChatUiEvent.SendMessage -> {
                sendMessage(event.roomId)
            }

            is ChatUiEvent.LoadRoomInfo -> {
                viewModelScope.launch(Dispatchers.IO) {

                    viewModelState.update {
                        it.copy(isLoading = true)
                    }

                    awaitAll(
                        getChatRoomInfoAsync(event.roomId),
                        getChatRoomMembersAsync(event.roomId),
                        getAllRoomMessagesAsync(event.roomId)
                    )

                    viewModelState.update {
                        it.copy(isLoading = false)
                    }
                }

            }

            is ChatUiEvent.AddUserToGroupChat -> {
                addUserToGroupChat(event.roomId)
            }

            is ChatUiEvent.OnAddUserToGroupChatRoomTextChanged -> {
                isDialogAddUserToGroupChatRoomUsernameError = false
                dialogAddUserToGroupChatRoomUsernameError = ""
                dialogAddUserToGroupChatRoomUsername = event.text
            }

            is ChatUiEvent.LikeMessage -> {
                updateMessageLike(event.messageId)
            }

            ChatUiEvent.HideMessageActionsDialog -> {
                clickedMessage = null
            }

            is ChatUiEvent.ShowMessageActionsDialog -> {
                clickedMessage = event.message
            }
        }
    }

    private fun addUserToGroupChat(roomId: String) {
        viewModelScope.launch(Dispatchers.IO) {

            viewModelState.update {
                it.copy(isLoading = true)
            }

            when (
                val result =
                    addUserToGroupChatUseCase.invoke(roomId, dialogAddUserToGroupChatRoomUsername)
            ) {
                is DataState.Error -> {
                    isDialogAddUserToGroupChatRoomUsernameError = true
                    dialogAddUserToGroupChatRoomUsernameError = result.message
                }

                is DataState.Success -> {
                    dialogAddUserToGroupChatRoomUsername = ""
                }
            }

            viewModelState.update {
                it.copy(isLoading = false)
            }
        }
    }


    private fun sendMessage(roomId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            if (messageText.value.isNotBlank()) {
                val username = FirebaseAuth.getInstance().currentUser?.displayName!!

                val message = MessageDto(
                    id = "",
                    text = messageText.value,
                    roomId = roomId,
                    timestamp = 0,
                    username = username,
                    isRead = false,
                    usernamesWhoLiked = mutableListOf()
                )

                webSocketManager.sendMessage(message)
                messageText.value = ""
            }
        }
    }

    private fun updateMessageLike(messageId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val username = FirebaseAuth.getInstance().currentUser?.displayName!!

            webSocketManager.sendUpdateMessageLike(
                messageId = messageId,
                whoLikedUsername = username
            )
        }
    }

    private fun CoroutineScope.getAllRoomMessagesAsync(roomId: String) = async {

        when (val result = roomService.getAllChatRoomMessages(roomId)) {
            is DataState.Error -> {
                viewModelState.update {
                    it.copy(error = result.message)
                }
            }

            is DataState.Success -> {
                if (result.data.isEmpty()) {
                    viewModelState.update {
                        it.copy(messages = emptyList())
                    }
                } else {
                    viewModelState.update {
                        it.copy(messages = result.data)
                    }
                }
            }
        }
        Log.d("riko", "getAllRoomMessagesAsync")
        observeMessages(roomId)
        observeChatRoomMembers()
        observeUpdateMessage(roomId)
//        observeRoomDetailsChanges(roomId)
    }

    private fun CoroutineScope.getChatRoomInfoAsync(roomId: String) = async {
        when (val result = getChatRoomUseCase.invoke(roomId)) {
            is DataState.Error -> {
                viewModelState.update {
                    it.copy(error = result.message)
                }
            }

            is DataState.Success -> {
                viewModelState.update {
                    it.copy(chatRoom = result.data)
                }
            }
        }
    }

    private fun CoroutineScope.getChatRoomMembersAsync(roomId: String) = async {
        when (val result = getChatRoomMembersUseCase.invoke(roomId)) {
            is DataState.Error -> {
                viewModelState.update {
                    it.copy(error = result.message)
                }
            }

            is DataState.Success -> {
                viewModelState.update {
                    it.copy(chatRoomMembers = result.data)
                }
            }
        }
    }

    private fun observeMessages(roomId: String) {
        webSocketManager.newMessageFlow.onEach { message ->
            Log.d("riko", "observeMessages")
            if (message.roomId == roomId) {
                val newList = viewModelState.value.messages?.toMutableList()?.apply {
                    add(0, message)
                }
                viewModelState.update {
                    it.copy(messages = newList, error = null)
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun observeUpdateMessage(roomId: String) {
        webSocketManager.updateMessageFlow.onEach { message ->
            if (message.roomId == roomId) {

                Log.d("riko", "oldList: ${viewModelState.value.messages}")

                val newList = viewModelState.value.messages?.toMutableList()

                newList?.let { list ->
                    val index = list.indexOfFirst { it.id == message.id }
                    if (index != -1) {
                        list[index] = message
                    }
                }

                Log.d("riko", "newList: $newList")

                viewModelState.update {
                    it.copy(messages = newList, error = null)
                }

            }
        }.launchIn(viewModelScope)
    }

    private fun observeChatRoomMembers() {
        webSocketManager.updateChatRoomMembersFlow
            .onEach { users ->
                viewModelState.update {
                    it.copy(chatRoomMembers = users)
                }
            }
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("riko", "OnCleared Chat")
    }

}