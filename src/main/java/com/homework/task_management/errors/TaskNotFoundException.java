package com.homework.task_management.errors;

public class TaskNotFoundException extends RuntimeException {

    public TaskNotFoundException(String id) {
        super("Task with id: " + id + " was not found");
    }
}
