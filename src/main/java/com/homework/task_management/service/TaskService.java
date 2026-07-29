package com.homework.task_management.service;

import com.homework.task_management.dto.CreateTaskRequest;
import com.homework.task_management.dto.TaskResponse;
import com.homework.task_management.dto.UpdateTaskRequest;
import com.homework.task_management.model.Task;

import java.util.List;

public interface TaskService {

    TaskResponse createTask(CreateTaskRequest request);

    List<TaskResponse> getAll();

    TaskResponse getTaskById(String id);

    TaskResponse updateTask(String id, UpdateTaskRequest request);

    void deleteTaskById(String id);
}
