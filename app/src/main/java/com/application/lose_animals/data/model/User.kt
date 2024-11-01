package com.application.lose_animals.data.model

data class User(
    val id: String = "", // Уникальный идентификатор пользователя
    val email: String = "",
    val username: String = "",
    val publishedAds: List<String> = listOf() // Список ID объявлений, опубликованных пользователем
)
