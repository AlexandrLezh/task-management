package com.homework.task_management.service.impl;

import com.homework.task_management.dto.CreateTaskRequest;
import com.homework.task_management.dto.TaskResponse;
import com.homework.task_management.dto.UpdateTaskRequest;
import com.homework.task_management.errors.TaskNotFoundException;
import com.homework.task_management.mapper.TaskMapper;
import com.homework.task_management.model.Task;
import com.homework.task_management.model.TaskStatus;
import com.homework.task_management.repository.TaskRepository;
import com.homework.task_management.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;

    @Override
    public TaskResponse createTask(CreateTaskRequest request) {

        Task task = taskMapper.toEntity(request);

        task.setStatus(TaskStatus.TODO);
        task.setCreatedAt(Instant.now());

        Task savedTask = taskRepository.save(task);

        return taskMapper.toResponse(savedTask);
    }

    @Override
    public List<TaskResponse> getAll() {

        return taskRepository.findAll().stream()
                .map(taskMapper::toResponse)
                .toList();
    }

    @Override
    public TaskResponse getTaskById(String id) {

        Task taskFromDb = getTaskEntityFromDb(id);

        return taskMapper.toResponse(taskFromDb);
    }

    @Override
    public TaskResponse updateTask(String id, UpdateTaskRequest request) {

        Task taskFromDb = getTaskEntityFromDb(id);
        taskMapper.updateEntity(taskFromDb, request);
        Task updatedTask = taskRepository.save(taskFromDb);

        return taskMapper.toResponse(updatedTask);
    }

    @Override
    public void deleteTaskById(String id) {

        Task taskToDelete = getTaskEntityFromDb(id);

        taskRepository.delete(taskToDelete);
    }

    private Task getTaskEntityFromDb(String id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
    }
}
