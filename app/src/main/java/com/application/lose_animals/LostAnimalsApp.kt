package com.application.lose_animals

import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.application.lose_animals.data.model.Animal
import com.application.lose_animals.ui.screens.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@Composable
fun LostAnimalsApp() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "profile") {
        composable("profile") {
            ProfileScreen(navController = navController, onLogout = {
                navController.navigate("login") {
                    popUpTo("profile") { inclusive = true }
                }
            })
        }
        composable("addAnimal") {
            AddAnimalScreen(onAnimalAdded = {
                navController.navigate("profile") // Переход на профиль после добавления
            })
        }
        composable("login") {
            LoginScreen(onLoginSuccess = { navController.navigate("profile") })
        }
        composable("register") {
            RegisterScreen(onRegistrationSuccess = { navController.navigate("login") })
        }
        composable("editAnimal/{animalId}") { backStackEntry ->
            val animalId = backStackEntry.arguments?.getString("animalId")
            if (animalId != null) {
                // Используем State для хранения животного
                var animal by remember { mutableStateOf<Animal?>(null) }

                animal?.let { animal ->
                    EditAnimalScreen(animal = animal) {
                        navController.navigate("profile")
                    }
                } ?: run {
                    // Загрузка изображения, пока не получен объект animal
                    Text(text = "Loading animal details...")
                }
            }
        }
    }
}
