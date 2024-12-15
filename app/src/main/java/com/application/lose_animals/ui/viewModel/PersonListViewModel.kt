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

@HiltViewModel
class PersonListViewModel @Inject constructor(
    private val getPersonsUseCase: GetPersonsUseCase
) : ViewModel() {
    private val _persons = MutableStateFlow<List<Person>>(emptyList())
    val persons: StateFlow<List<Person>> = _persons

    init {
        loadPersons()
    }

    private fun loadPersons() {
        viewModelScope.launch {
            getPersonsUseCase().collect { personList ->
                _persons.value = personList
            }
        }
    }
}