package com.example.rikochat.domain.usecase.getChatRoom

import com.example.rikochat.domain.api.message.RoomService
import com.example.rikochat.domain.model.chatRoom.ChatRoom
import com.example.rikochat.utils.DataState

class GetChatRoomUseCase(
    private val roomService: RoomService
) {
    suspend operator fun invoke(roomId: String): DataState<ChatRoom> {
        return roomService.getChatRoom(roomId)
    }
}