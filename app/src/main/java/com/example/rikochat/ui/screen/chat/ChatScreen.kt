package com.example.rikochat.ui.screen.chat

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.rikochat.R
import com.example.rikochat.domain.model.chatRoom.ChatRoom
import com.example.rikochat.domain.model.chatRoom.ChatRoomType
import com.example.rikochat.domain.model.message.Message
import com.example.rikochat.domain.model.user.User

@Composable
fun ChatScreen(
    roomId: String,
    viewModel: ChatViewModel
) {

    LaunchedEffect(
        key1 = roomId,
        block = {
            viewModel.onEvent(ChatUiEvent.LoadRoomInfo(roomId))
        }
    )

    val state = viewModel.uiState.collectAsState()

    val openAddUserToGroupChatDialog = remember { mutableStateOf(false) }

    when (val stateData = state.value) {

        is ChatUiState.EmptyChat -> {
            EmptyChatContent(
                currentUser = stateData.currentUser,
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
                messages = stateData.messages,
                currentUser = stateData.currentUser,
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
    currentUser: User,
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
                        it.username != currentUser.username
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
private fun ChatSuccessLoadContent(
    chatRoom: ChatRoom,
    chatRoomMembers: List<User>,
    messages: List<Message>,
    currentUser: User,
    messageText: String,
    onMessageChange: (String) -> Unit,
    sendMessage: () -> Unit,
    addUserToChatRoom: () -> Unit,
    viewModel: ChatViewModel
) {
    val configuration = LocalConfiguration.current

    val screenWidth = remember(configuration) { configuration.screenWidthDp.dp }

    val keyboardController = LocalSoftwareKeyboardController.current

    Scaffold(
        containerColor = MaterialTheme.colorScheme.secondary,
        topBar = {
            when (chatRoom.type) {
                ChatRoomType.Direct -> {
                    val user = chatRoomMembers.first {
                        it.username != currentUser.username
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
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
                reverseLayout = true
            ) {
                item {
                    Spacer(modifier = Modifier.height(32.dp))
                }

                itemsIndexed(items = messages, key = { index, message ->
                    messages[index].id
                }) { index, message ->
                    val isOwnMessage = remember {
                        message.username == currentUser.username
                    }

                    val isMessageFirstOfDay = remember(messages) {
                        messages.last {
                            message.formattedDate == it.formattedDate
                        } == message
                    }

                    val isOwnPrevMessage = remember(messages) {
                        if (index == messages.size - 1) false else {
                            message.username == messages[index + 1].username
                        }
                    }

                    val isOwnNextMessage = remember(messages) {
                        if (index == 0) false else {
                            message.username == messages[index - 1].username
                        }
                    }

                    val isTimeDifferenceMore5Min = remember(messages) {
                        if (messages.size == 1) {
                            true
                        } else if (index != messages.size - 1) {
                            message.timestamp - messages[index + 1].timestamp > 300_000 // 5 min

                        } else {
                            messages[index - 1].timestamp - message.timestamp > 300_000
                        }
                    }


                    MessageItem(
                        message = message,
                        isOwnMessage = isOwnMessage,
                        screenWidth = screenWidth,
                        isOwnNextMessage = isOwnNextMessage,
                        isOwnPrevMessage = isOwnPrevMessage,
                        isTimeDifferenceMore5Min = isTimeDifferenceMore5Min,
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

                    Spacer(
                        modifier = Modifier.height(
                            if (isOwnPrevMessage) {
                                if (isTimeDifferenceMore5Min) 5.dp else 1.dp
                            } else 32.dp
                        )
                    )

                }
            }

            Row(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.secondaryContainer)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = messageText,
                    onValueChange = { onMessageChange(it) },
                    placeholder = { Text(text = "Enter message") },
                    modifier = Modifier.weight(1f),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        cursorColor = MaterialTheme.colorScheme.primary,
                        focusedTextColor = MaterialTheme.colorScheme.primary,
                        unfocusedTextColor = MaterialTheme.colorScheme.primary
                    ),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            sendMessage()
                            keyboardController?.hide()
                        }
                    )
                )

                IconButton(
                    onClick = {
                        sendMessage()
                        keyboardController?.hide()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Send message",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

        }

    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun MessageItem(
    message: Message,
    isOwnMessage: Boolean,
    screenWidth: Dp,
    isOwnNextMessage: Boolean,
    isOwnPrevMessage: Boolean,
    isTimeDifferenceMore5Min: Boolean,
    viewModel: ChatViewModel
) {

    val bottomStartPaddingMessage = remember(isOwnNextMessage) {
        if (isOwnNextMessage) {
            if (!isOwnMessage) 5.dp else 10.dp
        } else {
            10.dp
        }
    }

    val topStartPaddingMessage = remember(isOwnPrevMessage) {
        if (isOwnPrevMessage) {
            if (!isOwnMessage) 5.dp else 10.dp
        } else {
            10.dp
        }
    }

    val bottomEndPaddingMessage = remember(isOwnNextMessage) {
        if (isOwnNextMessage) {
            if (isOwnMessage) 5.dp else 10.dp
        } else {
            10.dp
        }
    }

    val topEndPaddingMessage = remember(isOwnPrevMessage) {
        if (isOwnPrevMessage) {
            if (isOwnMessage) 5.dp else 10.dp
        } else {
            10.dp
        }
    }


    val messageClickAreaWidth = remember { screenWidth - 100.dp }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                viewModel.onEvent(ChatUiEvent.ShowMessageActionsDialog(message))
            }
            .animateContentSize(),
        contentAlignment = if (isOwnMessage) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        AnimatedContent(
            targetState = message.usernamesWhoLiked.isNotEmpty(),
            label = "",
            transitionSpec = {
                scaleIn(initialScale = 0.9f, animationSpec = tween(300))
                    .togetherWith(scaleOut(targetScale = 1f, animationSpec = tween(300)))
            }
        ) { targetState ->
            Column(
                modifier = Modifier
                    .width(IntrinsicSize.Max)
                    .background(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = RoundedCornerShape(
                            topStart = topStartPaddingMessage,
                            bottomStart = bottomStartPaddingMessage,
                            topEnd = topEndPaddingMessage,
                            bottomEnd = bottomEndPaddingMessage
                        )
                    )
                    .padding(8.dp)
                    .animateContentSize()
            ) {

                val isMessageSmallWithoutAdditionalItems =
                    remember(targetState) { message.text.length < 35 && !targetState }

                if ((!isOwnPrevMessage || isTimeDifferenceMore5Min) && !isOwnMessage) {
                    Text(
                        text = message.username,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }


                Row(
                    modifier = Modifier.widthIn(50.dp, messageClickAreaWidth),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        modifier = Modifier.padding(end = if (isMessageSmallWithoutAdditionalItems) 8.dp else 0.dp),
                        text = message.text,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        lineHeight = 20.sp
                    )

                    if (isMessageSmallWithoutAdditionalItems) {
                        Text(
                            modifier = Modifier.padding(top = 10.dp),
                            text = message.formattedTime,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            fontSize = 10.sp,
                            textAlign = TextAlign.End
                        )
                    }
                }


                if (!isMessageSmallWithoutAdditionalItems) {
                    MessageBottomContainer(
                        modifier = Modifier
                            .align(Alignment.End)
                            .padding(top = 5.dp)
                            .fillMaxWidth(),
                        message = message
                    )
                }

            }

        }

        if (viewModel.clickedMessage != null && message.id == viewModel.clickedMessage!!.id) {
            MessageActions(viewModel = viewModel)
        }
    }

}

@Composable
fun MessageBottomContainer(
    modifier: Modifier = Modifier,
    message: Message
) {

    val isDateOnlyInfo = remember(message) { message.usernamesWhoLiked.isEmpty() }

    Row(
        modifier = modifier,
        horizontalArrangement = if (isDateOnlyInfo) Arrangement.End else Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        if (message.usernamesWhoLiked.isNotEmpty()) {
            Row(modifier = Modifier.padding(end = 16.dp)) {
                Icon(
                    modifier = Modifier.size(24.dp),
                    imageVector = Icons.Filled.ThumbUp,
                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                    contentDescription = null
                )

                Text(
                    text = message.usernamesWhoLiked.size.toString(),
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }

        }

        Text(
            modifier = Modifier.align(Alignment.Bottom),
            text = message.formattedTime,
            color = MaterialTheme.colorScheme.onSecondaryContainer,
            fontSize = 10.sp,
            textAlign = TextAlign.End
        )


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
        },
        deleteAction = {
            viewModel.onEvent(
                ChatUiEvent.DeleteMessage(
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
                        verticalAlignment = Alignment.CenterVertically,
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
                    verticalAlignment = Alignment.CenterVertically,
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

private fun calculateBottomStartPaddingMessage(
    isOwnNextMessage: Boolean,
    isOwnMessage: Boolean
): Dp {
    return if (isOwnNextMessage) {
        if (!isOwnMessage) 5.dp else 10.dp
    } else {
        10.dp
    }
}

private fun calculateTopStartPaddingMessage(isOwnPrevMessage: Boolean, isOwnMessage: Boolean): Dp {
    return if (isOwnPrevMessage) {
        if (!isOwnMessage) 5.dp else 10.dp
    } else {
        10.dp
    }
}

private fun calculateBottomEndPaddingMessage(isOwnNextMessage: Boolean, isOwnMessage: Boolean): Dp {
    return if (isOwnNextMessage) {
        if (isOwnMessage) 5.dp else 10.dp
    } else {
        10.dp
    }
}

private fun calculateTopEndPaddingMessage(isOwnPrevMessage: Boolean, isOwnMessage: Boolean): Dp {
    return if (isOwnPrevMessage) {
        if (isOwnMessage) 5.dp else 10.dp
    } else {
        10.dp
    }
}