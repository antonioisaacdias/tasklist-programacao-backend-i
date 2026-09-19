package com.antoniodias.tasklist.task.dto;

import com.antoniodias.tasklist.task.enums.TaskPriority;
import com.antoniodias.tasklist.task.enums.TaskStatus;

import java.time.LocalDate;
import java.util.UUID;

public record TaskRequest(
        String title,
        String description,
        TaskPriority priority,
        TaskStatus status,
        LocalDate dueDate,
        UUID projectId,
        UUID ownerId
) {
}
