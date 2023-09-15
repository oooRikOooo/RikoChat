package com.example.rikochat

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.remember
import androidx.navigation.compose.rememberNavController
import com.example.rikochat.navigation.RootNavGraph
import com.example.rikochat.ui.theme.RikoChatTheme
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
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
                        isUserLoggedIn = Firebase.auth.currentUser != null
                    )
                }

            }
        }
    }
}
