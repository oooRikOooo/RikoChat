package com.example.rikochat.data.remote.api.user

import android.util.Log
import com.example.rikochat.data.remote.api.NO_USER_FOUND
import com.example.rikochat.data.remote.mapper.ChatRoomMapper
import com.example.rikochat.data.remote.mapper.UserMapper
import com.example.rikochat.data.remote.model.rest.chatRoom.ChatRoomDto
import com.example.rikochat.data.remote.model.rest.user.UserDto
import com.example.rikochat.domain.api.user.UserService
import com.example.rikochat.domain.model.chatRoom.ChatRoom
import com.example.rikochat.domain.model.user.User
import com.example.rikochat.utils.DataState
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType

class UserServiceImpl(
    private val client: HttpClient,
    private val userMapper: UserMapper,
    private val chatRoomMapper: ChatRoomMapper
) : UserService {
    override suspend fun getUser(username: String): DataState<User> {

        val response = client.get(UserService.Endpoints.User.url) {
            url {
                parameters.append("username", username)
            }
        }
        return try {
            when (response.status) {
                HttpStatusCode.OK -> {
                    val userDto = response.body<UserDto>()

                    val user = userMapper.mapFromEntity(userDto)

                    DataState.Success(user)
                }

                HttpStatusCode.NoContent -> {
                    DataState.Error(NO_USER_FOUND)
                }

                else -> {
                    DataState.Error(response.body())
                }
            }


        } catch (e: Exception) {
            e.printStackTrace()
            DataState.Error(e.localizedMessage ?: "InternalError")
        }

    }

    override suspend fun getUserChatRooms(): DataState<List<ChatRoom>> {

        return try {
            Log.d("riko", "getUserChatRooms")
            val response = client.get(UserService.Endpoints.UserRooms.url){
                contentType(ContentType.Application.Json)
            }

            Log.d("riko", "getUserChatRooms return")

            when (response.status) {
                HttpStatusCode.OK -> {
                    val roomsDto = response.body<List<ChatRoomDto>>()

                    val rooms = chatRoomMapper.mapFromEntityList(roomsDto)

                    Log.d("riko", "getUserChatRooms OK")
                    DataState.Success(rooms)
                }

                HttpStatusCode.Unauthorized -> {
                    Log.d("riko", "getUserChatRooms Unauthorized")
                    DataState.Error(NO_USER_FOUND)
                }

                else -> {
                    val error: String = response.body()
                    Log.d("riko", "getUserChatRooms else: $error")
                    DataState.Error(error)
                }
            }
        } catch (e: Exception) {
            Log.d("riko", "getUserChatRooms else: ${e.message}")
            e.printStackTrace()
            DataState.Error(e.localizedMessage ?: "InternalError")
        }
    }


}