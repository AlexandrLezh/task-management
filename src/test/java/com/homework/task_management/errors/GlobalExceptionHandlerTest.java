package com.homework.task_management.errors;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.HttpInputMessage;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @Mock
    private MethodArgumentNotValidException methodArgumentNotValidException;

    @Mock
    private BindingResult bindingResult;

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void shouldHandleGeneralException() {
        RuntimeException exception = new RuntimeException("boom");

        ResponseEntity<ApiError> response = handler.handleGeneralException(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().statusCode()).isEqualTo(500);
        assertThat(response.getBody().message()).isEqualTo("Internal server error");
        assertThat(response.getBody().errors()).containsExactly("boom");
    }

    @Test
    void shouldHandleValidationException() {
        when(methodArgumentNotValidException.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(
                new FieldError("request", "email", "must be a well-formed email address"),
                new FieldError("request", "password", "must not be blank")
        ));

        ResponseEntity<ApiError> response = handler.handleValidationException(methodArgumentNotValidException);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().statusCode()).isEqualTo(400);
        assertThat(response.getBody().message()).isEqualTo("Validation failed");
        assertThat(response.getBody().errors()).containsExactly(
                "email: must be a well-formed email address",
                "password: must not be blank"
        );
    }

    @Test
    void shouldHandleInvalidRequestBody() {
        HttpInputMessage inputMessage = org.mockito.Mockito.mock(HttpInputMessage.class);
        HttpMessageNotReadableException exception = new HttpMessageNotReadableException("bad json", inputMessage);

        ResponseEntity<ApiError> response = handler.handleInvalidRequestBody(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().statusCode()).isEqualTo(400);
        assertThat(response.getBody().message()).isEqualTo("invalid request body");
        assertThat(response.getBody().errors()).containsExactly(
                "Request contains invalid JSON or unsupported enum value"
        );
    }

    @Test
    void shouldHandleTaskNotFound() {
        TaskNotFoundException exception = new TaskNotFoundException("123");

        ResponseEntity<ApiError> response = handler.handleTaskNotFound(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().statusCode()).isEqualTo(404);
        assertThat(response.getBody().message()).isEqualTo("Resource not found");
        assertThat(response.getBody().errors()).containsExactly("Task with id: 123 was not found");
    }

    @Test
    void shouldHandleUserAlreadyExists() {
        UserAlreadyExistsException exception = new UserAlreadyExistsException("user@example.com");

        ResponseEntity<ApiError> response = handler.handleUserAlreadyExists(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().statusCode()).isEqualTo(409);
        assertThat(response.getBody().message()).isEqualTo("Resource already exists");
        assertThat(response.getBody().errors()).containsExactly("User already exists: user@example.com");
    }

    @Test
    void shouldHandleInvalidCredentials() {
        InvalidCredentialsException exception = new InvalidCredentialsException();

        ResponseEntity<ApiError> response = handler.handleHttpMessageNotReadable(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().statusCode()).isEqualTo(401);
        assertThat(response.getBody().message()).isEqualTo("User has not been authenticated");
        assertThat(response.getBody().errors()).containsExactly("Invalid credentials: ");
    }
}
