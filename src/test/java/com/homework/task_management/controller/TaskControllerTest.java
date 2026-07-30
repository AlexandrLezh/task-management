package com.homework.task_management.controller;

import com.homework.task_management.dto.CreateTaskRequest;
import com.homework.task_management.dto.TaskResponse;
import com.homework.task_management.dto.UpdateTaskRequest;
import com.homework.task_management.model.TaskPriority;
import com.homework.task_management.model.TaskStatus;
import com.homework.task_management.service.TaskService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskControllerTest {

    @Mock
    private TaskService taskService;

    @InjectMocks
    private TaskController taskController;

    @Test
    void shouldCreateTaskAndReturn201() {
        CreateTaskRequest request = new CreateTaskRequest("Task", "Desc", TaskPriority.MEDIUM);
        TaskResponse expected = new TaskResponse(
                "1",
                "Task",
                "Desc",
                TaskStatus.TODO,
                TaskPriority.MEDIUM,
                Instant.now(),
                Instant.now()
        );
        when(taskService.createTask(request)).thenReturn(expected);

        ResponseEntity<TaskResponse> response = taskController.createTask(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo(expected);
        verify(taskService).createTask(request);
    }

    @Test
    void shouldReturnPagedTasks() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<TaskResponse> expected = new PageImpl<>(List.of(
                new TaskResponse("1", "Task", "Desc", TaskStatus.TODO, TaskPriority.LOW, Instant.now(), Instant.now())
        ));
        when(taskService.getAll(TaskStatus.TODO, TaskPriority.HIGH, pageable)).thenReturn(expected);

        ResponseEntity<Page<TaskResponse>> response = taskController.getAllTasks(TaskStatus.TODO, TaskPriority.HIGH, pageable);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(expected);
        verify(taskService).getAll(TaskStatus.TODO, TaskPriority.HIGH, pageable);
    }

    @Test
    void shouldReturnTaskById() {
        String id = "task-id";
        TaskResponse expected = new TaskResponse(
                id,
                "Task",
                "Desc",
                TaskStatus.TODO,
                TaskPriority.LOW,
                Instant.now(),
                Instant.now()
        );
        when(taskService.getTaskById(id)).thenReturn(expected);

        ResponseEntity<TaskResponse> response = taskController.findTaskById(id);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(expected);
        verify(taskService).getTaskById(id);
    }

    @Test
    void shouldUpdateTask() {
        String id = "task-id";
        UpdateTaskRequest request = new UpdateTaskRequest("Updated", "Updated desc", TaskStatus.DONE, TaskPriority.HIGH);
        TaskResponse expected = new TaskResponse(
                id,
                "Updated",
                "Updated desc",
                TaskStatus.DONE,
                TaskPriority.HIGH,
                Instant.now(),
                Instant.now()
        );
        when(taskService.updateTask(id, request)).thenReturn(expected);

        ResponseEntity<TaskResponse> response = taskController.updateTask(id, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(expected);
        verify(taskService).updateTask(id, request);
    }

    @Test
    void shouldDeleteTaskAndReturn204() {
        String id = "task-id";

        ResponseEntity<Void> response = taskController.deleteTask(id);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(response.getBody()).isNull();
        verify(taskService).deleteTaskById(id);
    }
}
