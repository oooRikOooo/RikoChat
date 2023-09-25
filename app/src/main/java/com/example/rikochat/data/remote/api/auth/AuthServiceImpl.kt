package com.example.rikochat.data.remote.api.auth

import android.util.Log
import com.example.rikochat.data.remote.api.ApiErrors
import com.example.rikochat.data.remote.mapper.UserMapper
import com.example.rikochat.data.remote.model.register.RegisterRequestDao
import com.example.rikochat.data.remote.model.rest.user.UserDto
import com.example.rikochat.domain.api.auth.AuthService
import com.example.rikochat.domain.model.user.User
import com.example.rikochat.domain.repository.TokenRepository
import com.example.rikochat.domain.repository.CurrentUserRepository
import com.example.rikochat.utils.DataState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.coroutines.tasks.await

class AuthServiceImpl(
    private val client: HttpClient,
    private val userMapper: UserMapper,
    private val tokenRepository: TokenRepository,
    private val currentUserRepository: CurrentUserRepository
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
                contentType(ContentType.Application.Json)
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
                Log.d("riko", "AuthServiceImpl login Firebase Successful sign in")
                val token = it.getIdToken(true).await().token
                Log.d("riko", "AuthServiceImpl login Token: $token")
                if (token == null) {
                    FirebaseAuth.getInstance().signOut()
                    DataState.Error(ApiErrors.InternalError.errorMessage)
                } else {

                    tokenRepository.updateAuthToken(token)

                    val response = client.get(AuthService.Endpoints.Login.url){
                        contentType(ContentType.Application.Json)
                    }

                    when(response.status){
                        HttpStatusCode.OK -> {
                            val userDto = response.body<UserDto>()

                            val user = userMapper.mapFromEntity(userDto)

                            currentUserRepository.saveCurrentUser(user)

                            DataState.Success(user)
                        }

                        HttpStatusCode.NoContent -> {
                            FirebaseAuth.getInstance().signOut()
                            tokenRepository.updateAuthToken("")

                            val errorMessage = response.body<String>()

                            DataState.Error(errorMessage)
                        }

                        HttpStatusCode.Unauthorized -> {
                            FirebaseAuth.getInstance().signOut()
                            tokenRepository.updateAuthToken("")

                            val errorMessage = response.body<String>()

                            DataState.Error(errorMessage)
                        }
                        else -> {
                            FirebaseAuth.getInstance().signOut()
                            tokenRepository.updateAuthToken("")

                            DataState.Error(ApiErrors.InternalError.errorMessage)
                        }
                    }

                }
            } ?: run {
                FirebaseAuth.getInstance().signOut()
                tokenRepository.updateAuthToken("")

                Log.d("riko", "AuthServiceImpl login  Firebase User Is Null")
                DataState.Error(ApiErrors.InternalError.errorMessage)
            }
        } catch (e: Exception) {
            FirebaseAuth.getInstance().signOut()
            tokenRepository.updateAuthToken("")

            Log.d("riko", "AuthServiceImpl login  Exception: ${e.message}")
            DataState.Error(e.message ?: ApiErrors.InternalError.errorMessage)
        }

    }


}