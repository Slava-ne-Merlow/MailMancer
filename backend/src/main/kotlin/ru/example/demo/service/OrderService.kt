package ru.example.demo.service

import io.micrometer.core.annotation.Timed
import io.micrometer.core.instrument.MeterRegistry
import org.slf4j.MDC
import org.springframework.stereotype.Service
import ru.example.demo.dto.enums.UserRoles
import ru.example.demo.dto.request.CreateRequest
import ru.example.demo.entity.OrderEntity
import ru.example.demo.exception.type.UnauthorizedException
import ru.example.demo.repository.OrderRepository
import ru.example.demo.repository.UserRepository
import ru.example.demo.util.Loggable

@Service
class OrderService(
    private val orderRepository: OrderRepository,
    private val userRepository: UserRepository,
    metricRegistry : MeterRegistry,
) : Loggable() {
    private val counter = metricRegistry.counter("orders")

    @Timed
    fun createOrder(request: CreateRequest, token: String): OrderEntity {

        logger.debug("Попытка создания заказа с токеном: {} и параметрами: {}", token, request)

        val user = userRepository.findByToken(token)
            ?: throw UnauthorizedException("Недействителен токен авторизации")

        MDC.put("userId", user.id.toString())

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

        logger.info("Создан заказ с ID: {}", savedOrder.id)

        return savedOrder
    }

    
    fun getOrders(closed: Boolean, token: String): List<OrderEntity> {

        logger.debug("Запрос на получение заказов с токеном: {} закрытые: {}",token, closed)

        val user = userRepository.findByToken(token)
            ?: throw UnauthorizedException("Недействителен токен авторизации")

        logger.debug("Роль пользователя: {}", user.role)

        MDC.put("userId", user.id.toString())

        val orders = when (user.role) {
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

        logger.info("Найдено {} заказов", orders.size)

        return orders
    }
}