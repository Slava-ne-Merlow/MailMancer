package ru.example.demo.integration

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.should
import org.junit.jupiter.api.Test
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.startWith
import org.junit.jupiter.api.BeforeEach
import ru.example.demo.dto.enums.UserRoles
import ru.example.demo.dto.model.UserCompany
import ru.example.demo.dto.request.RegisterHeadRequest
import ru.example.demo.entity.UserEntity
import ru.example.demo.exception.type.EntityAlreadyExistsException


class RegisterHeadTest : AbstractServiceTest() {

    @BeforeEach
    fun init() {
        userRepository.deleteAll()
        userCompanyRepository.deleteAll()
    }

    @Test
    fun `успешная регистрация`() {
        val request = RegisterHeadRequest(
            headLogin = "admin",
            headName = "Name",
            headPassword = "123456",
            companyName = "Company",
            email = "email@example.com",
            emailPassword = "123456"
        )

        val savedUser = authService.registerHead(request)

        savedUser shouldNotBe null
        savedUser.company.email shouldBe request.email

        userRepository.findByLogin("admin") shouldNotBe null
        userCompanyRepository.findByEmail("email@example.com") shouldNotBe null
    }


    @Test
    fun `ошибка если почта компании уже занята`() {
        val company = UserCompany(
            name = "Company",
            email = "email@example.com",
            password = "123456"
        )

        val existingCompany = userCompanyRepository.save(company.toEntity())

        val request = RegisterHeadRequest(
            headLogin = "admin",
            headName = "Name",
            headPassword = "123456",
            companyName = "NewCompany",
            email = existingCompany.email,
            emailPassword = "123456"
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
            email = "email@example.com",
            password = "123456"
        )


        val existingCompany = userCompanyRepository.save(company.toEntity())

        val user = UserEntity(
            login = "admin",
            name = "ExistingName",
            password = "123456",
            role = UserRoles.HEAD,
            company = existingCompany,
            token = "token"
        )

        val existingUser = userRepository.save(user)

        val request = RegisterHeadRequest(
            headLogin = existingUser.login,
            headName = "NewName",
            headPassword = "123456",
            companyName = "Company",
            email = "new_email@example.com",
            emailPassword = "123456"
        )

        val exception = shouldThrow<EntityAlreadyExistsException> {
            authService.registerHead(request)
        }
        exception.message should startWith("Логин ${request.headLogin} занят")
    }
}