package com.application.lose_animals.ui.components

import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun BottomNavigationBar(
    currentDestination: String,
    onNavigateToProfile: () -> Unit,
    onNavigateToAddPerson: () -> Unit,
    onNavigateToPeople: () -> Unit,
    onNavigateToChat: () -> Unit,
    onSosClick: () -> Unit
) {
    NavigationBar {
        NavigationBarItem(
            icon = { Icon(Icons.Filled.People, contentDescription = "People") },
            label = { Text("People") },
            selected = currentDestination == "people",
            onClick = onNavigateToPeople
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Person, contentDescription = "Profile") },
            label = { Text("Profile") },
            selected = currentDestination == "profile",
            onClick = onNavigateToProfile
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Add, contentDescription = "Add") },
            label = { Text("Add") },
            selected = currentDestination == "addPerson",
            onClick = onNavigateToAddPerson
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Chat, contentDescription = "Chat") },
            label = { Text("Chat") },
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
