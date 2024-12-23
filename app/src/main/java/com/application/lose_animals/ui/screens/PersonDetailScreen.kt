package com.application.lose_animals.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import com.application.lose_animals.data.model.Person
import com.application.lose_animals.ui.viewModel.PersonDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonDetailScreen(
    personId: String,
    viewModel: PersonDetailViewModel = hiltViewModel()
) {
    var person by remember { mutableStateOf<Person?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    // Fetch person details
    LaunchedEffect(personId) {
        person = viewModel.getPersonById(personId)
        isLoading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Person Details", style = MaterialTheme.typography.titleLarge) },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        content = { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                if (isLoading) {
                    CircularProgressIndicator()
                } else {
                    person?.let { personData ->
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Display photo
                            SubcomposeAsyncImage(
                                model = personData.photoUrl,
                                contentDescription = "Person Photo",
                                modifier = Modifier
                                    .size(200.dp)
                                    .padding(16.dp),
                                loading = {
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator()
                                    }
                                },
                                error = {
                                    Text(
                                        text = "Image not available",
                                        textAlign = TextAlign.Center,
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.padding(16.dp)
                                    )
                                }
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Display person details
                            Text(
                                text = "Name: ${personData.name}",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            Text(
                                text = "Description: ${personData.description}",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            Text(
                                text = "Last Seen Location: ${personData.lastSeenLocation}",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                            // Add additional details as needed
                            Text(
                                text = "Status: ${personData.status}",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }
                    } ?: run {
                        Text(
                            text = "Person not found",
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        }
    )
}
