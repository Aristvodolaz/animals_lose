package com.application.lose_animals.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.application.lose_animals.data.model.Animal
import com.application.lose_animals.ui.viewModel.AnimalDetailViewModel

@Composable
fun AnimalDetailScreen(animalId: String, viewModel: AnimalDetailViewModel = hiltViewModel()) {
    
    var animal by remember { mutableStateOf<Animal?>(null) }
    var isLoading by remember { mutableStateOf(true) }


    LaunchedEffect(animalId) {
        animal = viewModel.getAnimalById(animalId)
        isLoading = false
    }


    if (isLoading) {
        Text(text = "Loading...", modifier = Modifier.padding(16.dp))
    } else {
        animal?.let { animalData ->
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Name: ${animalData.name}", style = MaterialTheme.typography.titleMedium)
                Text(text = "Description: ${animalData.description}", style = MaterialTheme.typography.bodyMedium)
                Text(text = "Location: ${animalData.location}", style = MaterialTheme.typography.bodyMedium)
                // Add more fields if needed
            }
        } ?: run {
            Text(text = "Animal not found", modifier = Modifier.padding(16.dp))
        }
    }
}
