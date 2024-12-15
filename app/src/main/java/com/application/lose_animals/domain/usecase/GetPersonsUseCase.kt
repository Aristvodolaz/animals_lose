package com.application.lose_animals.domain.usecase


import com.application.lose_animals.data.model.Person
import com.application.lose_animals.domain.repository.PersonRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPersonsUseCase @Inject constructor(
    private val repository: PersonRepository
) {
    operator fun invoke(): Flow<List<Person>> = repository.getPersons()
}
