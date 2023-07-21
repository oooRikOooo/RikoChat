package com.example.rikochat.ui.screen.chat

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rikochat.domain.api.chatSocket.ChatSocketService
import com.example.rikochat.domain.api.message.MessageService
import com.example.rikochat.utils.DataState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ChatViewModel(
    private val messageService: MessageService,
    private val chatSocketService: ChatSocketService
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

    fun onEvent(event: ChatUiEvent){
        when(event){
            is ChatUiEvent.OnMessageChanged -> {
                messageText.value = event.message
            }
            ChatUiEvent.SendMessage -> {
                sendMessage()
            }
        }
    }

    fun connectToChat(username: String) {

        getAllMessages()

        viewModelScope.launch(Dispatchers.IO) {

            when (val result = chatSocketService.initSession(username)) {
                is DataState.Error -> {
                    viewModelState.update {
                        it.copy(error = result.message)
                    }
                }

                is DataState.Success -> {
                    chatSocketService.observeMessages()
                        .onEach { message ->
                            val newList = viewModelState.value.messages?.toMutableList()?.apply {
                                add(0, message)
                            }
                            viewModelState.update {
                                it.copy(messages = newList, error = null)
                            }
                        }.launchIn(viewModelScope)
                }
            }

        }
    }

    fun disconnect() {
        viewModelScope.launch(Dispatchers.IO) {
            chatSocketService.closeSession()
        }
    }

    fun sendMessage() {
        viewModelScope.launch(Dispatchers.IO) {
            if (messageText.value.isNotBlank()) {
                chatSocketService.sendMessage(messageText.value)
                messageText.value = ""
            }
        }
    }

    fun getAllMessages() {

        viewModelState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch(Dispatchers.IO) {

            when (val result = messageService.getAllMessages()) {
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
            viewModelState.update {
                it.copy(isLoading = false)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        disconnect()
    }
}