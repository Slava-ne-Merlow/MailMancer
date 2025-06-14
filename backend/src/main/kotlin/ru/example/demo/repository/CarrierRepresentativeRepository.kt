package ru.example.demo.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.example.demo.entity.CarrierCompanyEntity
import ru.example.demo.entity.CarrierRepresentativeEntity

interface CarrierRepresentativeRepository
    : JpaRepository<CarrierRepresentativeEntity, Long> {
    fun findByCarrierCompany(carrierCompany: CarrierCompanyEntity): List<CarrierRepresentativeEntity>
}