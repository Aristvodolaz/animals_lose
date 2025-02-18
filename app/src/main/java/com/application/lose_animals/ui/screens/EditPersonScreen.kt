package com.application.lose_animals.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.application.lose_animals.data.model.Person
import com.application.lose_animals.ui.components.CustomTextField
import com.application.lose_animals.ui.components.DropdownMenuField
import com.application.lose_animals.ui.viewModel.EditPersonViewModel
import com.google.firebase.auth.FirebaseAuth

@SuppressLint("UnrememberedMutableState")
@Composable
fun EditPersonScreen(
    person: Person,
    viewModel: EditPersonViewModel = hiltViewModel(),
    onPersonUpdated: () -> Unit
) {
    var name by remember { mutableStateOf(person.name) }
    var description by remember { mutableStateOf(person.description) }
    var lastSeenLocation by remember { mutableStateOf(person.lastSeenLocation) }
    var status by remember { mutableStateOf(person.status) }
    var photoUrl by remember { mutableStateOf(person.photoUrl ?: "") }

    val isFormValid by derivedStateOf {
        name.isNotBlank() &&
                description.isNotBlank() &&
                lastSeenLocation.isNotBlank() &&
                photoUrl.isNotBlank()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF6A11CB),
                        Color(0xFF2575FC)
                    )
                )
            )
            .padding(16.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Редактировать информацию",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            CustomTextField(
                value = name,
                onValueChange = { name = it },
                label = "Имя",
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            CustomTextField(
                value = description,
                onValueChange = { description = it },
                label = "Описание",
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            CustomTextField(
                value = lastSeenLocation,
                onValueChange = { lastSeenLocation = it },
                label = "Последнее известное место",
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            DropdownMenuField(
                label = "Статус",
                selectedItem = status,
                onItemSelected = { status = it },
                options = listOf("Пропал", "Найден"),
            )
            Spacer(modifier = Modifier.height(8.dp))

            CustomTextField(
                value = photoUrl,
                onValueChange = { photoUrl = it },
                label = "Ссылка на фото",
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val updatedPerson = person.copy(
                        name = name,
                        description = description,
                        lastSeenLocation = lastSeenLocation,
                        status = status,
                        photoUrl = photoUrl
                    )
                    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: "unknown_user"

                    viewModel.updatePerson(updatedPerson, userId) { success ->
                        if (success) {
                            onPersonUpdated()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = isFormValid
            ) {
                Text("Обновить информацию")
            }


            if (!isFormValid) {
                Text(
                    text = "Пожалуйста, заполните все поля перед обновлением.",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}
