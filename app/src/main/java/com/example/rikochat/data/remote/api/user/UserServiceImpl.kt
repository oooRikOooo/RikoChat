package com.example.rikochat.data.remote.api.user

import com.example.rikochat.data.remote.api.NO_USER_FOUND
import com.example.rikochat.data.remote.mapper.UserMapper
import com.example.rikochat.data.remote.model.userResponse.UserDto
import com.example.rikochat.domain.api.user.UserService
import com.example.rikochat.domain.model.user.User
import com.example.rikochat.utils.DataState
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType

class UserServiceImpl(
    private val client: HttpClient,
    private val userMapper: UserMapper
) : UserService {
    override suspend fun getUser(username: String): DataState<User> {

        val response = client.get(UserService.Endpoints.User.url) {
            url {
                parameters.append("username", username)
            }
        }
        return try {
            if (response.status == HttpStatusCode.OK) {
                val userDto = response.body<UserDto>()

                val user = userMapper.mapFromEntity(userDto)

                DataState.Success(user)
            } else if (response.status == HttpStatusCode.NoContent) {
                DataState.Error(NO_USER_FOUND)
            } else {
                DataState.Error(response.body())
            }


        } catch (e: Exception) {
            e.printStackTrace()
            DataState.Error(e.localizedMessage ?: "InternalError")
        }

    }

    override suspend fun saveUser(user: User): DataState<Unit> {

        val userDto = userMapper.mapToEntity(user)

        val response = client.post(UserService.Endpoints.User.url) {
            contentType(ContentType.Application.Json)
            setBody(userDto)
        }

        return try {

            if (response.status == HttpStatusCode.Created) {
                DataState.Success(Unit)
            } else {
                DataState.Error("Internal Error")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            DataState.Error(e.localizedMessage ?: "Internal Error")
        }

    }
}