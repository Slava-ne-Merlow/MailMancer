package ru.example.demo.unit.auth

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.startWith
import io.mockk.every
import org.junit.jupiter.api.Test
import ru.example.demo.dto.enums.UserRoles
import ru.example.demo.dto.model.Invite
import ru.example.demo.dto.model.UserCompany
import ru.example.demo.dto.model.User
import ru.example.demo.dto.request.RegisterManagerRequest
import ru.example.demo.exception.type.EntityAlreadyExistsException
import ru.example.demo.exception.type.ExpiredTokenException
import ru.example.demo.exception.type.NotFoundException
import ru.example.demo.unit.AbstractUnitTest
import java.time.LocalDateTime
import java.time.Duration


class RegisterManagerTest : AbstractUnitTest() {
    @Test
    fun `успешная регистрация`() {
        val request = RegisterManagerRequest(
            name = "Name",
            login = "login",
            password = "password",
            inviteToken = "token"
        )

        val company = UserCompany(
            name = "Company",
            email = "email@example.com",
            password = "123456"
        )

        val invite = Invite(
            token = "token",
            company = company
        )

        val user = User(
            login = request.login,
            name = request.name,
            password = request.password,
            role = UserRoles.MANAGER,
            company = company,
            token = "token"
        )

        every { inviteRepository.findByToken("token") } answers { invite.toEntity() }
        every { userRepository.findByLogin(request.login) } answers { null }
        every { tokenService.generateToken() } answers { "token" }
        every { userRepository.save(any()) } answers { firstArg() }

        val savedUser = authService.registerManager(request).toUser()

        savedUser shouldBe user


    }

    @Test
    fun `ошибка если приглашение не найдено`() {
        val request = RegisterManagerRequest(
            name = "Name",
            login = "login",
            password = "password",
            inviteToken = "token"
        )

        every { inviteRepository.findByToken("token") } answers { null }


        val exception = shouldThrow<NotFoundException> {
            authService.registerManager(request)
        }

        exception.message should startWith("Приглашение недействительно")
    }

    @Test
    fun `ошибка если логин занят`() {
        val request = RegisterManagerRequest(
            name = "Name",
            login = "login",
            password = "password",
            inviteToken = "token"
        )

        val company = UserCompany(
            name = "Company",
            email = "email@example.com",
            password = "123456"
        )

        val invite = Invite(
            token = "token",
            company = company
        )

        val oldUser = User(
            login = request.login,
            name = request.name,
            password = request.password,
            role = UserRoles.MANAGER,
            company = company,
            token = "token"
        )

        every { inviteRepository.findByToken("token") } answers { invite.toEntity() }
        every { userRepository.findByLogin(request.login) } answers { oldUser.toEntity() }


        val exception = shouldThrow<EntityAlreadyExistsException> {
            authService.registerManager(request)
        }

        exception.message should startWith("Логин ${request.login} занят")


    }

    @Test
    fun `ошибка если прглашение истекло`() {
        val request = RegisterManagerRequest(
            name = "Name",
            login = "login",
            password = "password",
            inviteToken = "token"
        )

        val company = UserCompany(
            name = "Company",
            email = "email@example.com",
            password = "123456"
        )

        val invite = Invite(
            token = "token",
            company = company,
            createdAt = LocalDateTime.now() - Duration.ofHours(25),
        )


        every { inviteRepository.findByToken("token") } answers { invite.toEntity() }
        every { userRepository.findByLogin(request.login) } answers { null }


        val exception = shouldThrow<ExpiredTokenException> {
            authService.registerManager(request)
        }

        exception.message should startWith("Приглашение истекло")

    }
}