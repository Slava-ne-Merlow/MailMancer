package ru.example.demo.service

import org.springframework.stereotype.Service
import ru.example.demo.dto.enums.UserRoles
import ru.example.demo.dto.request.CreateRequest
import ru.example.demo.entity.OrderEntity
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
        if (currentUser.login == login || currentUser.role != HEAD) {
            throw BadRequestException("У вас недостаточно прав для удаления пользователя.")
        }
        val deletedUser = userRepository.findByLogin(login)
        userRepository.deleteById(deletedUser)
        orderRepository.updateOrdersByUserId(deletedUser.id, currentUser.id)
    }

    fun getTeam(token: String) : List<MemberRequestResponse>{
        val currentUser = userRepository.findByToken(token)
        val id = currentUser.company_id
        val users = userRepository.findByCompanyId(currentUser.company_id)
        return users
    }
}