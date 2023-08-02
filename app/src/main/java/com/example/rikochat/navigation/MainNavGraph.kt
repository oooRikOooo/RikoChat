package com.example.rikochat.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.example.rikochat.ui.screen.chat.ChatScreen
import com.example.rikochat.ui.screen.main.MainScreen
import org.koin.androidx.compose.getViewModel

fun NavGraphBuilder.mainNavGraph(navController: NavController) {
    navigation(startDestination = MainNav.MAIN_HOME_SCREEN, route = MainNav.MAIN_ROUTE) {
        composable(
            route = "${MainNav.CHAT_SCREEN}/{username}",
            arguments = listOf(
                navArgument(name = "username") {
                    type = NavType.StringType
                }
            )
        ) {
            val username = it.arguments?.getString("username")
            ChatScreen(username = username!!, viewModel = getViewModel())
        }

        composable(
            route = MainNav.MAIN_HOME_SCREEN
        ) {
            MainScreen(navigateToChat = {
                navController.navigate("chat_screen/$it")
            })
        }
    }
}

object MainNav {
    const val MAIN_ROUTE = "main"
    const val MAIN_HOME_SCREEN = "home_screen"
    const val CHAT_SCREEN = "chat_screen"
}