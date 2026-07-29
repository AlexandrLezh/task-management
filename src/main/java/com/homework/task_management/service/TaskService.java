package com.homework.task_management.service;

import com.homework.task_management.model.Task;

import java.util.List;

public interface TaskService {

    Task createTask(Task task);

    List<Task> getAll();

    Task getTaskById(String id);

    Task updateTask(String id, Task task);

    void deleteTaskById(String id);
}
