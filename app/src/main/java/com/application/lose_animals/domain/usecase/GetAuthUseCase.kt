package com.application.lose_animals.domain.usecase

import com.application.lose_animals.data.model.User
import com.application.lose_animals.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(): User? = repository.getCurrentUser()
}
