package com.antoniodias.tasklist.project.dto;

import com.antoniodias.tasklist.project.entity.Project;

import java.time.LocalDateTime;
import java.util.UUID;

public record simProjectResponse(UUID id, String name, String description, LocalDateTime createdAt) {

    public static ProjectResponse from(Project project) {
        return new ProjectResponse(
                project.getId(),
                project.getName(),
                project.getDescription(),
                project.getCreatedAt()
        );
    }
}
