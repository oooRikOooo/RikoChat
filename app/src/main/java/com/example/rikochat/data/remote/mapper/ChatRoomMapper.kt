package com.example.rikochat.data.remote.mapper

import com.example.rikochat.data.remote.model.rest.chatRoom.ChatRoomDto
import com.example.rikochat.domain.model.chatRoom.ChatRoom
import com.example.rikochat.domain.model.chatRoom.ChatRoomType

class ChatRoomMapper : BaseMapper<ChatRoom, ChatRoomDto> {
    override fun mapToEntity(model: ChatRoom): ChatRoomDto {
        return ChatRoomDto(
            id = model.id,
            title = model.title,
            picture = model.picture,
            type = model.type.name,
            lastMessageTimeStamp = model.lastMessageTimeStampFormatted.toLong(),
            lastMessage = model.lastMessage,
            ownerId = model.ownerId
        )
    }

    override fun mapFromEntity(dto: ChatRoomDto): ChatRoom {
        return ChatRoom(
            id = dto.id,
            title = dto.title,
            picture = dto.picture,
            type = ChatRoomType.getChatRoomTypeByName(dto.type),
            lastMessageTimeStampFormatted = dto.lastMessageTimeStamp.toString(),
            lastMessage = dto.lastMessage,
            ownerId = dto.ownerId
        )
    }

    fun mapToEntityList(chatRooms: List<ChatRoom>): List<ChatRoomDto> {
        return chatRooms.map { mapToEntity(it) }
    }

    fun mapFromEntityList(chatRoomsDto: List<ChatRoomDto>): List<ChatRoom> {
        return chatRoomsDto.map { mapFromEntity(it) }
    }
}