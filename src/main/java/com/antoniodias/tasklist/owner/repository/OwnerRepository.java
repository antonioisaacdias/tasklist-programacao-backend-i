package com.antoniodias.tasklist.owner.repository;

import com.antoniodias.tasklist.owner.entity.Owner;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OwnerRepository extends JpaRepository<Owner, UUID> {
}
