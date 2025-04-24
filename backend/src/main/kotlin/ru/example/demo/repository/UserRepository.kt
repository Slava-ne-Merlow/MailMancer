package ru.example.demo.repository


import org.springframework.data.jpa.repository.JpaRepository
import ru.example.demo.entity.UserEntity


interface UserRepository : JpaRepository<UserEntity, Long> {
    fun findByToken(token: String): UserEntity?
    fun findByLogin(login: String): UserEntity?
    fun findAllByCompany(company: UserCompanyEntity): List<User>
}