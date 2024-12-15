package com.application.lose_animals.ui.viewModel

import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.lose_animals.data.model.Person
import com.application.lose_animals.data.repository.AuthRepositoryImpl
import com.application.lose_animals.domain.repository.PersonRepository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class AddPersonViewModel @Inject constructor(
    private val personRepository: PersonRepository,
    private val authRepository: AuthRepositoryImpl,
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage
) : ViewModel() {

    fun addPerson(
        name: String,
        description: String,
        lastSeenLocation: String,
        photoUri: String?,
        status: String,
        onComplete: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val userId = authRepository.getCurrentUser()?.id ?: return@launch
                val photoUrl = photoUri?.let { uploadImageToFirebase(it.toUri()) }

                val person = Person(
                    id = "",
                    name = name,
                    description = description,
                    lastSeenLocation = lastSeenLocation,
                    photoUrl = photoUrl,
                    status = status,
                    userId = userId
                )

                personRepository.addPerson(person)
                onComplete(true)
            } catch (e: Exception) {
                e.printStackTrace()
                onComplete(false)
            }
        }
    }

    private suspend fun uploadImageToFirebase(imageUri: Uri): String {
        val storageRef = storage.reference
        val fileRef = storageRef.child("images/${System.currentTimeMillis()}.jpg")

        fileRef.putFile(imageUri).await()
        return fileRef.downloadUrl.await().toString()
    }
}
