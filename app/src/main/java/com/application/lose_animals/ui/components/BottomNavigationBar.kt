package com.application.lose_animals.ui.components

import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.People
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun BottomNavigationBar(
    currentDestination: String,
    onNavigateToProfile: () -> Unit,
    onNavigateToAddPerson: () -> Unit,
    onNavigateToPeople: () -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = Color.White
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Filled.People, contentDescription = "People") },
            label = { Text("People") },
            selected = currentDestination == "people",
            onClick = onNavigateToPeople,
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFFFFD700),
                selectedTextColor = Color(0xFFFFD700),
                unselectedIconColor = Color.White,
                unselectedTextColor = Color.White
            )
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Add, contentDescription = "Add Person") },
            label = { Text("Add Person") },
            selected = currentDestination == "addPerson",
            onClick = onNavigateToAddPerson,
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFFFFD700),
                selectedTextColor = Color(0xFFFFD700),
                unselectedIconColor = Color.White,
                unselectedTextColor = Color.White
            )
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Person, contentDescription = "Profile") },
            label = { Text("Profile") },
            selected = currentDestination == "profile",
            onClick = onNavigateToProfile,
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF6750A3),
                selectedTextColor = Color(0xFFFBB63E),
                unselectedIconColor = Color.White,
                unselectedTextColor = Color.White
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun BottomNavigationBarPreview() {
    BottomNavigationBar(
        currentDestination = "profile",
        onNavigateToProfile = {},
        onNavigateToAddPerson = {},
        onNavigateToPeople = {}
    )
}