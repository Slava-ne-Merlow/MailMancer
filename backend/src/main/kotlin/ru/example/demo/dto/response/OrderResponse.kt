package ru.example.demo.dto.response

data class OrderResponse(
    val id: Long,
    val trackNumber: String,
    val from: String,
    val to: String,
    val type: String,
    val author: String,
)