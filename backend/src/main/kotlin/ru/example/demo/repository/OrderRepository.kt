package ru.example.demo.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.example.demo.entity.OrderEntity
import ru.example.demo.entity.UserCompanyEntity
import ru.example.demo.entity.UserEntity

interface OrderRepository : JpaRepository<OrderEntity, Long> {
    fun findAllByClosedDateNotNullAndUser(user: UserEntity): List<OrderEntity>
    fun findAllByClosedDateIsNullAndUser(user: UserEntity): List<OrderEntity>

    fun findAllByClosedDateNotNullAndUser_Company(company: UserCompanyEntity): List<OrderEntity>
    fun findAllByClosedDateIsNullAndUser_Company(company: UserCompanyEntity): List<OrderEntity>
}
