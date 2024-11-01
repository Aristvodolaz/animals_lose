package com.application.lose_animals.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.application.lose_animals.data.model.Animal
import com.application.lose_animals.ui.components.AnimalCard
import com.application.lose_animals.ui.viewmodel.ProfileViewModel // Импортируем ProfileViewModel

@Composable
fun ProfileScreen(
    navController: NavHostController,
    onLogout: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel() // Инъектируем ProfileViewModel
) {
    val user by viewModel.user.collectAsState()
    val userAnimals by viewModel.userAnimals.collectAsState()

    Column(modifier = Modifier.padding(16.dp)) {
        user?.let {
            Text(text = "Username: ${it.username}", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn {
                items(userAnimals) { animal ->
                    AnimalCard(animal = animal, onClick = {
                        // Переход к экрану редактирования
                        navController.navigate("editAnimal/${animal.id}")
                    })
                }
            }
        } ?: run {
            Text(text = "Loading profile...", modifier = Modifier.padding(16.dp))
        }
    }
}
