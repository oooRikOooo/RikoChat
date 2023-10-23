package com.example.rikochat.navigation

import android.util.Log
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.example.rikochat.ui.screen.chat.ChatScreen
import com.example.rikochat.ui.screen.home.HomeScreen
import com.example.rikochat.utils.navigation.popUpToInclusive
import org.koin.androidx.compose.getViewModel

fun NavGraphBuilder.mainNavGraph(navController: NavController) {
    navigation(startDestination = MainNav.MAIN_HOME_SCREEN, route = MainNav.MAIN_ROUTE) {
        composable(
            route = "${MainNav.CHAT_SCREEN}/{roomId}",
            arguments = listOf(
                navArgument(name = "roomId") {
                    type = NavType.StringType
                }
            ),
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Companion.Left,
                    animationSpec = tween(700)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Companion.Right,
                    animationSpec = tween(700)
                )
            },
        ) {
            val roomId = it.arguments?.getString("roomId")
            ChatScreen(
                viewModel = getViewModel(),
                roomId = roomId!!
            )
        }

        composable(
            route = MainNav.MAIN_HOME_SCREEN
        ) {
            Log.d("riko", "composable HOME SCREEN")
            HomeScreen(
                viewModel = getViewModel(),
                navigateToChat = { roomId ->
                    navController.navigate("chat_screen/$roomId")
                },
                navigateToLogin = {
                    navController.navigate(route = AuthNav.AUTH_ROUTE) {
                        popUpToInclusive(MainNav.MAIN_HOME_SCREEN)
                    }
                }
            )
        }

    }
}

object MainNav {
    const val MAIN_ROUTE = "main"
    const val MAIN_HOME_SCREEN = "home_screen"
    const val CHAT_SCREEN = "chat_screen"
}