package ru.example.demo.service

import jakarta.transaction.Transactional
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
import ru.example.demo.util.PasswordEncoder


@Service
class AuthService(
    private val userRepository: UserRepository,
    private val userCompanyRepository: UserCompanyRepository,
    private val inviteRepository: InviteRepository,
    private val tokenService: TokenService,
    private val passwordEncoder: PasswordEncoder,
) : Loggable() {
    @Transactional
    fun registerHead(request: RegisterHeadRequest): UserEntity {
        logger.debug("Запрос на регистрацию HEAD'а с параметрами: {}", request)

        userRepository.findByEmail(request.email)?.let {
            throw EntityAlreadyExistsException("Почта ${request.email} занята")
        }
        userRepository.findByLogin(request.login)?.let {
            throw EntityAlreadyExistsException("Логин ${request.login} занят")
        }

        val token = tokenService.generateToken()

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
            token = token
        )

        val savedUser = userRepository.save(user)

        logger.info("Создан user с id = {}", savedUser.id)


        return savedUser
    }

    @Transactional
    fun registerManager(request: RegisterManagerRequest): UserEntity {
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

        val token = tokenService.generateToken()

        logger.info("Приглашение с id = {}, companyID: {}", invite.id, invite.company.id)

        val newUser = UserEntity(
            name = request.name,
            login = request.login,
            email = request.email,
            password = passwordEncoder.encode(request.password),
            role = UserRoles.MANAGER,
            company = company,
            token = token
        )

        val savedUser = userRepository.save(newUser)

        logger.info("Создали пользователя с id = {}", savedUser.id)

        return savedUser
    }

    @Transactional
    fun loginUser(request: LoginUserRequest): UserEntity {

        logger.debug("Запрос на авторизацию с параметрами: {}", request)

        val user = userRepository.findByLogin(request.login)
            ?: throw NotFoundException("Пользователь с логином ${request.login} не найден")

        if (!passwordEncoder.matches(request.password, user.password)) {
            throw UnauthorizedException("Неверный логин или пароль")
        }

        user.token = tokenService.generateToken()

        val savedUser = userRepository.save(user)

        logger.info("Найден пользователь с id = {}", savedUser.id)

        return savedUser
    }


}
