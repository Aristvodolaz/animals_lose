package com.application.lose_animals.domain.repository

import com.application.lose_animals.data.model.Person
import kotlinx.coroutines.flow.Flow

interface PersonRepository {
    fun getPersons(): Flow<List<Person>>
    fun getUserPersons(userId: String): Flow<List<Person>>
    suspend fun addPerson(person: Person)
    suspend fun getPersonById(id: String): Person?
    suspend fun updatePerson(person: Person, updatedBy: String) // Добавлен updatedBy
    suspend fun deletePerson(personId: String)
}
