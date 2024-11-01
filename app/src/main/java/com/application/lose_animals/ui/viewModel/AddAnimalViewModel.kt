package com.application.lose_animals.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.lose_animals.data.model.Animal
import com.application.lose_animals.domain.repository.AnimalRepository
import com.application.lose_animals.data.repository.AuthRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddAnimalViewModel @Inject constructor(
    private val animalRepository: AnimalRepository,
    private val authRepository: AuthRepositoryImpl
) : ViewModel() {

    fun addAnimal(name: String, description: String, location: String, photoUrl: String?, status: String, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            val userId = authRepository.getCurrentUser()?.id ?: return@launch
            val animal = Animal(
                id = "", // ID будет автоматически сгенерирован Firebase
                name = name,
                description = description,
                location = location,
                photoUrl = photoUrl,
                status = status,
                userId = userId
            )
            animalRepository.addAnimal(animal)
            onComplete(true)
        }
    }
}
