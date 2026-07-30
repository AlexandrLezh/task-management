package com.homework.task_management.service;

import com.homework.task_management.configuration.JwtProperties;
import com.homework.task_management.dto.AuthResponse;
import com.homework.task_management.dto.LoginRequest;
import com.homework.task_management.dto.RegisterRequest;
import com.homework.task_management.errors.InvalidCredentialsException;
import com.homework.task_management.errors.UserAlreadyExistsException;
import com.homework.task_management.model.User;
import com.homework.task_management.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtEncoder jwtEncoder;

    @Mock
    private JwtProperties jwtProperties;

    @InjectMocks
    private AuthService authService;

    @Test
    void shouldRegisterUserSuccessfully() {
        RegisterRequest request = new RegisterRequest("user@example.com", "strong-pass");

        when(userRepository.existsByEmail(request.email())).thenReturn(false);
        when(passwordEncoder.encode(request.password())).thenReturn("encoded-pass");

        authService.register(request);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        assertThat(userCaptor.getValue().getEmail()).isEqualTo("user@example.com");
        assertThat(userCaptor.getValue().getPassword()).isEqualTo("encoded-pass");
    }

    @Test
    void shouldThrowWhenRegisteringExistingUser() {
        RegisterRequest request = new RegisterRequest("user@example.com", "strong-pass");
        when(userRepository.existsByEmail(request.email())).thenReturn(true);

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(UserAlreadyExistsException.class)
                .hasMessage("User already exists: user@example.com");

        verify(passwordEncoder, never()).encode(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenLoginUserNotFound() {
        LoginRequest request = new LoginRequest("missing@example.com", "pass");
        when(userRepository.findByEmail(request.email())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessage("Invalid credentials: ");

        verify(passwordEncoder, never()).matches(any(), any());
        verify(jwtEncoder, never()).encode(any());
    }

    @Test
    void shouldThrowWhenLoginPasswordDoesNotMatch() {
        LoginRequest request = new LoginRequest("user@example.com", "wrong-pass");
        User user = new User("user@example.com", "encoded-pass");

        when(userRepository.findByEmail(request.email())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(request.password(), user.getPassword())).thenReturn(false);

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessage("Invalid credentials: ");

        verify(jwtEncoder, never()).encode(any());
    }

    @Test
    void shouldLoginAndReturnTokenWhenCredentialsAreValid() {
        LoginRequest request = new LoginRequest("user@example.com", "strong-pass");
        User user = new User("user@example.com", "encoded-pass");
        UUID userId = UUID.randomUUID();
        ReflectionTestUtils.setField(user, "id", userId);

        Jwt jwt = new Jwt(
                "generated-token",
                Instant.now(),
                Instant.now().plusSeconds(3600),
                Map.of("alg", "HS256"),
                Map.of("sub", userId.toString())
        );

        when(userRepository.findByEmail(request.email())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(request.password(), user.getPassword())).thenReturn(true);
        when(jwtProperties.expiration()).thenReturn(3600L);
        when(jwtEncoder.encode(any(JwtEncoderParameters.class))).thenReturn(jwt);

        AuthResponse response = authService.login(request);

        assertThat(response.token()).isEqualTo("generated-token");

        ArgumentCaptor<JwtEncoderParameters> paramsCaptor = ArgumentCaptor.forClass(JwtEncoderParameters.class);
        verify(jwtEncoder).encode(paramsCaptor.capture());
        JwtClaimsSet claims = paramsCaptor.getValue().getClaims();
        assertThat(claims.getSubject()).isEqualTo(userId.toString());
        assertThat(claims.<String>getClaim("email")).isEqualTo("user@example.com");
    }
}
