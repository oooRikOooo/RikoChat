package com.example.rikochat.ui.screen.chat

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.rikochat.R
import com.example.rikochat.domain.model.chatRoom.ChatRoom
import com.example.rikochat.domain.model.chatRoom.ChatRoomType
import com.example.rikochat.domain.model.message.Message
import com.example.rikochat.domain.model.user.User

@Composable
fun ChatScreen(
    username: String,
    roomId: String,
    viewModel: ChatViewModel
) {

    LaunchedEffect(
        key1 = Unit,
        block = {
            Log.d("riko", "LaunchedEffect")
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
                addUserToChatRoom = { openAddUserToGroupChatDialog.value = true },
                viewModel = viewModel
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
        containerColor = MaterialTheme.colorScheme.secondary,
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
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.secondary),
            contentAlignment = Alignment.BottomCenter
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.secondary)
            ) {
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
    addUserToChatRoom: () -> Unit,
    viewModel: ChatViewModel
) {
    val configuration = LocalConfiguration.current

    val screenWidth = configuration.screenWidthDp.dp

    Scaffold(
        containerColor = MaterialTheme.colorScheme.secondary,
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
                .background(MaterialTheme.colorScheme.secondary)
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
                        isOwnPrevMessage = isOwnPrevMessage,
                        viewModel = viewModel
                    )

                    if (isMessageFirstOfDay) {

                        Spacer(modifier = Modifier.height(32.dp))

                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = message.formattedDate,
                                modifier = Modifier.background(Color.Transparent)
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
    isOwnPrevMessage: Boolean,
    viewModel: ChatViewModel
) {

    val bottomStartPaddingMessage = if (isOwnNextMessage) {
        if (!isOwnMessage) 5.dp else 10.dp
    } else {
        10.dp
    }

    val topStartPaddingMessage = if (isOwnPrevMessage) {
        if (!isOwnMessage) 5.dp else 10.dp
    } else {
        10.dp
    }

    val bottomEndPaddingMessage = if (isOwnNextMessage) {
        if (isOwnMessage) 5.dp else 10.dp
    } else {
        10.dp
    }

    val topEndPaddingMessage = if (isOwnPrevMessage) {
        if (isOwnMessage) 5.dp else 10.dp
    } else {
        10.dp
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                viewModel.onEvent(ChatUiEvent.ShowMessageActionsDialog(message))
            },
        contentAlignment = if (isOwnMessage) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        Column(
            modifier = Modifier
                .widthIn(75.dp, screenWidth - 100.dp)
                .background(
                    color = MaterialTheme.colorScheme.secondaryContainer,
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
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }

            Row(
                modifier = Modifier
                    .align(Alignment.End),
                verticalAlignment = CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Text(
                    text = message.text,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )

                Text(
                    modifier = Modifier.padding(top = 10.dp),
                    text = message.formattedTime,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    fontSize = 10.sp
                )
            }


            if (message.usernamesWhoLiked.isNotEmpty()) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Icon(imageVector = Icons.Filled.ThumbUp, contentDescription = null)

                    Text(text = message.usernamesWhoLiked.size.toString())
                }

            }

        }

        if (viewModel.clickedMessage != null && message.id == viewModel.clickedMessage!!.id) {
            MessageActions(viewModel = viewModel)
        }
    }

}

@Composable
fun MessageActions(
    isMessageContainText: Boolean = true,
    viewModel: ChatViewModel
) {

    val clipboardManager = LocalClipboardManager.current

    val actionList = MessageAction.getListOfActions(
        replyAction = {
            viewModel.clickedMessage = null
        },
        copyAction = if (isMessageContainText) {
            {
                clipboardManager.setText(AnnotatedString(viewModel.clickedMessage!!.text))
                viewModel.clickedMessage = null
            }
        } else null,
        likeAction = {
            viewModel.onEvent(
                ChatUiEvent.LikeMessage(
                    viewModel.clickedMessage!!.id
                )
            )
            viewModel.clickedMessage = null
        }
    )

    DropdownMenu(
        expanded = viewModel.clickedMessage != null,
        onDismissRequest = { viewModel.onEvent(ChatUiEvent.HideMessageActionsDialog) }
    ) {
        actionList.forEach {
            DropdownMenuItem(
                text = {
                    Row(
                        verticalAlignment = CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        Icon(imageVector = it.icon, contentDescription = null)
                        Text(text = stringResource(it.text))
                    }
                },
                onClick = it.onClick
            )
        }
    }

//    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
//        LazyColumn(content = {
//            items(items = actionList) {
//                Row(modifier = Modifier.clickable { it.onClick() }) {
//                    Icon(imageVector = it.icon, contentDescription = null)
//                }
//            }
//        })
//    }
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
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                Text(
                    text = stringResource(id = R.string.add_user_to_group_chat),
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
                    Text(text = stringResource(id = R.string.add_user))
                }

            }
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupChatTopBar(title: String, membersCount: Int, addUserToChatRoom: () -> Unit) {
    Surface(shadowElevation = 8.dp) {
        TopAppBar(
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary
            ),
            title = {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 10.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(20.dp),
                    verticalAlignment = CenterVertically,
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

                    val memberMessage =
                        if (membersCount == 1) stringResource(id = R.string.member) else stringResource(
                            id = R.string.members
                        )

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = title,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Text(
                            text = "$membersCount $memberMessage",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onPrimary
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
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        )

    }
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