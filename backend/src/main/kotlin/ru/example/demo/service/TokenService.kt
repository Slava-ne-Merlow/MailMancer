package ru.example.demo.service

import org.springframework.stereotype.Service
import ru.example.demo.util.Loggable
import java.util.*

@Service
class TokenService : Loggable() {
    fun generateToken(): String {
        return UUID.randomUUID().toString()
    }
}
