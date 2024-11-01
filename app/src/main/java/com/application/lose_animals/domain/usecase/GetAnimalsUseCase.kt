package com.application.lose_animals.domain.usecase

import com.application.lose_animals.data.model.Animal
import com.application.lose_animals.domain.repository.AnimalRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAnimalsUseCase @Inject constructor(
    private val repository: AnimalRepository
) {
    operator fun invoke(): Flow<List<Animal>> = repository.getAnimals()
}
