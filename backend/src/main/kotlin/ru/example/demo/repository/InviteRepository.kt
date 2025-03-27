package ru.example.demo.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.example.demo.entity.InviteEntity

interface InviteRepository : JpaRepository<InviteEntity, Long> {
    fun findByToken(userToken: String): InviteEntity?
}