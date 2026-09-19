package com.antoniodias.tasklist.tag.controller;

import com.antoniodias.tasklist.tag.dto.TagRequest;
import com.antoniodias.tasklist.tag.dto.TagResponse;
import com.antoniodias.tasklist.tag.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/tags")
@RequiredArgsConstructor
public class TagController {

    private final TagService service;

    @PostMapping
    public ResponseEntity<TagResponse> create(@RequestBody TagRequest request) {
        TagResponse created = service.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(created.id()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping
    public List<TagResponse> findAll() {
        return service.findAll();
    }
}
