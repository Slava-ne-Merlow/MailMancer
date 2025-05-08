package ru.example.demo.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "orders")
data class OrderEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    val name: String,

    @Column(name = "download_address")
    val downloadAddress: String,

    @Column(name = "delivery_address")
    val deliveryAddress: String,

    val kind: String,

    @Column(name = "additional_requirements")
    val additionalRequirements: String? = null,

    @Column(name = "created_date", nullable = false)
    val createdDate: LocalDateTime = LocalDateTime.now(),

    @Column(name = "closed_date")
    val closedDate: LocalDateTime? = null,

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    var user: UserEntity,

    @ManyToMany
    @JoinTable(
        name = "order_recipients",
        joinColumns = [JoinColumn(name = "order_id")],
        inverseJoinColumns = [JoinColumn(name = "recipient_id")]
    )
    val recipients: MutableSet<CarrierRepresentativeEntity> = mutableSetOf(),
)
