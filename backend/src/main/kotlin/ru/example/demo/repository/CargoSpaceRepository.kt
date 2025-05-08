package ru.example.demo.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.example.demo.entity.CargoSpaceEntity
import ru.example.demo.entity.OrderEntity

interface CargoSpaceRepository
    : JpaRepository<CargoSpaceEntity, Long> {
        fun findAllByOrder(order: OrderEntity): List<CargoSpaceEntity>
}