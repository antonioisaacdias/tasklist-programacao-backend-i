package com.antoniodias.tasklist.owner.controller;

import com.antoniodias.tasklist.owner.dto.OwnerRequest;
import com.antoniodias.tasklist.owner.dto.OwnerResponse;
import com.antoniodias.tasklist.owner.service.OwnerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/owners")
@RequiredArgsConstructor
public class OwnerController {

    private final OwnerService service;

    @PostMapping
    public ResponseEntity<OwnerResponse> create(@RequestBody OwnerRequest request) {
        OwnerResponse created = service.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(created.id()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping
    public List<OwnerResponse> findAll() {
        return service.findAll();
    }
}
