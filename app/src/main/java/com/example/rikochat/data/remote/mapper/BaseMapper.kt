package com.example.rikochat.data.remote.mapper

interface BaseMapper<T, DTO> {
    fun mapToEntity(model: T): DTO
    fun mapFromEntity(dto: DTO): T
}