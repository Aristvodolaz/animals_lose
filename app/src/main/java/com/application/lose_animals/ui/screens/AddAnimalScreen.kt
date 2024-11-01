package com.application.lose_animals.ui.screens
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberImagePainter
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

    // Launcher для выбора изображения
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            selectedImageUri = uri
            photoUrl = uri.toString() // Сохраняем URI как строку для хранения
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        TextField(value = name, onValueChange = { name = it }, label = { Text("Name") })
        Spacer(modifier = Modifier.height(8.dp))
        TextField(value = description, onValueChange = { description = it }, label = { Text("Description") })
        Spacer(modifier = Modifier.height(8.dp))
        TextField(value = location, onValueChange = { location = it }, label = { Text("Location") })
        Spacer(modifier = Modifier.height(8.dp))

        // Отображение выбранного изображения
        selectedImageUri?.let { uri ->
            Image(
                painter = rememberImagePainter(uri),
                contentDescription = "Selected Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(bottom = 8.dp)
                    .clickable {
                        // Добавьте функциональность, если хотите изменить изображение
                    }
            )
        }

        // Кнопка для выбора изображения
        Button(onClick = { launcher.launch("image/*") }) {
            Text("Select Image")
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextField(value = status, onValueChange = { status = it }, label = { Text("Status (Lost/Found)") })
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            viewModel.addAnimal(name, description, location, photoUrl.ifEmpty { null }, status) { success ->
                if (success) {
                    onAnimalAdded()
                }
            }
        }) {
            Text("Add Animal")
        }
    }
}
