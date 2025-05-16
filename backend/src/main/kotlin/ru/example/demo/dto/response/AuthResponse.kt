package ru.example.demo.dto.response

import ru.example.demo.dto.enums.UserRoles

data class AuthResponse(
    val token: String,
    val role: UserRoles,
    val login: String,
    val name: String,
)