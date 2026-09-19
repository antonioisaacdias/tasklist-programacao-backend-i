package com.antoniodias.tasklist.tag.dto;

import com.antoniodias.tasklist.tag.entity.Tag;

import java.util.UUID;

public record TagResponse(UUID id, String name, String color) {

    public static TagResponse from(Tag tag) {
        return new TagResponse(tag.getId(), tag.getName(), tag.getColor());
    }
}
