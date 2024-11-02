package com.application.lose_animals.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.application.lose_animals.data.model.Animal
import com.application.lose_animals.ui.viewModel.EditAnimalViewModel

@Composable
fun EditAnimalScreen(
    animal: Animal,
    viewModel: EditAnimalViewModel = hiltViewModel(),
    onAnimalUpdated: () -> Unit
) {
    var name by remember { mutableStateOf(animal.name) }
    var description by remember { mutableStateOf(animal.description) }
    var location by remember { mutableStateOf(animal.location) }
    var status by remember { mutableStateOf(animal.status) }
    var photoUrl by remember { mutableStateOf(animal.photoUrl ?: "") }

    Column(modifier = Modifier.padding(16.dp)) {
        TextField(value = name, onValueChange = { name = it }, label = { Text("Name") })
        Spacer(modifier = Modifier.height(8.dp))
        TextField(value = description, onValueChange = { description = it }, label = { Text("Description") })
        Spacer(modifier = Modifier.height(8.dp))
        TextField(value = location, onValueChange = { location = it }, label = { Text("Location") })
        Spacer(modifier = Modifier.height(8.dp))
        TextField(value = status, onValueChange = { status = it }, label = { Text("Status") })
        Spacer(modifier = Modifier.height(8.dp))
        TextField(value = photoUrl, onValueChange = { photoUrl = it }, label = { Text("Photo URL") })
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            val updatedAnimal = animal.copy(name = name, description = description, location = location, status = status, photoUrl = photoUrl)
            viewModel.updateAnimal(updatedAnimal) { success ->
                if (success) {
                    onAnimalUpdated()
                }
            }
        }) {
            Text("Update Animal")
        }
    }
}
