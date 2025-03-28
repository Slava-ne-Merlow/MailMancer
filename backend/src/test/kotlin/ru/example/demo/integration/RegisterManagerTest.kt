package ru.example.demo.integration

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.should
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.startWith
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import ru.example.demo.dto.enums.UserRoles
import ru.example.demo.dto.model.UserCompany
import ru.example.demo.dto.request.RegisterManagerRequest
import ru.example.demo.entity.InviteEntity
import ru.example.demo.entity.UserEntity
import ru.example.demo.exception.type.EntityAlreadyExistsException
import ru.example.demo.exception.type.ExpiredTokenException
import ru.example.demo.exception.type.NotFoundException
import java.time.LocalDateTime
import java.time.Duration


class RegisterManagerTest : AbstractServiceTest() {

    @BeforeEach
    fun init() {
        inviteRepository.deleteAll()
        userRepository.deleteAll()
        userCompanyRepository.deleteAll()
    }

    @Test
    fun `успешная регистрация менеджера`() {
        val company = UserCompany(
            name = "Company",
            email = "email@example.com",
            password = "123456"
        )

        val savedCompany = userCompanyRepository.save(company.toEntity())

        val invite = InviteEntity(
            company = savedCompany,
            token = "valid_invite"
        )

        val savedInvite = inviteRepository.save(invite)

        val request = RegisterManagerRequest(
            inviteToken = savedInvite.token,
            login = "login",
            name = "Name",
            password = "password"
        )

        val savedManager = authService.registerManager(request)

        savedManager shouldNotBe null
        savedManager.company.id shouldBe savedCompany.id
        userRepository.findByLogin("login") shouldNotBe null
    }

    @Test
    fun `ошибка если приглашение не найдено`() {
        val request = RegisterManagerRequest(
            inviteToken = "invalid_token",
            login = "login",
            name = "Name",
            password = "password"
        )

        val exception = shouldThrow<NotFoundException> {
            authService.registerManager(request)
        }

        exception.message should startWith("Приглашение недействительно")
    }

    @Test
    fun `ошибка если логин занят`() {

        val company = UserCompany(
            name = "Company",
            email = "email@example.com",
            password = "123456"
        )


        val existingCompany = userCompanyRepository.save(company.toEntity())

        val user = UserEntity(
            login = "login",
            name = "ExistingName",
            password = "123456",
            role = UserRoles.HEAD,
            company = existingCompany,
            token = "token"
        )

        val existingUser = userRepository.save(user)

        val invite = InviteEntity(
            company = existingCompany,
            token = "valid_invite",
            createdAt = LocalDateTime.now() - Duration.ofHours(25)
        )

        val savedInvite = inviteRepository.save(invite)

        val request = RegisterManagerRequest(
            name = "Name",
            login = "login",
            password = "password",
            inviteToken = savedInvite.token
        )

        val exception = shouldThrow<EntityAlreadyExistsException> {
            authService.registerManager(request)
        }

        exception.message should startWith("Логин ${existingUser.login} занят")
    }

    @Test
    fun `ошибка если приглашение истекло`() {
        val company = UserCompany(
            name = "Company",
            email = "email@example.com",
            password = "123456"
        )

        val savedCompany = userCompanyRepository.save(company.toEntity())

        val invite = InviteEntity(
            company = savedCompany,
            token = "valid_invite",
            createdAt = LocalDateTime.now() - Duration.ofHours(25)
        )

        val savedInvite = inviteRepository.save(invite)

        val request = RegisterManagerRequest(
            name = "Name",
            login = "login",
            password = "123456",
            inviteToken = savedInvite.token
        )

        val exception = shouldThrow<ExpiredTokenException> {
            authService.registerManager(request)
        }

        exception.message should startWith("Приглашение истекло")
    }
}