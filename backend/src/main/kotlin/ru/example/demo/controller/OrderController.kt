package ru.example.demo.controller

import org.springframework.web.bind.annotation.*
import ru.example.demo.dto.request.CreateRequest
import ru.example.demo.dto.response.OrderResponse
import ru.example.demo.service.OrderService

@CrossOrigin(origins = ["http://Localhost:3000", "http://192.168.1.76:3000"])
@RestController
@RequestMapping("/api/v1")
class OrderController(
    val orderService: OrderService
) {
    @PostMapping("/create")
    fun createOrder(@RequestBody request: CreateRequest, @RequestHeader("Authorization") token: String) {
        val savedOrder = orderService.createOrder(request, token)
        println(savedOrder)
    }

    @GetMapping("/orders/{closed}")
    fun getOrders(@PathVariable closed: Boolean, @RequestHeader("Authorization") token: String): List<OrderResponse> {
        val orders = orderService.getOrders(closed, token)
        val answer = orders.map {
            OrderResponse(
                trackNumber = it.name,
                from = it.downloadAddress,
                to = it.deliveryAddress,
                type = it.kind,
                author = it.user.login
            )
        }
        return answer
    }
}