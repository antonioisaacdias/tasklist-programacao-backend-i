package com.antoniodias.tasklist.comment.dto;

import com.antoniodias.tasklist.comment.entity.Comment;

import java.time.LocalDateTime;
import java.util.UUID;

public record CommentResponse(UUID id, String text, String author, LocalDateTime createdAt, UUID taskId) {

    public static CommentResponse from(Comment comment) {
        return new CommentResponse(
                comment.getId(),
                comment.getText(),
                comment.getAuthor(),
                comment.getCreatedAt(),
                comment.getTask().getId()
        );
    }
}
