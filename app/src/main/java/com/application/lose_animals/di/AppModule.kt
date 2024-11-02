package com.application.lose_animals.di

import com.application.lose_animals.data.repository.AnimalRepositoryImpl
import com.application.lose_animals.data.repository.AuthRepositoryImpl
import com.application.lose_animals.data.source.FirebaseSource
import com.application.lose_animals.domain.repository.AnimalRepository
import com.application.lose_animals.domain.repository.AuthRepository
import com.application.lose_animals.domain.usecase.GetAnimalsUseCase
import com.application.lose_animals.domain.usecase.UpdateAnimalUseCase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Provides
    @Singleton
    fun provideFirebaseDatabase(): FirebaseDatabase {
        return FirebaseDatabase.getInstance().apply {
            setPersistenceEnabled(true)
        }
    }

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    @Provides
    @Singleton
    fun provideFirebaseSource(firestore: FirebaseFirestore): FirebaseSource = FirebaseSource(firestore)

    @Provides
    @Singleton
    fun provideAnimalRepository(firebaseSource: FirebaseSource): AnimalRepository = AnimalRepositoryImpl(firebaseSource)

    @Provides
    @Singleton
    fun provideAuthRepository(auth: FirebaseAuth, database: FirebaseDatabase): AuthRepository = AuthRepositoryImpl(auth, database)

    @Provides
    @Singleton
    fun provideGetAnimalsUseCase(repository: AnimalRepository): GetAnimalsUseCase = GetAnimalsUseCase(repository)

    @Provides
    @Singleton
    fun provideUpdateAnimalUseCase(repository: AnimalRepository): UpdateAnimalUseCase = UpdateAnimalUseCase(repository)
}
