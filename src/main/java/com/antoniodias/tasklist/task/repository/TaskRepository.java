package com.antoniodias.tasklist.task.repository;

import com.antoniodias.tasklist.task.entity.Task;
import com.antoniodias.tasklist.task.enums.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TaskRepository extends JpaRepository<Task, UUID> {

    Page<Task> findByStatus(TaskStatus status, Pageable pageable);

    Page<Task> findByProjectId(UUID projectId, Pageable pageable);

    Page<Task> findByStatusAndProjectId(TaskStatus status, UUID projectId, Pageable pageable);
}
