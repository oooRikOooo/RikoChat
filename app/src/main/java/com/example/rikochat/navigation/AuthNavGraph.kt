package com.example.rikochat.navigation

import androidx.compose.material3.SnackbarHostState
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.rikochat.ui.screen.createUsername.CreateUsernameScreen
import com.example.rikochat.ui.screen.login.LoginScreen
import com.example.rikochat.ui.screen.registration.RegistrationScreen
import com.example.rikochat.utils.navigation.popUpToInclusive
import org.koin.androidx.compose.getViewModel

fun NavGraphBuilder.authNavGraph(
    navController: NavController,
    snackBarHostState: SnackbarHostState
) {
    navigation(startDestination = AuthNav.REGISTRATION_SCREEN, route = AuthNav.AUTH_ROUTE) {

        composable(route = AuthNav.REGISTRATION_SCREEN) {
            RegistrationScreen(
                viewModel = getViewModel(),
                snackBarHostState = snackBarHostState,
                navigateToLogin = {
                    navController.navigate(route = AuthNav.LOGIN_SCREEN) {
                        popUpToInclusive(AuthNav.REGISTRATION_SCREEN)
                    }
                })
        }

        composable(route = AuthNav.LOGIN_SCREEN) {
            LoginScreen(
                viewModel = getViewModel(),
                snackBarHostState = snackBarHostState,
                navigateToMainScreen = {
                    navController.navigate(route = MainNav.MAIN_ROUTE) {
                        popUpToInclusive(AuthNav.LOGIN_SCREEN)
                    }
                },
                navigateToRegisterScreen = {
                    navController.navigate(route = AuthNav.REGISTRATION_SCREEN) {
                        popUpToInclusive(AuthNav.LOGIN_SCREEN)
                    }
                },
                navigateToCreateUsernameScreen = {
                    navController.navigate(route = AuthNav.CREATE_USERNAME_SCREEN) {
                        popUpToInclusive(AuthNav.LOGIN_SCREEN)
                    }
                })
        }

        composable(route = AuthNav.CREATE_USERNAME_SCREEN) {
            CreateUsernameScreen(
                viewModel = getViewModel(),
                snackBarHostState = snackBarHostState,
                navigateToLoginScreen = {
                    navController.navigate(route = AuthNav.LOGIN_SCREEN) {
                        popUpToInclusive(AuthNav.CREATE_USERNAME_SCREEN)
                    }
                })
        }

    }
}

object AuthNav {
    const val AUTH_ROUTE = "auth"
    const val REGISTRATION_SCREEN = "registration"
    const val LOGIN_SCREEN = "login"
    const val CREATE_USERNAME_SCREEN = "create_username"
}