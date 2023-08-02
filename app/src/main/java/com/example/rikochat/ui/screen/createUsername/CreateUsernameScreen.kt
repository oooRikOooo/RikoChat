package com.example.rikochat.ui.screen.createUsername

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.rikochat.utils.lifecycle.DisposableEffectWithLifeCycle
import com.example.rikochat.utils.ui.LoadingProgressIndicator
import com.example.rikochat.utils.ui.ShowSnackBar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

@Composable
fun CreateUsernameScreen(
    viewModel: CreateUsernameViewModel,
    snackBarHostState: SnackbarHostState,
    navigateToLoginScreen: () -> Unit
) {

    val uiState = viewModel.uiState.collectAsStateWithLifecycle()

    when (val state = uiState.value) {

        is CreateUsernameUiState.FailedCreate -> {
            viewModel.onEvent(CreateUsernameUiEvent.ShowSnackBar(state.error))
        }

        CreateUsernameUiState.Idle -> {}

        CreateUsernameUiState.Loading -> LoadingProgressIndicator()

        CreateUsernameUiState.SuccessfulCreate -> {
            LaunchedEffect(
                key1 = Unit,
                block = {
                    navigateToLoginScreen()
                    viewModel.onEvent(CreateUsernameUiEvent.CreateUsernameScreenIsClosed)
                }
            )
        }

    }

    DisposableEffectWithLifeCycle(
        onStop = {
            Firebase.auth.currentUser?.let {
                Firebase.auth.signOut()
            }

            navigateToLoginScreen()
        },
        onDestroy = {
            Firebase.auth.currentUser?.let {
                Firebase.auth.signOut()
            }
        }, onDispose = {
            Firebase.auth.currentUser?.let {
                Firebase.auth.signOut()
            }
        }
    )

    Box(
        modifier = Modifier
            .padding(50.dp)
            .fillMaxSize()
    ) {
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center),
            value = viewModel.username,
            onValueChange = {
                viewModel.onEvent(CreateUsernameUiEvent.OnUsernameTextChanged(it))
            },
            label = { Text(text = "Username") }
        )

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
            onClick = {
                if (viewModel.username.length < 3) {
                    viewModel.onEvent(CreateUsernameUiEvent.ShowSnackBar("Username is too short"))
                    return@Button
                }

                viewModel.onEvent(CreateUsernameUiEvent.CreateUserName)
            }
        )
        {
            Text(text = "Create username")
        }
    }

    if (viewModel.showSnackBar) {
        ShowSnackBar(
            snackBarHostState = snackBarHostState,
            text = viewModel.snackBarText
        ) { viewModel.onEvent(CreateUsernameUiEvent.HideSnackBar) }
    }
}