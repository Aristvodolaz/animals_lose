package com.application.lose_animals.ui.screens


import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.application.lose_animals.ui.components.PersonCard
import com.application.lose_animals.ui.viewModel.PersonListViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonListScreen(
    navController: NavController,
    viewModel: PersonListViewModel = hiltViewModel()
) {
    val persons by viewModel.persons.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Missing Persons", style = MaterialTheme.typography.titleLarge) },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        content = { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                items(persons) { person ->
                    PersonCard(
                        person = person,
                        onClick = {
                            navController.navigate("personDetail/${person.id}")
                        },
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }
            }
        }
    )
}