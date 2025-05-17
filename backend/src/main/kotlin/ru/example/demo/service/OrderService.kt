package ru.example.demo.service

import io.micrometer.core.annotation.Timed
import io.micrometer.core.instrument.MeterRegistry
import org.slf4j.MDC
import org.springframework.stereotype.Service
import ru.example.demo.dto.enums.UserRoles
import ru.example.demo.dto.request.CreateRequest
import ru.example.demo.entity.CargoSpaceEntity
import ru.example.demo.entity.OrderEntity
import ru.example.demo.exception.type.BadRequestException
import ru.example.demo.exception.type.ForbiddenException
import ru.example.demo.repository.CargoSpaceRepository
import ru.example.demo.repository.OrderRepository
import ru.example.demo.util.Loggable

@Service
class OrderService(
    private val orderRepository: OrderRepository,
    private val cargoSpaceRepository: CargoSpaceRepository,
    private val tokenService: TokenService,
    metricRegistry: MeterRegistry,
) : Loggable() {
    private val counter = metricRegistry.counter("orders")

    @Timed
    fun createOrder(request: CreateRequest, token: String): OrderEntity {

        logger.debug("Попытка создания заказа с токеном: {} и параметрами: {}", token, request)

        val user = tokenService.getUserFromToken(token)


        MDC.put("userId", user.id.toString())

        val order = OrderEntity(
            name = request.name,
            downloadAddress = request.from,
            deliveryAddress = request.to,
            kind = request.kind,
            user = user
        )

        val savedOrder = orderRepository.save(order)

        request.cargoSpaces.forEach {
            cargoSpaceRepository.save(
                CargoSpaceEntity(
                    quantity = it.quantity,
                    weight = it.weight,
                    height = it.height,
                    length = it.length,
                    width = it.width,
                    order = order
                )
            )
        }

        logger.info("Создан заказ с ID: {}", savedOrder.id)

        return savedOrder
    }

    fun getOrders(closed: Boolean, token: String): List<OrderEntity> {

        logger.debug("Запрос на получение заказов с токеном: {} закрытые: {}", token, closed)

        val user = tokenService.getUserFromToken(token)

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

    fun getOrder(id: Long, token: String): Pair<OrderEntity, List<CargoSpaceEntity>> {

        val user = tokenService.getUserFromToken(token)

        val order = orderRepository.findById(id).orElse(null)
            ?: throw BadRequestException("Заказа с id = $id не существует")


        if (user.role != UserRoles.HEAD || user.login != order.user.login) {
            throw ForbiddenException("Нет доступа к заказу")
        }

        val cargoSpaces = cargoSpaceRepository.findAllByOrder(order)

        return order to cargoSpaces
    }
}