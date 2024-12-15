package com.application.lose_animals.ui.screens


import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.application.lose_animals.data.model.Person
import com.application.lose_animals.ui.viewModel.PersonDetailViewModel

@Composable
fun PersonDetailScreen(personId: String, viewModel: PersonDetailViewModel = hiltViewModel()) {
    var person by remember { mutableStateOf<Person?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(personId) {
        person = viewModel.getPersonById(personId)
        isLoading = false
    }

    if (isLoading) {
        Text(text = "Loading...", modifier = Modifier.padding(16.dp))
    } else {
        person?.let { personData ->
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Name: ${personData.name}", style = MaterialTheme.typography.titleMedium)
                Text(text = "Description: ${personData.description}", style = MaterialTheme.typography.bodyMedium)
                Text(text = "Last Seen Location: ${personData.lastSeenLocation}", style = MaterialTheme.typography.bodyMedium)
                // Add more fields if needed
            }
        } ?: run {
            Text(text = "Person not found", modifier = Modifier.padding(16.dp))
        }
    }
}