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


class GetTeamTest : AbstractUnitTest() {
    @Test
    fun `успешный запрос команды`() {
        val userToken = "token1"

        val company = UserCompany(
            name = "name"
        )

        val user1 = User(
            name = "name1",
            login = "login1",
            email = "email@example.com1",
            password = "123456",
            role = UserRoles.HEAD,
            company = company,
            token = "token1"
        )

        val user2 = User(
            name = "name2",
            login = "login2",
            email = "email@example.com2",
            password = "123456",
            role = UserRoles.MANAGER,
            company = company,
            token = "token1"
        )


        every { userRepository.findByToken(userToken) } answers { user1.toEntity() }
        every { userRepository.findAllByCompany(company.toEntity()) } answers { listOf(user1.toEntity(), user2.toEntity()) }


        val users = teamService.getTeam(userToken)


        verify(exactly = 1) { userRepository.findByToken(userToken) }
        verify(exactly = 1) { userRepository.findAllByCompany(company.toEntity()) }

        users shouldBe listOf(user1.toEntity(), user2.toEntity())
    }

    @Test
    fun `ошибка если токен авторизации не существет`() {
        val userToken = "token"

        every { userRepository.findByToken(userToken) } answers { null }

        val exception = shouldThrow<UnauthorizedException> {
            teamService.getTeam(userToken)
        }

        exception.message should startWith("Недействителен токен авторизации")
    }
}