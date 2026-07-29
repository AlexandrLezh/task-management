package com.homework.task_management.mapper;

import com.homework.task_management.dto.CreateTaskRequest;
import com.homework.task_management.dto.TaskResponse;
import com.homework.task_management.dto.UpdateTaskRequest;
import com.homework.task_management.model.Task;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface TaskMapper {

    TaskResponse toResponse(Task task);

    Task toEntity(CreateTaskRequest request);

    void updateEntity(
            @MappingTarget Task task,
            UpdateTaskRequest request
    );
}
