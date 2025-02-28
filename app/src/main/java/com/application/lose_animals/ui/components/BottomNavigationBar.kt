package com.application.lose_animals.ui.components

import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun BottomNavigationBar(
    currentDestination: String,
    onNavigateToProfile: () -> Unit,
    onNavigateToAddPerson: () -> Unit,
    onNavigateToPeople: () -> Unit,
    onNavigateToChat: () -> Unit,
    onNavigateToMap: () -> Unit,
    onNavigateToRecognition: () -> Unit,
    onSosClick: () -> Unit
) {
    NavigationBar {
        NavigationBarItem(
            icon = { Icon(Icons.Filled.People, contentDescription = "Люди") },
            label = { Text("Люди") },
            selected = currentDestination == "people",
            onClick = onNavigateToPeople
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Map, contentDescription = "Карта") },
            label = { Text("Карта") },
            selected = currentDestination == "map",
            onClick = onNavigateToMap
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Add, contentDescription = "Добавить") },
            label = { Text("Добавить") },
            selected = currentDestination == "addPerson",
            onClick = onNavigateToAddPerson
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.PhotoCamera, contentDescription = "Распознать") },
            label = { Text("Распознать") },
            selected = currentDestination == "recognition",
            onClick = onNavigateToRecognition
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Person, contentDescription = "Профиль") },
            label = { Text("Профиль") },
            selected = currentDestination == "profile",
            onClick = onNavigateToProfile
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Chat, contentDescription = "Чат") },
            label = { Text("Чат") },
            selected = currentDestination == "chat",
            onClick = onNavigateToChat
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Warning, contentDescription = "SOS", tint = Color.Red) },
            label = { Text("SOS", color = Color.Red) },
            selected = false,
            onClick = onSosClick
        )
    }
}
