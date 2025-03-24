package ru.example.demo.service

import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import ru.example.demo.dto.enums.UserRoles
import ru.example.demo.dto.request.LoginUserRequest
import ru.example.demo.dto.request.RegisterHeadRequest
import ru.example.demo.dto.request.RegisterManagerRequest
import ru.example.demo.entity.InviteEntity
import ru.example.demo.entity.UserCompanyEntity
import ru.example.demo.entity.UserEntity
import ru.example.demo.exception.type.EntityAlreadyExistsException
import ru.example.demo.exception.type.ExpiredTokenException
import ru.example.demo.exception.type.NotFoundException
import ru.example.demo.exception.type.UnauthorizedException
import ru.example.demo.repository.InviteRepository
import ru.example.demo.repository.UserCompanyRepository
import ru.example.demo.repository.UserRepository


@Service
class AuthService(
    private val userRepository: UserRepository,
    private val userCompanyRepository: UserCompanyRepository,
    private val inviteRepository: InviteRepository,
    private val tokenService: TokenService,
) {
    @Transactional
    fun registerHead(request: RegisterHeadRequest): UserEntity {
        userCompanyRepository.findByEmail(request.email)?.let {
            throw EntityAlreadyExistsException("Почта ${request.email} занята")
        }
        userRepository.findByLogin(request.headLogin)?.let {
            throw EntityAlreadyExistsException("Логин ${request.headLogin} занят")
        }

        val token = tokenService.generateToken()

        val company = UserCompanyEntity(
            name = request.companyName,
            email = request.email,
            password = request.emailPassword
        )

        val savedCompany = userCompanyRepository.save(company)

        val user = UserEntity(
            login = request.headLogin,
            name = request.headName,
            password = request.headPassword,
            role = UserRoles.HEAD,
            company = savedCompany,
            token = token
        )
        val savedUser = userRepository.save(user)

        return savedUser
    }

    @Transactional
    fun registerManager(request: RegisterManagerRequest): UserEntity {
        val invite = inviteRepository.findByToken(request.inviteToken)
            ?: throw NotFoundException("Приглашение недействительно")

        userRepository.findByLogin(request.managerLogin)?.let {
            throw EntityAlreadyExistsException("Логин ${request.managerLogin} занят")
        }

        if (invite.checkToken()) {
            throw ExpiredTokenException("Приглашение истекло")
        }

        val company = invite.company
        val token = tokenService.generateToken()


        val newUser = UserEntity(
            login = request.managerLogin,
            name = request.managerName,
            password = request.managerPassword,
            role = UserRoles.MANAGER,
            company = company,
            token = token
        )

        val savedUser = userRepository.save(newUser)

        return savedUser
    }

    @Transactional
    fun loginUser(request: LoginUserRequest): UserEntity {
        val user = userRepository.findByLogin(request.login)
            ?: throw NotFoundException("Логина ${request.login} не существует")

        if (user.checkPassword(request.password)) {
            throw UnauthorizedException("Неверный логин или пароль")
        }

        user.token = tokenService.generateToken()

        val savedUser = userRepository.save(user)

        return savedUser
    }

    @Transactional
    fun generateInvite(userToken: String): String {

        val user = userRepository.findByToken(userToken)
            ?: throw UnauthorizedException("Недействителен токен авторизации")

        val company = user.company

        val token = tokenService.generateToken()

        val invite = InviteEntity(
            company = company,
            token = token
        )

        val savedInvite = inviteRepository.save(invite)

//        Пока localhost:8080 потом разберусь, как лучше сделать
        return "https://localhost:8080/manager/sign-up?token=${savedInvite.token}"
    }

}
