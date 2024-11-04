package com.application.lose_animals.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberImagePainter
import com.application.lose_animals.ui.components.CustomTextField
import com.application.lose_animals.ui.components.DropdownMenuField
import com.application.lose_animals.ui.viewModel.AddAnimalViewModel

@Composable
fun AddAnimalScreen(
    viewModel: AddAnimalViewModel = hiltViewModel(),
    onAnimalAdded: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var photoUrl by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("Lost") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            selectedImageUri = uri
            photoUrl = uri.toString()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Add a Lost/Found Animal",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Name Input
        CustomTextField(
            value = name,
            onValueChange = { name = it },
            label = "Animal Name"
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Description Input
        CustomTextField(
            value = description,
            onValueChange = { description = it },
            label = "Description"
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Location Input
        CustomTextField(
            value = location,
            onValueChange = { location = it },
            label = "Location"
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Image Preview
        selectedImageUri?.let { uri ->
            Image(
                painter = rememberImagePainter(uri),
                contentDescription = "Selected Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(bottom = 8.dp)
                    .clickable { /* Implement functionality if you want to change the image */ }
            )
        } ?: run {
            Text("No image selected", color = Color.Gray, modifier = Modifier.padding(bottom = 8.dp))
        }

        // Select Image Button
        Button(onClick = { launcher.launch("image/*") }, modifier = Modifier.fillMaxWidth()) {
            Text("Select Image")
        }
        Spacer(modifier = Modifier.height(8.dp))

        // Status Dropdown Menu
        DropdownMenuField(
            label = "Status",
            selectedItem = status,
            onItemSelected = { status = it },
            options = listOf("Lost", "Found")
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Add Animal Button
        Button(
            onClick = {
                viewModel.addAnimal(name, description, location, photoUrl.ifEmpty { null }, status) { success ->
                    if (success) {
                        onAnimalAdded()
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text("Add Animal", color = Color.White)
        }
    }
}
