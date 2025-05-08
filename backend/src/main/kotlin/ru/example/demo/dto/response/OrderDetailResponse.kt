package ru.example.demo.dto.response

import ru.example.demo.dto.model.CargoSpace

data class OrderDetailResponse(
    val number: String,
    val from: String,
    val to: String,
    val created: String,
    val closed: String?,
    val add: String?,
    val kind: String,
    val cargoDetails: List<CargoSpace>

)