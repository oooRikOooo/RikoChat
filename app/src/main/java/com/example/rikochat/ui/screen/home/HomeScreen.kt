package com.example.rikochat.ui.screen.home

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.rikochat.R
import com.example.rikochat.domain.model.chatRoom.ChatRoom
import com.example.rikochat.domain.model.user.User
import com.example.rikochat.utils.ui.LoadingProgressIndicator
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    navigateToChat: (String) -> Unit,
    navigateToLogin: () -> Unit
) {

    LaunchedEffect(key1 = Unit, block = {
        viewModel.onEvent(HomeUiEvent.LoadInitialData)
    })

//    val lifecycleOwner = LocalLifecycleOwner.current
//    DisposableEffect(key1 = lifecycleOwner, effect = {
//        val observer = LifecycleEventObserver { _, event ->
//            when (event) {
//                Lifecycle.Event.ON_CREATE -> {
//                    Log.d("riko", "On Create")
//                    viewModel.connectToWebSocket()
//                }
//
//                Lifecycle.Event.ON_PAUSE -> {}
//                Lifecycle.Event.ON_DESTROY -> {
//                    Log.d("riko", "On Destroy")
//                    viewModel.disconnect()
//                }
//
//                else -> {
//                    Log.d("riko", event.name)
//                }
//            }
//        }
//
//        lifecycleOwner.lifecycle.addObserver(observer)
//
//        onDispose {
//            lifecycleOwner.lifecycle.removeObserver(observer)
//        }
//    })

    val uiState = viewModel.uiState.collectAsStateWithLifecycle()

    when (val state = uiState.value) {
        is HomeUiState.EmptyScreen -> {
            DrawerWithEmptyContent(viewModel = viewModel, currentUser = state.currentUser)
        }

        is HomeUiState.Error -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.secondary),
                contentAlignment = Alignment.Center
            ) {
                Text(text = state.message)

            }
        }

        HomeUiState.Idle -> {}

        HomeUiState.Loading -> {
            LoadingProgressIndicator()
        }

        is HomeUiState.SuccessLoad -> {
            DrawerWithContent(
                viewModel = viewModel,
                chatRooms = state.rooms,
                user = state.currentUser,
                navigateToChat = navigateToChat
            )
        }

        HomeUiState.UserNotLoggedIn -> {
            Log.d("riko", "UserNotLoggedIn")
            navigateToLogin.invoke()
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrawerWithContent(
    viewModel: HomeViewModel,
    chatRooms: List<ChatRoom>,
    user: User,
    navigateToChat: (String) -> Unit
) {

    val openDialog = remember { mutableStateOf(false) }

    val drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    ModalNavigationDrawer(drawerContent = {
        DrawerContent(user = user)
    }, drawerState = drawerState) {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.secondary,
            topBar = {
                MainScreenTopBar(
                    drawerState = drawerState,
                    logout = {}
                )
            }
        ) { paddingValues ->
            MainContent(
                paddingValues = paddingValues,
                items = chatRooms,
                navigateToChat = navigateToChat,
                openCreateChatRoomDialog = { openDialog.value = it }
            )

            if (openDialog.value) {
                CreateChatRoomDialog(
                    viewModel = viewModel,
                    closeDialog = { openDialog.value = false }
                )
            }
        }
    }

}

@Composable
private fun DrawerWithEmptyContent(
    viewModel: HomeViewModel,
    currentUser: User
) {
    val openDialog = remember { mutableStateOf(false) }

    val drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    ModalNavigationDrawer(drawerContent = {
        DrawerContent(user = currentUser)
    }, drawerState = drawerState) {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.secondary,
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        openDialog.value = true
                    }
                ) {
                    Icon(Icons.Filled.Add, "Create Chat Room")
                }
            }
        ) { innerPaddingValues ->
            Box(
                modifier = Modifier
                    .padding(innerPaddingValues)
                    .fillMaxSize(), contentAlignment = Alignment.Center
            ) {
                Text(text = "No chats... :(")
            }

            if (openDialog.value) {
                CreateChatRoomDialog(
                    viewModel = viewModel,
                    closeDialog = { openDialog.value = false }
                )
            }
        }
    }

}

@Composable
fun MainContent(
    paddingValues: PaddingValues,
    items: List<ChatRoom>,
    openCreateChatRoomDialog: (Boolean) -> Unit,
    navigateToChat: (String) -> Unit
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.secondary,
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    openCreateChatRoomDialog(true)
                }
            ) {
                Icon(Icons.Filled.Add, "Create Chat Room")
            }
        }
    ) { innerPaddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .padding(innerPaddingValues)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.secondary),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            itemsIndexed(items) { index, item ->
                ChatItem(chatRoom = item, navigateToChat = navigateToChat)

                if (index < items.lastIndex)
                    Divider(
//                        color = Color.LightGray,
                        thickness = 1.dp,
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )

            }
        }
    }

}

@Composable
fun ChatItem(chatRoom: ChatRoom, navigateToChat: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .clickable { navigateToChat(chatRoom.id) },
        horizontalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        AsyncImage(
            model = chatRoom.picture,
            modifier = Modifier
                .width(50.dp)
                .height(50.dp)
                .clip(CircleShape)
                .background(White),
            contentScale = ContentScale.Crop,
            contentDescription = "Chat room picture",
            error = rememberVectorPainter(image = Icons.Filled.AccountCircle)
        )

        Text(text = chatRoom.title, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSecondary)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateChatRoomDialog(
    viewModel: HomeViewModel,
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
                    value = viewModel.dialogChatRoomTitle,
                    onValueChange = {
                        viewModel.onEvent(
                            HomeUiEvent.OnDialogTitleTextChanged(
                                it
                            )
                        )
                    },
                    singleLine = true,
                    isError = viewModel.isDialogChatRoomTitleError,
                    supportingText = {
                        if (viewModel.isDialogChatRoomTitleError) {
                            Text(
                                text = viewModel.dialogChatRoomTitleError,
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    },
                    trailingIcon = {
                        if (viewModel.isDialogChatRoomTitleError) {
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
                        if (viewModel.dialogChatRoomTitle.isNotBlank()) {
                            viewModel.onEvent(HomeUiEvent.CreateChatRoom)
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
private fun MainScreenTopBar(
    drawerState: DrawerState,
    logout: () -> Unit
) {

    val coroutineScopeTopBar = rememberCoroutineScope()

    Surface(shadowElevation = 8.dp) {
        TopAppBar(
            colors = topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary
            ),
            title = {
                Text(
                    text = stringResource(id = R.string.app_name),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            },

            actions = {
                IconButton(
                    onClick = { }) {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        contentDescription = "Search through contacts"
                    )
                }
            },
            navigationIcon = {
                IconButton(onClick = {
                    if (drawerState.isClosed) {
                        coroutineScopeTopBar.launch {
                            drawerState.open()
                        }
                    } else {
                        coroutineScopeTopBar.launch {
                            drawerState.close()
                        }
                    }
                }) {
                    Icon(
                        imageVector = Icons.Filled.Menu,
                        contentDescription = "Menu button",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        )

    }
}

@Composable
private fun DrawerContent(
    user: User
) {
    ModalDrawerSheet(modifier = Modifier.fillMaxHeight(), drawerShape = RectangleShape) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(24.dp)
        )

        Header(avatar = "", userName = user.username)
    }

}

@Composable
private fun Header(avatar: String, userName: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF020203))
            .height(144.dp)
    ) {

        Column(
            Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 48.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AsyncImage(
                model = avatar,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .padding(start = 16.dp)
                    .clip(CircleShape)
                    .width(40.dp)
                    .height(40.dp),
                contentDescription = "User Profile Picture"
            )

            Text(
                text = userName,
                modifier = Modifier.padding(start = 16.dp),
                style = TextStyle(
                    fontSize = 20.sp,
                    lineHeight = 24.sp,
                    color = White,
                    letterSpacing = 0.15.sp,
                )
            )
        }

    }

}