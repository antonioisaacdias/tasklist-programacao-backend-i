package com.antoniodias.tasklist.task.service;

import com.antoniodias.tasklist.owner.entity.Owner;
import com.antoniodias.tasklist.owner.service.OwnerService;
import com.antoniodias.tasklist.project.service.ProjectService;
import com.antoniodias.tasklist.shared.exception.ResourceNotFoundException;
import com.antoniodias.tasklist.tag.dto.TagResponse;
import com.antoniodias.tasklist.tag.entity.Tag;
import com.antoniodias.tasklist.tag.service.TagService;
import com.antoniodias.tasklist.task.dto.TaskRequest;
import com.antoniodias.tasklist.task.dto.TaskResponse;
import com.antoniodias.tasklist.task.entity.Task;
import com.antoniodias.tasklist.task.entity.TaskTag;
import com.antoniodias.tasklist.task.enums.TaskPriority;
import com.antoniodias.tasklist.task.enums.TaskStatus;
import com.antoniodias.tasklist.task.repository.TaskRepository;
import com.antoniodias.tasklist.task.repository.TaskTagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository repository;
    private final TaskTagRepository taskTagRepository;
    private final ProjectService projectService;
    private final OwnerService ownerService;
    private final TagService tagService;

    @Transactional
    public TaskResponse create(TaskRequest request) {
        Task task = new Task();
        apply(task, request);
        Task saved = repository.saveAndFlush(task);
        log.info("Task created id={} projectId={} priority={}", saved.getId(), saved.getProject().getId(), saved.getPriority());
        return TaskResponse.from(saved);
    }

    public Page<TaskResponse> findAll(TaskStatus status, UUID projectId, Pageable pageable) {
        return findEntities(status, projectId, pageable).map(TaskResponse::from);
    }

    public Page<TaskResponse> findByProject(UUID projectId, Pageable pageable) {
        projectService.findEntityById(projectId);
        return repository.findByProjectId(projectId, pageable).map(TaskResponse::from);
    }

    public TaskResponse findById(UUID id) {
        return TaskResponse.from(findEntityById(id));
    }

    @Transactional
    public TaskResponse update(UUID id, TaskRequest request) {
        Task task = findEntityById(id);
        apply(task, request);
        if (request.status() != null) {
            changeStatus(task, request.status());
        }
        Task saved = repository.saveAndFlush(task);
        log.info("Task updated id={}", id);
        return TaskResponse.from(saved);
    }

    @Transactional
    public TaskResponse complete(UUID id) {
        Task task = findEntityById(id);
        changeStatus(task, TaskStatus.DONE);
        return TaskResponse.from(task);
    }

    @Transactional
    public void delete(UUID id) {
        Task task = findEntityById(id);
        repository.delete(task);
        log.info("Task deleted id={}", id);
    }

    @Transactional
    public TagResponse addTag(UUID taskId, UUID tagId) {
        Task task = findEntityById(taskId);
        Tag tag = tagService.findEntityById(tagId);
        if (taskTagRepository.findByTaskIdAndTagId(taskId, tagId).isEmpty()) {
            TaskTag link = new TaskTag();
            link.setTask(task);
            link.setTag(tag);
            taskTagRepository.save(link);
            log.info("Tag linked taskId={} tagId={}", taskId, tagId);
        }
        return TagResponse.from(tag);
    }

    @Transactional
    public void removeTag(UUID taskId, UUID tagId) {
        findEntityById(taskId);
        TaskTag link = taskTagRepository.findByTaskIdAndTagId(taskId, tagId)
                .orElseThrow(() -> new ResourceNotFoundException("TaskTag", tagId));
        taskTagRepository.delete(link);
        log.info("Tag unlinked taskId={} tagId={}", taskId, tagId);
    }

    public List<TagResponse> findTags(UUID taskId) {
        findEntityById(taskId);
        return taskTagRepository.findByTaskId(taskId).stream()
                .map(link -> TagResponse.from(link.getTag()))
                .toList();
    }

    public Task findEntityById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task", id));
    }

    private void apply(Task task, TaskRequest request) {
        task.setTitle(request.title());
        task.setDescription(request.description());
        task.setDueDate(request.dueDate());
        task.setProject(projectService.findEntityById(request.projectId()));
        task.setOwner(resolveOwner(request.ownerId()));
        task.setPriority(resolvePriority(request.priority()));
    }

    private void changeStatus(Task task, TaskStatus status) {
        if (task.getStatus() == status) {
            return;
        }
        log.info("Task status change id={} {} -> {}", task.getId(), task.getStatus(), status);
        task.setStatus(status);
        if (status == TaskStatus.DONE) {
            task.setCompletedAt(LocalDateTime.now());
        } else {
            task.setCompletedAt(null);
        }
    }

    private TaskPriority resolvePriority(TaskPriority priority) {
        if (priority == null) {
            return TaskPriority.MEDIUM;
        }
        return priority;
    }

    private Owner resolveOwner(UUID ownerId) {
        if (ownerId == null) {
            return null;
        }
        return ownerService.findEntityById(ownerId);
    }

    private Page<Task> findEntities(TaskStatus status, UUID projectId, Pageable pageable) {
        if (status != null && projectId != null) {
            return repository.findByStatusAndProjectId(status, projectId, pageable);
        }
        if (status != null) {
            return repository.findByStatus(status, pageable);
        }
        if (projectId != null) {
            return repository.findByProjectId(projectId, pageable);
        }
        return repository.findAll(pageable);
    }
}
