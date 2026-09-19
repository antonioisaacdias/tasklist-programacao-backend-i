package com.antoniodias.tasklist.tag.repository;

import com.antoniodias.tasklist.tag.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TagRepository extends JpaRepository<Tag, UUID> {
}
