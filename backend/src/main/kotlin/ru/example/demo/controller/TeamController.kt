package ru.example.demo.controller

import org.springframework.web.bind.annotation.*
import ru.example.demo.dto.request.CreateRequest
import ru.example.demo.dto.response.OrderResponse
import ru.example.demo.service.OrderService

@CrossOrigin(origins = ["http://Localhost:3000", "http://192.168.1.76:3000"])
@RestController
@RequestMapping("/api/v1")
class TeamController(
    val teamService : TeamService
) {
    @PostMapping("team/member/{login}")
    fun delete(@PathVariable login : String, @RequestHeader("Authorization") token: String) : ResponseEntity<String> {
        teamService.deleteMember(login, token)
        return ResponseEntity("Пользователь был успешно удалён.", HttpStatus.OK)
    }

    @GetMapping("/team")
    fun get(@RequestHeader("Authorization") token: String): List<MemberRequestResponse> {
        val users = teamService.get(token)
        val response : List<MemberRequestResponse> = users.map { it.name, it.login, it.role }
    }
}
