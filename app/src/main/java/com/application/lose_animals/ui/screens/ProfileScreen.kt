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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.application.lose_animals.data.model.User
import com.application.lose_animals.ui.components.PersonCard
import com.application.lose_animals.ui.viewModel.ProfileViewModel

@Composable
fun ProfileScreen(
    navController: NavController,
    onLogout: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val user by viewModel.user.collectAsState()
    val userPersons by viewModel.userPersons.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(MaterialTheme.colorScheme.background)
    ) {

        HeaderSection(user = user)

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(userPersons) { person ->
                PersonCard(person = person, onClick = {
                    navController.navigate("editPerson/${person.id}")
                })
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text("Выйти", color = Color.White)
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
        // Заглушка для фото профиля (если необходимо добавить картинку, раскомментируйте)
        /*
        Image(
            painter = painterResource(id = R.drawable.ic_profile_placeholder), // Placeholder image
            contentDescription = "Фото профиля",
            modifier = Modifier
                .size(100.dp)
                .align(Alignment.CenterHorizontally)
        )
        */

        Spacer(modifier = Modifier.height(8.dp))

        // Имя пользователя
        Text(
            text = user?.username ?: "Загрузка...",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Email пользователя
        Text(
            text = user?.email ?: "",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}
