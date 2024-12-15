package com.application.lose_animals.ui.viewModel

import androidx.lifecycle.ViewModel
import com.application.lose_animals.data.model.Person
import com.application.lose_animals.domain.repository.PersonRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PersonDetailViewModel @Inject constructor(
    private val personRepository: PersonRepository
) : ViewModel() {

    suspend fun getPersonById(personId: String): Person? {
        return personRepository.getPersonById(personId)
    }
}