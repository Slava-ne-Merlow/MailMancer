package ru.example.demo.unit

import io.mockk.mockk
import ru.example.demo.repository.InviteRepository
import ru.example.demo.repository.UserCompanyRepository
import ru.example.demo.repository.UserRepository
import ru.example.demo.service.AuthService
import ru.example.demo.service.EmailService
import ru.example.demo.service.TokenService

abstract class AbstractUnitTest {
    val userRepository = mockk<UserRepository>()
    val userCompanyRepository = mockk<UserCompanyRepository>()
    val inviteRepository = mockk<InviteRepository>()
    val tokenService = mockk<TokenService>()
    val emailService = mockk<EmailService>()
    val authService = AuthService(userRepository, userCompanyRepository, inviteRepository, tokenService, emailService)

}