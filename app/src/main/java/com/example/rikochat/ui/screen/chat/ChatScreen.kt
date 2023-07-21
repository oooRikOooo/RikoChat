package com.example.rikochat.ui.screen.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.rikochat.domain.model.message.Message
import com.example.rikochat.ui.theme.Black20
import com.example.rikochat.ui.theme.LightOrange

@Composable
fun ChatScreen(
    username: String,
    viewModel: ChatViewModel
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(key1 = lifecycleOwner, effect = {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_CREATE) {
                viewModel.connectToChat(username)
            } else if (event == Lifecycle.Event.ON_STOP) {
                viewModel.disconnect()
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    })

    val state = viewModel.uiState.collectAsStateWithLifecycle()

    when (val stateData = state.value) {

        ChatUiState.EmptyChat -> {
            EmptyChatContent(
                messageText = viewModel.messageText.value,
                onMessageChange = {
                    viewModel.onEvent(ChatUiEvent.OnMessageChanged(it))
                },
                sendMessage = { viewModel.onEvent(ChatUiEvent.SendMessage) }
            )
        }

        ChatUiState.FailureLoad -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "Error")
            }
        }

        ChatUiState.Idle -> {}

        ChatUiState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        is ChatUiState.SuccessLoad -> {
            ChatSuccessLoadContent(
                data = stateData.data,
                username = username,
                messageText = viewModel.messageText.value,
                onMessageChange = {
                    viewModel.onEvent(ChatUiEvent.OnMessageChanged(it))
                },
                sendMessage = { viewModel.onEvent(ChatUiEvent.SendMessage) }
            )
        }

    }

}

@Composable
fun EmptyChatContent(
    messageText: String,
    onMessageChange: (String) -> Unit,
    sendMessage: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
        Row(modifier = Modifier.fillMaxWidth()) {
            TextField(
                value = messageText,
                onValueChange = { onMessageChange(it) },
                placeholder = { Text(text = "Enter message") },
                modifier = Modifier.weight(1f)
            )

            IconButton(onClick = { sendMessage() }) {
                Icon(
                    imageVector = Icons.Default.Send, contentDescription = "Send message"
                )
            }
        }

    }

}

@Composable
private fun ChatSuccessLoadContent(
    data: List<Message>,
    username: String,
    messageText: String,
    onMessageChange: (String) -> Unit,
    sendMessage: () -> Unit
) {
    val configuration = LocalConfiguration.current

    val screenWidth = configuration.screenWidthDp.dp

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            reverseLayout = true
        ) {
            item {
                Spacer(modifier = Modifier.height(32.dp))
            }

            itemsIndexed(data) { index, message ->
                val isOwnMessage = message.username == username

                val isMessageFirstOfDay = data.last {
                    message.formattedDate == it.formattedDate
                } == message

                val isOwnPrevMessage = if (index == data.size - 1) false else {
                    message.username == data[index + 1].username
                }

                val isOwnNextMessage = if (index == 0) false else {
                    message.username == data[index - 1].username
                }

                val bottomStartPaddingMessage = if (isOwnNextMessage) {
                    if (!isOwnMessage) 5.dp else 20.dp
                } else {
                    20.dp
                }

                val topStartPaddingMessage = if (isOwnPrevMessage) {
                    if (!isOwnMessage) 5.dp else 20.dp
                } else {
                    20.dp
                }

                val bottomEndPaddingMessage = if (isOwnNextMessage) {
                    if (isOwnMessage) 5.dp else 20.dp
                } else {
                    20.dp
                }

                val topEndPaddingMessage = if (isOwnPrevMessage) {
                    if (isOwnMessage) 5.dp else 20.dp
                } else {
                    20.dp
                }

                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = if (isOwnMessage) Alignment.CenterEnd else Alignment.CenterStart
                ) {
                    Column(
                        modifier = Modifier
                            .widthIn(75.dp, screenWidth - 100.dp)
                            .drawBehind {
                                if (!isOwnNextMessage) {
                                    val triangleWidth = 10.dp.toPx()
                                    val trianglePath = Path().apply {
                                        if (isOwnMessage) {
                                            moveTo(
                                                size.width,
                                                size.height - bottomEndPaddingMessage.toPx()
                                            )
                                            lineTo(size.width + triangleWidth, size.height)
                                            lineTo(
                                                size.width - bottomEndPaddingMessage.toPx(),
                                                size.height
                                            )
                                            close()
                                        } else {
                                            moveTo(0f, size.height - bottomEndPaddingMessage.toPx())
                                            lineTo(-1 * triangleWidth, size.height)
                                            lineTo(bottomEndPaddingMessage.toPx(), size.height)
                                            close()
                                        }
                                    }

                                    drawPath(
                                        path = trianglePath,
                                        color = LightOrange
                                    )
                                }
                            }
                            .background(
                                color = LightOrange,
                                shape = RoundedCornerShape(
                                    topStart = topStartPaddingMessage,
                                    bottomStart = bottomStartPaddingMessage,
                                    topEnd = topEndPaddingMessage,
                                    bottomEnd = bottomEndPaddingMessage
                                )
                            )
                            .padding(vertical = 8.dp, horizontal = 16.dp)
                    ) {
                        if (!isOwnPrevMessage) {
                            Text(
                                text = message.username,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        Row(
                            modifier = Modifier
                                .align(Alignment.End),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Text(text = message.text, color = Color.White)

                            Text(
                                modifier = Modifier.padding(top = 10.dp),
                                text = message.formattedTime,
                                color = Color.White,
                                fontSize = 10.sp
                            )
                        }

                    }
                }

                if (isMessageFirstOfDay) {

                    Spacer(modifier = Modifier.height(32.dp))

                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text(
                            text = message.formattedDate,
                            modifier = Modifier.background(Black20),
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(if (isOwnPrevMessage) 3.dp else 32.dp))

            }
        }

        Row(modifier = Modifier.fillMaxWidth()) {
            TextField(
                value = messageText,
                onValueChange = { onMessageChange(it) },
                placeholder = { Text(text = "Enter message") },
                modifier = Modifier.weight(1f)
            )

            IconButton(onClick = { sendMessage() }) {
                Icon(
                    imageVector = Icons.Default.Send, contentDescription = "Send message"
                )
            }
        }
    }
}