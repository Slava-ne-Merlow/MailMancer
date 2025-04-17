package ru.example.demo.service

import org.springframework.stereotype.Service
import ru.example.demo.dto.enums.UserRoles
import ru.example.demo.dto.request.CreateRequest
import ru.example.demo.entity.OrderEntity
import ru.example.demo.exception.type.UnauthorizedException
import ru.example.demo.repository.OrderRepository
import ru.example.demo.repository.UserRepository

@Service
class OrderService(
    private val orderRepository: OrderRepository,
    private val userRepository: UserRepository,
) {
    fun createOrder(request: CreateRequest, token: String): OrderEntity {

        val user = userRepository.findByToken(token)
            ?: throw UnauthorizedException("Недействителен токен авторизации")

        val order = OrderEntity(
            name = request.name,
            downloadAddress = request.from,
            deliveryAddress = request.to,
            width = request.width,
            height = request.height,
            weight = request.weight,
            length = request.length,
            kind = request.kind,
            user = user
        )

        val savedOrder = orderRepository.save(order)

        return savedOrder
    }

    fun getOrders(closed: Boolean, token: String): List<OrderEntity> {
        val user = userRepository.findByToken(token)
            ?: throw UnauthorizedException("Недействителен токен авторизации")

        return when (user.role) {
            UserRoles.HEAD -> {
                if (!closed) {
                    orderRepository.findAllByClosedDateIsNullAndUser_Company(user.company)
                } else {
                    orderRepository.findAllByClosedDateNotNullAndUser_Company(user.company)
                }
            }
            UserRoles.MANAGER -> {
                if (!closed) {
                    orderRepository.findAllByClosedDateIsNullAndUser(user)
                } else {
                    orderRepository.findAllByClosedDateNotNullAndUser(user)
                }
            }
        }
    }


}