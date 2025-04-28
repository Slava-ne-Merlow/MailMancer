package ru.example.demo.service

import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import ru.example.demo.dto.enums.UserRoles
import ru.example.demo.entity.InviteEntity
import ru.example.demo.entity.UserEntity
import ru.example.demo.exception.type.BadRequestException
import ru.example.demo.exception.type.ForbiddenException
import ru.example.demo.exception.type.UnauthorizedException
import ru.example.demo.repository.InviteRepository
import ru.example.demo.repository.OrderRepository
import ru.example.demo.repository.UserRepository
import ru.example.demo.util.Loggable


@Service
class TeamService(
    private val orderRepository: OrderRepository,
    private val userRepository: UserRepository,
    private val inviteRepository: InviteRepository,
    private val tokenService: TokenService
) : Loggable() {

    @org.springframework.transaction.annotation.Transactional
    fun deleteMember(login: String, token: String): String {
        val currentUser = userRepository.findByToken(token)
            ?: throw UnauthorizedException("Недействителен токен авторизации")

        logger.debug("Найден пользователь по логину: ${currentUser.login}, id: ${currentUser.id}")

        if (currentUser.role != UserRoles.HEAD) {
            throw ForbiddenException("Недостаточно прав")
        }

        if (currentUser.login == login) {
            throw BadRequestException("Вы не можете удалить сами себя")
        }

        val userToDelete = userRepository.findByLogin(login)
            ?: throw BadRequestException("Такого логина не существует")

        logger.debug("Найден пользователь по логину: ${userToDelete.login}, id: ${userToDelete.id}")

        val orders = orderRepository.findAllByUser(userToDelete)

        orders.forEach { order ->
            order.user = currentUser
        }

        orderRepository.saveAll(orders)

        userRepository.deleteByLogin(login)

        logger.info("Удаление пользователя прошло успешно")

        return "Пользователь был успешно удалён"
    }

    fun getTeam(token: String): List<UserEntity> {
        val currentUser = userRepository.findByToken(token)
            ?: throw UnauthorizedException("Недействителен токен авторизации")

        logger.debug("Найден пользователь по логину: ${currentUser.login}, id : ${currentUser.id}")

        val company = currentUser.company

        val users = userRepository.findAllByCompany(company).sortedBy { it.role == UserRoles.HEAD }.reversed()

        logger.info("Поиск команды пользователя прошёл успешно")

        return users
    }

    @Transactional
    fun generateInvite(userToken: String): String {

        logger.debug("Запрос на создание приглашения в компанию user'а с токеном: {}", userToken)

        val user = userRepository.findByToken(userToken)
            ?: throw UnauthorizedException("Недействителен токен авторизации")

        if (user.role != UserRoles.HEAD) {
            throw ForbiddenException("Недостаточно прав")
        }

        val company = user.company

        val token = tokenService.generateToken()

        val invite = InviteEntity(
            company = company,
            token = token
        )

        val savedInvite = inviteRepository.save(invite)

        logger.info("Создали приглашение с id = {}", savedInvite.id)

//        Пока localhost:3000 потом разберусь, как лучше сделать
        return "http://localhost:3000/register?token=${savedInvite.token}"
    }
}
