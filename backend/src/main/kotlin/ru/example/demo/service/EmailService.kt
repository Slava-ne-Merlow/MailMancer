package ru.example.demo.service

import jakarta.mail.*
import org.springframework.stereotype.Service
import ru.example.demo.exception.type.BadRequestException
import ru.example.demo.util.Loggable
import java.util.*

@Service
class EmailService : Loggable() {
    fun testConnection(email: String, password: String): Boolean {
        val domain = email.substringAfter("@").lowercase()
        val props = when {
            domain.contains("gmail") -> {
                Properties().apply {
                    put("mail.debug", "false")
                    put("mail.store.protocol", "imaps")
                    put("mail.imap.host", "imap.gmail.com")
                    put("mail.imap.port", "993")
                    put("mail.imap.ssl.enable", "true")
                }
            }

            domain.contains("yandex") -> {
                Properties().apply {
                    put("mail.debug", "false")
                    put("mail.store.protocol", "imaps")
                    put("mail.imap.host", "imap.yandex.ru")
                    put("mail.imap.port", "993")
                    put("mail.imap.ssl.enable", "true")
                }
            }

            else -> {
                throw BadRequestException("Провайдер $domain не поддерживается")
            }
        }

        try {
            val session = Session.getInstance(props, null)
            val store = session.store.apply {
                connect(
                    props.getProperty("mail.imap.host"),
                    email,
                    password
                )
            }
            store.close()
            return true
        } catch (e: AuthenticationFailedException) {
            println("Authentication failed")
            return false
        } catch (e: Exception) {
            println(e)
            return false

        }
    }
}