package com.homework.task_management.service.impl;

import com.homework.task_management.errors.TaskNotFoundException;
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

    @Override
    public Task createTask(Task task) {

        task.setStatus(TaskStatus.TODO);
        task.setCreatedAt(Instant.now());

        return taskRepository.save(task);
    }

    @Override
    public List<Task> getAll() {

        return taskRepository.findAll();
    }

    @Override
    public Task getTaskById(String id) {

        return taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
    }

    @Override
    public Task updateTask(String id, Task updatedTask) {

        Task taskFromDb = getTaskById(id);

        taskFromDb.setTitle(updatedTask.getTitle());
        taskFromDb.setDescription(updatedTask.getDescription());
        taskFromDb.setStatus(updatedTask.getStatus());

        return taskRepository.save(taskFromDb);
    }

    @Override
    public void deleteTaskById(String id) {

        taskRepository.deleteById(id);
    }
}
