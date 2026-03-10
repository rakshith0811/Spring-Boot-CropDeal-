package com.cropdeal.filter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.when;

import com.cropdeal.model.CustomUserDetails;
import com.cropdeal.model.User;
import com.cropdeal.service.CustomerUserDetailsService;
import com.cropdeal.util.JwtUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;

class JwtFilterTest {

    @InjectMocks
    private JwtFilter jwtFilter;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private CustomerUserDetailsService service;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain chain;

    private CustomUserDetails details;
    private String token;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        User user = new User();
        user.setUsername("john");
        user.setPassword("dummy");
        user.setRole("FARMER");
        user.setActive(true);

        details = new CustomUserDetails(user);

        token = "valid.jwt.token";

        // Clean security context before each test
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilterInternal_setsAuthentication_whenTokenValid() throws Exception {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtUtil.extractUsername(token)).thenReturn(details.getUsername());
        when(service.loadUserByUsername(details.getUsername())).thenReturn(details);
        when(jwtUtil.validateToken(token, details)).thenReturn(true);

        // Act
        jwtFilter.doFilterInternal(request, response, chain);

        // Assert
        var auth = SecurityContextHolder.getContext().getAuthentication();
        assertThat(auth).isNotNull();
        assertThat(auth.getPrincipal()).isEqualTo(details);
        then(chain).should().doFilter(request, response);
    }
}
