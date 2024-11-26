package com.application.lose_animals.data.model

data class Animal(
    val id: String = "",  // Default values
    val name: String = "",
    val description: String = "",
    val location: String = "",
    val photoUrl: String? = null,
    val status: String = "",
    val userId: String = ""
)

