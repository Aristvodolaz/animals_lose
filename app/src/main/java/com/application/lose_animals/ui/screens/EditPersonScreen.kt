package com.application.lose_animals.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.application.lose_animals.data.model.Person
import com.application.lose_animals.ui.components.CustomTextField
import com.application.lose_animals.ui.components.DropdownMenuField
import com.application.lose_animals.ui.viewModel.EditPersonViewModel

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

    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "Редактировать информацию",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        CustomTextField(
            value = name,
            onValueChange = { name = it },
            label = "Имя"
        )
        Spacer(modifier = Modifier.height(8.dp))

        CustomTextField(
            value = description,
            onValueChange = { description = it },
            label = "Описание"
        )
        Spacer(modifier = Modifier.height(8.dp))

        CustomTextField(
            value = lastSeenLocation,
            onValueChange = { lastSeenLocation = it },
            label = "Последнее известное место"
        )
        Spacer(modifier = Modifier.height(8.dp))

        DropdownMenuField(
            label = "Статус",
            selectedItem = status,
            onItemSelected = { status = it },
            options = listOf("Пропал", "Найден")
        )
        Spacer(modifier = Modifier.height(8.dp))

        CustomTextField(
            value = photoUrl,
            onValueChange = { photoUrl = it },
            label = "Ссылка на фото"
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
                viewModel.updatePerson(updatedPerson) { success ->
                    if (success) {
                        onPersonUpdated()
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = isFormValid,
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isFormValid) MaterialTheme.colorScheme.primary else Color.Gray
            )
        ) {
            Text("Обновить информацию")
        }

        if (!isFormValid) {
            Text(
                text = "Пожалуйста, заполните все поля перед обновлением.",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}
