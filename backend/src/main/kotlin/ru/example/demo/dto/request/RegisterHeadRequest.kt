package ru.example.demo.dto.request

data class RegisterHeadRequest(
    val name: String,
    val login: String,
    val password: String,
    val email: String,
)