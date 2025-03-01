package com.application.lose_animals.data.model

data class Person(
    val id: String = "",
    val description: String = "",
    val lastSeenLocation: String = "",
    val name: String = "",
    val photoUrl: String = "",
    val status: String = "",
    val userId: String = "",
    val latitude: Double = 0.0,  // 📍 Добавили широту
    val longitude: Double = 0.0, // 📍 Добавили долготу
    val lastUpdated: Long = System.currentTimeMillis(),
    val updatedBy: String = "",
    val contactPhone: String = "" // Добавили контактный телефон
) {
    // Firestore требует пустой конструктор (по умолчанию)
    constructor() : this("", "", "", "", "", "", "", 0.0, 0.0, System.currentTimeMillis(), "", "")
}
