package ru.example.demo.controller

import org.springframework.web.bind.annotation.*
import ru.example.demo.dto.response.InviteResponse
import ru.example.demo.dto.response.MemberRequestResponse
import ru.example.demo.dto.response.SuccessResponse
import ru.example.demo.service.TeamService
import ru.example.demo.util.Loggable

@CrossOrigin(origins = ["http://Localhost:3000"])
@RestController
@RequestMapping("/api/v1/team")
class TeamController(
    val teamService: TeamService
) : Loggable() {
    @DeleteMapping("/member/{login}")
    fun delete(@PathVariable login: String, @RequestHeader("Authorization") token: String): SuccessResponse {
        val message = teamService.deleteMember(login, token)

        return SuccessResponse(message = message)
    }

    @GetMapping
    fun get(@RequestHeader("Authorization") token: String): List<MemberRequestResponse> {
        val users = teamService.getTeam(token)

        val response = users.map { MemberRequestResponse(it.name, it.login, it.role, it.email) }

        return response
    }

    @GetMapping("/generate-invite")
    fun generateInvite(@RequestHeader("Authorization") userToken: String): InviteResponse {
        val url = teamService.generateInvite(userToken)

        return InviteResponse(url = url)
    }
}
