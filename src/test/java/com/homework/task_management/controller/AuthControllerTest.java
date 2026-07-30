package com.homework.task_management.controller;

import com.homework.task_management.dto.AuthResponse;
import com.homework.task_management.dto.LoginRequest;
import com.homework.task_management.dto.RegisterRequest;
import com.homework.task_management.service.AuthService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    @Test
    void shouldRegisterByDelegatingToService() {
        RegisterRequest request = new RegisterRequest("user@example.com", "strong-pass");

        authController.register(request);

        verify(authService).register(request);
    }

    @Test
    void shouldReturnTokenOnLogin() {
        LoginRequest request = new LoginRequest("user@example.com", "strong-pass");
        AuthResponse expected = new AuthResponse("jwt-token");
        when(authService.login(request)).thenReturn(expected);

        AuthResponse response = authController.login(request);

        assertThat(response).isEqualTo(expected);
        verify(authService).login(request);
    }
}
