package ru.example.demo.dto.request

import ru.example.demo.dto.model.CargoSpace

data class CreateRequest(
    val name: String,
    val from: String,
    val to: String,
    val kind: String,
    val additionalRequirements: String?,
    val cargoSpaces: MutableList<CargoSpace>,
)