package com.homework.task_management.dto;

import com.homework.task_management.model.TaskStatus;

public record UpdateTaskRequest(
        String title,
        String description,
        TaskStatus status
) {
}
