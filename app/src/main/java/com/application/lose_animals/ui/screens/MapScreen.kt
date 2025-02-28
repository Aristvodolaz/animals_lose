package com.application.lose_animals.ui.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.application.lose_animals.data.model.Person
import com.application.lose_animals.ui.viewModel.PersonListViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    onNavigateBack: () -> Unit = {},
    onPersonClick: (String) -> Unit = {},
    onNavigateToAddPerson: () -> Unit = {},
    viewModel: PersonListViewModel = hiltViewModel()
) {
    val persons by viewModel.persons.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var mapProperties by remember { mutableStateOf(MapProperties(isMyLocationEnabled = false)) }
    var selectedPerson by remember { mutableStateOf<Person?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var showFilterDialog by remember { mutableStateOf(false) }
    var filterRadius by remember { mutableStateOf(10.0) } // Радиус поиска в км
    var filterStatus by remember { mutableStateOf("Все") } // Статус фильтрации
    
    // Координаты центра карты (по умолчанию - Москва)
    var centerLocation by remember { mutableStateOf(LatLng(55.7558, 37.6173)) }
    
    // Проверка разрешения на доступ к местоположению
    val locationPermissionGranted = remember {
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }
    
    // Преобразование адресов в координаты
    val personMarkers = remember(persons, filterRadius, filterStatus) {
        isLoading = true
        val markers = mutableListOf<Pair<Person, LatLng?>>()
        
        persons.forEach { person ->
            val coordinates = getCoordinatesFromAddress(context, person.lastSeenLocation)
            if (coordinates != null) {
                // Фильтрация по статусу
                if (filterStatus == "Все" || person.status == filterStatus) {
                    // Фильтрация по расстоянию
                    val distance = calculateDistance(centerLocation, coordinates)
                    if (distance <= filterRadius) {
                        markers.add(Pair(person, coordinates))
                    }
                }
            } else {
                // Если не удалось получить координаты, добавляем без фильтрации по расстоянию
                if (filterStatus == "Все" || person.status == filterStatus) {
                    markers.add(Pair(person, null))
                }
            }
        }
        
        isLoading = false
        markers
    }
    
    // Состояние камеры
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(centerLocation, 10f)
    }
    
    LaunchedEffect(locationPermissionGranted) {
        if (locationPermissionGranted) {
            mapProperties = mapProperties.copy(isMyLocationEnabled = true)
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Карта пропавших людей",
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
                actions = {
                    IconButton(onClick = { showFilterDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = "Фильтры"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Кнопка для центрирования карты на текущем местоположении
                if (locationPermissionGranted) {
                    FloatingActionButton(
                        onClick = {
                            scope.launch {
                                cameraPositionState.animate(
                                    update = CameraUpdateFactory.newLatLngZoom(
                                        centerLocation, 14f
                                    ),
                                    durationMs = 1000
                                )
                            }
                        },
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MyLocation,
                            contentDescription = "Моё местоположение"
                        )
                    }
                }
                
                // Основная кнопка для добавления нового человека
                FloatingActionButton(
                    onClick = { onNavigateToAddPerson() },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Добавить объявление"
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Карта Google Maps
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                properties = mapProperties,
                cameraPositionState = cameraPositionState,
                onMapClick = { selectedPerson = null }
            ) {
                personMarkers.forEach { (person, coordinates) ->
                    if (coordinates != null) {
                        Marker(
                            state = MarkerState(position = coordinates),
                            title = person.name,
                            snippet = person.status,
                            icon = BitmapDescriptorFactory.defaultMarker(
                                when (person.status) {
                                    "Пропавший" -> BitmapDescriptorFactory.HUE_RED
                                    "Найден" -> BitmapDescriptorFactory.HUE_GREEN
                                    else -> BitmapDescriptorFactory.HUE_AZURE
                                }
                            ),
                            onClick = {
                                selectedPerson = person
                                true
                            }
                        )
                    }
                }
            }
            
            // Индикатор загрузки
            AnimatedVisibility(
                visible = isLoading,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier.align(Alignment.Center)
            ) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                            shape = CircleShape
                        )
                        .padding(8.dp)
                )
            }
            
            // Карточка выбранного животного
            AnimatedVisibility(
                visible = selectedPerson != null,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
            ) {
                selectedPerson?.let { person ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = person.name,
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                
                                StatusChip(status = person.status)
                            }
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            Text(
                                text = "Последнее местонахождение: ${person.lastSeenLocation}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            Button(
                                onClick = { onPersonClick(person.id) },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary
                                )
                            ) {
                                Text("Подробнее")
                            }
                        }
                    }
                }
            }
            
            // Диалог фильтров
            if (showFilterDialog) {
                AlertDialog(
                    onDismissRequest = { showFilterDialog = false },
                    title = { Text("Настройки фильтрации") },
                    text = {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Выбор радиуса поиска
                            Text(
                                text = "Радиус поиска: ${filterRadius.toInt()} км",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            
                            Slider(
                                value = filterRadius.toFloat(),
                                onValueChange = { filterRadius = it.toDouble() },
                                valueRange = 1f..50f,
                                steps = 49,
                                modifier = Modifier.fillMaxWidth()
                            )
                            
                            // Выбор статуса
                            Text(
                                text = "Статус животного:",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                FilterChip(
                                    selected = filterStatus == "Все",
                                    onClick = { filterStatus = "Все" },
                                    label = { Text("Все") },
                                    modifier = Modifier.weight(1f)
                                )
                                
                                FilterChip(
                                    selected = filterStatus == "Пропавший",
                                    onClick = { filterStatus = "Пропавший" },
                                    label = { Text("Пропавшие") },
                                    modifier = Modifier.weight(1f)
                                )
                                
                                FilterChip(
                                    selected = filterStatus == "Найден",
                                    onClick = { filterStatus = "Найден" },
                                    label = { Text("Найденные") },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = { showFilterDialog = false }
                        ) {
                            Text("Применить")
                        }
                    }
                )
            }
        }
    }
}

// Функция для получения координат из адреса
private fun getCoordinatesFromAddress(context: Context, address: String): LatLng? {
    val geocoder = Geocoder(context, Locale.getDefault())
    try {
        val addresses = geocoder.getFromLocationName(address, 1)
        if (addresses != null && addresses.isNotEmpty()) {
            val location = addresses[0]
            return LatLng(location.latitude, location.longitude)
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return null
}

// Функция для расчета расстояния между двумя точками (в км)
private fun calculateDistance(point1: LatLng, point2: LatLng): Double {
    val earthRadius = 6371.0 // Радиус Земли в км
    
    val dLat = Math.toRadians(point2.latitude - point1.latitude)
    val dLng = Math.toRadians(point2.longitude - point1.longitude)
    
    val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
            Math.cos(Math.toRadians(point1.latitude)) * Math.cos(Math.toRadians(point2.latitude)) *
            Math.sin(dLng / 2) * Math.sin(dLng / 2)
    
    val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
    
    return earthRadius * c
}

@Composable
fun StatusChip(status: String) {
    val (backgroundColor, contentColor) = when (status.lowercase()) {
        "пропавший" -> MaterialTheme.colorScheme.errorContainer to MaterialTheme.colorScheme.onErrorContainer
        "найден" -> MaterialTheme.colorScheme.tertiaryContainer to MaterialTheme.colorScheme.onTertiaryContainer
        else -> MaterialTheme.colorScheme.secondaryContainer to MaterialTheme.colorScheme.onSecondaryContainer
    }
    
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = backgroundColor,
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Text(
            text = status,
            style = MaterialTheme.typography.labelMedium,
            color = contentColor,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
        )
    }
} 