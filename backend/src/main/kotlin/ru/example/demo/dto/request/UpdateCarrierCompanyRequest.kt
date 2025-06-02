package ru.example.demo.dto.request

data class UpdateCarrierCompanyRequest(
    val type: String,
    val id: Long?,
    val name: String?,
    val field: String,
    val value: String
)