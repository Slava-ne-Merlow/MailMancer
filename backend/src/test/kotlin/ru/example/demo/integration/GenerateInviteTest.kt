package ru.example.demo.integration

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldStartWith
import io.kotest.matchers.string.startWith
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import ru.example.demo.dto.enums.UserRoles
import ru.example.demo.entity.UserCompanyEntity
import ru.example.demo.entity.UserEntity
import ru.example.demo.exception.type.UnauthorizedException


class GenerateInviteTest : AbstractServiceTest() {
    lateinit var savedCompany: UserCompanyEntity
    lateinit var savedUser: UserEntity

    @BeforeEach
    fun init() {
        inviteRepository.deleteAll()
        userRepository.deleteAll()
        userCompanyRepository.deleteAll()

        val company = UserCompanyEntity(
            name = "name",
            email = "email@example.com",
            password = "123456"
        )

        savedCompany = userCompanyRepository.save(company)

        val user = UserEntity(
            login = "login",
            name = "name",
            password = "123456",
            role = UserRoles.HEAD,
            company = savedCompany,
            token = "old_token"
        )
        savedUser = userRepository.save(user)
    }

    @Test
    fun `успешное создание приглашения`() {

        val inviteUrl = authService.generateInvite(savedUser.token)

        inviteUrl shouldStartWith "https://localhost:3000/register?token="

        inviteRepository.findAll().size shouldBe 1
    }

    @Test
    fun `ошибка если токен приглашения истёк`() {
        val exception = shouldThrow<UnauthorizedException> {
            authService.generateInvite("invalid_token")
        }

        exception.message should startWith("Недействителен токен авторизации")
    }
}
