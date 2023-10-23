package com.example.rikochat.data.remote.api.chatSocket

sealed class ChatSocketAction(val name: String) {

    object SendMessage : ChatSocketAction("send_message")

    object UpdateMessage : ChatSocketAction("update_message")

    object DeleteMessage : ChatSocketAction("delete_message")

    object UserAdded : ChatSocketAction("user_added")

    object UserRemoved : ChatSocketAction("user_removed")

}