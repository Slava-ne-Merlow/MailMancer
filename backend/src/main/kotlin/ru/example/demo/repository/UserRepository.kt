package ru.example.demo.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.example.demo.entity.UserCompanyEntity
import ru.example.demo.entity.UserEntity

interface UserRepository : JpaRepository<UserEntity, Long> {
    fun findByLogin(login: String): UserEntity?
    fun findAllByCompany(company: UserCompanyEntity): List<UserEntity>
    fun deleteByLogin(login: String)
    fun findByEmail(email: String): UserEntity?
    fun findByToken(token: String): UserEntity?
    fun findCompanyById(id: Long): UserCompanyEntity?
}