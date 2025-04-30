package ru.example.demo.entity

import jakarta.persistence.*
import ru.example.demo.dto.model.CargoSpace

@Entity
@Table(name = "cargo_spaces")
data class CargoSpaceEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    val quantity: Long,

    val weight: Double,

    val length: Double,

    val height: Double,

    val width: Double,

    @ManyToOne
    @JoinColumn(name = "order_id")
    var order: OrderEntity,
) {
    fun toModel () : CargoSpace = CargoSpace(
        quantity = quantity,
        weight = weight,
        length = length,
        height = height,
        width = width,
    )
}