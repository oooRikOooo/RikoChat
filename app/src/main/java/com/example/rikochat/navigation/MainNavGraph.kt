package com.example.rikochat.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.example.rikochat.ui.screen.chat.ChatScreen
import com.example.rikochat.ui.screen.main.MainScreen
import com.example.rikochat.utils.navigation.popUpToInclusive
import org.koin.androidx.compose.getViewModel

fun NavGraphBuilder.mainNavGraph(navController: NavController) {
    navigation(startDestination = MainNav.MAIN_HOME_SCREEN, route = MainNav.MAIN_ROUTE) {
        composable(
            route = "${MainNav.CHAT_SCREEN}/{username}/{roomId}",
            arguments = listOf(
                navArgument(name = "username") {
                    type = NavType.StringType
                },
                navArgument(name = "roomId") {
                    type = NavType.StringType
                }
            )
        ) {
            val username = it.arguments?.getString("username")
            val roomId = it.arguments?.getString("roomId")
            ChatScreen(username = username!!, viewModel = getViewModel(), roomId = roomId!!)
        }

        composable(
            route = MainNav.MAIN_HOME_SCREEN
        ) {
            MainScreen(viewModel = getViewModel(), navigateToChat = { username, roomId ->
                navController.navigate("chat_screen/$username/$roomId")
            }, navigateToLogin = {
                navController.navigate(route = AuthNav.AUTH_ROUTE) {
                    popUpToInclusive(MainNav.MAIN_HOME_SCREEN)
                }
            })
        }
    }
}

object MainNav {
    const val MAIN_ROUTE = "main"
    const val MAIN_HOME_SCREEN = "home_screen"
    const val CHAT_SCREEN = "chat_screen"
}