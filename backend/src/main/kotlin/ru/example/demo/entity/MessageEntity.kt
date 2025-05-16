package ru.example.demo.entity

import jakarta.persistence.*
import ru.example.demo.dto.enums.MessageType
import java.time.LocalDateTime

@Entity
@Table(name = "messages",
    indexes = [
        Index(name = "idx_message_order", columnList = "order_id"),
        Index(name = "idx_message_user_company", columnList = "user_company_id")
    ])

data class MessageEntity (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    val date: LocalDateTime,

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    val order: OrderEntity,

    @ManyToOne
    @JoinColumn(name = "user_company_id", nullable = false)
    val company: CarrierRepresentativeEntity,

    val type: MessageType,

    val message: String,

    )