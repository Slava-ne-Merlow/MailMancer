package ru.example.demo.security

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("test")
class PasswordEncodingTest {

    @Autowired
    private lateinit var passwordEncoder: PasswordEncoder

    @Test
    fun `password should be properly hashed with BCrypt`() {
        // Arrange
        val rawPassword = "securePassword123"
        
        // Act
        val encodedPassword = passwordEncoder.encode(rawPassword)
        
        // Assert
        // Проверяем, что пароль начинается с префикса BCrypt
        assertTrue(encodedPassword.startsWith("$2a$") || encodedPassword.startsWith("$2b$"))
        
        // Проверяем что пароль не совпадает с оригиналом
        assertFalse(encodedPassword == rawPassword)
        
        // Проверяем что BCrypt правильно верифицирует пароль
        assertTrue(passwordEncoder.matches(rawPassword, encodedPassword))
        
        // Проверяем что неверный пароль не проходит верификацию
        assertFalse(passwordEncoder.matches("wrongPassword", encodedPassword))
    }
    
    @Test
    fun `different encodings of same password should produce different hashes`() {
        // Arrange
        val password = "samePassword123"
        
        // Act
        val firstHash = passwordEncoder.encode(password)
        val secondHash = passwordEncoder.encode(password)
        
        // Assert
        // Проверяем, что хеши разные (из-за соли)
        assertFalse(firstHash == secondHash)
        
        // Проверяем, что оба хеша валидны для исходного пароля
        assertTrue(passwordEncoder.matches(password, firstHash))
        assertTrue(passwordEncoder.matches(password, secondHash))
    }
    
    @Test
    fun `verify bcrypt work factor is sufficient`() {
        // Arrange
        val password = "testPassword123"
        val startTime = System.currentTimeMillis()
        
        // Act
        val hash = passwordEncoder.encode(password)
        val endTime = System.currentTimeMillis()
        val timeElapsed = endTime - startTime
        
        // Assert
        // Проверяем, что хеширование занимает разумное время (~200-500ms рекомендуется)
        // Слишком быстрое хеширование означает слабую защиту против брутфорса,
        // слишком медленное - может повлиять на производительность сервера при высокой нагрузке
        assertTrue(timeElapsed > 50, "Хеширование пароля слишком быстрое, возможно низкий work factor")
        assertTrue(timeElapsed < 1000, "Хеширование пароля слишком медленное, может повлиять на производительность")
        
        // Проверяем, что BCrypt работает корректно
        assertTrue(passwordEncoder.matches(password, hash))
    }
}
