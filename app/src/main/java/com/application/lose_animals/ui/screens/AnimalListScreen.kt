import android.annotation.SuppressLint
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.application.lose_animals.ui.components.AnimalCard
import com.application.lose_animals.ui.viewModel.AnimalListViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

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
            TopAppBar(title = { Text("Lost Animals") })
        }
    ) {
        LazyColumn {
            items(animals) { animal ->
                AnimalCard(
                    animal = animal,
                    onClick = {
                        navController.navigate("animalDetail/${animal.id}")
                    }
                )
            }
        }
    }
}
