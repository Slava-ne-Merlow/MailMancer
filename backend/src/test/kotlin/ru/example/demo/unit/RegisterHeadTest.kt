package ru.example.demo.unit

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.should
import io.mockk.every
import io.mockk.verify
import org.junit.jupiter.api.Test
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.startWith
import ru.example.demo.dto.enums.UserRoles
import ru.example.demo.dto.model.User
import ru.example.demo.dto.model.UserCompany
import ru.example.demo.dto.request.RegisterHeadRequest
import ru.example.demo.exception.type.EntityAlreadyExistsException
import ru.example.demo.exception.type.UnauthorizedException


class RegisterHeadTest : AbstractUnitTest() {
    @Test
    fun `успешная регистрация`() {
        val request = RegisterHeadRequest(
            headLogin = "admin",
            headName = "Name",
            headPassword = "123456",
            companyName = "Company",
            email = "email@example.com",
            emailPassword = "123456",
        )

        val newCompany = UserCompany(
            name = request.companyName,
            email = request.email,
            password = request.emailPassword
        )


        val newHead = User(
            login = request.headLogin,
            name = request.headName,
            password = request.headPassword,
            role = UserRoles.HEAD,
            company = newCompany,
            token = "token"
        )

        every { userRepository.save(any()) } answers { firstArg() }
        every { userCompanyRepository.save(any()) } answers { firstArg() }
        every { userRepository.findByLogin(any()) } answers { null }
        every { userCompanyRepository.findByEmail(any()) } answers { null }
        every { emailService.testConnection(any(), any()) } answers { true }
        every { tokenService.generateToken() } returns "token"

        val savedUser = authService.registerHead(request).toUser()

        verify(exactly = 1) { userCompanyRepository.save(any()) }
        verify(exactly = 1) { userRepository.save(any()) }
        verify(exactly = 1) { tokenService.generateToken() }

        savedUser shouldBe newHead
        savedUser.company shouldBe newCompany

    }

    @Test
    fun `ошибка если почта компании уже занята`() {
        val request = RegisterHeadRequest(
            headLogin = "admin",
            headName = "Name",
            headPassword = "123456",
            companyName = "Company",
            email = "email@example.com",
            emailPassword = "123456",
        )

        val oldCompany = UserCompany(
            name = "Name",
            email = "email@example.com",
            password = "123456"
        )

        every { userCompanyRepository.findByEmail(any()) } answers { oldCompany.toEntity() }

        val exception = shouldThrow<EntityAlreadyExistsException> {
            authService.registerHead(request)
        }
        exception.message should startWith("Почта ${request.email} занята")
    }

    @Test
    fun `ошибка если пароль к почте неверный`() {
        val request = RegisterHeadRequest(
            headLogin = "admin",
            headName = "Name",
            headPassword = "123456",
            companyName = "Company",
            email = "email@example.com",
            emailPassword = "123456",
        )


        every { userRepository.save(any()) } answers { firstArg() }
        every { userCompanyRepository.save(any()) } answers { firstArg() }
        every { userRepository.findByLogin(any()) } answers { null }
        every { userCompanyRepository.findByEmail(any()) } answers { null }
        every { emailService.testConnection(any(), any()) } answers { false }
        every { tokenService.generateToken() } returns "token"

        val exception = shouldThrow<UnauthorizedException> {
            authService.registerHead(request)
        }
        exception.message should startWith("Email ${request.email} не прошёл проверку")
    }

    @Test
    fun `шибка если логин уже занят`() {
        val request = RegisterHeadRequest(
            headLogin = "admin",
            headName = "Name",
            headPassword = "123456",
            companyName = "Company",
            email = "email@example.com",
            emailPassword = "123456",
        )

        val oldCompany = UserCompany(
            name = request.companyName,
            email = request.email,
            password = request.emailPassword
        )

        val oldHead = User(
            login = request.headLogin,
            name = request.headName,
            password = request.headPassword,
            role = UserRoles.HEAD,
            company = oldCompany,
            token = "token"
        )
        every { userCompanyRepository.findByEmail(any()) } answers { null }
        every { userRepository.findByLogin(any()) } answers { oldHead.toEntity() }

        val exception = shouldThrow<EntityAlreadyExistsException> {
            authService.registerHead(request)
        }
        exception.message should startWith("Логин ${request.headLogin} занят")
    }
}