package com.application.lose_animals.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.application.lose_animals.R
import com.application.lose_animals.data.model.User
import com.application.lose_animals.ui.components.AnimalCard
import com.application.lose_animals.ui.viewModel.ProfileViewModel

@Composable
fun ProfileScreen(
    navController: NavHostController,
    onLogout: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val user by viewModel.user.collectAsState()
    val userAnimals by viewModel.userAnimals.collectAsState()

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)
        .background(MaterialTheme.colorScheme.background)) {

        // Profile Header
        HeaderSection(user = user)

        Spacer(modifier = Modifier.height(16.dp))

        // Animals List
        LazyColumn {
            items(userAnimals) { animal ->
                AnimalCard(animal = animal, onClick = {
                    navController.navigate("editAnimal/${animal.id}")
                })
            }
        }

        // Logout Button
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text("Logout", color = Color.White)
        }
    }
}

@Composable
fun HeaderSection(user: User?) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primaryContainer, shape = MaterialTheme.shapes.medium)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Profile Picture Placeholder
//        Image(
////            painter = painterResource(id = R.drawable.ic_profile_placeholder), // Placeholder image
//            contentDescription = "Profile Picture",
//            modifier = Modifier
//                .size(100.dp)
//                .align(Alignment.CenterHorizontally)
//        )

        Spacer(modifier = Modifier.height(8.dp))

        // User Name
        Text(
            text = user?.username ?: "Loading...",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )

        Spacer(modifier = Modifier.height(4.dp))

        // User Email
        Text(
            text = user?.email ?: "",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}
