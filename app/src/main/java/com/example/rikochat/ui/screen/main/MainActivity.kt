package com.example.rikochat.ui.screen.main

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.example.rikochat.navigation.RootNavGraph
import com.example.rikochat.ui.theme.RikoChatTheme
import com.example.rikochat.utils.ui.LoadingProgressIndicator
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {

    private val viewModel by viewModel<MainViewModel>()

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            val state = viewModel.state.collectAsStateWithLifecycle()

            when (val stateValue = state.value) {
                MainUiState.Idle -> {}

                MainUiState.Loading -> {
                    LoadingProgressIndicator()
                }

                is MainUiState.SuccessTokenFetch -> {
                    RikoChatTheme {

                        val navHostController = rememberNavController()
                        val snackBarHostState = remember { SnackbarHostState() }

                        Scaffold(
                            snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
                            containerColor = MaterialTheme.colorScheme.secondary
                        ) {
                            RootNavGraph(
                                navController = navHostController,
                                snackBarHostState = snackBarHostState,
                                isUserLoggedIn = stateValue.token.isNotEmpty()
                            )
                        }

                    }
                }
            }

        }
    }
}
