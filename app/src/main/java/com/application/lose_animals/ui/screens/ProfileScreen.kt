package com.application.lose_animals.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.application.lose_animals.data.model.Animal
import com.application.lose_animals.ui.components.AnimalCard
import com.application.lose_animals.ui.viewModel.ProfileViewModel

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel(),
    onLogout: () -> Unit
) {
    val user by viewModel.user.collectAsState()
    val userAnimals by viewModel.userAnimals.collectAsState()

    Column(modifier = Modifier.padding(16.dp)) {
        user?.let {
            Text(text = "Username: ${it.username}", style = MaterialTheme.typography.titleMedium)
            Text(text = "Email: ${it.email}", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                viewModel.logout()
                onLogout()
            }) {
                Text("Logout")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "Your Ads", style = MaterialTheme.typography.titleMedium)
            LazyColumn {
                items(userAnimals) { animal ->
                    AnimalCard(animal = animal, onClick = { /* Переход к детальной информации */ })
                }
            }
        } ?: run {
            Text(text = "Loading profile...", modifier = Modifier.padding(16.dp))
        }
    }
}
