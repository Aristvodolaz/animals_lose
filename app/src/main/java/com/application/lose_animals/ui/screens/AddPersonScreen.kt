package com.application.lose_animals.ui.screens

import android.annotation.SuppressLint
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import com.application.lose_animals.ui.viewModel.AddPersonViewModel

@SuppressLint("UnrememberedMutableState")
@Composable
fun AddPersonScreen(
    viewModel: AddPersonViewModel = hiltViewModel(),
    onPersonAdded: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var lastSeenLocation by remember { mutableStateOf("") }
    var photoUrl by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("Missing") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    val isFormValid by derivedStateOf {
        name.isNotBlank() &&
                description.isNotBlank() &&
                lastSeenLocation.isNotBlank() &&
                selectedImageUri != null
    }

    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            selectedImageUri = uri
            photoUrl = uri.toString()
        }
    }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Add Missing Person",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        CustomTextField(
            value = name,
            onValueChange = { name = it },
            label = "Full Name"
        )
        Spacer(modifier = Modifier.height(8.dp))

        CustomTextField(
            value = description,
            onValueChange = { description = it },
            label = "Description"
        )
        Spacer(modifier = Modifier.height(8.dp))

        CustomTextField(
            value = lastSeenLocation,
            onValueChange = { lastSeenLocation = it },
            label = "Last Seen Location"
        )
        Spacer(modifier = Modifier.height(8.dp))

        if (selectedImageUri != null) {
            Image(
                painter = rememberImagePainter(selectedImageUri),
                contentDescription = "Selected Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(bottom = 8.dp)
                    .clickable {}
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(Color.Gray.copy(alpha = 0.3f))
                    .clickable { launcher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Tap to select an image",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        DropdownMenuField(
            label = "Status",
            selectedItem = status,
            onItemSelected = { status = it },
            options = listOf("Missing", "Found")
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                isLoading = true
                viewModel.addPerson(name, description, lastSeenLocation, photoUrl.ifEmpty { null }, status) { success ->
                    if (success) {
                        onPersonAdded()
                    }
                    isLoading = false
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = isFormValid,
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isFormValid) MaterialTheme.colorScheme.primary else Color.Gray
            )
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Text("Add Person", color = Color.White)
            }
        }

        if (!isFormValid) {
            Text(
                text = "Please fill out all fields and select an image.",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}
