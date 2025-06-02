package ru.example.demo.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.example.demo.entity.CarrierCompanyEntity
import ru.example.demo.entity.UserCompanyEntity

interface CarrierCompanyRepository
    : JpaRepository<CarrierCompanyEntity, Long> {
        fun findByUserCompany(userCompany: UserCompanyEntity): List<CarrierCompanyEntity>
        fun findByName(name: String): CarrierCompanyEntity?
}