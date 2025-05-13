package ru.example.demo.service

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.crypto.password.PasswordEncoder
import ru.example.demo.dto.UserDto
import ru.example.demo.entity.UserEntity
import ru.example.demo.exception.UserNotFoundException
import ru.example.demo.repository.UserRepository
import java.time.LocalDateTime
import java.util.*

@ExtendWith(MockitoExtension::class)
class UserServiceTest {

    @Mock
    private lateinit var userRepository: UserRepository
    
    @Mock
    private lateinit var passwordEncoder: PasswordEncoder
    
    @Mock
    private lateinit var tokenService: TokenService
    
    private lateinit var userService: UserService
    
    private val testUser = UserEntity(
        id = 1L,
        username = "testuser",
        password = "hashedPassword",
        email = "test@example.com",
        createdAt = LocalDateTime.now(),
        updatedAt = LocalDateTime.now(),
        active = true,
        role = "USER"
    )
    
    private val testUserDetails = User
        .withUsername("testuser")
        .password("hashedPassword")
        .roles("USER")
        .build()
    
    @BeforeEach
    fun setUp() {
        userService = UserService(userRepository, passwordEncoder, tokenService)
    }
    
    @Test
    fun `create user should hash password and save user`() {
        val userDto = UserDto(
            id = 0,
            username = "testuser",
            password = "plainPassword",
            email = "test@example.com"
        )
        
        whenever(passwordEncoder.encode("plainPassword")).thenReturn("hashedPassword")
        whenever(userRepository.save(any())).thenReturn(testUser)
        
        val result = userService.createUser(userDto)
        
        assertEquals("hashedPassword", result.password)
        verify(passwordEncoder).encode("plainPassword")
        verify(userRepository).save(any())
    }
    
    @Test
    fun `authenticate should verify password and generate token`() {
        whenever(userRepository.findByUsername("testuser")).thenReturn(testUser)
        whenever(passwordEncoder.matches("plainPassword", "hashedPassword")).thenReturn(true)
        whenever(tokenService.generateTokenForUser("testuser", "USER")).thenReturn("jwtToken123")
        
        val result = userService.authenticate("testuser", "plainPassword")
        
        assertEquals("jwtToken123", result.token)
        verify(passwordEncoder).matches("plainPassword", "hashedPassword")
        verify(tokenService).generateTokenForUser("testuser", "USER")
        verify(userRepository).save(any())
    }
    
    @Test
    fun `authenticate should throw exception for invalid password`() {
        whenever(userRepository.findByUsername("testuser")).thenReturn(testUser)
        whenever(passwordEncoder.matches("wrongPassword", "hashedPassword")).thenReturn(false)
        
        val exception = assertThrows(IllegalArgumentException::class.java) {
            userService.authenticate("testuser", "wrongPassword")
        }
        
        assertEquals("Invalid password", exception.message)
    }
    
    @Test
    fun `authenticate should throw exception for non-existent user`() {
        whenever(userRepository.findByUsername("nonexistent")).thenReturn(null)
        
        val exception = assertThrows(UserNotFoundException::class.java) {
            userService.authenticate("nonexistent", "password")
        }
        
        assertEquals("User not found with username: nonexistent", exception.message)
    }
    
    @Test
    fun `validate token should check token and return user`() {
        val token = "validToken"
        
        whenever(tokenService.validateToken(token)).thenReturn(true)
        whenever(tokenService.getUsernameFromToken(token)).thenReturn("testuser")
        whenever(userRepository.findByUsername("testuser")).thenReturn(testUser)
        
        val result = userService.validateToken(token)
        
        assertEquals(testUser, result)
    }
    
    @Test
    fun `validate token should throw exception for invalid token`() {
        val token = "invalidToken"
        
        whenever(tokenService.validateToken(token)).thenReturn(false)
        
        assertThrows(IllegalArgumentException::class.java) {
            userService.validateToken(token)
        }
    }
    
    @Test
    fun `loadUserByUsername should return UserDetails for valid username`() {
        whenever(userRepository.findByUsername("testuser")).thenReturn(testUser)
        
        val result = userService.loadUserByUsername("testuser")
        
        assertEquals("testuser", result.username)
        assertEquals("hashedPassword", result.password)
        assertTrue(result.authorities.any { it.authority == "ROLE_USER" })
    }
    
    @Test
    fun `loadUserByUsername should throw exception for non-existent user`() {
        whenever(userRepository.findByUsername("nonexistent")).thenReturn(null)
        
        assertThrows(org.springframework.security.core.userdetails.UsernameNotFoundException::class.java) {
            userService.loadUserByUsername("nonexistent")
        }
    }
}
