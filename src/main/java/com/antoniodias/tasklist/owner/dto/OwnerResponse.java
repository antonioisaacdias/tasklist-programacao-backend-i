package com.antoniodias.tasklist.owner.dto;

import com.antoniodias.tasklist.owner.entity.Owner;

import java.util.UUID;

public record OwnerResponse(UUID id, String name, String email) {

    public static OwnerResponse from(Owner owner) {
        return new OwnerResponse(owner.getId(), owner.getName(), owner.getEmail());
    }
}
