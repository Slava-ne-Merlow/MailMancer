package ru.example.demo.controller

import org.springframework.web.bind.annotation.*
import ru.example.demo.dto.response.MemberRequestResponse
import ru.example.demo.dto.response.SuccessResponse
import ru.example.demo.service.TeamService
import ru.example.demo.util.Loggable

@CrossOrigin(origins = ["http://Localhost:3000", "http://192.168.1.76:3000"])
@RestController
@RequestMapping("/api/v1")
class TeamController(
    val teamService: TeamService
) : Loggable() {
    @DeleteMapping("team/member/{login}")
    fun delete(@PathVariable login: String, @RequestHeader("Authorization") token: String): SuccessResponse {
        teamService.deleteMember(login, token)

        return SuccessResponse(message = "Пользователь был успешно удалён")
    }

    @GetMapping("/team")
    fun get(@RequestHeader("Authorization") token: String): List<MemberRequestResponse> {
        val users = teamService.getTeam(token)

        val response = users.map { MemberRequestResponse(it.name, it.login, it.role) }

        return response
    }
}
