package com.antoniodias.tasklist.project.service;

import com.antoniodias.tasklist.project.dto.ProjectRequest;
import com.antoniodias.tasklist.project.dto.ProjectResponse;
import com.antoniodias.tasklist.project.entity.Project;
import com.antoniodias.tasklist.project.repository.ProjectRepository;
import com.antoniodias.tasklist.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository repository;

    @Transactional
    public ProjectResponse create(ProjectRequest request) {
        Project project = new Project();
        project.setName(request.name());
        project.setDescription(request.description());
        Project saved = repository.saveAndFlush(project);
        log.info("Project created id={} name={}", saved.getId(), saved.getName());
        return ProjectResponse.from(saved);
    }

    public List<ProjectResponse> findAll() {
        return repository.findAll().stream().map(ProjectResponse::from).toList();
    }

    public Project findEntityById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project", id));
    }
}
