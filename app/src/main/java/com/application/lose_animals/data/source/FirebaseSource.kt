package com.application.lose_animals.data.source

import com.application.lose_animals.data.model.Animal
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseSource @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    fun getAnimals(): Flow<List<Animal>> = flow {
        val snapshot = firestore.collection("animals").get().await()
        emit(snapshot.toObjects(Animal::class.java))
    }

    suspend fun addAnimal(animal: Animal) {
        firestore.collection("animals").add(animal).await()
    }

    suspend fun getAnimalById(id: String): Animal? {
        val documentSnapshot = firestore.collection("animals").document(id).get().await()
        return documentSnapshot.toObject(Animal::class.java)
    }

    suspend fun updateAnimal(animal: Animal) {
        val animalRef = firestore.collection("animals").document(animal.id)
        animalRef.set(animal).await() // Используем set для обновления документа
    }

    suspend fun deleteAnimal(animalId: String) {
        val animalRef = firestore.collection("animals").document(animalId)
        animalRef.delete().await() // Удаляем документ
    }
}
