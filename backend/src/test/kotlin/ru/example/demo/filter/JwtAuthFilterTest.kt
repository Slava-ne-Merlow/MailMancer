package ru.example.demo.filter

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetailsService
import ru.example.demo.service.TokenService

class JwtAuthFilterTest {

    @Mock
    private lateinit var tokenService: TokenService

    @Mock
    private lateinit var userDetailsService: UserDetailsService

    @Mock
    private lateinit var request: HttpServletRequest

    @Mock
    private lateinit var response: HttpServletResponse

    @Mock
    private lateinit var filterChain: FilterChain

    private lateinit var jwtAuthFilter: JwtAuthFilter

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        jwtAuthFilter = JwtAuthFilter(tokenService, userDetailsService)
        SecurityContextHolder.clearContext()
    }

    @Test
    fun `doFilterInternal should continue chain if Authorization header is missing`() {
        `when`(request.getHeader("Authorization")).thenReturn(null)

        jwtAuthFilter.doFilterInternal(request, response, filterChain)

        verify(filterChain, times(1)).doFilter(request, response)
        verify(tokenService, never()).getUsernameFromToken(anyString())
    }

    @Test
    fun `doFilterInternal should continue chain if Authorization header doesn't start with Bearer`() {
        `when`(request.getHeader("Authorization")).thenReturn("Basic dXNlcjpwYXNzd29yZA==")

        jwtAuthFilter.doFilterInternal(request, response, filterChain)

        verify(filterChain, times(1)).doFilter(request, response)
        verify(tokenService, never()).getUsernameFromToken(anyString())
    }

    @Test
    fun `doFilterInternal should set authentication if token is valid`() {
        val jwt = "valid-jwt-token"
        val username = "testuser"
        val userDetails = User.withUsername(username)
            .password("password")
            .roles("USER")
            .build()

        `when`(request.getHeader("Authorization")).thenReturn("Bearer $jwt")
        `when`(tokenService.getUsernameFromToken(jwt)).thenReturn(username)
        `when`(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails)
        `when`(tokenService.validateToken(jwt)).thenReturn(true)

        jwtAuthFilter.doFilterInternal(request, response, filterChain)

        verify(filterChain, times(1)).doFilter(request, response)
        verify(tokenService, times(1)).getUsernameFromToken(jwt)
        verify(tokenService, times(1)).validateToken(jwt)
        
        val authentication = SecurityContextHolder.getContext().authentication
        assert(authentication != null)
        assert(authentication.principal == userDetails)
    }

    @Test
    fun `doFilterInternal should not set authentication if token is invalid`() {
        val jwt = "invalid-jwt-token"
        val username = "testuser"
        val userDetails = User.withUsername(username)
            .password("password")
            .roles("USER")
            .build()

        `when`(request.getHeader("Authorization")).thenReturn("Bearer $jwt")
        `when`(tokenService.getUsernameFromToken(jwt)).thenReturn(username)
        `when`(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails)
        `when`(tokenService.validateToken(jwt)).thenReturn(false)

        jwtAuthFilter.doFilterInternal(request, response, filterChain)

        verify(filterChain, times(1)).doFilter(request, response)
        verify(tokenService, times(1)).getUsernameFromToken(jwt)
        verify(tokenService, times(1)).validateToken(jwt)
        
        val authentication = SecurityContextHolder.getContext().authentication
        assert(authentication == null)
    }
}
