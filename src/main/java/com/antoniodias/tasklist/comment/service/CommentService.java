package com.antoniodias.tasklist.comment.service;

import com.antoniodias.tasklist.comment.dto.CommentRequest;
import com.antoniodias.tasklist.comment.dto.CommentResponse;
import com.antoniodias.tasklist.comment.entity.Comment;
import com.antoniodias.tasklist.comment.repository.CommentRepository;
import com.antoniodias.tasklist.task.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository repository;
    private final TaskService taskService;

    @Transactional
    public CommentResponse create(UUID taskId, CommentRequest request) {
        Comment comment = new Comment();
        comment.setText(request.text());
        comment.setAuthor(request.author());
        comment.setTask(taskService.findEntityById(taskId));
        return CommentResponse.from(repository.save(comment));
    }

    public List<CommentResponse> findByTask(UUID taskId) {
        taskService.findEntityById(taskId);
        return repository.findByTaskIdOrderByCreatedAtAsc(taskId).stream()
                .map(CommentResponse::from)
                .toList();
    }
}
