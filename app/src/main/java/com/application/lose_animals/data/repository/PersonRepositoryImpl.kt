package com.application.lose_animals.data.repository


import com.application.lose_animals.data.model.Person
import com.application.lose_animals.data.source.FirebaseSource
import com.application.lose_animals.domain.repository.PersonRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PersonRepositoryImpl @Inject constructor(
    private val firebaseSource: FirebaseSource
) : PersonRepository {

    // Get all persons from the repository (calls FirebaseSource)
    override fun getPersons(): Flow<List<Person>> = firebaseSource.getPersons()

    // Add a new person (calls FirebaseSource)
    override suspend fun addPerson(person: Person) = firebaseSource.addPerson(person)

    // Get a specific person by their ID (calls FirebaseSource)
    override suspend fun getPersonById(personId: String): Person? {
        return firebaseSource.getPersonById(personId) // Now calling FirebaseSource's getPersonById method
    }

    // Get persons reported by a specific user (filtering persons by userId)
    override fun getUserPersons(userId: String): Flow<List<Person>> {
        return firebaseSource.getPersons().map { persons ->
            persons.filter { it.userId == userId } // Filter persons by userId
        }
    }

    // Update a person (calls FirebaseSource)
    override suspend fun updatePerson(person: Person) {
        firebaseSource.updatePerson(person) // Calling FirebaseSource's update method
    }

    // Delete a person (calls FirebaseSource)
    override suspend fun deletePerson(personId: String) {
        firebaseSource.deletePerson(personId) // Calling FirebaseSource's delete method
    }
}
