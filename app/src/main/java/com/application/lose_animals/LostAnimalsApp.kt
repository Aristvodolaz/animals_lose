package com.application.lose_animals

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.application.lose_animals.ui.screens.*
import com.application.lose_animals.ui.viewModel.MainViewModel
import com.application.lose_animals.ui.components.BottomNavigationBar
import com.application.lose_animals.data.model.Person
import com.application.lose_animals.ui.components.SosButton

@Composable
fun LostAnimalsApp() {
    val navController = rememberNavController()
    val viewModel: MainViewModel = hiltViewModel()
    val isAuthenticated by viewModel.isAuthenticated.collectAsState()
    var currentDestination by remember { mutableStateOf("login") }

    LaunchedEffect(isAuthenticated) {
        println("Auth state changed: $isAuthenticated")
        if (isAuthenticated) {
            navController.navigate("profile") {
                popUpTo("login") { inclusive = true }
            }
        }
    }



    Scaffold(
        bottomBar = {
            if (isAuthenticated) {
                BottomNavigationBar(
                    currentDestination = currentDestination,
                    onNavigateToAddPerson = { navController.navigate("addPerson") },
                    onNavigateToProfile = { navController.navigate("profile") },
                    onNavigateToPeople = { navController.navigate("people") },
                    onNavigateToChat = { navController.navigate("chat") } ,
                    onSosClick = { navController.navigate("sos") } // Переход на SOS

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
                ProfileScreen(onLogout = {
                    navController.navigate("login") {
                        popUpTo("profile") { inclusive = true }
                    }
                }, onEditPerson = { person ->
                    navController.navigate("editPerson/${person.id}")
                })
            }
            composable("addPerson") {
                currentDestination = "addPerson"
                AddPersonScreen(onPersonAdded = {
                    navController.navigate("profile")
                })
            }
            composable("login") {
                currentDestination = "login"
                LoginScreen(
                    onLoginSuccess = {
                        viewModel.isAuthenticated
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
            composable("people") {
                currentDestination = "people"
                PersonListScreen(navController)
            }

            composable("sos") {
                SosButton(context = LocalContext.current, userId = "123", userName = "User")
            }
            composable("personDetail/{personId}") { backStackEntry ->
                currentDestination = "personDetail"
                val personId = backStackEntry.arguments?.getString("personId")
                if (personId != null) {
                    PersonDetailScreen(personId = personId)
                } else {
                    Text(text = "Person not found")
                }
            }
            composable("editPerson/{personId}") { backStackEntry ->
                currentDestination = "editPerson"
                val personId = backStackEntry.arguments?.getString("personId")
                if (personId != null) {
                    var person by remember { mutableStateOf<Person?>(null) }
                    LaunchedEffect(personId) {
                        person = viewModel.getPersonById(personId)
                    }
                    person?.let { person ->
                        EditPersonScreen(person = person) {
                            navController.navigate("profile")
                        }
                    } ?: run {
                        Text(text = "Loading person details...")
                    }
                }
            }
            composable("chat") { // Добавленный экран чата
                currentDestination = "chat"
                ChatScreen()
            }
        }
    }
}
