package com.application.lose_animals.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.lose_animals.data.model.Animal
import com.application.lose_animals.data.model.User
import com.application.lose_animals.domain.repository.AnimalRepository
import com.application.lose_animals.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val animalRepository: AnimalRepository
) : ViewModel() {

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user.asStateFlow()

    private val _userAnimals = MutableStateFlow<List<Animal>>(emptyList())
    val userAnimals: StateFlow<List<Animal>> = _userAnimals.asStateFlow()

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            val currentUser = authRepository.getCurrentUser()
            _user.value = currentUser
            currentUser?.let {
                loadUserAnimals(it.id)
            }
        }
    }

    private fun loadUserAnimals(userId: String) {
        animalRepository.getUserAnimals(userId).onEach { animals ->
            _userAnimals.value = animals
        }.launchIn(viewModelScope)
    }

    fun logout() {
        authRepository.logout()
    }
}
