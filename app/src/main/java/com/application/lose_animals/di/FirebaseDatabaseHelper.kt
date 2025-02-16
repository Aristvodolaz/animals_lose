package com.application.lose_animals.di

import com.application.lose_animals.data.model.Person
import com.google.firebase.database.FirebaseDatabase

object FirebaseDatabaseHelper {
    private val database = FirebaseDatabase.getInstance()
    val chatRef = database.getReference("chats")

    fun updatePerson(person: Person) {
        val updatedPerson = person.copy(lastUpdated = System.currentTimeMillis(), updatedBy = "Admin")
        FirebaseDatabaseHelper.database.getReference("persons").child(person.id).setValue(updatedPerson)
    }

}
