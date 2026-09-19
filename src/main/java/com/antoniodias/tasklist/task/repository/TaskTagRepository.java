package com.antoniodias.tasklist.task.repository;

import com.antoniodias.tasklist.task.entity.TaskTag;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TaskTagRepository extends JpaRepository<TaskTag, UUID> {

    @EntityGraph(attributePaths = "tag")
    List<TaskTag> findByTaskId(UUID taskId);

    Optional<TaskTag> findByTaskIdAndTagId(UUID taskId, UUID tagId);
}
