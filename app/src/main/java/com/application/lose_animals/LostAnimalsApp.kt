package com.application.lose_animals

import AnimalDetailScreen
import AnimalListScreen
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.application.lose_animals.ui.screens.LoginScreen
import com.application.lose_animals.ui.screens.ProfileScreen
import com.application.lose_animals.ui.screens.RegisterScreen

@Composable
fun LostAnimalsApp() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "login") {
        composable("login") { LoginScreen(navController) }
        composable("register") { RegisterScreen(navController) }
        composable("animalList") { AnimalListScreen(navController) }
        composable("animalDetail/{animalId}") { backStackEntry ->
            val animalId = backStackEntry.arguments?.getString("animalId") ?: return@composable
            AnimalDetailScreen(animalId = animalId)
        }
//        composable("createAnnouncement") { CreateAnnouncementScreen(navController) }
        composable("profile") {
            ProfileScreen(onLogout = { navController.navigate("login") {
                popUpTo("profile") { inclusive = true }
                }
            })
        }
    //        composable("search") { SearchScreen(navController) }
    }
}
