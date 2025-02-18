package com.application.lose_animals.domain.usecase

import com.application.lose_animals.data.model.Person
import com.application.lose_animals.domain.repository.PersonRepository
import javax.inject.Inject

class UpdatePersonUseCase @Inject constructor(
    private val repository: PersonRepository
) {
    suspend operator fun invoke(person: Person, updatedBy: String) {
        repository.updatePerson(person, updatedBy) // Передаем updatedBy
    }
}
