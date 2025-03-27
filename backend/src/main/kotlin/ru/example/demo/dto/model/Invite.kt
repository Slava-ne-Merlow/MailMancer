package ru.example.demo.dto.model

import ru.example.demo.entity.InviteEntity
import java.time.LocalDateTime

data class Invite(
    val token: String,
    val company: UserCompany,
    val createdAt: LocalDateTime = LocalDateTime.now()
) {
    fun toEntity() = InviteEntity (
        token = token,
        company = company.toEntity(),
        createdAt = createdAt
    )
}