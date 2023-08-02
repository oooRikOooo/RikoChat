package com.example.rikochat.ui.screen.registration

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.rikochat.utils.isValidEmail
import com.example.rikochat.utils.ui.LoadingProgressIndicator
import com.example.rikochat.utils.ui.ShowSnackBar

@Composable
fun RegistrationScreen(
    viewModel: RegistrationViewModel,
    snackBarHostState: SnackbarHostState,
    navigateToLogin: () -> Unit
) {

    val uiState = viewModel.uiState.collectAsStateWithLifecycle()

    var passwordVisible by remember { mutableStateOf(false) }
    val localFocusManager = LocalFocusManager.current

    when (val state = uiState.value) {
        is RegistrationUiState.FailedRegistration -> {
            viewModel.onEvent(RegistrationUiEvent.ShowSnackBar(state.error))
        }

        RegistrationUiState.Idle -> {}
        RegistrationUiState.SuccessfulRegister -> {
            LaunchedEffect(
                key1 = Unit,
                block = {
                    navigateToLogin()
                }
            )

        }

        RegistrationUiState.Loading -> LoadingProgressIndicator()
    }

    Box(
        modifier = Modifier
            .padding(50.dp)
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .padding(bottom = 10.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = viewModel.username,
                onValueChange = {
                    viewModel.onEvent(RegistrationUiEvent.OnUserNameTextChanged(it))
                },
                label = {
                    Text(text = "Username")
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(
                    onNext = {
                        localFocusManager.moveFocus(FocusDirection.Down)
                    }
                ),
                singleLine = true,
            )

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = viewModel.email,
                onValueChange = {
                    viewModel.onEvent(RegistrationUiEvent.OnEmailTextChanged(it))
                },
                label = {
                    Text(text = "Email")
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(
                    onNext = {
                        localFocusManager.moveFocus(FocusDirection.Down)
                    }
                ),
                singleLine = true,
            )

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = viewModel.password,
                onValueChange = {
                    viewModel.onEvent(RegistrationUiEvent.OnPasswordTextChanged(it))
                },
                label = {
                    Text(text = "Password")
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val icon = if (passwordVisible)
                        Icons.Filled.Visibility
                    else Icons.Filled.VisibilityOff

                    val description = if (passwordVisible)
                        "Hide password"
                    else "Show password"

                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector = icon, description)
                    }

                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        localFocusManager.clearFocus()
                    }
                ),
                singleLine = true,
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
            verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterVertically),
        ) {
            Button(
                modifier = Modifier
                    .fillMaxWidth(),
                onClick = {
                    if (viewModel.username.length < 3) {
                        viewModel.onEvent(RegistrationUiEvent.ShowSnackBar("Username is too short"))
                        return@Button
                    }

                    if (!isValidEmail(viewModel.email)) {
                        viewModel.onEvent(RegistrationUiEvent.ShowSnackBar("Invalid email"))
                        return@Button
                    }

                    if (viewModel.password.length < 6) {
                        viewModel.onEvent(RegistrationUiEvent.ShowSnackBar("Password is too short"))
                        return@Button
                    }

                    viewModel.onEvent(RegistrationUiEvent.Register)
                }
            ) {
                Text(text = "Create account")
            }

            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { navigateToLogin() },
                text = "Already have an account?\nClick to log in",
                textAlign = TextAlign.End,
                fontSize = 12.sp
            )

        }
    }

    if (viewModel.showSnackBar) {
        ShowSnackBar(
            snackBarHostState = snackBarHostState,
            text = viewModel.snackBarText
        ) { viewModel.onEvent(RegistrationUiEvent.HideSnackBar) }
    }

}