package ru.example.demo.dto.response

import ru.example.demo.dto.enums.UserRoles

data class MemberRequestResponse(
    val name: String,
    val login: String,
    val role: UserRoles,
    val email: String
)