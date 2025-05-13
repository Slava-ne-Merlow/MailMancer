package ru.example.demo.dto

import ru.example.demo.dto.enums.UserRoles

data class UserDto(
    val username: String,
    val password: String,
    val email: String,
    val name: String,
    val role: UserRoles,
    val companyId: Long
)
