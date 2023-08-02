package com.example.rikochat.data.remote.api.auth

import com.example.rikochat.data.remote.mapper.IsUserNameAvailableMapper
import com.example.rikochat.data.remote.model.isUserNameAvailableResponse.IsUserNameAvailableDto
import com.example.rikochat.domain.api.auth.AuthService
import com.example.rikochat.domain.model.isUserNameAvailable.IsUserNameAvailable
import com.example.rikochat.domain.model.user.User
import com.example.rikochat.utils.DataState
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.ktx.Firebase
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.tasks.await

class AuthServiceImpl(
    private val client: HttpClient,
    private val mapper: IsUserNameAvailableMapper
) : AuthService {
    override suspend fun createAccount(
        username: String,
        email: String,
        password: String
    ): DataState<User> {

        return try {
            val result = Firebase.auth.createUserWithEmailAndPassword(email, password).await()

            result.user?.let {
                val profileUpdates = userProfileChangeRequest {
                    displayName = username
                }

                it.updateProfile(profileUpdates).await()

                val user = User(username, it.email ?: email)

                Firebase.auth.signOut()

                DataState.Success(user)

            } ?: run {
                DataState.Error("Internal Error")
            }
        } catch (e: Exception) {
            DataState.Error(e.message ?: "InternalError")
        }

    }

    override suspend fun login(email: String, password: String): DataState<FirebaseUser> {
        return try {
            val result = Firebase.auth.signInWithEmailAndPassword(email, password).await()

            return result.user?.let {
                DataState.Success(it)
            } ?: run {
                DataState.Error("Internal Error")
            }
        } catch (e: Exception) {
            DataState.Error(e.message ?: "InternalError")
        }

    }

    override suspend fun checkIsUserNameAvailable(username: String): DataState<IsUserNameAvailable> {
        return try {
            val isUserNameAvailableDto =
                client.get(AuthService.Endpoints.CheckIsUserNameAvailable.url) {
                    url {
                        parameters.append("username", username)
                    }
                }.body<IsUserNameAvailableDto>()

            val isUserNameAvailable = mapper.mapFromEntity(isUserNameAvailableDto)

            if (isUserNameAvailable.isUserNameAvailable) {
                DataState.Success(mapper.mapFromEntity(isUserNameAvailableDto))
            } else {
                DataState.Error("Username is not available")
            }


        } catch (e: Exception) {
            DataState.Error(e.message ?: "")
        }
    }
}