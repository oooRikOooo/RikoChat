package com.example.rikochat.domain.usecase.addUserToGroupChat

import com.example.rikochat.domain.api.message.RoomService
import com.example.rikochat.utils.DataState

class AddUserToGroupChatUseCase(
    private val roomService: RoomService
) {
    suspend operator fun invoke(roomId: String, username: String): DataState<Unit> {
        return roomService.addUserToChatRoom(username, roomId)
    }
}