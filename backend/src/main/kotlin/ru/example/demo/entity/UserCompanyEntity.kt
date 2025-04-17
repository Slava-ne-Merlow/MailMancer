package ru.example.demo.entity

import jakarta.persistence.*
import ru.example.demo.dto.model.UserCompany

@Entity
@Table(name = "user_companies")
data class UserCompanyEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    val name: String,

    @Column(unique = true, nullable = false)
    val email: String,

    val password: String,


    ) {
    fun toUserCompany(): UserCompany = UserCompany(
        name = name,
        email = email,
        password = password,
    )
}