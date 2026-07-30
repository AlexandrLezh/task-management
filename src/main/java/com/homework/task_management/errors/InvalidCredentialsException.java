package com.homework.task_management.errors;

public class InvalidCredentialsException extends RuntimeException
{
    public InvalidCredentialsException() {
        super("Invalid credentials: ");
    }
}
