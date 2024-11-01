package com.application.lose_animals.data.model

data class Animal(
    val id: String,
    val name: String,
    val description: String,
    val location: String,
    val photoUrl: String?,
    val status: String,
    val userId: String // Добавлено поле для связи объявления с пользователем
)
