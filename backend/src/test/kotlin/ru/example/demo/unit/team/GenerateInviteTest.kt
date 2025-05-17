package ru.example.demo.unit.team

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.startWith
import io.mockk.every
import io.mockk.verify
import org.junit.jupiter.api.Test
import ru.example.demo.dto.enums.UserRoles
import ru.example.demo.dto.model.User
import ru.example.demo.dto.model.UserCompany
import ru.example.demo.exception.type.UnauthorizedException
import ru.example.demo.unit.AbstractUnitTest


class GenerateInviteTest : AbstractUnitTest() {
    @Test
    fun `успешное создание приглашения`() {
        val userToken = "token"

        val company = UserCompany(
            name = "name",
        )

        val user = User(
            name = "name",
            login = "login",
            email = "email@example.com",
            password = "123456",
            role = UserRoles.HEAD,
            company = company
        )


        every { tokenService.generateToken() } returns "token"
        every { inviteRepository.save(any()) } answers { firstArg() }

        val message = teamService.generateInvite(userToken)


        verify(exactly = 1) { inviteRepository.save(any()) }
        verify(exactly = 1) { tokenService.generateToken() }

        message shouldBe "http://localhost:3000/register?token=token"
    }

    @Test
    fun `ошибка если токен приглашения истёк`() {
        val userToken = "token"

        val exception = shouldThrow<UnauthorizedException> {
            teamService.generateInvite(userToken)
        }

        exception.message should startWith("Недействителен токен авторизации")
    }
}