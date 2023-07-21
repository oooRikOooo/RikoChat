package com.example.rikochat.data.remote.mapper

import com.example.rikochat.data.remote.model.message.MessageDto
import com.example.rikochat.domain.model.message.Message
import com.example.rikochat.utils.toHoursMinutes
import com.example.rikochat.utils.toStringDate

class MessageMapper {

    fun mapFromEntity(loginDto: MessageDto): Message{
        return Message(
            text = loginDto.text,
            formattedDate = loginDto.timestamp.toStringDate(),
            formattedTime = loginDto.timestamp.toHoursMinutes(),
            username = loginDto.username
        )
    }

    fun mapFromEntityList(list: List<MessageDto>): List<Message>{
        return list.map {
            mapFromEntity(it)
        }
    }
}