package com.homework.task_management.service.impl;

import com.homework.task_management.dto.CreateTaskRequest;
import com.homework.task_management.dto.TaskResponse;
import com.homework.task_management.dto.UpdateTaskRequest;
import com.homework.task_management.errors.TaskNotFoundException;
import com.homework.task_management.mapper.TaskMapper;
import com.homework.task_management.model.Task;
import com.homework.task_management.model.TaskPriority;
import com.homework.task_management.model.TaskStatus;
import com.homework.task_management.repository.TaskRepository;
import com.homework.task_management.service.TaskService;
import com.homework.task_management.utility.CurrentUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final CurrentUserService currentUserService;

    @Override
    public TaskResponse createTask(CreateTaskRequest request) {

        UUID userId = currentUserService.getCurrentUserId();
        Task task = taskMapper.toEntity(request);

        task.setUserId(userId);
        task.setStatus(TaskStatus.TODO);
        task.setCreatedAt(Instant.now());

        Task savedTask = taskRepository.save(task);

        return taskMapper.toResponse(savedTask);
    }

    @Override
    public Page<TaskResponse> getAll(TaskStatus status, TaskPriority priority, Pageable pageable) {

        UUID userId = currentUserService.getCurrentUserId();
        Task taskFilter = new Task();

        taskFilter.setUserId(userId);
        taskFilter.setStatus(status);
        taskFilter.setPriority(priority);

        ExampleMatcher matcher = ExampleMatcher
                .matchingAll()
                .withIgnoreNullValues();

        Example<Task> example = Example.of(
                taskFilter,
                matcher
        );

        return taskRepository.findAll(example, pageable)
                .map(taskMapper::toResponse);
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

        UUID userId = currentUserService.getCurrentUserId();

        return taskRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new TaskNotFoundException(id));
    }
}
