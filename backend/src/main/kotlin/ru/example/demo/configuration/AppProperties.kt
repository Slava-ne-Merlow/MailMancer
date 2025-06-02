package ru.example.demo.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "app")
class FileStorageProperties {
    lateinit var uploadDir: String
}
