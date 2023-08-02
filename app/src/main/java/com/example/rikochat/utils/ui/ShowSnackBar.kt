package com.example.rikochat.utils.ui

import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect

@Composable
fun ShowSnackBar(
    snackBarHostState: SnackbarHostState,
    text: String,
    onDismiss: () -> Unit
) {

    LaunchedEffect(snackBarHostState) {
        when (snackBarHostState.showSnackbar(message = text)) {
            SnackbarResult.Dismissed -> onDismiss()
            SnackbarResult.ActionPerformed -> {}
        }
    }

}