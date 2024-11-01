package com.application.lose_animals.domain.repository

import com.application.lose_animals.data.model.Animal
import kotlinx.coroutines.flow.Flow

interface AnimalRepository {
    fun getAnimals(): Flow<List<Animal>>
    suspend fun addAnimal(animal: Animal)
    suspend fun getAnimalById(id: String): Animal?
    fun getUserAnimals(userId: String): Flow<List<Animal>> // Метод для получения объявлений пользователя
}
