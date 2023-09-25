package com.example.rikochat.data.remote.api.message

import com.example.rikochat.data.remote.api.ApiErrors
import com.example.rikochat.data.remote.mapper.ChatRoomMapper
import com.example.rikochat.data.remote.mapper.MessageMapper
import com.example.rikochat.data.remote.mapper.UserMapper
import com.example.rikochat.data.remote.model.rest.chatRoom.ChatRoomDto
import com.example.rikochat.data.remote.model.rest.chatRoom.CreateChatRoomRequestBody
import com.example.rikochat.data.remote.model.rest.message.MessageDto
import com.example.rikochat.domain.api.message.RoomService
import com.example.rikochat.domain.model.chatRoom.ChatRoom
import com.example.rikochat.domain.model.message.Message
import com.example.rikochat.domain.model.user.User
import com.example.rikochat.utils.DataState
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType

class RoomServiceImpl(
    private val client: HttpClient,
    private val messageMapper: MessageMapper,
    private val roomMapper: ChatRoomMapper,
    private val userMapper: UserMapper
) : RoomService {
    override suspend fun getAllChatRoomMessages(roomId: String): DataState<List<Message>> {
        return try {
            val messages =
                client.get(RoomService.Endpoints.GetAllChatRoomMessages.url) {
                    url {
                        parameters.append("roomId", roomId)
                    }
                }.body<List<MessageDto>>()
            return DataState.Success(messageMapper.mapFromEntityList(messages))
        } catch (e: Exception) {
            DataState.Error(e.message ?: "")
        }
    }

    override suspend fun getAllUserChatRooms(): DataState<List<ChatRoom>> {

        val response = client.get(RoomService.Endpoints.GetAllUserChatRooms.url)

        return try {
            if (response.status == HttpStatusCode.OK) {
                val chatRoomDto = response.body<List<ChatRoomDto>>()
                DataState.Success(roomMapper.mapFromEntityList(chatRoomDto))
            } else {
                DataState.Error(response.body())
            }

        } catch (e: Exception) {
            e.printStackTrace()
            DataState.Error(e.localizedMessage ?: "Internal Error")
        }
    }

    override suspend fun createChatRoom(
        requestBody: CreateChatRoomRequestBody
    ): DataState<ChatRoom> {
        val response = client.post(RoomService.Endpoints.CreateChatRoom.url) {
            contentType(ContentType.Application.Json)
            setBody(requestBody)
        }

        return try {
            when (response.status) {
                HttpStatusCode.OK -> {
                    val chatRoomDto: ChatRoomDto = response.body()
                    val chatRoom = roomMapper.mapFromEntity(chatRoomDto)
                    DataState.Success(chatRoom)
                }

                HttpStatusCode.NoContent -> {
                    DataState.Error(ApiErrors.ChatRoomNotCreated.errorMessage)
                }

                else -> {
                    DataState.Error(ApiErrors.InternalError.errorMessage)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            DataState.Error(e.localizedMessage ?: ApiErrors.InternalError.errorMessage)
        }
    }

    override suspend fun getChatRoom(roomId: String): DataState<ChatRoom> {
        val response = client.get(RoomService.Endpoints.GetChatRoom.url) {
            url {
                parameters.append("roomId", roomId)
            }
        }

        return try {
            when (response.status) {
                HttpStatusCode.OK -> {
                    val chatRoom = roomMapper.mapFromEntity(response.body())
                    DataState.Success(chatRoom)
                }

                HttpStatusCode.NoContent -> {
                    DataState.Error(ApiErrors.ChatRoomNotExists.errorMessage)
                }

                else -> {
                    DataState.Error(ApiErrors.InternalError.errorMessage)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            DataState.Error(e.localizedMessage ?: ApiErrors.InternalError.errorMessage)
        }
    }

    override suspend fun getAllChatRoomUsers(roomId: String): DataState<List<User>> {
        val response = client.get(RoomService.Endpoints.GetAllChatRoomUsers.url) {
            url {
                parameters.append("roomId", roomId)
            }
        }

        return try {
            if (response.status == HttpStatusCode.OK) {
                val users = userMapper.mapFromEntityList(response.body())
                DataState.Success(users)
            } else {
                DataState.Error(response.body())
            }
        } catch (e: Exception) {
            e.printStackTrace()
            DataState.Error(e.localizedMessage ?: ApiErrors.InternalError.errorMessage)
        }
    }

    override suspend fun addUserToChatRoom(username: String, roomId: String): DataState<Unit> {
        val response = client.post(RoomService.Endpoints.AddUserToChatRoom.url) {
            url {
                parameters.append("username", username)
                parameters.append("roomId", roomId)
            }
        }

        return try {
            when (response.status) {
                HttpStatusCode.OK -> {
                    DataState.Success(Unit)
                }

                HttpStatusCode.Accepted -> {
                    DataState.Error(ApiErrors.UserNotAdded.errorMessage)
                }

                else -> {
                    DataState.Error(ApiErrors.InternalError.errorMessage)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            DataState.Error(e.localizedMessage ?: ApiErrors.InternalError.errorMessage)
        }
    }

    override suspend fun deleteUserFromChatRoom(userId: String, roomId: String): DataState<Unit> {
        val response = client.delete(RoomService.Endpoints.DeleteUserFromChatRoom.url) {
            url {
                parameters.append("userId", userId)
                parameters.append("roomId", roomId)
            }
        }

        return try {
            when (response.status) {
                HttpStatusCode.OK -> {
                    DataState.Success(Unit)
                }

                HttpStatusCode.NotFound -> {
                    DataState.Error(ApiErrors.UserNotFound.errorMessage)
                }

                else -> {
                    DataState.Error(ApiErrors.InternalError.errorMessage)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            DataState.Error(e.localizedMessage ?: ApiErrors.InternalError.errorMessage)
        }
    }
}