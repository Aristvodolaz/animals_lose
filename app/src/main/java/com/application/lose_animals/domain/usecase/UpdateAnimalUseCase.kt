package com.application.lose_animals.domain.usecase

import com.application.lose_animals.data.model.Animal
import com.application.lose_animals.domain.repository.AnimalRepository
import javax.inject.Inject

class UpdateAnimalUseCase @Inject constructor(
    private val animalRepository: AnimalRepository
) {
    suspend operator fun invoke(animal: Animal) {
        animalRepository.updateAnimal(animal)
    }
}
