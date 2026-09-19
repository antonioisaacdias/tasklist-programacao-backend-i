package com.antoniodias.tasklist.task.dto;

import com.antoniodias.tasklist.task.entity.Task;
import com.antoniodias.tasklist.task.enums.TaskPriority;
import com.antoniodias.tasklist.task.enums.TaskStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record TaskResponse(
        UUID id,
        String title,
        String description,
        TaskStatus status,
        TaskPriority priority,
        LocalDate dueDate,
        LocalDateTime createdAt,
        LocalDateTime completedAt,
        UUID projectId,
        UUID ownerId
) {

    public static TaskResponse from(Task task) {
        UUID ownerId = task.getOwner() == null ? null : task.getOwner().getId();
        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getPriority(),
                task.getDueDate(),
                task.getCreatedAt(),
                task.getCompletedAt(),
                task.getProject().getId(),
                ownerId
        );
    }
}
