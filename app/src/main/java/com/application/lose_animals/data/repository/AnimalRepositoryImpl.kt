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
        return firebaseSource.getAnimalById(id) // Реализация вызова из FirebaseSource
    }

    override fun getUserAnimals(userId: String): Flow<List<Animal>> {
        return firebaseSource.getAnimals().map { animals ->
            animals.filter { it.userId == userId }
        }
    }
}
