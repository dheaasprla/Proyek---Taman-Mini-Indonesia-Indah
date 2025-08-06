package com.example.proyektmii.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.proyektmii.ui.screens.EntranceScreen
import com.example.proyektmii.ui.screens.DetailScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "entrance"
    ) {
        composable("entrance") {
            EntranceScreen { cardId ->
                navController.navigate("detail/$cardId")
            }
        }

        composable(
            route = "detail/{cardId}",
            arguments = listOf(navArgument("cardId") {
                type = NavType.StringType
            })
        ) { backStack ->
            val id = backStack.arguments?.getString("cardId")!!
            DetailScreen(cardId = id)
        }
    }
}
