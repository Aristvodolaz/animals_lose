package com.application.lose_animals.data.source

import com.application.lose_animals.data.model.Animal
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseSource @Inject constructor(private val firestore: FirebaseFirestore) {

    // Get all animals from Firestore and wrap in a Flow
    fun getAnimals(): Flow<List<Animal>> = flow {
        try {
            val snapshot = firestore.collection("animals").get().await() // Fetch all animals from the "animals" collection
            val animals = snapshot.toObjects(Animal::class.java)  // Deserialize the Firestore documents into Animal objects
            emit(animals)  // Emit the list of animals as a flow
        } catch (e: Exception) {
            emit(emptyList())  // In case of an error, emit an empty list
        }
    }

    // Add a new animal document to Firestore
    suspend fun addAnimal(animal: Animal) {
        firestore.collection("animals").add(animal).await() // Add a new animal to the collection
    }

    // Update an existing animal document in Firestore
    suspend fun updateAnimal(animal: Animal) {
        val animalDocRef = firestore.collection("animals").document(animal.id) // Reference to a specific animal document
        animalDocRef.set(animal).await()  // Update the animal data
    }

    // Delete an animal document from Firestore by its ID
    suspend fun deleteAnimal(animalId: String) {
        val animalDocRef = firestore.collection("animals").document(animalId) // Reference to the animal document by ID
        animalDocRef.delete().await()  // Delete the document from Firestore
    }

    // Get a specific animal by its ID
    suspend fun getAnimalById(animalId: String): Animal? {
        return try {
            val snapshot = firestore.collection("animals").document(animalId).get().await() // Fetch the animal document by ID
            snapshot.toObject(Animal::class.java)  // Deserialize the document into an Animal object
        } catch (e: Exception) {
            null // Return null if an error occurs (e.g., document not found)
        }
    }
}
