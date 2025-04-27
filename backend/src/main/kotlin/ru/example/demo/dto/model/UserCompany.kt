package ru.example.demo.dto.model

import ru.example.demo.entity.UserCompanyEntity

data class UserCompany(
    val name: String
) {
    fun toEntity() = UserCompanyEntity(
        name = name
    )
}