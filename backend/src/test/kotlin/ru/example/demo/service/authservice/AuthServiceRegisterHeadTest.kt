package ru.example.demo.service.authservice

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.should
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.startWith
import ru.example.demo.dto.enums.UserRoles
import ru.example.demo.dto.model.User
import ru.example.demo.dto.model.UserCompany
import ru.example.demo.dto.request.RegisterHeadRequest
import ru.example.demo.exception.type.EntityAlreadyExistsException
import ru.example.demo.repository.InviteRepository
import ru.example.demo.repository.UserCompanyRepository
import ru.example.demo.repository.UserRepository
import ru.example.demo.service.AuthService
import ru.example.demo.service.TokenService
import java.util.*


class AuthServiceRegisterHeadTest {
    private val userRepository = mockk<UserRepository>()
    private val userCompanyRepository = mockk<UserCompanyRepository>()
    private val inviteRepository = mockk<InviteRepository>()
    private val tokenService = mockk<TokenService>()
    private val authService = AuthService(userRepository, userCompanyRepository, inviteRepository, tokenService)

    @Test
    fun `когда head успешно регистрируется, должны появиться новая компания и пользователь`() {
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
        every { tokenService.generateToken() } returns "token"

        val savedUser = authService.registerHead(request).toUser()

        savedUser shouldBe newHead
        savedUser.company shouldBe newCompany

    }

    @Test
    fun `когда head регистрируется, email компании уже занят`() {
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
        exception.message should startWith("Почта email@example.com занята")
    }

    @Test
    fun `когда head регистрируется, login пользователя уже занят`() {
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
        exception.message should startWith("Логин admin занят")
    }

}