package com.homework.task_management.repository;

import com.homework.task_management.model.Task;
import com.homework.task_management.model.TaskPriority;
import com.homework.task_management.model.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;

public interface TaskRepository extends MongoRepository<Task, String>, QueryByExampleExecutor<Task> {
}
