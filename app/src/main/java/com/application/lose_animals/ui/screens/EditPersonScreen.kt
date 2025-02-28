package com.application.lose_animals.ui.screens

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.application.lose_animals.data.model.Person
import com.application.lose_animals.ui.components.CustomTextField
import com.application.lose_animals.ui.components.DropdownMenuField
import com.application.lose_animals.ui.viewModel.EditPersonViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnrememberedMutableState")
@Composable
fun EditPersonScreen(
    person: Person,
    viewModel: EditPersonViewModel = hiltViewModel(),
    onPersonUpdated: () -> Unit,
    onNavigateBack: () -> Unit = {}
) {
    var name by remember { mutableStateOf(person.name) }
    var description by remember { mutableStateOf(person.description) }
    var lastSeenLocation by remember { mutableStateOf(person.lastSeenLocation) }
    var status by remember { mutableStateOf(person.status) }
    var photoUrl by remember { mutableStateOf(person.photoUrl ?: "") }
    var isUpdating by remember { mutableStateOf(false) }
    var showSuccessMessage by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()

    val isFormValid by derivedStateOf {
        name.isNotBlank() &&
                description.isNotBlank() &&
                lastSeenLocation.isNotBlank() &&
                photoUrl.isNotBlank()
    }
    
    val updatePerson = {
        if (isFormValid) {
            isUpdating = true
            val updatedPerson = person.copy(
                name = name,
                description = description,
                lastSeenLocation = lastSeenLocation,
                status = status,
                photoUrl = photoUrl
            )
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: "unknown_user"

            viewModel.updatePerson(updatedPerson, userId) { success ->
                isUpdating = false
                if (success) {
                    showSuccessMessage = true
                    scope.launch {
                        delay(1500)
                        onPersonUpdated()
                    }
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Редактирование животного",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Назад"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = updatePerson,
                expanded = isFormValid,
                icon = { 
                    if (isUpdating) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(Icons.Default.Save, contentDescription = "Сохранить") 
                    }
                },
                text = { Text("Сохранить") },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                enabled = isFormValid && !isUpdating
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Заголовок
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = "Обновите информацию о животном",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
                
                // Форма редактирования
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 2.dp
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Имя
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Имя животного") },
                            modifier = Modifier.fillMaxWidth(),
                            leadingIcon = { 
                                Icon(
                                    Icons.Default.Person, 
                                    contentDescription = null
                                ) 
                            },
                            singleLine = true,
                            isError = name.isBlank(),
                            supportingText = {
                                if (name.isBlank()) {
                                    Text("Имя не может быть пустым")
                                }
                            }
                        )
                        
                        // Описание
                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = { Text("Описание") },
                            modifier = Modifier.fillMaxWidth(),
                            leadingIcon = { 
                                Icon(
                                    Icons.Default.Description, 
                                    contentDescription = null
                                ) 
                            },
                            minLines = 2,
                            maxLines = 4,
                            isError = description.isBlank(),
                            supportingText = {
                                if (description.isBlank()) {
                                    Text("Описание не может быть пустым")
                                }
                            }
                        )
                        
                        // Последнее местонахождение
                        OutlinedTextField(
                            value = lastSeenLocation,
                            onValueChange = { lastSeenLocation = it },
                            label = { Text("Последнее местонахождение") },
                            modifier = Modifier.fillMaxWidth(),
                            leadingIcon = { 
                                Icon(
                                    Icons.Default.LocationOn, 
                                    contentDescription = null
                                ) 
                            },
                            singleLine = true,
                            isError = lastSeenLocation.isBlank(),
                            supportingText = {
                                if (lastSeenLocation.isBlank()) {
                                    Text("Местонахождение не может быть пустым")
                                }
                            }
                        )
                        
                        // Статус
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "Статус",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
                            )
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                StatusOption(
                                    title = "Пропал",
                                    selected = status == "Пропал",
                                    color = MaterialTheme.colorScheme.errorContainer,
                                    onTextColor = MaterialTheme.colorScheme.onErrorContainer,
                                    modifier = Modifier.weight(1f),
                                    onClick = { status = "Пропал" }
                                )
                                
                                StatusOption(
                                    title = "Найден",
                                    selected = status == "Найден",
                                    color = MaterialTheme.colorScheme.tertiaryContainer,
                                    onTextColor = MaterialTheme.colorScheme.onTertiaryContainer,
                                    modifier = Modifier.weight(1f),
                                    onClick = { status = "Найден" }
                                )
                            }
                        }
                        
                        // Ссылка на фото
                        OutlinedTextField(
                            value = photoUrl,
                            onValueChange = { photoUrl = it },
                            label = { Text("Ссылка на фото") },
                            modifier = Modifier.fillMaxWidth(),
                            leadingIcon = { 
                                Icon(
                                    Icons.Default.Photo, 
                                    contentDescription = null
                                ) 
                            },
                            singleLine = true,
                            isError = photoUrl.isBlank(),
                            supportingText = {
                                if (photoUrl.isBlank()) {
                                    Text("Ссылка на фото не может быть пустой")
                                }
                            }
                        )
                    }
                }
                
                // Сообщение о валидации
                AnimatedVisibility(
                    visible = !isFormValid,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onErrorContainer
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = "Пожалуйста, заполните все поля перед сохранением",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(80.dp)) // Пространство для FAB
            }
            
            // Сообщение об успешном обновлении
            AnimatedVisibility(
                visible = showSuccessMessage,
                enter = fadeIn(tween(300)),
                exit = fadeOut(tween(300)),
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(32.dp)
            ) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 6.dp
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Save,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onTertiaryContainer,
                            modifier = Modifier.size(48.dp)
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text(
                            text = "Информация успешно обновлена!",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatusOption(
    title: String,
    selected: Boolean,
    color: Color,
    onTextColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .height(48.dp),
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        color = if (selected) color else MaterialTheme.colorScheme.surface,
        border = if (!selected) 
            BorderStroke(1.dp, MaterialTheme.colorScheme.outline) 
        else null
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = if (selected) onTextColor else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
