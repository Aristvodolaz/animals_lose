package com.application.lose_animals.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.lose_animals.data.model.Person
import com.application.lose_animals.data.model.User
import com.application.lose_animals.domain.repository.AuthRepository
import com.application.lose_animals.domain.repository.PersonRepository
import com.application.lose_animals.domain.usecase.GetPersonsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getPersonsUseCase: GetPersonsUseCase,
    private val personRepository: PersonRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user.asStateFlow()

    private val _persons = MutableStateFlow<List<Person>>(emptyList())
    val persons: StateFlow<List<Person>> = _persons.asStateFlow()

    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated.asStateFlow()

    init {
        checkAuthentication()
        loadPersons()
    }

    private fun checkAuthentication() {
        viewModelScope.launch {
            val currentUser = authRepository.getCurrentUser()
            _user.value = currentUser
            _isAuthenticated.value = currentUser != null
        }
    }

    private fun loadPersons() {
        viewModelScope.launch {
            try {
                getPersonsUseCase().collect { personList ->
                    _persons.value = personList
                }
            } catch (e: Exception) {
                _persons.value = emptyList()
            }
        }
    }

    suspend fun getPersonById(personId: String): Person? {
        return personRepository.getPersonById(personId)
    }
}
