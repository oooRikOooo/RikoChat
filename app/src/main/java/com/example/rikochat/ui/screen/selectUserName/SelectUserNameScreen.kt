package com.example.rikochat.ui.screen.selectUserName

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun SelectUserNameScreen(
    viewModel: SelectUserNameViewModel,
    navigateToChat: (String) -> Unit
) {

    val uiState = viewModel.uiState.collectAsStateWithLifecycle()

    when (uiState.value) {
        is SelectUserNameUiState.FailureLogin -> {

        }

        SelectUserNameUiState.Idle -> {}

        is SelectUserNameUiState.SuccessLogin -> {
            LaunchedEffect(key1 = Unit, block = {
                navigateToChat(viewModel.usernameText.value)
                viewModel.onEvent(SelectUserNameUiEvent.ScreenClosed)
            })
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp), contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.End
        ) {

            Text(text = "Choose your username", fontWeight = FontWeight.Bold, fontSize = 16.sp)

            TextField(
                value = viewModel.usernameText.value,
                onValueChange = { viewModel.onEvent(SelectUserNameUiEvent.OnUsernameChanged(it)) },
                modifier = Modifier.fillMaxWidth()
            )

            Button(onClick = { viewModel.onEvent(SelectUserNameUiEvent.OnJoinClick) }) {
                Text(text = "Save")
            }

        }

    }
}