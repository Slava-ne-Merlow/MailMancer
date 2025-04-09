package ru.example.demo.dto.request

data class EmailCredentials(
    val email:String,
    val password:String,
    val service:String
)