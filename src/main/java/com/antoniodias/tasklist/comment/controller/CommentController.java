package com.antoniodias.tasklist.comment.controller;

import com.antoniodias.tasklist.comment.dto.CommentRequest;
import com.antoniodias.tasklist.comment.dto.CommentResponse;
import com.antoniodias.tasklist.comment.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/tasks/{taskId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentResponse create(@PathVariable UUID taskId, @RequestBody CommentRequest request) {
        return service.create(taskId, request);
    }

    @GetMapping
    public List<CommentResponse> findByTask(@PathVariable UUID taskId) {
        return service.findByTask(taskId);
    }
}
