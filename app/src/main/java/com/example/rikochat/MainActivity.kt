package com.example.rikochat

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.rikochat.ui.screen.chat.ChatScreen
import com.example.rikochat.ui.screen.selectUser.SelectUserScreen
import com.example.rikochat.ui.theme.RikoChatTheme
import org.koin.androidx.viewmodel.ext.android.getViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RikoChatTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = "select_user") {
                        composable(route = "select_user") {
                            SelectUserScreen(
                                viewModel = getViewModel(),
                                navigateToChat = {
                                    navController.navigate("chat_screen/$it")
                                }
                            )
                        }

                        composable(
                            route = "chat_screen/{username}",
                            arguments = listOf(
                                navArgument(name = "username") {
                                    type = NavType.StringType
                                }
                            )
                        ) {
                            val username = it.arguments?.getString("username")
                            ChatScreen(username = username!!, viewModel = getViewModel())
                        }

                    }
                }
            }
        }
    }
}
