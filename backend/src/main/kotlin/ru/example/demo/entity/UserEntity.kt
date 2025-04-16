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

    @Column(unique = true)
    val login: String,

    val name: String,

    val password: String,

    val role: UserRoles,

    @Column(unique = true)
    var token: String,

    @ManyToOne()
    @JoinColumn(name = "company_id", nullable = false)
    val company: UserCompanyEntity,


    ) {

    fun toUser(): User {

        return User(
            login = login,
            name = name,
            password = password,
            token = token,
            role = role,
            company = company.toUserCompany()

        )
    }

    fun checkPassword(userPassword: String) : Boolean {
        return password != userPassword
    }
}