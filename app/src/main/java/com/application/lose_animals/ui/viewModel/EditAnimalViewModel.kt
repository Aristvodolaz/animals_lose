package com.application.lose_animals.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.lose_animals.data.model.Animal
import com.application.lose_animals.domain.usecase.UpdateAnimalUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditAnimalViewModel @Inject constructor(
    private val updateAnimalUseCase: UpdateAnimalUseCase
) : ViewModel() {

    fun updateAnimal(animal: Animal, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                updateAnimalUseCase(animal)
                onComplete(true)
            } catch (e: Exception) {
                onComplete(false)
            }
        }
    }
}
