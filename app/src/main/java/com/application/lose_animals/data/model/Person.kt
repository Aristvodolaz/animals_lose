package com.application.lose_animals.data.model

data class Person(
    val id: String = "",
    val description: String = "",
    val lastSeenLocation: String = "",
    val name: String = "",
    val photoUrl: String = "",
    val status: String = "",
    val userId: String = ""
) {
    // Firestore требует пустой конструктор (по умолчанию)
    constructor() : this("", "", "", "", "", "", "")
}
