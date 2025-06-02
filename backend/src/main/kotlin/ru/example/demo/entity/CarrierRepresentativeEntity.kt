package ru.example.demo.entity

import jakarta.persistence.*
import ru.example.demo.dto.model.CarrierRepresentative

@Entity
@Table(name = "carrier_representative")
data class CarrierRepresentativeEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    var name: String,

    @ManyToOne()
    @JoinColumn(name = "carrier_company_id", nullable = false)
    val carrierCompany: CarrierCompanyEntity,

    @Column(unique = true)
    var email: String,

    var phoneNumber: String? = null,

    @ManyToMany(mappedBy = "recipients")
    val orders: MutableSet<OrderEntity> = mutableSetOf()
) {
    fun toModel() = CarrierRepresentative(
        id, name, email, phoneNumber
    )
}