package com.application.lose_animals.domain.repository

import com.application.lose_animals.data.model.Animal
import kotlinx.coroutines.flow.Flow

interface AnimalRepository {
    fun getAnimals(): Flow<List<Animal>>
    fun getUserAnimals(userId: String): Flow<List<Animal>>
    suspend fun addAnimal(animal: Animal)
    suspend fun getAnimalById(id: String): Animal?
    suspend fun updateAnimal(animal: Animal) // Новый метод для обновления животного
    suspend fun deleteAnimal(animalId: String) // Метод для удаления животного
}
