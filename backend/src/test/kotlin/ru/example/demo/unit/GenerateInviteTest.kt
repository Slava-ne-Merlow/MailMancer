package ru.example.demo.unit

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.startWith
import io.mockk.every
import io.mockk.verify
import org.junit.jupiter.api.Test
import ru.example.demo.dto.enums.UserRoles
import ru.example.demo.dto.model.Invite
import ru.example.demo.dto.model.User
import ru.example.demo.dto.model.UserCompany
import ru.example.demo.exception.type.UnauthorizedException


class GenerateInviteTest : AbstractUnitTest() {
    @Test
    fun `генерация приглашения`() {
        val userToken = "token"

        val company = UserCompany(
            name = "name",
            email = "email@example.com",
            password = "123456"
        )

        val user = User(
            login = "login",
            name = "name",
            password = "123456",
            role = UserRoles.HEAD,
            company = company,
            token = "token"
        )

        val invite = Invite(
            token = "token",
            company = company,
        )

        every { userRepository.findByToken(userToken) } answers { user.toEntity() }
        every { tokenService.generateToken() } returns "token"
        every { inviteRepository.save(any()) } answers { firstArg() }

        val message = authService.generateInvite(userToken)


        verify(exactly = 1) { userRepository.findByToken(userToken) }
        verify(exactly = 1) { inviteRepository.save(any()) }
        verify(exactly = 1) { tokenService.generateToken() }

        message shouldBe "https://localhost:8080/manager/sign-up?token=token"
    }

    @Test
    fun `генерация приглашения, но авторизация недействительна`() {
        val userToken = "token"

        every { userRepository.findByToken(userToken) } answers { null }

        val exception = shouldThrow<UnauthorizedException> {
            authService.generateInvite(userToken)
        }

        exception.message should startWith("Недействителен токен авторизации")
    }
}