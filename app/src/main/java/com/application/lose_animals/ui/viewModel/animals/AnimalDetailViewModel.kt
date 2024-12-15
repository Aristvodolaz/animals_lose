package com.application.lose_animals.ui.viewModel.animals

import androidx.lifecycle.ViewModel
import com.application.lose_animals.data.model.Animal
import com.application.lose_animals.domain.repository.AnimalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AnimalDetailViewModel @Inject constructor(
    private val animalRepository: AnimalRepository
) : ViewModel() {

    suspend fun getAnimalById(animalId: String): Animal? {
        return animalRepository.getAnimalById(animalId)
    }
}
