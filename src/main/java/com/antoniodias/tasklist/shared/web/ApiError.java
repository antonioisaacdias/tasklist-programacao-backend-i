package com.antoniodias.tasklist.shared.web;

import java.time.Instant;

public record ApiError(String error, Instant timestamp) {

    public static ApiError of(String message) {
        return new ApiError(message, Instant.now());
    }
}
