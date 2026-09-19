package com.antoniodias.tasklist.task.controller;

import com.antoniodias.tasklist.tag.dto.TagResponse;
import com.antoniodias.tasklist.task.dto.TagLinkRequest;
import com.antoniodias.tasklist.task.dto.TaskRequest;
import com.antoniodias.tasklist.task.dto.TaskResponse;
import com.antoniodias.tasklist.task.enums.TaskStatus;
import com.antoniodias.tasklist.task.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class TaskController {

    private final TaskService service;

    @PostMapping("/tasks")
    public ResponseEntity<TaskResponse> create(@RequestBody TaskRequest request) {
        TaskResponse created = service.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(created.id()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping("/tasks")
    public List<TaskResponse> findAll(
            @RequestParam(required = false) TaskStatus status,
            @RequestParam(required = false) UUID projectId) {
        return service.findAll(status, projectId);
    }

    @GetMapping("/tasks/{id}")
    public TaskResponse findById(@PathVariable UUID id) {
        return service.findById(id);
    }

    @PutMapping("/tasks/{id}")
    public TaskResponse update(@PathVariable UUID id, @RequestBody TaskRequest request) {
        return service.update(id, request);
    }

    @PutMapping("/tasks/{id}/complete")
    public TaskResponse complete(@PathVariable UUID id) {
        return service.complete(id);
    }

    @DeleteMapping("/tasks/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }

    @GetMapping("/projects/{projectId}/tasks")
    public List<TaskResponse> findByProject(@PathVariable UUID projectId) {
        return service.findByProject(projectId);
    }

    @GetMapping("/tasks/{id}/tags")
    public List<TagResponse> findTags(@PathVariable UUID id) {
        return service.findTags(id);
    }

    @PostMapping("/tasks/{id}/tags")
    @ResponseStatus(HttpStatus.CREATED)
    public TagResponse addTag(@PathVariable UUID id, @RequestBody TagLinkRequest request) {
        return service.addTag(id, request.tagId());
    }

    @DeleteMapping("/tasks/{id}/tags/{tagId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeTag(@PathVariable UUID id, @PathVariable UUID tagId) {
        service.removeTag(id, tagId);
    }
}
