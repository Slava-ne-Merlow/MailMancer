package ru.example.demo

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import ru.example.demo.dto.LoginRequest
import ru.example.demo.dto.UserDto
import ru.example.demo.repository.UserRepository

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class JwtTokenEndToEndTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var userRepository: UserRepository

    @BeforeEach
    @AfterEach
    fun cleanup() {
        userRepository.deleteAll()
    }

    @Test
    fun `full JWT token lifecycle test`() {
        // 1. Регистрация пользователя
        val userDto = UserDto(
            id = 0,
            username = "jwtuser",
            password = "jwtpassword123",
            email = "jwt@example.com"
        )

        mockMvc.perform(
            post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDto))
        )
            .andExpect(status().isOk)

        // 2. Вход в систему и получение токена
        val loginResponse = mockMvc.perform(
            post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                    LoginRequest(
                        username = "jwtuser",
                        password = "jwtpassword123"
                    )
                ))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.token").exists())
            .andReturn()

        val responseJson = objectMapper.readTree(loginResponse.response.contentAsString)
        val token = responseJson.get("token").asText()

        // 3. Проверка валидности токена
        mockMvc.perform(
            get("/api/auth/validate")
                .header("Authorization", "Bearer $token")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$").value(true))

        // 4. Тест доступа к защищенному ресурсу с токеном
        // Здесь предполагается, что у вас есть защищенный ресурс для тестирования
        mockMvc.perform(
            get("/api/auth/validate")
                .header("Authorization", "Bearer $token")
        )
            .andExpect(status().isOk)

        // 5. Проверка недопустимого токена
        mockMvc.perform(
            get("/api/auth/validate")
                .header("Authorization", "Bearer invalid.token.here")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$").value(false))

        // 6. Проверка доступа без токена (должен быть запрещён для защищенных ресурсов)
        mockMvc.perform(
            get("/api/users/1")
        )
            .andExpect(status().isUnauthorized)
    }
}
