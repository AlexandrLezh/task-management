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
import com.homework.task_management.utility.CurrentUserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TaskMapper taskMapper;

    @Mock
    private CurrentUserService currentUserService;

    @InjectMocks
    private TaskServiceImpl taskService;

    @Test
    void shouldCreateTaskSuccessfully() {
        UUID userId = UUID.randomUUID();
        CreateTaskRequest request = new CreateTaskRequest(
                "Learn MongoDB",
                "Description",
                TaskPriority.LOW
        );

        Task task = new Task();
        task.setTitle("Learn MongoDB");
        task.setDescription("Description");

        TaskResponse response = new TaskResponse(
                "1",
                "Learn MongoDB",
                "Description",
                TaskStatus.TODO,
                TaskPriority.LOW,
                Instant.now(),
                Instant.now()
        );

        when(currentUserService.getCurrentUserId()).thenReturn(userId);
        when(taskMapper.toEntity(request)).thenReturn(task);
        when(taskRepository.save(any(Task.class))).thenReturn(task);
        when(taskMapper.toResponse(task)).thenReturn(response);

        TaskResponse result = taskService.createTask(request);

        assertThat(result.title()).isEqualTo("Learn MongoDB");
        ArgumentCaptor<Task> captor = ArgumentCaptor.forClass(Task.class);
        verify(taskRepository).save(captor.capture());
        assertThat(captor.getValue().getUserId()).isEqualTo(userId);
        assertThat(captor.getValue().getStatus()).isEqualTo(TaskStatus.TODO);
        assertThat(captor.getValue().getCreatedAt()).isNotNull();
    }

    @Test
    void shouldReturnTaskById() {
        UUID userId = UUID.randomUUID();
        String id = "123";
        Task task = new Task();
        task.setId(id);
        task.setTitle("Test task");

        TaskResponse response =
                new TaskResponse(
                        id,
                        "Test task",
                        null,
                        TaskStatus.TODO,
                        TaskPriority.LOW,
                        Instant.now(),
                        Instant.now()
                );

        when(currentUserService.getCurrentUserId()).thenReturn(userId);
        when(taskRepository.findByIdAndUserId(id, userId)).thenReturn(Optional.of(task));
        when(taskMapper.toResponse(task)).thenReturn(response);

        TaskResponse result = taskService.getTaskById(id);

        assertThat(result.id()).isEqualTo(id);
        assertThat(result.title()).isEqualTo("Test task");
        verify(taskRepository).findByIdAndUserId(id, userId);
    }

    @Test
    void shouldThrowExceptionWhenTaskNotFound() {
        UUID userId = UUID.randomUUID();
        String id = "999";

        when(currentUserService.getCurrentUserId()).thenReturn(userId);
        when(taskRepository.findByIdAndUserId(id, userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.getTaskById(id))
                .isInstanceOf(TaskNotFoundException.class)
                .hasMessage("Task with id: 999 was not found");
        verify(taskRepository).findByIdAndUserId(id, userId);
    }

    @Test
    void shouldDeleteTaskSuccessfully() {
        UUID userId = UUID.randomUUID();
        String id = "123";
        Task taskToDelete = new Task();
        taskToDelete.setId(id);
        taskToDelete.setTitle("Test task");

        when(currentUserService.getCurrentUserId()).thenReturn(userId);
        when(taskRepository.findByIdAndUserId(id, userId)).thenReturn(Optional.of(taskToDelete));

        taskService.deleteTaskById(id);

        verify(taskRepository).findByIdAndUserId(id, userId);
        verify(taskRepository).delete(taskToDelete);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistingTask() {
        UUID userId = UUID.randomUUID();
        String id = "123";

        when(currentUserService.getCurrentUserId()).thenReturn(userId);
        when(taskRepository.findByIdAndUserId(id, userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.deleteTaskById(id))
                .isInstanceOf(TaskNotFoundException.class)
                .hasMessage("Task with id: 123 was not found");

        verify(taskRepository).findByIdAndUserId(id, userId);
        verify(taskRepository, never()).delete(any(Task.class));
    }

    @Test
    void shouldReturnAllTasksWithPagination() {
        UUID userId = UUID.randomUUID();

        Instant firstCreatedAt = Instant.parse( "2026-07-29T15:00:00Z" );
        Instant secondCreatedAt = Instant.parse( "2026-07-29T16:00:00Z" );

        Task firstTask = new Task();
        firstTask.setId("1");
        firstTask.setTitle("Learn MongoDB");
        firstTask.setDescription("Study Spring Data MongoDB");
        firstTask.setStatus(TaskStatus.TODO);
        firstTask.setPriority(TaskPriority.LOW);
        firstTask.setCreatedAt(firstCreatedAt);
        firstTask.setUpdatedAt(firstCreatedAt);

        Task secondTask = new Task();
        secondTask.setId("2");
        secondTask.setTitle("Write unit tests");
        secondTask.setDescription("Write service layer tests");
        secondTask.setStatus(TaskStatus.IN_PROGRESS);
        secondTask.setPriority(TaskPriority.HIGH);
        secondTask.setCreatedAt(secondCreatedAt);
        secondTask.setUpdatedAt(secondCreatedAt);


        TaskResponse firstResponse = new TaskResponse(
                "1",
                "Learn MongoDB",
                "Study Spring Data MongoDB",
                TaskStatus.TODO,
                TaskPriority.LOW,
                firstCreatedAt,
                firstCreatedAt
        );

        TaskResponse secondResponse = new TaskResponse(
                "2",
                "Write unit tests",
                "Write service layer tests",
                TaskStatus.IN_PROGRESS,
                TaskPriority.HIGH,
                secondCreatedAt,
                secondCreatedAt
        );

        Pageable pageable = PageRequest.of(
                0,
                2,
                Sort.by(
                        Sort.Direction.DESC,
                        "createdAt"
                )
        );

        Page<Task> taskPage = new PageImpl<>(
                List.of(firstTask, secondTask),
                pageable,
                5
        );

        when(currentUserService.getCurrentUserId()).thenReturn(userId);
        when(taskRepository.findAll(
                ArgumentMatchers.<Example<Task>>any(),
                eq(pageable)
        )).thenReturn(taskPage);
        when(taskMapper.toResponse(firstTask)).thenReturn(firstResponse);
        when(taskMapper.toResponse(secondTask)).thenReturn(secondResponse);

        Page<TaskResponse> result = taskService.getAll(
                null,
                null,
                pageable
        );

        assertThat(result).hasSize(2);
        assertThat(result.getContent())
                .containsExactly(
                        firstResponse,
                        secondResponse
                );
        assertThat(result.getNumber()).isZero();
        // Total Tasks
        assertThat(result.getTotalElements()).isEqualTo(5);
        // Page size
        assertThat(result.getSize()).isEqualTo(2);
        // 5 Tasks divided by 2 pageSize equal 3 Pages
        assertThat(result.getTotalPages()).isEqualTo(3);
        verify(taskRepository).findAll(ArgumentMatchers.<Example<Task>>any(), eq(pageable));
        verify(taskMapper).toResponse(firstTask);
        verify(taskMapper).toResponse(secondTask);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistingTask() {
        UUID userId = UUID.randomUUID();
        String id = "unknown-id";

        UpdateTaskRequest request = new UpdateTaskRequest(
                "Updated task",
                "Updated description",
                TaskStatus.DONE,
                TaskPriority.MEDIUM
        );

        when(currentUserService.getCurrentUserId()).thenReturn(userId);
        when(taskRepository.findByIdAndUserId(id, userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.updateTask(id, request))
                .isInstanceOf(TaskNotFoundException.class)
                .hasMessage("Task with id: unknown-id was not found");
        verify(taskRepository).findByIdAndUserId(id, userId);
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test void shouldReturnTasksFilteredByStatus() {
        UUID userId = UUID.randomUUID();
        TaskStatus status = TaskStatus.TODO;
        Pageable pageable = PageRequest.of(0, 10);
        Task task = new Task();
        task.setId("1");
        task.setTitle("Todo task");
        task.setStatus(status);
        task.setPriority( TaskPriority.MEDIUM );

        TaskResponse response = new TaskResponse(
                "1",
                "Todo task",
                null,
                TaskStatus.TODO,
                TaskPriority.MEDIUM,
                Instant.parse( "2026-07-29T15:00:00Z" ),
                Instant.parse( "2026-07-29T15:00:00Z" )
        );

        Page<Task> taskPage = new PageImpl<>(
                List.of(task),
                pageable,
                1
        );

        when(currentUserService.getCurrentUserId()).thenReturn(userId);
        when(taskRepository.findAll(ArgumentMatchers.<Example<Task>>any(), eq(pageable))).thenReturn(taskPage);
        when(taskMapper.toResponse(task)).thenReturn(response);


        Page<TaskResponse> result = taskService.getAll( status, null, pageable );

        assertThat(result) .hasSize(1);
        assertThat(result.getContent()).containsExactly(response);
        verify(taskRepository).findAll(ArgumentMatchers.<Example<Task>>any(), eq(pageable));
        verify(taskRepository, never()).findAll(pageable);
    }

    @Test void shouldReturnTasksFilteredByPriority() {
        UUID userId = UUID.randomUUID();
        TaskPriority priority = TaskPriority.HIGH;
        Pageable pageable = PageRequest.of(
                0,
                10
        );

        Task task = new Task();
        task.setId("1");
        task.setTitle("High priority task");
        task.setStatus(TaskStatus.TODO);
        task.setPriority(priority);

        TaskResponse response = new TaskResponse(
                "1",
                "High priority task",
                null,
                TaskStatus.TODO,
                TaskPriority.HIGH,
                Instant.parse("2026-07-29T15:00:00Z"),
                Instant.parse( "2026-07-29T15:00:00Z")
        );

        Page<Task> taskPage = new PageImpl<>(
                List.of(task),
                pageable,
                1
        );

        when(currentUserService.getCurrentUserId()).thenReturn(userId);
        when(taskRepository.findAll(ArgumentMatchers.<Example<Task>>any(), eq(pageable))).thenReturn(taskPage);
        when(taskMapper.toResponse(task)).thenReturn(response);

        Page<TaskResponse> result = taskService.getAll(null, priority, pageable );

        assertThat(result).hasSize(1);
        assertThat(result.getContent()).containsExactly(response);
        verify(taskRepository).findAll(ArgumentMatchers.<Example<Task>>any(), eq(pageable));
        verify(taskRepository, never()).findAll(pageable);
    }

    @Test void shouldReturnTasksFilteredByStatusAndPriority() {
        UUID userId = UUID.randomUUID();
        TaskStatus status = TaskStatus.TODO;
        TaskPriority priority = TaskPriority.HIGH;
        Pageable pageable = PageRequest.of(
                0,
                10
        );

        Task task = new Task();
        task.setId("1");
        task.setTitle("High priority todo task");
        task.setStatus(status);
        task.setPriority(priority);

        TaskResponse response = new TaskResponse(
                "1",
                "High priority todo task",
                null,
                status,
                priority,
                Instant.parse("2026-07-29T15:00:00Z"),
                Instant.parse("2026-07-29T15:00:00Z")
        );

        Page<Task> taskPage = new PageImpl<>(
                List.of(task),
                pageable,
                1
        );

        when(currentUserService.getCurrentUserId()).thenReturn(userId);
        when(taskRepository.findAll(ArgumentMatchers.<Example<Task>>any(), eq(pageable))).thenReturn(taskPage);
        when(taskMapper.toResponse(task)).thenReturn(response);

        Page<TaskResponse> result = taskService.getAll(status, priority, pageable);

        assertThat(result).hasSize(1);
        assertThat(result.getContent()).containsExactly(response);
        verify(taskRepository).findAll(ArgumentMatchers.<Example<Task>>any(), eq(pageable));
        verify(taskRepository, never()).findAll(pageable);
    }
}
