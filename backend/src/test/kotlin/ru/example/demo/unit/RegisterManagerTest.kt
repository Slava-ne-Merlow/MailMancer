package ru.example.demo.unit

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
import java.time.LocalDateTime
import java.time.Duration


class RegisterManagerTest : AbstractUnitTest() {
    @Test
    fun `успешная регистрация`() {
        val request = RegisterManagerRequest(
            managerName = "Name",
            managerLogin = "login",
            managerPassword = "password",
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
            login = request.managerLogin,
            name = request.managerName,
            password = request.managerPassword,
            role = UserRoles.MANAGER,
            company = company,
            token = "token"
        )

        every { inviteRepository.findByToken("token") } answers { invite.toEntity() }
        every { userRepository.findByLogin(request.managerLogin) } answers { null }
        every { tokenService.generateToken() } answers { "token" }
        every { userRepository.save(any()) } answers { firstArg() }

        val savedUser = authService.registerManager(request).toUser()

        savedUser shouldBe user


    }

    @Test
    fun `регистрация, но приглашения не существует`() {
        val request = RegisterManagerRequest(
            managerName = "Name",
            managerLogin = "login",
            managerPassword = "password",
            inviteToken = "token"
        )

        every { inviteRepository.findByToken("token") } answers { null }


        val exception = shouldThrow<NotFoundException> {
            authService.registerManager(request)
        }

        exception.message should startWith("Приглашение недействительно")
    }

    @Test
    fun `регистрация, но login уже занят `() {
        val request = RegisterManagerRequest(
            managerName = "Name",
            managerLogin = "login",
            managerPassword = "password",
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
            login = request.managerLogin,
            name = request.managerName,
            password = request.managerPassword,
            role = UserRoles.MANAGER,
            company = company,
            token = "token"
        )

        every { inviteRepository.findByToken("token") } answers { invite.toEntity() }
        every { userRepository.findByLogin(request.managerLogin) } answers { oldUser.toEntity() }


        val exception = shouldThrow<EntityAlreadyExistsException> {
            authService.registerManager(request)
        }

        exception.message should startWith("Логин ${request.managerLogin} занят")


    }

    @Test
    fun `регистрация, но приглашение истекло`() {
        val request = RegisterManagerRequest(
            managerName = "Name",
            managerLogin = "login",
            managerPassword = "password",
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
        every { userRepository.findByLogin(request.managerLogin) } answers { null }


        val exception = shouldThrow<ExpiredTokenException> {
            authService.registerManager(request)
        }

        exception.message should startWith("Приглашение истекло")

    }
}