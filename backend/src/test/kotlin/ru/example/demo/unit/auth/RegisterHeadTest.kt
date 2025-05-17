package ru.example.demo.unit.auth
//
//import io.kotest.assertions.throwables.shouldThrow
//import io.kotest.matchers.should
//import io.mockk.every
//import io.mockk.verify
//import org.junit.jupiter.api.Test
//import io.kotest.matchers.shouldBe
//import io.kotest.matchers.string.startWith
//import ru.example.demo.dto.enums.UserRoles
//import ru.example.demo.dto.model.User
//import ru.example.demo.dto.model.UserCompany
//import ru.example.demo.dto.request.RegisterHeadRequest
//import ru.example.demo.exception.type.EntityAlreadyExistsException
//import ru.example.demo.exception.type.UnauthorizedException
//import ru.example.demo.unit.AbstractUnitTest
//
//
//class RegisterHeadTest : AbstractUnitTest() {
//    @Test
//    fun `успешная регистрация`() {
//        val request = RegisterHeadRequest(
//            name = "Name",
//            login = "admin",
//            email = "email@example.com",
//            password = "123456"
//        )
//
//        val newCompany = UserCompany(
//            name = request.name + "'s Team",
//        )
//
//
//        val newHead = User(
//            name = request.name,
//            login = request.login,
//            email = request.email,
//            password = request.password,
//            role = UserRoles.HEAD,
//            company = newCompany,
//        )
//
//        every { userRepository.save(any()) } answers { firstArg() }
//        every { userCompanyRepository.save(any()) } answers { firstArg() }
//        every { userRepository.findByLogin(any()) } answers { null }
//        every { userRepository.findByEmail(any()) } answers { null }
//        every { tokenService.generateToken() } returns "token"
//
//        val savedUser = authService.registerHead(request).toUser()
//
//        verify(exactly = 1) { userCompanyRepository.save(any()) }
//        verify(exactly = 1) { userRepository.save(any()) }
//        verify(exactly = 1) { tokenService.generateToken() }
//
//        savedUser shouldBe newHead
//        savedUser.company shouldBe newCompany
//
//    }
//
//    @Test
//    fun `ошибка если почта уже занята`() {
//        val request = RegisterHeadRequest(
//            name = "Name",
//            login = "admin",
//            password = "123456",
//            email = "email@example.com",
//        )
//
//        val company = UserCompany(
//            name = request.name + "'s Team",
//        )
//
//        val newHead = User(
//            name = request.name,
//            login = request.login,
//            email = request.email,
//            password = request.password,
//            role = UserRoles.HEAD,
//            company = company,
//        )
//
//        every { userRepository.findByEmail(any()) } answers { newHead.toEntity() }
//
//        val exception = shouldThrow<EntityAlreadyExistsException> {
//            authService.registerHead(request)
//        }
//        exception.message should startWith("Почта ${request.email} занята")
//    }
//
//
//    @Test
//    fun `ошибка если логин уже занят`() {
//        val request = RegisterHeadRequest(
//            name = "Name",
//            login = "admin",
//            password = "123456",
//            email = "email@example.com",
//        )
//
//        val oldCompany = UserCompany(
//            name = request.name + "'s Team",
//        )
//
//        val oldHead = User(
//            name = request.name,
//            login = request.login,
//            email = request.email,
//            password = request.password,
//            role = UserRoles.HEAD,
//            company = oldCompany,
//        )
//        every { userRepository.findByEmail(any()) } answers { null }
//        every { userRepository.findByLogin(any()) } answers { oldHead.toEntity() }
//
//        val exception = shouldThrow<EntityAlreadyExistsException> {
//            authService.registerHead(request)
//        }
//        exception.message should startWith("Логин ${request.login} занят")
//    }
//}