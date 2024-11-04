import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.application.lose_animals.ui.components.AnimalCard
import com.application.lose_animals.ui.viewModel.AnimalListViewModel
import androidx.navigation.NavController

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimalListScreen(
    navController: NavController,
    viewModel: AnimalListViewModel = hiltViewModel()
) {
    val animals by viewModel.animals.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Lost Animals", style = MaterialTheme.typography.titleLarge) },
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
                    .padding(16.dp) // Add padding around the list
            ) {
                items(animals) { animal ->
                    AnimalCard(
                        animal = animal,
                        onClick = {
                            navController.navigate("animalDetail/${animal.id}")
                        },
                        modifier = Modifier.padding(bottom = 12.dp) // Space between cards
                    )
                }
            }
        }
    )
}
