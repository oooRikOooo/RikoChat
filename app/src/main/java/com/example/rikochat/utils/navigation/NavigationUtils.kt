package com.example.rikochat.utils.navigation

import androidx.navigation.NavOptionsBuilder
import androidx.navigation.PopUpToBuilder

fun NavOptionsBuilder.popUpToInclusive(
    route: String,
    popUpToBuilder: PopUpToBuilder.() -> Unit = {}
) {
    popUpTo(route = route) {
        popUpToBuilder(this)
        inclusive = true
    }
}