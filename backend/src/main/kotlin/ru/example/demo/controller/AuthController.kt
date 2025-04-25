package ru.example.demo.controller


import org.springframework.web.bind.annotation.*
import ru.example.demo.dto.request.RegisterHeadRequest
import ru.example.demo.service.AuthService
import ru.example.demo.dto.request.LoginUserRequest
import ru.example.demo.dto.request.RegisterManagerRequest
import ru.example.demo.dto.response.AuthResponse
import ru.example.demo.util.Loggable

@CrossOrigin(origins = ["http://Localhost:3000", "http://192.168.1.76:3000"])
@RestController
@RequestMapping("/api/v1")
class AuthController(
    val authService: AuthService,
) : Loggable() {
    @PostMapping("/head/sign-up")
    fun registerHead(@RequestBody request: RegisterHeadRequest): AuthResponse {
        val user = authService.registerHead(request)
        return AuthResponse(
            userId = user.id,
            token = user.token,
            companyId = user.company.id,
            role = user.role,
            login = user.login,
            name = user.name,
        )
    }

    @PostMapping("/manager/sign-up")
    fun registerManager(@RequestBody request: RegisterManagerRequest): AuthResponse {
        val user = authService.registerManager(request)
        return AuthResponse(
            userId = user.id,
            token = user.token,
            companyId = user.company.id,
            role = user.role,
            login = user.login,
            name = user.name,
        )
    }

    @PostMapping("/sign-in")
    fun loginUser(@RequestBody request: LoginUserRequest): AuthResponse {
        val user = authService.loginUser(request)
        return AuthResponse(
            userId = user.id,
            token = user.token,
            companyId = user.company.id,
            role = user.role,
            login = user.login,
            name = user.name,
        )
    }

}