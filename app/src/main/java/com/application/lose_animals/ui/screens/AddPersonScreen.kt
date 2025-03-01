package com.application.lose_animals.ui.screens

import android.annotation.SuppressLint
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.application.lose_animals.ui.viewModel.AddPersonViewModel
import com.application.lose_animals.ui.components.AddressAutocompleteField
import com.application.lose_animals.data.model.AddressSuggestion

@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun AddPersonScreen(
    viewModel: AddPersonViewModel = hiltViewModel(),
    onPersonAdded: () -> Unit,
    onNavigateBack: () -> Unit = {}
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var lastSeenLocation by remember { mutableStateOf("") }
    var photoUrl by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("Пропавший") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var showStatusMenu by remember { mutableStateOf(false) }
    var selectedAddressSuggestion by remember { mutableStateOf<AddressSuggestion?>(null) }
    var addressCoordinates by remember { mutableStateOf<Pair<Double, Double>?>(null) }

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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Добавить объявление") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Фото животного
            Box(
                modifier = Modifier
                    .padding(vertical = 16.dp)
                    .size(180.dp),
                contentAlignment = Alignment.Center
            ) {
                if (selectedImageUri != null) {
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .shadow(8.dp, CircleShape),
                        shape = CircleShape
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(selectedImageUri),
                            contentDescription = "Фото",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    }
                    
                    // Кнопка изменения фото
                    FloatingActionButton(
                        onClick = { launcher.launch("image/*") },
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(48.dp),
                        containerColor = MaterialTheme.colorScheme.primary
                    ) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Изменить фото",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                } else {
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .shadow(4.dp, CircleShape)
                            .clickable { launcher.launch("image/*") },
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                Icons.Default.AddAPhoto,
                                contentDescription = "Добавить фото",
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Добавить фото",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Форма добавления
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Имя животного
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Имя человека") },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        ),
                        singleLine = true
                    )

                    // Описание
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Описание") },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Description,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        ),
                        maxLines = 5
                    )

                    // Местоположение
                    AddressAutocompleteField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        label = "Последнее местоположение",
                        onAddressSelected = { suggestion ->
                            selectedAddressSuggestion = suggestion
                            lastSeenLocation = suggestion.value
                        },
                        onCoordinatesReceived = { coordinates ->
                            addressCoordinates = coordinates
                            // Здесь можно сохранить координаты в модель данных
                        }
                    )

                    // Статус
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = status,
                            onValueChange = {},
                            label = { Text("Статус") },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Info,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            },
                            trailingIcon = {
                                IconButton(onClick = { showStatusMenu = true }) {
                                    Icon(
                                        Icons.Default.ArrowDropDown,
                                        contentDescription = "Выбрать статус"
                                    )
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline
                            ),
                            readOnly = true,
                            singleLine = true
                        )

                        DropdownMenu(
                            expanded = showStatusMenu,
                            onDismissRequest = { showStatusMenu = false },
                            modifier = Modifier.width(IntrinsicSize.Min)
                        ) {
                            listOf("Пропавший", "Найден").forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option) },
                                    onClick = {
                                        status = option
                                        showStatusMenu = false
                                    },
                                    leadingIcon = {
                                        Icon(
                                            if (option == "Пропавший") Icons.Default.Search else Icons.Default.CheckCircle,
                                            contentDescription = null
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Сообщение о валидации
            AnimatedVisibility(
                visible = !isFormValid,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.errorContainer,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Warning,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onErrorContainer
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Пожалуйста, заполните все поля и выберите изображение",
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Кнопка добавления
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
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = isFormValid && !isLoading,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                AnimatedContent(
                    targetState = isLoading,
                    transitionSpec = {
                        fadeIn() with fadeOut()
                    }
                ) { loading ->
                    if (loading) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Добавить объявление",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
