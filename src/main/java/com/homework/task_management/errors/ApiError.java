package com.homework.task_management.errors;

import java.time.Instant;
import java.util.List;

public record ApiError(

        Instant timestamp,

        int statusCode,

        String message,

        List<String> errors
) {
}
