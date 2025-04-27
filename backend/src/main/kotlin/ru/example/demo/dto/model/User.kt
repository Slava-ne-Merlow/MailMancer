package ru.example.demo.dto.model

import ru.example.demo.dto.enums.UserRoles
import ru.example.demo.entity.UserEntity

data class User(
    val name: String,
    val login: String,
    val email: String,
    val password: String,
    val role: UserRoles,
    val token: String,
    val company: UserCompany
) {
    fun toEntity() = UserEntity(
        name = name,
        login = login,
        email = email,
        password = password,
        role = role,
        token = token,
        company = company.toEntity()
    )
}