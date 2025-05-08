package ru.example.demo.service

import org.springframework.stereotype.Service
import ru.example.demo.util.Loggable
import java.util.*

@Service
class TokenService : Loggable() {
    fun generateToken(): String {
        logger.debug("Создание токена")
        return UUID.randomUUID().toString()
    }
}
