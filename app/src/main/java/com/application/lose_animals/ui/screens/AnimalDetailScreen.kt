import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.application.lose_animals.data.model.Animal
import com.application.lose_animals.ui.viewModel.AnimalDetailViewModel

@Composable
fun AnimalDetailScreen(animalId: String, viewModel: AnimalDetailViewModel = hiltViewModel()) {
    val animal by produceState<Animal?>(initialValue = null, animalId) {
        value = viewModel.getAnimalById(animalId)
    }

    animal?.let { animalData ->
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Name: ${animalData.name}", style = MaterialTheme.typography.titleMedium)
            Text(text = "Description: ${animalData.description}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Location: ${animalData.location}", style = MaterialTheme.typography.bodyMedium)
            // Добавьте другие поля для отображения
        }
    } ?: run {
        Text(text = "Loading...", modifier = Modifier.padding(16.dp))
    }
}
