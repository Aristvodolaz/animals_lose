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

    override fun getAnimals(): Flow<List<Animal>> = firebaseSource.getAnimals()

    override suspend fun addAnimal(animal: Animal) = firebaseSource.addAnimal(animal)

    override suspend fun getAnimalById(id: String): Animal? {
        return firebaseSource.getAnimalById(id)
    }

    override fun getUserAnimals(userId: String): Flow<List<Animal>> {
        return firebaseSource.getAnimals().map { animals ->
            animals.filter { it.userId == userId }
        }
    }

    override suspend fun updateAnimal(animal: Animal) {
        firebaseSource.updateAnimal(animal) // Вызов метода обновления из FirebaseSource
    }

    override suspend fun deleteAnimal(animalId: String) {
        firebaseSource.deleteAnimal(animalId)
    }
}
