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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.PeopleOutline
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.rikochat.domain.model.chatRoom.ChatRoom
import com.example.rikochat.domain.model.chatRoom.ChatRoomType
import com.example.rikochat.domain.model.message.Message
import com.example.rikochat.domain.model.user.User
import com.example.rikochat.ui.theme.Black20
import com.example.rikochat.ui.theme.LightGreen

@Composable
fun ChatScreen(
    username: String,
    roomId: String,
    viewModel: ChatViewModel
) {

    LaunchedEffect(
        key1 = Unit,
        block = {
            viewModel.onEvent(ChatUiEvent.LoadRoomInfo(roomId))
        }
    )

    val state = viewModel.uiState.collectAsStateWithLifecycle()

    val openAddUserToGroupChatDialog = remember { mutableStateOf(false) }

    when (val stateData = state.value) {

        is ChatUiState.EmptyChat -> {
            EmptyChatContent(
                username = username,
                chatRoom = stateData.chatRoom,
                chatRoomMembers = stateData.chatRoomMembers,
                messageText = viewModel.messageText.value,
                onMessageChange = {
                    viewModel.onEvent(ChatUiEvent.OnMessageChanged(it))
                },
                sendMessage = { viewModel.onEvent(ChatUiEvent.SendMessage(roomId)) },
                addUserToChatRoom = { openAddUserToGroupChatDialog.value = true }
            )

            if (openAddUserToGroupChatDialog.value) {
                AddUserToGroupChatRoomDialog(
                    roomId = roomId,
                    viewModel = viewModel,
                    closeDialog = { openAddUserToGroupChatDialog.value = false }
                )
            }
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
                chatRoom = stateData.chatRoom,
                chatRoomMembers = stateData.chatRoomMembers,
                messages = stateData.data,
                username = username,
                messageText = viewModel.messageText.value,
                onMessageChange = {
                    viewModel.onEvent(ChatUiEvent.OnMessageChanged(it))
                },
                sendMessage = { viewModel.onEvent(ChatUiEvent.SendMessage(roomId)) },
                addUserToChatRoom = { openAddUserToGroupChatDialog.value = true }
            )

            if (openAddUserToGroupChatDialog.value) {
                AddUserToGroupChatRoomDialog(
                    roomId = roomId,
                    viewModel = viewModel,
                    closeDialog = { openAddUserToGroupChatDialog.value = false }
                )
            }
        }

    }

}

@Composable
fun EmptyChatContent(
    username: String,
    chatRoom: ChatRoom,
    chatRoomMembers: List<User>,
    messageText: String,
    onMessageChange: (String) -> Unit,
    sendMessage: () -> Unit,
    addUserToChatRoom: () -> Unit
) {
    Scaffold(
        topBar = {
            when (chatRoom.type) {
                ChatRoomType.Direct -> {
                    val user = chatRoomMembers.first {
                        it.username != username
                    }

                    DirectChatTopBar(user = user, addUserToChatRoom = addUserToChatRoom)

                }

                ChatRoomType.Group -> GroupChatTopBar(
                    title = chatRoom.title,
                    membersCount = chatRoomMembers.size,
                    addUserToChatRoom = addUserToChatRoom
                )
            }
        }

    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            contentAlignment = Alignment.BottomCenter
        ) {
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

}

@Composable
private fun ChatSuccessLoadContent(
    chatRoom: ChatRoom,
    chatRoomMembers: List<User>,
    messages: List<Message>,
    username: String,
    messageText: String,
    onMessageChange: (String) -> Unit,
    sendMessage: () -> Unit,
    addUserToChatRoom: () -> Unit
) {
    val configuration = LocalConfiguration.current

    val screenWidth = configuration.screenWidthDp.dp

    Scaffold(
        topBar = {
            when (chatRoom.type) {
                ChatRoomType.Direct -> {
                    val user = chatRoomMembers.first {
                        it.username != username
                    }

                    DirectChatTopBar(user = user, addUserToChatRoom = addUserToChatRoom)

                }

                ChatRoomType.Group -> GroupChatTopBar(
                    title = chatRoom.title,
                    membersCount = chatRoomMembers.size,
                    addUserToChatRoom = addUserToChatRoom
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
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

                itemsIndexed(messages) { index, message ->
                    val isOwnMessage = message.username == username

                    val isMessageFirstOfDay = messages.last {
                        message.formattedDate == it.formattedDate
                    } == message

                    val isOwnPrevMessage = if (index == messages.size - 1) false else {
                        message.username == messages[index + 1].username
                    }

                    val isOwnNextMessage = if (index == 0) false else {
                        message.username == messages[index - 1].username
                    }

                    MessageItem(
                        message = message,
                        isOwnMessage = isOwnMessage,
                        screenWidth = screenWidth,
                        isOwnNextMessage = isOwnNextMessage,
                        isOwnPrevMessage = isOwnPrevMessage
                    )

                    if (isMessageFirstOfDay) {

                        Spacer(modifier = Modifier.height(32.dp))

                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
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
}

@Composable
private fun MessageItem(
    message: Message,
    isOwnMessage: Boolean,
    screenWidth: Dp,
    isOwnNextMessage: Boolean,
    isOwnPrevMessage: Boolean
) {

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
                                moveTo(
                                    0f,
                                    size.height - bottomEndPaddingMessage.toPx()
                                )
                                lineTo(-1 * triangleWidth, size.height)
                                lineTo(bottomEndPaddingMessage.toPx(), size.height)
                                close()
                            }
                        }

                        drawPath(
                            path = trianglePath,
                            color = LightGreen
                        )
                    }
                }
                .background(
                    color = LightGreen,
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

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddUserToGroupChatRoomDialog(
    roomId: String,
    viewModel: ChatViewModel,
    closeDialog: () -> Unit
) {
    AlertDialog(onDismissRequest = { closeDialog() }) {
        Surface(
            modifier = Modifier
                .wrapContentWidth()
                .wrapContentHeight(),
            shape = MaterialTheme.shapes.large
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "New group chat",
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Start),
                    textAlign = TextAlign.Start
                )

                OutlinedTextField(
                    value = viewModel.dialogAddUserToGroupChatRoomUsername,
                    onValueChange = {
                        viewModel.onEvent(
                            ChatUiEvent.OnAddUserToGroupChatRoomTextChanged(
                                it
                            )
                        )
                    },
                    singleLine = true,
                    isError = viewModel.isDialogAddUserToGroupChatRoomUsernameError,
                    supportingText = {
                        if (viewModel.isDialogAddUserToGroupChatRoomUsernameError) {
                            Text(
                                text = viewModel.dialogAddUserToGroupChatRoomUsernameError,
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    },
                    trailingIcon = {
                        if (viewModel.isDialogAddUserToGroupChatRoomUsernameError) {
                            Icon(
                                imageVector = Icons.Filled.Error,
                                contentDescription = "Error: Empty title field"
                            )
                        }
                    }
                )

                Button(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    onClick = {
                        if (viewModel.dialogAddUserToGroupChatRoomUsername.isNotBlank()) {
                            viewModel.onEvent(ChatUiEvent.AddUserToGroupChat(roomId))
                            closeDialog()
                        }

                    }
                ) {
                    Text(text = "Create group")
                }

            }
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupChatTopBar(title: String, membersCount: Int, addUserToChatRoom: () -> Unit) {
    TopAppBar(
        title = {
            Row(
                modifier = Modifier
                    .padding(vertical = 25.dp, horizontal = 10.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = "",
                    contentDescription = "Group chat picture",
                    modifier = Modifier
                        .width(50.dp)
                        .height(50.dp)
                        .clip(CircleShape)
                        .background(Color.White),
                    error = rememberVectorPainter(image = Icons.Filled.PeopleOutline)
                )

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(text = title, fontSize = 25.sp)
                    Text(
                        text = membersCount.toString() + if (membersCount == 1) "member" else "members",
                        fontSize = 16.sp
                    )
                }

            }
        },
        actions = {
            IconButton(onClick = {
                addUserToChatRoom()
            }) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Add user to group chat",
                    tint = Color.White
                )
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DirectChatTopBar(user: User, addUserToChatRoom: () -> Unit) {
    TopAppBar(
        title = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(text = user.username, fontSize = 16.sp)
            }
        })
}