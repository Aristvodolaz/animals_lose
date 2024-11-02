package com.application.lose_animals.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.lose_animals.data.model.Animal
import com.application.lose_animals.data.model.User
import com.application.lose_animals.domain.usecase.GetAnimalsUseCase
import com.application.lose_animals.domain.repository.AnimalRepository
import com.application.lose_animals.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getAnimalsUseCase: GetAnimalsUseCase,
    private val animalRepository: AnimalRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user.asStateFlow()

    private val _animals = MutableStateFlow<List<Animal>>(emptyList())
    val animals: StateFlow<List<Animal>> = _animals.asStateFlow()

    private val _isAuthenticated = MutableStateFlow(false) // Добавляем состояние аутентификации
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated.asStateFlow()

    init {
        checkAuthentication() // Проверяем аутентификацию при инициализации
        loadAnimals() // Загружаем животных независимо от статуса аутентификации
    }

    private fun checkAuthentication() {
        viewModelScope.launch {
            val currentUser = authRepository.getCurrentUser()
            _user.value = currentUser
            _isAuthenticated.value = currentUser != null // Обновляем состояние аутентификации
        }
    }

    private fun loadAnimals() {
        viewModelScope.launch {
            try {
                getAnimalsUseCase().collect { animalList ->
                    _animals.value = animalList
                }
            } catch (e: Exception) {
                // Обработка ошибок при загрузке животных
                _animals.value = emptyList()
            }
        }
    }

    suspend fun getAnimalById(animalId: String): Animal? {
        return animalRepository.getAnimalById(animalId)
    }
}
