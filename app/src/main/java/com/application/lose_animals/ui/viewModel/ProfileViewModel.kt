package com.application.lose_animals.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.lose_animals.data.model.Person
import com.application.lose_animals.data.model.User
import com.application.lose_animals.domain.repository.AuthRepository
import com.application.lose_animals.domain.repository.PersonRepository
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
    private val personRepository: PersonRepository
) : ViewModel() {

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user.asStateFlow()

    private val _userPersons = MutableStateFlow<List<Person>>(emptyList())
    val userPersons: StateFlow<List<Person>> = _userPersons.asStateFlow()

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            val currentUser = authRepository.getCurrentUser()
            _user.value = currentUser
            currentUser?.let {
                loadUserPersons(it.id)
            }
        }
    }

    private fun loadUserPersons(userId: String) {
        personRepository.getUserPersons(userId).onEach { persons ->
            _userPersons.value = persons
        }.launchIn(viewModelScope)
    }

    fun logout() {
        authRepository.logout()
    }
}
