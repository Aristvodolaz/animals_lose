package com.application.lose_animals.ui.screens

import android.annotation.SuppressLint

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.application.lose_animals.ui.components.PersonCard
import com.application.lose_animals.ui.viewModel.PersonListViewModel
import com.application.lose_animals.util.LocationHelper
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun PersonListScreen(
    navController: NavController,
    viewModel: PersonListViewModel = hiltViewModel()
) {
    val persons by viewModel.persons.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val context = LocalContext.current

    var userLat by remember { mutableStateOf(0.0) }
    var userLon by remember { mutableStateOf(0.0) }

    val locationHelper = remember { LocationHelper(context) }

    LaunchedEffect(Unit) {
        locationHelper.getUserLocation { lat, lon ->
            userLat = lat
            userLon = lon
        }
    }

    val filteredPersons = persons.filter {
        distanceBetween(userLat, userLon, it.latitude, it.longitude) <= 50.0
    }


    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFF6A11CB),
                                Color(0xFF2575FC)
                            )
                        )
                    )
                    .padding(16.dp)
            ) {
                Text(
                    text = "Пропавшие люди",
                    style = MaterialTheme.typography.headlineMedium.copy(color = Color.White),
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                SearchBar(
                    searchQuery = searchQuery,
                    onQueryChanged = viewModel::updateSearchQuery
                )
            }
        },
        content = { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color(0xFFEDEDED), Color(0xFFFFFFFF))
                        )
                    )
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                if (filteredPersons.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Нет данных для отображения",
                            style = MaterialTheme.typography.bodyLarge.copy(color = Color.Gray)
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(filteredPersons) { person ->
                            PersonCard(
                                person = person,
                                onClick = {
                                    navController.navigate("personDetail/${person.id}")
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 12.dp)
                            )
                        }
                    }
                }
            }
        }
    )
}

fun distanceBetween(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val earthRadius = 6371.0
    val dLat = Math.toRadians(lat2 - lat1)
    val dLon = Math.toRadians(lon2 - lon1)

    val a = sin(dLat / 2) * sin(dLat / 2) +
            cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
            sin(dLon / 2) * sin(dLon / 2)

    val c = 2 * atan2(sqrt(a), sqrt(1 - a))

    return earthRadius * c // 🔹 Теперь функция возвращает значение!
}

@Composable
fun SearchBar(
    searchQuery: String,
    onQueryChanged: (String) -> Unit
) {
    OutlinedTextField(
        value = searchQuery,
        onValueChange = onQueryChanged,
        placeholder = { Text("Поиск по имени") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            focusedBorderColor = Color(0xFF6A11CB),
            unfocusedBorderColor = Color(0xFF2575FC),
            cursorColor = Color(0xFF6A11CB)
        )
    )
}
