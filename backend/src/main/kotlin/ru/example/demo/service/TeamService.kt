package ru.example.demo.service

import org.springframework.stereotype.Service
import ru.example.demo.dto.enums.UserRoles
import ru.example.demo.entity.UserEntity
import ru.example.demo.exception.type.BadRequestException
import ru.example.demo.exception.type.ForbiddenException
import ru.example.demo.exception.type.UnauthorizedException
import ru.example.demo.repository.OrderRepository
import ru.example.demo.repository.UserRepository

@Service
class TeamService(
    private val orderRepository: OrderRepository,
    private val userRepository: UserRepository,
) {
    fun deleteMember(login : String, token : String) {
        val currentUser = userRepository.findByToken(token)
            ?: throw UnauthorizedException("Недействителен токен авторизации")
        if (currentUser.role != UserRoles.HEAD) {
            throw ForbiddenException("Недостаточно прав")
        }
        if (currentUser.login == login) {
            throw BadRequestException("Вы не можете удалить сами себя")
        }
        val userToDelete = userRepository.findByLogin(login)
            ?: throw BadRequestException("Недействителен логин пользователя")
        val orders = orderRepository.findAllByUser(userToDelete)
        orders.forEach { order ->
            order.user = currentUser
        }
        orderRepository.saveAll(orders)
        userRepository.deleteByLogin(login)
    }

    fun getTeam(token: String) : List<UserEntity> {
        val currentUser = userRepository.findByToken(token)
            ?: throw UnauthorizedException("Недействителен токен авторизации")
        val company = currentUser.company
        val users = userRepository.findAllByCompany(company)
        return users
    }
}