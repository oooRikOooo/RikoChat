package com.example.rikochat.ui.screen.chat

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Reply
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.rikochat.R

sealed class MessageAction(
    open val onClick: () -> Unit,
    @StringRes open val text: Int,
    open val icon: ImageVector
) {

    data class Reply(
        override val onClick: () -> Unit,
        override val text: Int = R.string.reply,
        override val icon: ImageVector = Icons.Filled.Reply

    ) : MessageAction(onClick, text, icon)

    data class Copy(
        override val onClick: () -> Unit,
        override val text: Int = R.string.copy,
        override val icon: ImageVector = Icons.Filled.ContentCopy
    ) : MessageAction(onClick, text, icon)

    data class Like(
        override val onClick: () -> Unit,
        override val text: Int = R.string.like,
        override val icon: ImageVector = Icons.Filled.ThumbUp
    ) : MessageAction(onClick, text, icon)

    companion object {

        fun getListOfActions(
            replyAction: (() -> Unit)? = null,
            copyAction: (() -> Unit)? = null,
            likeAction: (() -> Unit)? = null
        ): List<MessageAction> {
            val actionList = mutableListOf<MessageAction>()

            replyAction?.let {
                actionList.add(Reply(it))
            }

            copyAction?.let {
                actionList.add(Copy(it))
            }

            likeAction?.let {
                actionList.add(Like(it))
            }

            return actionList
        }

    }

}
