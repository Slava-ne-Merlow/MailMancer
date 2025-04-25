package ru.example.demo.integration.auth

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.should
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.startWith
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import ru.example.demo.dto.enums.UserRoles
import ru.example.demo.dto.model.UserCompany
import ru.example.demo.dto.request.LoginUserRequest
import ru.example.demo.entity.UserCompanyEntity
import ru.example.demo.entity.UserEntity
import ru.example.demo.exception.type.NotFoundException
import ru.example.demo.exception.type.UnauthorizedException
import ru.example.demo.integration.AbstractServiceTest


class LoginTest : AbstractServiceTest() {

    lateinit var savedUser: UserEntity
    lateinit var savedCompany: UserCompanyEntity

    @BeforeEach
    fun init() {
        inviteRepository.deleteAll()
        userRepository.deleteAll()
        userCompanyRepository.deleteAll()

        val company = UserCompany(
            name = "name",
            email = "email@example.com",
            password = "123456"
        )

        savedCompany = userCompanyRepository.save(company.toEntity())

        val user = UserEntity(
            login = "login",
            name = "name",
            password = "123456",
            role = UserRoles.MANAGER,
            company = savedCompany,
            token = "old_token"
        )
        savedUser = userRepository.save(user)
    }

    @Test
    fun `успешная авторизация`() {
        val request = LoginUserRequest(
            login = savedUser.login,
            password = savedUser.password,
        )

        val loggedInUser = authService.loginUser(request)

        loggedInUser shouldNotBe null
        loggedInUser.token shouldNotBe "old_token"
    }

    @Test
    fun `ошибка если логин не найден`() {
        val request = LoginUserRequest(
            login = "non_existing_user",
            password = savedUser.password,
        )

        val exception = shouldThrow<NotFoundException> {
            authService.loginUser(request)
        }

        exception.message should startWith("Логин ${request.login} занят")
    }

    @Test
    fun `ошибка если пароль неверный`() {
        val request = LoginUserRequest(
            login = savedUser.login,
            password = "non_correct_password"
        )

        val exception = shouldThrow<UnauthorizedException> {
            authService.loginUser(request)
        }

        exception.message should startWith("Неверный логин или пароль")
    }
}