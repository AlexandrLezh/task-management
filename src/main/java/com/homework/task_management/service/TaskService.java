package com.homework.task_management.service;

import com.homework.task_management.dto.CreateTaskRequest;
import com.homework.task_management.dto.TaskResponse;
import com.homework.task_management.dto.UpdateTaskRequest;
import com.homework.task_management.model.TaskPriority;
import com.homework.task_management.model.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TaskService {

    TaskResponse createTask(CreateTaskRequest request);

    Page<TaskResponse> getAll(TaskStatus status, TaskPriority priority, Pageable pageable);

    TaskResponse getTaskById(String id);

    TaskResponse updateTask(String id, UpdateTaskRequest request);

    void deleteTaskById(String id);
}
