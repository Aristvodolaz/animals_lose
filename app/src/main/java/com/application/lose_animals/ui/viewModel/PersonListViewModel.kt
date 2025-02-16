package com.application.lose_animals.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.lose_animals.data.model.Person
import com.application.lose_animals.domain.usecase.GetPersonsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import dagger.hilt.android.lifecycle.HiltViewModel

import kotlinx.coroutines.flow.*


@HiltViewModel
class PersonListViewModel @Inject constructor(
    private val getPersonsUseCase: GetPersonsUseCase
) : ViewModel() {

    private val _persons = MutableStateFlow<List<Person>>(emptyList())
    val persons: StateFlow<List<Person>> = _persons

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _userId = MutableStateFlow<String?>(null) // ID текущего пользователя
    val userId: StateFlow<String?> = _userId

    init {
        loadPersons()
    }

    fun setUser(userId: String) {
        _userId.value = userId
        loadPersons()
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    private fun loadPersons() {
        viewModelScope.launch {
            getPersonsUseCase()
                .combine(_searchQuery) { persons, query ->
                    if (query.isEmpty()) persons else persons.filter { it.name.contains(query, ignoreCase = true) }
                }
                .combine(_userId) { persons, userId ->
                    userId?.let { persons.filter { it.userId == userId } } ?: persons
                }
                .collect { filteredList ->
                    _persons.value = filteredList
                }
        }
    }
}
