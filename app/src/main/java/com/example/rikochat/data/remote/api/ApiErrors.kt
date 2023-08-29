package com.example.rikochat.data.remote.api

const val NO_USER_FOUND = "User Not Found"

sealed class ApiErrors(val errorMessage: String) {
    object UserNotFound : ApiErrors("User Not Found")

    object ChatRoomNotExists : ApiErrors("This chat room doesn't exist")

    object ChatRoomNotCreated : ApiErrors("Chat wasn't created")

    object InternalError : ApiErrors("Internal Error")

    object UserNotAdded : ApiErrors("User wasn't added")

}