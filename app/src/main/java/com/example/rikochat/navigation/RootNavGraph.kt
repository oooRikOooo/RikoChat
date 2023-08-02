package com.example.rikochat.navigation

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost

@Composable
fun RootNavGraph(
    navController: NavHostController,
    snackBarHostState: SnackbarHostState,
    isUserLoggedIn: Boolean
) {
    NavHost(
        navController = navController,
        startDestination = if (isUserLoggedIn) MainNav.MAIN_ROUTE else AuthNav.AUTH_ROUTE
    ) {
        authNavGraph(navController = navController, snackBarHostState = snackBarHostState)

        mainNavGraph(navController = navController)
    }

}