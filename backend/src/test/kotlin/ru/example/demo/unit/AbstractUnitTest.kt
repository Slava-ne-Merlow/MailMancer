package ru.example.demo.unit

import io.mockk.mockk
import org.springframework.security.crypto.password.PasswordEncoder
import ru.example.demo.repository.InviteRepository
import ru.example.demo.repository.OrderRepository
import ru.example.demo.repository.UserCompanyRepository
import ru.example.demo.repository.UserRepository
import ru.example.demo.service.AuthService
import ru.example.demo.service.TeamService
import ru.example.demo.service.TokenService

abstract class AbstractUnitTest {
    val userRepository = mockk<UserRepository>()
    val userCompanyRepository = mockk<UserCompanyRepository>()
    val orderRepository = mockk<OrderRepository>()
    val inviteRepository = mockk<InviteRepository>()
    val tokenService = mockk<TokenService>()
    val passwordEncoder =  mockk<PasswordEncoder>()

    val authService = AuthService(userRepository, userCompanyRepository, inviteRepository, tokenService, passwordEncoder)
    val teamService = TeamService(orderRepository, userRepository, inviteRepository, tokenService)

}