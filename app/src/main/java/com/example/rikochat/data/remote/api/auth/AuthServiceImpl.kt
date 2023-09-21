package com.example.rikochat.data.remote.api.auth

import com.example.rikochat.data.remote.api.ApiErrors
import com.example.rikochat.data.remote.mapper.UserMapper
import com.example.rikochat.data.remote.model.register.RegisterRequestDao
import com.example.rikochat.data.remote.model.rest.user.UserDto
import com.example.rikochat.domain.api.auth.AuthService
import com.example.rikochat.domain.model.user.User
import com.example.rikochat.domain.repository.TokenRepository
import com.example.rikochat.utils.DataState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.tasks.await

class AuthServiceImpl(
    private val client: HttpClient,
    private val userMapper: UserMapper,
    private val tokenRepository: TokenRepository
) : AuthService {

    override suspend fun register(
        username: String,
        email: String,
        password: String
    ): DataState<Unit> {
        return try {
            val registerRequestDto = RegisterRequestDao(
                email, username, password
            )

            val response = client.post(AuthService.Endpoints.Register.url) {
                setBody(registerRequestDto)
            }

            val responseBody: String = response.body()

            when (response.status) {
                HttpStatusCode.OK -> {
                    DataState.Success(Unit)
                }

                HttpStatusCode.BadRequest -> {
                    DataState.Error(responseBody)
                }

                HttpStatusCode.InternalServerError -> {
                    DataState.Error(responseBody)
                }

                else -> {
                    DataState.Error(responseBody)
                }
            }
        } catch (ex: Exception) {
            DataState.Error(ex.message ?: "")
        }
    }



    override suspend fun login(email: String, password: String): DataState<User> {
        return try {
            val result = Firebase.auth.signInWithEmailAndPassword(email, password).await()

            return result.user?.let {
                val token = it.getIdToken(true).await().token

                if (token == null) {
                    FirebaseAuth.getInstance().signOut()
                    DataState.Error(ApiErrors.InternalError.errorMessage)
                } else {

                    tokenRepository.saveAuthToken(token)

                    val response = client.get(AuthService.Endpoints.Login.url)

                    when(response.status){
                        HttpStatusCode.OK -> {
                            val userDto = response.body<UserDto>()

                            val user = userMapper.mapFromEntity(userDto)

                            DataState.Success(user)
                        }

                        HttpStatusCode.NoContent -> {
                            val errorMessage = response.body<String>()

                            DataState.Error(errorMessage)
                        }

                        else -> {
                            DataState.Error(ApiErrors.InternalError.errorMessage)
                        }
                    }

                }
            } ?: run {
                DataState.Error(ApiErrors.InternalError.errorMessage)
            }
        } catch (e: Exception) {
            DataState.Error(e.message ?: ApiErrors.InternalError.errorMessage)
        }

    }


}