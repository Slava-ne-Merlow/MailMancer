package ru.example.demo.dto.model

import ru.example.demo.entity.OrderEntity
import java.time.LocalDateTime

data class Order(
    val name: String,
    val downloadAddress: String,
    val deliveryAddress: String,
    val additionalRequirements: String?,
    val createdDate: LocalDateTime = LocalDateTime.now(),
    val closeDate: LocalDateTime? = null,
    val user: User,
    val kind: String,
) {
    fun toEntity() = OrderEntity(
        name = name,
        downloadAddress = downloadAddress,
        deliveryAddress = deliveryAddress,
        additionalRequirements = additionalRequirements,
        createdDate = createdDate,
        kind = kind,
        user = user.toEntity()
    )
}
