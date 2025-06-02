package ru.example.demo.controller

import org.springframework.web.bind.annotation.*
import ru.example.demo.dto.request.CreateRequest
import ru.example.demo.dto.response.OrderDetailResponse
import ru.example.demo.dto.response.OrderResponse
import ru.example.demo.dto.response.SuccessResponse
import ru.example.demo.service.OrderService
import ru.example.demo.util.Loggable

@CrossOrigin(origins = ["http://localhost:3000"])
@RestController
@RequestMapping("/api/v1/orders")
class OrderController(
    val orderService: OrderService
) : Loggable() {
    @PostMapping("/create")
    fun createOrder(
        @RequestHeader("Authorization") token: String,
        @RequestBody request: CreateRequest
    ): SuccessResponse {
        val savedOrder = orderService.createOrder(request, token)
        return SuccessResponse(message = "Успешное создание заказа с трек номером: ${savedOrder.name}")
    }

    @GetMapping("/{closed}")
    fun getOrders(@PathVariable closed: Boolean, @RequestHeader("Authorization") token: String): List<OrderResponse> {
        val orders = orderService.getOrders(closed, token)
        val answer = orders.map {
            OrderResponse(
                id = it.id,
                trackNumber = it.name,
                from = it.downloadAddress,
                to = it.deliveryAddress,
                type = it.kind,
                author = it.user.login
            )
        }
        return answer
    }

    @GetMapping("/get_by_id/{id}")
    fun getOrder(
        @RequestHeader("Authorization") token: String,
        @PathVariable id: Long
    ): OrderDetailResponse {
        val (order, cargoSpaces) = orderService.getOrder(id, token)

        return OrderDetailResponse(
            number = order.name,
            from = order.downloadAddress,
            to = order.deliveryAddress,
            created = order.createdDate.toLocalDate().toString(),
            closed = order.closedDate?.toLocalDate().toString(),
            kind = order.kind,
            add = order.additionalRequirements,
            cargoDetails = cargoSpaces.map {
                it.toModel()
            }

        )
    }
}