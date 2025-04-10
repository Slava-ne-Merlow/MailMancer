package ru.example.demo.service

import jakarta.mail.*
import org.springframework.stereotype.Service
import ru.example.demo.dto.request.EmailCredentials
import ru.example.demo.exception.type.UnauthorizedException
import java.util.*

@Service
class EmailService {
    fun testConnection(credentials:EmailCredentials): Boolean {
        val props = Properties().apply {
            put("mail.debug", "false")
            put("mail.store.protocol", "imaps")

            when (credentials.service.lowercase()) {
                "yandex" -> {
                    put("mail.imap.host", "imap.yandex.ru")
                    put("mail.imap.port", "993")
                    put("mail.imap.ssl.enable", "true")
                }
                "gmail" -> {
                    put("mail.imap.host", "imap.gmail.com")
                    put("mail.imap.port", "993")
                    put("mail.imap.ssl.enable", "true")
                }
                "outlook" -> {
                    put("mail.imap.host", "outlook.office365.com")
                    put("mail.imap.port", "993")
                    put("mail.imap.ssl.enable", "true")
                }
                else -> throw UnauthorizedException("Unsupported email service")
            }
        }

        val result = try {
            val session = Session.getInstance(props, null)
            val store = session.store.apply {
                connect(
                    props.getProperty("mail.imap.host"),
                    credentials.email,
                    credentials.password
                )
            }
            store.close()
            true
        } catch (e: AuthenticationFailedException) {
            false
        } catch (e: Exception) {
            false
        }
        return result
    }
}