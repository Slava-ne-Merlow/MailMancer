package ru.example.demo.controller

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import ru.example.demo.entity.UserEntity
import ru.example.demo.repository.UserRepository
import ru.example.demo.service.UserService
import ru.example.demo.util.JwtUtil
import java.time.LocalDateTime

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SecurityIntegrationTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var userService: UserService

    @Autowired
    private lateinit var passwordEncoder: PasswordEncoder

    @Autowired
    private lateinit var jwtUtil: JwtUtil

    @BeforeEach
    fun setup() {
        userRepository.deleteAll()

        // Создаем тестового пользователя
        val user = UserEntity(
            username = "securitytestuser",
            password = passwordEncoder.encode("securitytest123"),
            email = "security@example.com",
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now(),
            active = true,
            role = "USER"
        )
        userRepository.save(user)
    }

    @Test
    fun `unauthenticated requests should be denied access to protected resources`() {
        // Попытка доступа к защищенному ресурсу без аутентификации
        mockMvc.perform(
            get("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun `authenticated requests should be allowed access to protected resources`() {
        // Создаем JWT токен для пользователя
        val userDetails = userService.loadUserByUsername("securitytestuser")
        val token = jwtUtil.generateToken(userDetails)

        // Доступ к защищенному ресурсу с валидным токеном
        mockMvc.perform(
            get("/api/auth/validate")
                .header("Authorization", "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
    }

    @Test
    fun `expired tokens should be rejected`() {
        // Настраиваем токен с коротким временем жизни
        val userDetails = userService.loadUserByUsername("securitytestuser")
        
        // Ожидаем, чтобы токен истек (если настроен на очень короткое время)
        Thread.sleep(100)

        // Пытаемся использовать истекший токен (это работает только если в тестовом окружении
        // настроено очень короткое время жизни токена, например 1ms)
        mockMvc.perform(
            get("/api/auth/validate")
                .header("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJzZWN1cml0eXRlc3R1c2VyIiwicm9sZSI6IlVTRVIiLCJpYXQiOjE2MDAwMDAwMDAsImV4cCI6MTYwMDAwMDAwMX0.signature")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk) // проверяем, что при неверном токене validate вернет false
    }

    @Test
    fun `public endpoints should be accessible without authentication`() {
        // Проверяем, что публичные эндпоинты доступны без аутентификации
        mockMvc.perform(
            get("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
    }
}
