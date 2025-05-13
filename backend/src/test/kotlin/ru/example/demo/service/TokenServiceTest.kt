package ru.example.demo.service

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.springframework.security.core.userdetails.UserDetails
import ru.example.demo.util.JwtUtil
import java.util.*

class TokenServiceTest {

    @Mock
    private lateinit var jwtUtil: JwtUtil

    private lateinit var tokenService: TokenService

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        tokenService = TokenService(jwtUtil)
    }

    @Test
    fun `generateToken should create a token`() {
        val mockToken = "mock.jwt.token"
        whenever(jwtUtil.generateToken(any<UserDetails>())).thenReturn(mockToken)

        val result = tokenService.generateToken()

        assertEquals(mockToken, result)
    }

    @Test
    fun `generateTokenForUser should create a token with user details`() {
        val mockToken = "mock.jwt.token"
        whenever(jwtUtil.generateToken(any<UserDetails>())).thenReturn(mockToken)

        val result = tokenService.generateTokenForUser("testuser", "ADMIN")

        assertEquals(mockToken, result)
    }

    @Test
    fun `validateToken should return true for valid token`() {
        val validToken = "valid.jwt.token"
        whenever(jwtUtil.extractUsername(validToken)).thenReturn("testuser")
        whenever(jwtUtil.extractExpiration(validToken)).thenReturn(Date(System.currentTimeMillis() + 1000000))

        val result = tokenService.validateToken(validToken)

        assertTrue(result)
    }

    @Test
    fun `validateToken should return false for expired token`() {
        val expiredToken = "expired.jwt.token"
        whenever(jwtUtil.extractUsername(expiredToken)).thenReturn("testuser")
        whenever(jwtUtil.extractExpiration(expiredToken)).thenReturn(Date(System.currentTimeMillis() - 1000000))

        val result = tokenService.validateToken(expiredToken)

        assertFalse(result)
    }

    @Test
    fun `validateToken should return false for exception`() {
        val invalidToken = "invalid.jwt.token"
        whenever(jwtUtil.extractUsername(invalidToken)).thenThrow(RuntimeException("Invalid token"))

        val result = tokenService.validateToken(invalidToken)

        assertFalse(result)
    }

    @Test
    fun `getUsernameFromToken should return username for valid token`() {
        val validToken = "valid.jwt.token"
        val expectedUsername = "testuser"
        whenever(jwtUtil.extractUsername(validToken)).thenReturn(expectedUsername)

        val result = tokenService.getUsernameFromToken(validToken)

        assertEquals(expectedUsername, result)
    }

    @Test
    fun `getUsernameFromToken should return null for exception`() {
        val invalidToken = "invalid.jwt.token"
        whenever(jwtUtil.extractUsername(invalidToken)).thenThrow(RuntimeException("Invalid token"))

        val result = tokenService.getUsernameFromToken(invalidToken)

        assertNull(result)
    }

    @Test
    fun `getRoleFromToken should return role for valid token`() {
        val validToken = "valid.jwt.token"
        val expectedRole = "ADMIN"
        whenever(jwtUtil.extractClaim(any(), any())).thenReturn(expectedRole)

        val result = tokenService.getRoleFromToken(validToken)

        assertEquals(expectedRole, result)
    }

    @Test
    fun `getRoleFromToken should return null for exception`() {
        val invalidToken = "invalid.jwt.token"
        whenever(jwtUtil.extractClaim(any(), any())).thenThrow(RuntimeException("Invalid token"))

        val result = tokenService.getRoleFromToken(invalidToken)

        assertNull(result)
    }
}
