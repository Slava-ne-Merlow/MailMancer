package ru.example.demo.controller


import org.springframework.web.bind.annotation.*
import ru.example.demo.dto.request.RegisterHeadRequest
import ru.example.demo.service.AuthService
import ru.example.demo.dto.request.LoginUserRequest
import ru.example.demo.dto.request.RegisterManagerRequest
import ru.example.demo.dto.response.AuthResponse

@CrossOrigin(origins = ["http://Localhost:3000"])
@RestController
@RequestMapping("/api/v1")
class AuthController(
    val authService: AuthService,
) {
    @PostMapping("/head/sign-up")
    fun registerHead(@RequestBody request: RegisterHeadRequest): AuthResponse {
        val (user, token) = authService.registerHead(request)
        return AuthResponse(
            token = token,
            role = user.role,
            login = user.login,
            name = user.name,
        )
    }

    @PostMapping("/manager/sign-up")
    fun registerManager(@RequestBody request: RegisterManagerRequest): AuthResponse {
        val (user, token) = authService.registerManager(request)
        return AuthResponse(
            token = token,
            role = user.role,
            login = user.login,
            name = user.name,
        )
    }

    @PostMapping("/sign-in")
    fun loginUser(@RequestBody request: LoginUserRequest): AuthResponse {
        val (user, token) = authService.loginUser(request)
        return AuthResponse(
            token = token,
            role = user.role,
            login = user.login,
            name = user.name,
        )
    }

}