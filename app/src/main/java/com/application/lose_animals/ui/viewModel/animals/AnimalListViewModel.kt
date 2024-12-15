package com.application.lose_animals.ui.viewModel.animals

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.lose_animals.data.model.Animal
import com.application.lose_animals.domain.usecase.GetAnimalsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import dagger.hilt.android.lifecycle.HiltViewModel

@HiltViewModel
class AnimalListViewModel @Inject constructor(
    private val getAnimalsUseCase: GetAnimalsUseCase
) : ViewModel() {
    private val _animals = MutableStateFlow<List<Animal>>(emptyList())
    val animals: StateFlow<List<Animal>> = _animals

    init {
        loadAnimals()
    }

    private fun loadAnimals() {
        viewModelScope.launch {
            getAnimalsUseCase().collect { animalList ->
                _animals.value = animalList
            }
        }
    }
}
