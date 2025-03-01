package com.application.lose_animals.data.source

import com.application.lose_animals.data.model.Person
import com.application.lose_animals.data.model.SosMessage
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import android.util.Log

class FirebaseSource @Inject constructor(private val firestore: FirebaseFirestore) {

    // Получение всех объявлений о пропавших людях
    fun getPersons(): Flow<List<Person>> = flow {
        try {
            val snapshot = firestore.collection("persons").get().await()
            val persons = snapshot.toObjects(Person::class.java)
            emit(persons)
        } catch (e: Exception) {
            Log.e("FirebaseSource", "Ошибка загрузки данных: ${e.localizedMessage}")
            emit(emptyList())
        }
    }

    // Добавление нового человека в базу данных
    suspend fun addPerson(person: Person) {
        try {
            val docRef = firestore.collection("persons").add(person).await()
            val updatedPerson = person.copy(id = docRef.id)
            docRef.set(updatedPerson).await()
            Log.d("FirebaseSource", "Человек добавлен: ${updatedPerson.id}")
        } catch (e: Exception) {
            Log.e("FirebaseSource", "Ошибка добавления: ${e.localizedMessage}")
        }
    }

    // Обновление данных о человеке
    suspend fun updatePerson(person: Person, updatedBy: String) {
        try {
            val personDocRef = firestore.collection("persons").document(person.id)

            val updatedPerson = person.copy(
                lastUpdated = System.currentTimeMillis(),
                updatedBy = updatedBy
            )

            personDocRef.set(updatedPerson).await()
            Log.d("FirebaseSource", "Человек обновлен: ${updatedPerson.id}")
        } catch (e: Exception) {
            Log.e("FirebaseSource", "Ошибка обновления: ${e.localizedMessage}")
        }
    }

    // Удаление человека из базы
    suspend fun deletePerson(personId: String) {
        try {
            firestore.collection("persons").document(personId).delete().await()
            Log.d("FirebaseSource", "Человек удален: $personId")
        } catch (e: Exception) {
            Log.e("FirebaseSource", "Ошибка удаления: ${e.localizedMessage}")
        }
    }

    // Получение конкретного человека по ID
    suspend fun getPersonById(personId: String): Person? {
        return try {
            val snapshot = firestore.collection("persons").document(personId).get().await()
            snapshot.toObject(Person::class.java)
        } catch (e: Exception) {
            Log.e("FirebaseSource", "Ошибка получения данных: ${e.localizedMessage}")
            null
        }
    }

    // --- Добавляем поддержку SOS-сообщений ---

    // Отправка SOS-сообщения в базу данных
    suspend fun sendSosMessage(sosMessage: SosMessage) {
        try {
            val docRef = firestore.collection("sos_alerts").add(sosMessage).await()
            val updatedSosMessage = sosMessage.copy(id = docRef.id)
            docRef.set(updatedSosMessage).await()
            Log.d("FirebaseSource", "SOS сообщение отправлено: ${updatedSosMessage.id}")
        } catch (e: Exception) {
            Log.e("FirebaseSource", "Ошибка отправки SOS: ${e.localizedMessage}")
        }
    }

    // Получение всех SOS-сообщений
    fun getSosMessages(): Flow<List<SosMessage>> = flow {
        try {
            val snapshot = firestore.collection("sos_alerts").get().await()
            val sosMessages = snapshot.toObjects(SosMessage::class.java)
            emit(sosMessages)
        } catch (e: Exception) {
            Log.e("FirebaseSource", "Ошибка загрузки SOS сообщений: ${e.localizedMessage}")
            emit(emptyList())
        }
    }
}
