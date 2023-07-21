package com.example.rikochat.ui.screen.selectUser

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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun SelectUserScreen(
    viewModel: SelectUserViewModel,
    navigateToChat: (String) -> Unit
) {

    val uiState = viewModel.uiState.collectAsStateWithLifecycle()

    when (uiState.value) {
        is SelectUserUiState.FailureLogin -> {
        }

        SelectUserUiState.Idle -> {}

        is SelectUserUiState.SuccessLogin -> {
            LaunchedEffect(key1 = Unit, block = {
                navigateToChat(viewModel.usernameText.value)
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
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.End
        ) {
            TextField(
                value = viewModel.usernameText.value,
                onValueChange = { viewModel.onEvent(SelectUserUiEvent.OnUsernameChanged(it)) },
                modifier = Modifier.fillMaxWidth()
            )

            Button(onClick = { viewModel.onEvent(SelectUserUiEvent.OnJoinClick) }) {
                Text(text = "Join")
            }

        }

    }
}