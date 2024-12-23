package com.application.lose_animals.data.source

import com.application.lose_animals.data.model.Person
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseSource @Inject constructor(private val firestore: FirebaseFirestore) {

    fun getPersons(): Flow<List<Person>> = flow {
        try {
            val snapshot = firestore.collection("persons").get().await()
            val persons = snapshot.toObjects(Person::class.java)
            emit(persons)
        } catch (e: Exception) {
            e.printStackTrace() // Логирование ошибки
            emit(emptyList())
        }
    }


    suspend fun addPerson(person: Person) {
        val docRef = firestore.collection("persons").add(person).await()
        val updatedPerson = person.copy(id = docRef.id)
        docRef.set(updatedPerson).await() // Обновляем объект с заполненным `id`
    }


    suspend fun updatePerson(person: Person) {
        val personDocRef = firestore.collection("persons").document(person.id) // Reference to a specific person document
        personDocRef.set(person).await()  // Update the person data
    }

    suspend fun deletePerson(personId: String) {
        val personDocRef = firestore.collection("persons").document(personId) // Reference to the person document by ID
        personDocRef.delete().await()  // Delete the document from Firestore
    }

    suspend fun getPersonById(personId: String): Person? {
        return try {
            val snapshot = firestore.collection("persons").document(personId).get().await() // Fetch the person document by ID
            snapshot.toObject(Person::class.java)  // Deserialize the document into a Person object
        } catch (e: Exception) {
            null // Return null if an error occurs (e.g., document not found)
        }
    }
}
