package com.application.lose_animals.ui.screens
import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.application.lose_animals.data.model.Person
import com.application.lose_animals.ui.components.CustomTextField
import com.application.lose_animals.ui.components.DropdownMenuField
import com.application.lose_animals.ui.viewModel.EditPersonViewModel

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

    Column(modifier = Modifier.padding(16.dp)) {
        CustomTextField(value = name, onValueChange = { name = it }, label = "Name")
        Spacer(modifier = Modifier.height(8.dp))
        CustomTextField(value = description, onValueChange = { description = it }, label = "Description")
        Spacer(modifier = Modifier.height(8.dp))
        CustomTextField(value = lastSeenLocation, onValueChange = { lastSeenLocation = it }, label = "Last Seen Location")
        Spacer(modifier = Modifier.height(8.dp))
        DropdownMenuField(label = "Status", selectedItem = status, onItemSelected = { status = it }, options = listOf("Missing", "Found"))
        Spacer(modifier = Modifier.height(8.dp))
        CustomTextField(value = photoUrl, onValueChange = { photoUrl = it }, label = "Photo URL")
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            val updatedPerson = person.copy(name = name, description = description, lastSeenLocation = lastSeenLocation, status = status, photoUrl = photoUrl)
            viewModel.updatePerson(updatedPerson) { success ->
                if (success) {
                    onPersonUpdated()
                }
            }
        }) {
            Text("Update Person")
        }
    }
}
