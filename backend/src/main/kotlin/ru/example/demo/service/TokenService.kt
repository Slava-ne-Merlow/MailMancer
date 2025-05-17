package ru.example.demo.service

import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.stereotype.Service
import ru.example.demo.entity.UserEntity
import ru.example.demo.exception.type.UnauthorizedException
import ru.example.demo.repository.UserRepository
import ru.example.demo.util.JwtUtil
import ru.example.demo.util.Loggable
import java.util.*

@Service
class TokenService(
    private val jwtUtil: JwtUtil,
    private val userRepository: UserRepository
) : Loggable() {

    fun generateToken(): String {
        logger.debug("Создание JWT токена")
        val userDetails = User.builder()
            .username(UUID.randomUUID().toString())
            .password("")
            .authorities(SimpleGrantedAuthority("ANONYMOUS"))
            .build()
        return jwtUtil.generateToken(userDetails)
    }
    
    fun generateTokenForUser(username: String, role: String = "USER"): String {
        logger.debug("Создание JWT токена для пользователя: $username")
        val userDetails = User.builder()
            .username(username)
            .password("")
            .authorities(SimpleGrantedAuthority(role))
            .build()
        return jwtUtil.generateToken(userDetails)
    }
    
    fun validateToken(token: String): Boolean {
        logger.debug("Валидация JWT токена")
        try {
            // Only check for token expiration, as username is validated by other methods when needed
            val expiration = jwtUtil.extractExpiration(token)
            return !expiration.before(Date())
        } catch (e: Exception) {
            logger.error("Ошибка при валидации токена", e)
            return false
        }
    }
    
    fun getUsernameFromToken(token: String): String? {
        logger.debug("Извлечение имени пользователя из JWT токена")
        return try {
            jwtUtil.extractUsername(token)
        } catch (e: Exception) {
            logger.error("Ошибка при извлечении имени пользователя из токена", e)
            null
        }
    }
    fun getUserFromToken(token: String): UserEntity {
        val tokenWithoutBearer = extractTokenFromHeader(token)


        if (!validateToken(tokenWithoutBearer)) {
            throw UnauthorizedException("Недействительный токен")
        }

        val login = getUsernameFromToken(tokenWithoutBearer)
            ?: throw UnauthorizedException("Не удалось извлечь логин из токена")

        return userRepository.findByLogin(login)
            ?: throw UnauthorizedException("Пользователь не найден")
    }

    fun getRoleFromToken(token: String): String? {
        logger.debug("Извлечение роли из JWT токена")
        return try {
            val role = jwtUtil.extractClaim(token) { claims -> claims["role"] }
            when (role) {
                is String -> role
                null -> null
                else -> role.toString()
            }
        } catch (e: Exception) {
            logger.error("Ошибка при извлечении роли из токена", e)
            null
        }
    }

    fun getInviteToken() = UUID.randomUUID().toString()


    private fun extractTokenFromHeader(authHeader: String): String {
        return if (authHeader.startsWith("Bearer ")) {
            authHeader.substring(7)
        } else {
            authHeader
        }
    }
}
