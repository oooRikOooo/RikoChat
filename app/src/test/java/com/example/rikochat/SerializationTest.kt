package com.example.rikochat

import com.example.rikochat.data.remote.model.rest.user.UserDto
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Test

class SerializationTest {

    @Test
    fun testJsonToKotlin() {
        val test = UserDto("riko", "riko@gmail.com")

        val json = Json.encodeToString(test)

        println(json)
        val t2 = Json.decodeFromString<UserDto>(json)
        println(t2)
    }

}