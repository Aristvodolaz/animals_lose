package com.application.lose_animals.di

import com.application.lose_animals.data.repository.AnimalRepositoryImpl
import com.application.lose_animals.data.source.FirebaseSource
import com.application.lose_animals.domain.repository.AnimalRepository
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
    fun provideFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseSource(firestore: FirebaseFirestore): FirebaseSource = FirebaseSource(firestore)

    @Provides
    @Singleton
    fun provideAnimalRepository(firebaseSource: FirebaseSource): AnimalRepository = AnimalRepositoryImpl(firebaseSource)
}
