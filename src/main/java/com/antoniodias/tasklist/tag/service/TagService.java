package com.antoniodias.tasklist.tag.service;

import com.antoniodias.tasklist.shared.exception.ResourceNotFoundException;
import com.antoniodias.tasklist.tag.dto.TagRequest;
import com.antoniodias.tasklist.tag.dto.TagResponse;
import com.antoniodias.tasklist.tag.entity.Tag;
import com.antoniodias.tasklist.tag.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TagService {

    private final TagRepository repository;

    @Transactional
    public TagResponse create(TagRequest request) {
        Tag tag = new Tag();
        tag.setName(request.name());
        tag.setColor(request.color());
        return TagResponse.from(repository.save(tag));
    }

    public List<TagResponse> findAll() {
        return repository.findAll().stream().map(TagResponse::from).toList();
    }

    public Tag findEntityById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tag", id));
    }
}
