package ru.example.demo.integration.auth

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.should
import org.junit.jupiter.api.Test
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.startWith
import io.mockk.every
import org.junit.jupiter.api.BeforeEach
import ru.example.demo.dto.enums.UserRoles
import ru.example.demo.dto.model.UserCompany
import ru.example.demo.dto.request.RegisterHeadRequest
import ru.example.demo.entity.UserEntity
import ru.example.demo.exception.type.EntityAlreadyExistsException
import ru.example.demo.exception.type.UnauthorizedException
import ru.example.demo.integration.AbstractServiceTest


class RegisterHeadTest : AbstractServiceTest() {

    @BeforeEach
    fun init() {
        inviteRepository.deleteAll()
        userRepository.deleteAll()
        userCompanyRepository.deleteAll()
    }

    @Test
    fun `успешная регистрация`() {
        val request = RegisterHeadRequest(
            name = "Name",
            login = "admin",
            password = "123456",
            email = "email@example.com",
        )

        val savedUser = authService.registerHead(request)

        savedUser shouldNotBe null
        savedUser.company.name shouldBe request.name + "'s Team"

        userRepository.findByLogin("admin") shouldNotBe null
        userCompanyRepository.findById(savedUser.company.id) shouldNotBe null
    }

    @Test
    fun `ошибка если почта уже занята`() {
        val request1 = RegisterHeadRequest(
            name = "Name",
            login = "admin",
            password = "123456",
            email = "email@example.com",
        )

        val existingUser = authService.registerHead(request1)

        val request = RegisterHeadRequest(
            name = "Name",
            login = "admin",
            password = "123456",
            email = existingUser.email
        )

        val exception = shouldThrow<EntityAlreadyExistsException> {
            authService.registerHead(request)
        }
        exception.message should startWith("Почта ${request.email} занята")
    }

    @Test
    fun `ошибка если логин уже занят`() {
        val company = UserCompany(
            name = "Company",
        )

        val existingCompany = userCompanyRepository.save(company.toEntity())

        val user = UserEntity(
            name = "ExistingName",
            login = "admin",
            email = "email@example.com",
            password = "123456",
            role = UserRoles.HEAD,
            company = existingCompany,
            token = "token"
        )

        val existingUser = userRepository.save(user)

        val request = RegisterHeadRequest(
            name = "NewName",
            login = existingUser.login,
            password = "123456",
            email = "new_email@example.com",
        )

        val exception = shouldThrow<EntityAlreadyExistsException> {
            authService.registerHead(request)
        }
        exception.message should startWith("Логин ${request.login} занят")
    }
}