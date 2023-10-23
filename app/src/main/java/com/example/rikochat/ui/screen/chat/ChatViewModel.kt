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
import com.example.rikochat.domain.usecase.getCurrentUser.GetCurrentUserUseCase
import com.example.rikochat.utils.DataState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
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
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
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

                    getChatRoomInfo(event.roomId).await()

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

            is ChatUiEvent.DeleteMessage -> {
                deleteMessage(event.messageId)
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

                when (val result = getCurrentUserUseCase.invoke()) {
                    is DataState.Error -> {
                        viewModelState.update {
                            it.copy(error = "User not resolved")
                        }
                    }

                    is DataState.Success -> {
                        val message = MessageDto(
                            id = "",
                            text = messageText.value,
                            roomId = roomId,
                            timestamp = 0,
                            username = result.data.username,
                            isRead = false,
                            usernamesWhoLiked = mutableListOf()
                        )

                        webSocketManager.sendMessage(message)
                        messageText.value = ""
                    }
                }


            }
        }
    }

    private fun updateMessageLike(messageId: String) {
        viewModelScope.launch(Dispatchers.IO) {

            when (val result = getCurrentUserUseCase.invoke()) {
                is DataState.Error -> {
                    viewModelState.update {
                        it.copy(error = "User not resolved")
                    }
                }

                is DataState.Success -> {
                    webSocketManager.sendUpdateMessageLike(
                        messageId = messageId,
                        whoLikedUsername = result.data.username
                    )
                }
            }


        }
    }

    private fun deleteMessage(messageId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            webSocketManager.sendDeleteMessage(
                messageId = messageId
            )
        }
    }

    private fun CoroutineScope.getChatRoomInfo(roomId: String) = async {
        val currentUserResult = getCurrentUserUseCase.invoke()
        val messagesResult = roomService.getAllChatRoomMessages(roomId)
        val chatRoomInfoResult = getChatRoomUseCase.invoke(roomId)
        val chatRoomMembersResult = getChatRoomMembersUseCase.invoke(roomId)

        viewModelState.update { currentState ->
            currentState.copy(
                currentUser = when (currentUserResult) {
                    is DataState.Error -> currentState.currentUser
                    is DataState.Success -> currentUserResult.data
                },
                messages = when (messagesResult) {
                    is DataState.Error -> currentState.messages
                    is DataState.Success -> messagesResult.data
                },
                chatRoom = when (chatRoomInfoResult) {
                    is DataState.Error -> currentState.chatRoom
                    is DataState.Success -> chatRoomInfoResult.data
                },
                chatRoomMembers = when (chatRoomMembersResult) {
                    is DataState.Error -> currentState.chatRoomMembers
                    is DataState.Success -> chatRoomMembersResult.data
                },
                error = when {
                    currentUserResult is DataState.Error -> currentUserResult.message
                    messagesResult is DataState.Error -> messagesResult.message
                    chatRoomInfoResult is DataState.Error -> chatRoomInfoResult.message
                    chatRoomMembersResult is DataState.Error -> chatRoomMembersResult.message
                    else -> null
                }

            )
        }

        observeMessages(roomId)
        observeChatRoomMembers()
        observeUpdateMessage(roomId)
        observeDeletedMessage()
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

    private fun observeDeletedMessage() {
        webSocketManager.deleteMessageFlow.onEach { messageId ->
            val newList = viewModelState.value.messages?.toMutableList()

            newList?.let { list ->
                val index = list.indexOfFirst { it.id == messageId }

                if (index != -1) {
                    list.removeAt(index)
                    viewModelState.update {
                        it.copy(messages = newList)
                    }
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