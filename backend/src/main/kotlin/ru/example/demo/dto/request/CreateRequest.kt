package ru.example.demo.dto.request

data class CreateRequest(
    val name: String,
    val from: String,
    val to: String,
    val weight: Double,
    val length: Double,
    val width: Double,
    val height: Double,
    val kind: String,
    val additionalRequirements: String?,
)