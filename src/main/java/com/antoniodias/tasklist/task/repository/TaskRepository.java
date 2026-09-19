package com.antoniodias.tasklist.task.repository;

import com.antoniodias.tasklist.task.entity.Task;
import com.antoniodias.tasklist.task.enums.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TaskRepository extends JpaRepository<Task, UUID> {

    List<Task> findByStatus(TaskStatus status);

    List<Task> findByProjectId(UUID projectId);

    List<Task> findByStatusAndProjectId(TaskStatus status, UUID projectId);
}
