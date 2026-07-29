package com.homework.task_management.errors;

public class UserAlreadyExistsException extends RuntimeException {

    public UserAlreadyExistsException(String email) {
        super("User already exists: " + email);
    }
}
