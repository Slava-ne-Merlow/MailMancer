//package ru.example.demo.integration.auth
//
//import io.kotest.assertions.throwables.shouldThrow
//import io.kotest.matchers.should
//import io.kotest.matchers.shouldNotBe
//import io.kotest.matchers.shouldBe
//import io.kotest.matchers.string.startWith
//import org.junit.jupiter.api.BeforeEach
//import org.junit.jupiter.api.Test
//import ru.example.demo.dto.enums.UserRoles
//import ru.example.demo.dto.model.UserCompany
//import ru.example.demo.dto.request.RegisterManagerRequest
//import ru.example.demo.entity.InviteEntity
//import ru.example.demo.entity.UserEntity
//import ru.example.demo.exception.type.EntityAlreadyExistsException
//import ru.example.demo.exception.type.ExpiredTokenException
//import ru.example.demo.exception.type.NotFoundException
//import ru.example.demo.integration.AbstractServiceTest
//import java.time.LocalDateTime
//import java.time.Duration
//
//
//class RegisterManagerTest : AbstractServiceTest() {
//
//    @BeforeEach
//    fun init() {
//        inviteRepository.deleteAll()
//        userRepository.deleteAll()
//        userCompanyRepository.deleteAll()
//    }
//
//    @Test
//    fun `успешная регистрация менеджера`() {
//        val company = UserCompany(
//            name = "Company",
//        )
//
//        val savedCompany = userCompanyRepository.save(company.toEntity())
//
//        val invite = InviteEntity(
//            company = savedCompany,
//            token = "valid_invite"
//        )
//
//        val savedInvite = inviteRepository.save(invite)
//
//        val request = RegisterManagerRequest(
//            login = "login",
//            email = "email@example.com",
//            name = "Name",
//            password = "password",
//            token = savedInvite.token
//        )
//
//        val savedManager = authService.registerManager(request)
//
//        savedManager shouldNotBe null
//        savedManager.company.id shouldBe savedCompany.id
//        userRepository.findByLogin("login") shouldNotBe null
//    }
//
//    @Test
//    fun `ошибка если приглашение не найдено`() {
//        val request = RegisterManagerRequest(
//            token = "invalid_token",
//            login = "login",
//            name = "Name",
//            password = "password",
//            email = "email@example.com"
//
//        )
//
//        val exception = shouldThrow<NotFoundException> {
//            authService.registerManager(request)
//        }
//
//        exception.message should startWith("Приглашение недействительно")
//    }
//
//    @Test
//    fun `ошибка если логин занят`() {
//
//        val company = UserCompany(
//            name = "Company",
//        )
//
//
//        val existingCompany = userCompanyRepository.save(company.toEntity())
//
//        val user = UserEntity(
//            name = "ExistingName",
//            login = "login",
//            email = "email@example.com",
//            password = "123456",
//            role = UserRoles.HEAD,
//            company = existingCompany,
//            token = "token"
//        )
//
//        val existingUser = userRepository.save(user)
//
//        val invite = InviteEntity(
//            company = existingCompany,
//            token = "valid_invite",
//            createdAt = LocalDateTime.now() - Duration.ofHours(25)
//        )
//
//        val savedInvite = inviteRepository.save(invite)
//
//        val request = RegisterManagerRequest(
//            name = "Name",
//            login = "login",
//            email = "email@example.com",
//            password = "password",
//            token = savedInvite.token
//        )
//
//        val exception = shouldThrow<EntityAlreadyExistsException> {
//            authService.registerManager(request)
//        }
//
//        exception.message should startWith("Логин ${existingUser.login} занят")
//    }
//
//    @Test
//    fun `ошибка если приглашение истекло`() {
//        val company = UserCompany(
//            name = "Company",
//        )
//
//        val savedCompany = userCompanyRepository.save(company.toEntity())
//
//        val invite = InviteEntity(
//            company = savedCompany,
//            token = "valid_invite",
//            createdAt = LocalDateTime.now() - Duration.ofHours(25)
//        )
//
//        val savedInvite = inviteRepository.save(invite)
//
//        val request = RegisterManagerRequest(
//            name = "Name",
//            login = "login",
//            email = "email@example.com",
//            password = "123456",
//            token = savedInvite.token
//        )
//
//        val exception = shouldThrow<ExpiredTokenException> {
//            authService.registerManager(request)
//        }
//
//        exception.message should startWith("Приглашение истекло")
//    }
//}