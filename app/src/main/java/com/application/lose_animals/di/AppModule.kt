package com.application.lose_animals.di

import com.application.lose_animals.data.api.DaDataApiService
import com.application.lose_animals.data.repository.AddressRepository
import com.application.lose_animals.data.repository.AuthRepositoryImpl
import com.application.lose_animals.data.repository.PersonRepositoryImpl
import com.application.lose_animals.data.source.FirebaseSource
import com.application.lose_animals.domain.repository.AuthRepository
import com.application.lose_animals.domain.repository.PersonRepository
import com.application.lose_animals.domain.usecase.GetPersonsUseCase
import com.application.lose_animals.domain.usecase.UpdatePersonUseCase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
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
    fun provideFirebaseStorage(): FirebaseStorage {
        return FirebaseStorage.getInstance() // Provide Firebase Storage instance
    }

    @Provides
    @Singleton
    fun provideFirebaseSource(firestore: FirebaseFirestore): FirebaseSource = FirebaseSource(firestore)

    @Provides
    @Singleton
    fun providePersonRepository(firebaseSource: FirebaseSource): PersonRepository = PersonRepositoryImpl(firebaseSource)

    @Provides
    @Singleton
    fun provideAuthRepository(auth: FirebaseAuth, database: FirebaseDatabase): AuthRepository = AuthRepositoryImpl(auth, database)

    @Provides
    @Singleton
    fun provideGetPersonsUseCase(repository: PersonRepository): GetPersonsUseCase = GetPersonsUseCase(repository)

    @Provides
    @Singleton
    fun provideUpdatePersonUseCase(repository: PersonRepository): UpdatePersonUseCase = UpdatePersonUseCase(repository)
    
    @Provides
    @Singleton
    fun provideAddressRepository(daDataApiService: DaDataApiService): AddressRepository = AddressRepository(daDataApiService)
}
