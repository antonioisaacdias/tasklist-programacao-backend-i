package com.antoniodias.tasklist.shared.exception;

import java.util.UUID;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String resource, UUID id) {
        super(resource + " not found: " + id);
    }
}
