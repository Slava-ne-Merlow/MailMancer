package ru.example.demo.service

import jakarta.transaction.Transactional
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import ru.example.demo.dto.enums.UserRoles
import ru.example.demo.dto.request.LoginUserRequest
import ru.example.demo.dto.request.RegisterHeadRequest
import ru.example.demo.dto.request.RegisterManagerRequest
import ru.example.demo.entity.UserCompanyEntity
import ru.example.demo.entity.UserEntity
import ru.example.demo.exception.type.EntityAlreadyExistsException
import ru.example.demo.exception.type.ExpiredTokenException
import ru.example.demo.exception.type.NotFoundException
import ru.example.demo.exception.type.UnauthorizedException
import ru.example.demo.repository.InviteRepository
import ru.example.demo.repository.UserCompanyRepository
import ru.example.demo.repository.UserRepository
import ru.example.demo.util.Loggable


@Service
class AuthService(
    private val userRepository: UserRepository,
    private val userCompanyRepository: UserCompanyRepository,
    private val inviteRepository: InviteRepository,
    private val tokenService: TokenService,
    private val passwordEncoder: PasswordEncoder,
) : Loggable() {
    @Transactional
    fun registerHead(request: RegisterHeadRequest): Pair<UserEntity, String> {
        logger.debug("Запрос на регистрацию HEAD'а с параметрами: {}", request)

        userRepository.findByEmail(request.email)?.let {
            throw EntityAlreadyExistsException("Почта ${request.email} занята")
        }
        userRepository.findByLogin(request.login)?.let {
            throw EntityAlreadyExistsException("Логин ${request.login} занят")
        }

        val company = UserCompanyEntity(
            name = request.name + "'s Team",
        )

        val savedCompany = userCompanyRepository.save(company)

        logger.info("Создана компания с id = {}", savedCompany.id)

        val user = UserEntity(
            name = request.name,
            login = request.login,
            email = request.email,
            password = passwordEncoder.encode(request.password),
            role = UserRoles.HEAD,
            company = savedCompany,
        )

        val savedUser = userRepository.save(user)

        logger.info("Создан user с id = {}", savedUser.id)

        val token = tokenService.generateTokenForUser(user.login, user.role.name)

        return user to token
    }

    @Transactional
    fun registerManager(request: RegisterManagerRequest): Pair<UserEntity, String> {
        logger.debug("Запрос на регистрацию MANAGER'а с параметрами: {}", request)


        val invite = inviteRepository.findByToken(request.token)
            ?: throw NotFoundException("Приглашение недействительно")

        userRepository.findByLogin(request.login)?.let {
            throw EntityAlreadyExistsException("Логин ${request.login} занят")
        }

        userRepository.findByEmail(request.email)?.let {
            throw EntityAlreadyExistsException("Почта ${request.email} занята")
        }

        if (invite.checkToken()) {
            throw ExpiredTokenException("Приглашение истекло")
        }

        val company = invite.company

        logger.info("Приглашение с id = {}, companyID: {}", invite.id, invite.company.id)

        val newUser = UserEntity(
            name = request.name,
            login = request.login,
            email = request.email,
            password = passwordEncoder.encode(request.password),
            role = UserRoles.MANAGER,
            company = company
        )

        val savedUser = userRepository.save(newUser)

        logger.info("Создали пользователя с id = {}", savedUser.id)

        val token = tokenService.generateTokenForUser(savedUser.login, savedUser.role.name)

        return savedUser to token
    }

    fun loginUser(request: LoginUserRequest): Pair<UserEntity, String> {

        logger.debug("Запрос на авторизацию с параметрами: {}", request)

        val user = userRepository.findByLogin(request.login)
            ?: throw NotFoundException("Логин ${request.login} занят")

        if (!passwordEncoder.matches(request.password, user.password)) {
            throw UnauthorizedException("Неверный логин или пароль")
        }

        logger.info("Найден пользователь с id = {}", user.id)

        val token = tokenService.generateTokenForUser(user.login, user.role.name)

        return user to token
    }


}
