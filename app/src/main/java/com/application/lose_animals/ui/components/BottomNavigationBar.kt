package com.application.lose_animals.ui.components

import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview


@Composable
fun BottomNavigationBar(
    currentDestination: String,
    onNavigateToProfile: () -> Unit,
    onNavigateToAddAnimal: () -> Unit,
    onNavigateToPets: () -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = Color.White
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Pets, contentDescription = "Pets") },
            label = { Text("Pets") },
            selected = currentDestination == "pets",
            onClick = onNavigateToPets
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Add, contentDescription = "Add Pets") },
            label = { Text("Add Pets") },
            selected = currentDestination == "addPets",
            onClick = onNavigateToAddAnimal
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Person, contentDescription = "Profile") },
            label = { Text("Profile") },
            selected = currentDestination == "profile",
            onClick = onNavigateToProfile
        )
    }
}


@Preview(showBackground = true)
@Composable
fun BottomNavigationBarPreview() {
    BottomNavigationBar(
        currentDestination = "profile",
        onNavigateToProfile = {},
        onNavigateToAddAnimal = {},
        onNavigateToPets = {}
    )
}
