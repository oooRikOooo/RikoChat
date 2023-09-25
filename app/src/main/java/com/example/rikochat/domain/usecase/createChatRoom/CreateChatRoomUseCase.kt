package com.example.rikochat.domain.usecase.createChatRoom

import com.example.rikochat.data.remote.model.rest.chatRoom.CreateChatRoomRequestBody
import com.example.rikochat.domain.api.message.RoomService
import com.example.rikochat.domain.model.chatRoom.ChatRoom
import com.example.rikochat.domain.model.chatRoom.ChatRoomType
import com.example.rikochat.utils.DataState

class CreateChatRoomUseCase(
    private val roomService: RoomService
) {
    suspend operator fun invoke(
        ownerId: String,
        type: ChatRoomType,
        chatRoomTitle: String
    ): DataState<ChatRoom> {

        val requestBody = CreateChatRoomRequestBody(
            title = chatRoomTitle,
            type = type.name,
            ownerId = ownerId
        )
        return roomService.createChatRoom(requestBody)
    }
}