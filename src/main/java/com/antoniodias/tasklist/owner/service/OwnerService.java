package com.antoniodias.tasklist.owner.service;

import com.antoniodias.tasklist.owner.dto.OwnerRequest;
import com.antoniodias.tasklist.owner.dto.OwnerResponse;
import com.antoniodias.tasklist.owner.entity.Owner;
import com.antoniodias.tasklist.owner.repository.OwnerRepository;
import com.antoniodias.tasklist.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OwnerService {

    private final OwnerRepository repository;

    @Transactional
    public OwnerResponse create(OwnerRequest request) {
        Owner owner = new Owner();
        owner.setName(request.name());
        owner.setEmail(request.email());
        return OwnerResponse.from(repository.save(owner));
    }

    public List<OwnerResponse> findAll() {
        return repository.findAll().stream().map(OwnerResponse::from).toList();
    }

    public Owner findEntityById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Owner", id));
    }
}
