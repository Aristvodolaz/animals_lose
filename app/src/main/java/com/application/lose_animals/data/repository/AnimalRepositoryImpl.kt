package com.application.lose_animals.data.repository

import com.application.lose_animals.data.model.Animal
import com.application.lose_animals.data.source.FirebaseSource
import com.application.lose_animals.domain.repository.AnimalRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AnimalRepositoryImpl @Inject constructor(
    private val firebaseSource: FirebaseSource
) : AnimalRepository {

    // Get all animals from the repository (calls FirebaseSource)
    override fun getAnimals(): Flow<List<Animal>> = firebaseSource.getAnimals()

    // Add a new animal (calls FirebaseSource)
    override suspend fun addAnimal(animal: Animal) = firebaseSource.addAnimal(animal)

    // Get a specific animal by its ID (calls FirebaseSource)
    override suspend fun getAnimalById(animalId: String): Animal? {
        return firebaseSource.getAnimalById(animalId) // Now calling FirebaseSource's getAnimalById method
    }

    // Get animals owned by a user (filtering animals by userId)
    override fun getUserAnimals(userId: String): Flow<List<Animal>> {
        return firebaseSource.getAnimals().map { animals ->
            animals.filter { it.userId == userId } // Filter animals by userId
        }
    }

    // Update an animal (calls FirebaseSource)
    override suspend fun updateAnimal(animal: Animal) {
        firebaseSource.updateAnimal(animal) // Calling FirebaseSource's update method
    }

    // Delete an animal (calls FirebaseSource)
    override suspend fun deleteAnimal(animalId: String) {
        firebaseSource.deleteAnimal(animalId) // Calling FirebaseSource's delete method
    }
}
