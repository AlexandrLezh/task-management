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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

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

    @InjectMocks
    private TaskServiceImpl taskService;

    @Test
    void shouldCreateTaskSuccessfully() {

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

        when(taskMapper.toEntity(request)).thenReturn(task);
        when(taskRepository.save(task)).thenReturn(task);
        when(taskMapper.toResponse(task)).thenReturn(response);

        TaskResponse result = taskService.createTask(request);

        assertThat(result.title()).isEqualTo("Learn MongoDB");
        verify(taskRepository).save(task);
    }

    @Test
    void shouldReturnTaskById() {

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

        when(taskRepository.findById(id)).thenReturn(Optional.of(task));
        when(taskMapper.toResponse(task)).thenReturn(response);

        TaskResponse result = taskService.getTaskById(id);

        assertThat(result.id()).isEqualTo(id);
        assertThat(result.title()).isEqualTo("Test task");
        verify(taskRepository).findById(id);
    }

    @Test
    void shouldThrowExceptionWhenTaskNotFound() {

        String id = "999";

        when(taskRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.getTaskById(id))
                .isInstanceOf(TaskNotFoundException.class)
                .hasMessage("Task with id: 999 was not found");
        verify(taskRepository).findById(id);
    }

    @Test
    void shouldDeleteTaskSuccessfully() {

        String id = "123";
        Task taskToDelete = new Task();
        taskToDelete.setId(id);
        taskToDelete.setTitle("Test task");

        when(taskRepository.findById(id)).thenReturn(Optional.of(taskToDelete));

        taskService.deleteTaskById(id);

        verify(taskRepository).findById(id);
        verify(taskRepository).delete(taskToDelete);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistingTask() {

        String id = "123";

        when(taskRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.deleteTaskById(id))
                .isInstanceOf(TaskNotFoundException.class)
                .hasMessage("Task with id: 123 was not found");

        verify(taskRepository).findById(id);
        verify(taskRepository, never()).delete(any(Task.class));
    }

    @Test
    void shouldReturnAllTasks() {

        Task firstTask = new Task();
        firstTask.setId("1");
        firstTask.setTitle("Learn MongoDB");
        firstTask.setDescription("Study Spring Data MongoDB");
        firstTask.setStatus(TaskStatus.TODO);
        firstTask.setPriority(TaskPriority.LOW);
        firstTask.setCreatedAt(Instant.now());
        firstTask.setUpdatedAt(Instant.now());

        Task secondTask = new Task();
        secondTask.setId("2");
        secondTask.setTitle("Write unit tests");
        secondTask.setDescription("Write service layer tests");
        secondTask.setStatus(TaskStatus.IN_PROGRESS);
        secondTask.setPriority(TaskPriority.HIGH);
        secondTask.setCreatedAt(Instant.now());
        secondTask.setUpdatedAt(Instant.now());


        TaskResponse firstResponse = new TaskResponse(
                "1",
                "Learn MongoDB",
                "Study Spring Data MongoDB",
                TaskStatus.TODO,
                TaskPriority.LOW,
                firstTask.getCreatedAt(),
                firstTask.getUpdatedAt()
        );

        TaskResponse secondResponse = new TaskResponse(
                "2",
                "Write unit tests",
                "Write service layer tests",
                TaskStatus.IN_PROGRESS,
                TaskPriority.HIGH,
                secondTask.getCreatedAt(),
                secondTask.getUpdatedAt()
        );

        when(taskRepository.findAll()).thenReturn(List.of(firstTask, secondTask));
        when(taskMapper.toResponse(firstTask)).thenReturn(firstResponse);
        when(taskMapper.toResponse(secondTask)).thenReturn(secondResponse);

        List<TaskResponse> result = taskService.getAll();

        assertThat(result)
                .hasSize(2)
                .containsExactly(
                        firstResponse,
                        secondResponse
                );

        verify(taskRepository).findAll();
        verify(taskMapper).toResponse(firstTask);
        verify(taskMapper).toResponse(secondTask);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistingTask() {

        String id = "unknown-id";

        UpdateTaskRequest request = new UpdateTaskRequest(
                "Updated task",
                "Updated description",
                TaskStatus.DONE,
                TaskPriority.MEDIUM
        );

        when(taskRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.updateTask(id, request))
                .isInstanceOf(TaskNotFoundException.class)
                .hasMessage("Task with id: unknown-id was not found");
        verify(taskRepository).findById(id);
        verify(taskRepository, never()).save(any(Task.class));
    }
}
