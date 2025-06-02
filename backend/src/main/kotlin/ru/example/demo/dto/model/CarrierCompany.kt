package ru.example.demo.dto.model

class CarrierCompany (
    val name: String,
    val comment: String? = null,
    val contract: String? = null,
    val application: String? = null,
    val representatives: List<CarrierRepresentative>
)