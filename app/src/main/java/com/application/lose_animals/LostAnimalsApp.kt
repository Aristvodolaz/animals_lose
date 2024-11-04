package com.application.lose_animals

import AnimalListScreen
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.application.lose_animals.data.model.Animal
import com.application.lose_animals.ui.screens.*
import com.application.lose_animals.ui.viewModel.MainViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import androidx.compose.material3.*
import com.application.lose_animals.ui.components.BottomNavigationBar


@Composable
fun LostAnimalsApp() {
    val navController = rememberNavController()
    val viewModel: MainViewModel = hiltViewModel()
    val isAuthenticated by viewModel.isAuthenticated.collectAsState()
    var currentDestination by remember { mutableStateOf("login") }

    Scaffold(
        bottomBar = {
            if (isAuthenticated) {
                BottomNavigationBar(
                    currentDestination = currentDestination,
                    onNavigateToAddAnimal = { navController.navigate("addPets") },
                    onNavigateToProfile = { navController.navigate("profile") },
                    onNavigateToPets = { navController.navigate("pets") },
                    )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = if (isAuthenticated) "profile" else "login",
            Modifier.padding(innerPadding)
        ) {
            composable("profile") {
                currentDestination = "profile"
                ProfileScreen(navController = navController, onLogout = {
                    navController.navigate("login") {
                        popUpTo("profile") { inclusive = true }
                    }
                })
            }
            composable("addPets") {
                currentDestination = "addPets"
                AddAnimalScreen(onAnimalAdded = {
                    navController.navigate("addPets")
                })
            }
            composable("login") {
                currentDestination = "login"
                LoginScreen(
                    onLoginSuccess = {
                        navController.navigate("profile") {
                            popUpTo("login") { inclusive = true }
                        }
                    },
                    onNavigateToRegister = {
                        navController.navigate("register")
                    }
                )
            }
            composable("register") {
                currentDestination = "register"
                RegisterScreen(
                    onRegistrationSuccess = {
                        navController.navigate("login") {
                            popUpTo("register") { inclusive = true }
                        }
                    },
                    onNavigateToLogin = {
                        navController.navigate("login")
                    }
                )
            }
            composable("editAnimal/{animalId}") { backStackEntry ->
                currentDestination = "editAnimal"
                val animalId = backStackEntry.arguments?.getString("animalId")
                if (animalId != null) {
                    var animal by remember { mutableStateOf<Animal?>(null) }
                    animal?.let { animal ->
                        EditAnimalScreen(animal = animal) {
                            navController.navigate("profile")
                        }
                    } ?: run {
                        Text(text = "Loading animal details...")
                    }
                }
            }
            composable("pets") {
                currentDestination = "pets"
                AnimalListScreen(navController)
            }
        }
    }
}
