package com.example.rikochat.domain.usecase.getChatRoomMembers

import com.example.rikochat.domain.api.message.RoomService
import com.example.rikochat.domain.model.user.User
import com.example.rikochat.utils.DataState

class GetChatRoomMembersUseCase(
    private val roomService: RoomService
) {
    suspend operator fun invoke(roomId: String): DataState<List<User>> {
        return roomService.getAllChatRoomUsers(roomId)
    }
}