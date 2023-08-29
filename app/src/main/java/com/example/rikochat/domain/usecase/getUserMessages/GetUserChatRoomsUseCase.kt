package com.example.rikochat.domain.usecase.getUserMessages

import com.example.rikochat.domain.api.user.UserService
import com.example.rikochat.domain.model.chatRoom.ChatRoom
import com.example.rikochat.utils.DataState

class GetUserChatRoomsUseCase(
    private val userService: UserService
) {

    suspend operator fun invoke(username: String): DataState<List<ChatRoom>> {
        return userService.getUserChatRooms(username)
    }

}