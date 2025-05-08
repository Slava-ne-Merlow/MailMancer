package ru.example.demo.dto.request

data class RegisterManagerRequest(
    val name: String,
    val login: String,
    val email: String,
    val password: String,
    val token: String
)