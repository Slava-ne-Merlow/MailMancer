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

    @OneToMany(mappedBy = "company", fetch = FetchType.EAGER, cascade = [CascadeType.ALL])
    val users: MutableList<UserEntity> = mutableListOf(),

    ) {
    fun toUserCompany(): UserCompany = UserCompany(
        name = name,
        email = email,
        password = password,
    )

    override fun toString(): String {
        return "UserCompanyEntity(id=$id, name=$name, email=$email, password=$password, users=${users.map { it.id }})"
    }
}