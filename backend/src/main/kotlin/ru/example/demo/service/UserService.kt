package ru.example.demo.service

import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import ru.example.demo.dto.UserDto
import ru.example.demo.entity.UserEntity
import ru.example.demo.exception.UserNotFoundException
import ru.example.demo.repository.UserRepository

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val tokenService: TokenService
) : UserDetailsService {

    fun createUser(userDto: UserDto): UserEntity {
        val user = UserEntity(
            login = userDto.username,
            name = userDto.name,
            password = passwordEncoder.encode(userDto.password),
            email = userDto.email,
            role = userDto.role,
            token = "",
            company = userRepository.findCompanyById(userDto.companyId)
                ?: throw IllegalArgumentException("Company not found with id: ${userDto.companyId}")
        )
        return userRepository.save(user)
    }

    fun authenticate(username: String, password: String): UserEntity {
        val user = userRepository.findByLogin(username)
            ?: throw UserNotFoundException("User not found with username: $username")

        if (!passwordEncoder.matches(password, user.password)) {
            throw IllegalArgumentException("Invalid password")
        }

        val token = tokenService.generateTokenForUser(user.login, user.role.name)

        user.token = token
        userRepository.save(user)

        return user
    }

    fun validateToken(token: String): UserEntity {
        if (tokenService.validateToken(token)) {
            val username = tokenService.getUsernameFromToken(token)
                ?: throw IllegalArgumentException("Invalid token")

            return userRepository.findByLogin(username)
                ?: throw UserNotFoundException("User not found with username: $username")
        }
        throw IllegalArgumentException("Invalid token")
    }

    fun getUserById(id: Long): UserEntity {
        return userRepository.findById(id)
            .orElseThrow { UserNotFoundException("User not found with id: $id") }
    }

    fun findByUsername(username: String): UserEntity? {
        return userRepository.findByLogin(username)
    }

    override fun loadUserByUsername(username: String): UserDetails {
        val user = userRepository.findByLogin(username)
            ?: throw UsernameNotFoundException("User not found with username: $username")

        return User.builder()
            .username(user.login)
            .password(user.password)
            .roles(user.role.name)  // Convert enum to its name string representation
            .build()
    }
}
