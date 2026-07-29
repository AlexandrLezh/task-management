package com.homework.task_management.dto;

import com.homework.task_management.model.TaskPriority;
import com.homework.task_management.model.TaskStatus;

import java.time.Instant;

public record TaskResponse(

        String id,
        String title,
        String description,
        TaskStatus status,
        TaskPriority priority,
        Instant createdAt,
        Instant updatedAt
) {
}
