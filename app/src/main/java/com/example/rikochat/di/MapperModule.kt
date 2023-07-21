package com.example.rikochat.di

import com.example.rikochat.data.remote.mapper.MessageMapper
import org.koin.dsl.module

val mapperModule = module {
    factory { MessageMapper() }
}