package ru.example.demo.integration

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import ru.example.demo.repository.InviteRepository
import ru.example.demo.repository.UserCompanyRepository
import ru.example.demo.repository.UserRepository
import ru.example.demo.service.AuthService


@ActiveProfiles("test")
@SpringBootTest
abstract class AbstractServiceTest {
    @Autowired
    lateinit var authService: AuthService

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var userCompanyRepository: UserCompanyRepository

    @Autowired
    lateinit var inviteRepository: InviteRepository

}