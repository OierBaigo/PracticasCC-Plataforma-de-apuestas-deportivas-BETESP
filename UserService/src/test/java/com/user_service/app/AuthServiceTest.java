
package com.user_service.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.user_service.dto.LoginRequest;
import com.user_service.dto.LoginResponse;
import com.user_service.exception.InvalidCredentialsException;
import com.user_service.model.User;
import com.user_service.repository.UserRepo;
import com.user_service.security.JwtService;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepo userRepo;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setEmail("test@betesp.com");
        user.setPassword("encodedPassword");
        user.setUsername("testUser");
    }

    @Test
    void loginSuccessfully() {
        LoginRequest request = new LoginRequest(
                "test@betesp.com", "password123");

        when(userRepo.findByEmail("test@betesp.com"))
                .thenReturn(user);

        when(passwordEncoder.matches("password123", "encodedPassword"))
                .thenReturn(true);

        when(jwtService.generateToken(user))
                .thenReturn("fake-jwt-token");

        LoginResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals("fake-jwt-token", response.accessToken());
        assertEquals("Bearer", response.tokenType());

        verify(userRepo).findByEmail("test@betesp.com");
        verify(jwtService).generateToken(user);
    }

    @Test
    void loginFailsWhenUserDoesNotExist() {
        LoginRequest request = new LoginRequest(
                "unknown@betesp.com", "password123");

        when(userRepo.findByEmail("unknown@betesp.com"))
                .thenReturn(null);

        assertThrows(
                InvalidCredentialsException.class,
                () -> authService.login(request));

        verify(jwtService, never()).generateToken(any());
    }

    @Test
    void loginFailsWhenPasswordIsIncorrect() {
        LoginRequest request = new LoginRequest(
                "test@betesp.com", "wrongPassword");

        when(userRepo.findByEmail("test@betesp.com"))
                .thenReturn(user);

        when(passwordEncoder.matches("wrongPassword", "encodedPassword"))
                .thenReturn(false);

        assertThrows(
                InvalidCredentialsException.class,
                () -> authService.login(request));

        verify(jwtService, never()).generateToken(any());
    }
}
