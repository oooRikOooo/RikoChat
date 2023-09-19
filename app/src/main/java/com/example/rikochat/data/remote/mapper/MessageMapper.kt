package com.example.rikochat.data.remote.mapper

import com.example.rikochat.data.remote.model.rest.message.MessageDto
import com.example.rikochat.domain.model.message.Message
import com.example.rikochat.utils.toHoursMinutes
import com.example.rikochat.utils.toStringDate

class MessageMapper : BaseMapper<Message, MessageDto> {

    override fun mapFromEntity(dto: MessageDto): Message {
        return Message(
            id = dto.id,
            text = dto.text,
            formattedDate = dto.timestamp.toStringDate(),
            formattedTime = dto.timestamp.toHoursMinutes(),
            timestamp = dto.timestamp,
            username = dto.username,
            roomId = dto.roomId,
            usernamesWhoLiked = dto.usernamesWhoLiked,
            isRead = dto.isRead
        )
    }

    override fun mapToEntity(model: Message): MessageDto {
        return MessageDto(
            id = model.id,
            text = model.text,
            username = model.username,
            roomId = model.roomId,
            usernamesWhoLiked = model.usernamesWhoLiked,
            isRead = model.isRead,
            timestamp = model.timestamp
        )
    }

    fun mapFromEntityList(list: List<MessageDto>): List<Message> {
        return list.map {
            mapFromEntity(it)
        }
    }
}