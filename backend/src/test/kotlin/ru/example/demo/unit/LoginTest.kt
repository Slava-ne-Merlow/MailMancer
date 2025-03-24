package ru.example.demo.unit

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
import ru.example.demo.dto.request.LoginUserRequest
import ru.example.demo.exception.type.NotFoundException
import ru.example.demo.exception.type.UnauthorizedException


class LoginTest : AbstractUnitTest() {
    @Test
    fun `успешная авторизация`() {
        val request = LoginUserRequest(
            login = "login",
            password = "123456",
        )

        val company = UserCompany(
            name = "name",
            email = "email@example.com",
            password = "123456"
        )

        val user = User(
            login = "login",
            name = "name",
            password = "123456",
            role = UserRoles.MANAGER,
            company = company,
            token = "token"
        )

        every { userRepository.findByLogin(request.login) } answers { user.toEntity() }
        every { tokenService.generateToken() } returns "token"
        every { userRepository.save(any()) } answers { firstArg() }

        val savedUser = authService.loginUser(request).toUser()


        verify(exactly = 1) { userRepository.save(any()) }
        verify(exactly = 1) { tokenService.generateToken() }

        savedUser shouldBe user
    }

    @Test
    fun `авторизация, но неверный логин`(){
        val request = LoginUserRequest(
            login = "неверный логин",
            password = "123456",
        )

        every { userRepository.findByLogin(request.login) } answers { null }

        val exception = shouldThrow<NotFoundException> {
            authService.loginUser(request)
        }

        exception.message should startWith("Логина ${request.login} не существует")

    }

    @Test
    fun `авторизация, но неверный пароль`(){
        val request = LoginUserRequest(
            login = "login",
            password = "неверный пароль",
        )

        val company = UserCompany(
            name = "name",
            email = "email@example.com",
            password = "123456"
        )

        val user = User(
            login = "login",
            name = "name",
            password = "123456",
            role = UserRoles.MANAGER,
            company = company,
            token = "token"
        )

        every { userRepository.findByLogin(request.login) } answers { user.toEntity() }


        val exception = shouldThrow<UnauthorizedException> {
            authService.loginUser(request)
        }

        exception.message should startWith("Неверный логин или пароль")
    }
}