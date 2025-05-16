package ru.example.demo.entity


import jakarta.persistence.*
import ru.example.demo.dto.enums.UserRoles
import ru.example.demo.dto.model.User

@Entity
@Table(name = "users")
data class UserEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    val name: String,

    @Column(unique = true)
    val login: String,

    @Column(unique = true)
    val email: String,

    val password: String,

    val role: UserRoles,

    @ManyToOne()
    @JoinColumn(name = "company_id", nullable = false)
    val company: UserCompanyEntity,

    ) {
    fun toUser(): User {

        return User(
            name = name,
            login = login,
            email = email,
            password = password,
            role = role,
            company = company.toUserCompany()
        )
    }
}