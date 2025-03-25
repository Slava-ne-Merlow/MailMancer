package ru.example.demo.dto.request

data class RegisterManagerRequest (
    val name: String,
    val login: String,
    val password: String,
    val inviteToken: String
)