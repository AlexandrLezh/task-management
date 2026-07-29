package com.homework.task_management.dto;

public record CreateTaskRequest(
        String title,
        String description
) {
}
