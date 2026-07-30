package com.homework.task_management.utility;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CurrentUserServiceTest {

    private final CurrentUserService currentUserService = new CurrentUserService();

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldReturnCurrentUserIdFromSecurityContext() {
        UUID userId = UUID.randomUUID();
        SecurityContextHolder.getContext().setAuthentication(
                new TestingAuthenticationToken(userId.toString(), null)
        );

        UUID result = currentUserService.getCurrentUserId();

        assertThat(result).isEqualTo(userId);
    }

    @Test
    void shouldThrowWhenPrincipalNameIsNotUuid() {
        SecurityContextHolder.getContext().setAuthentication(
                new TestingAuthenticationToken("not-a-uuid", null)
        );

        assertThatThrownBy(() -> currentUserService.getCurrentUserId())
                .isInstanceOf(IllegalArgumentException.class);
    }
}
