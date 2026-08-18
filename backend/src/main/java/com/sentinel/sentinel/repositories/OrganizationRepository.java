package com.sentinel.sentinel.repositories;

import com.sentinel.sentinel.models.Organization;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrganizationRepository extends JpaRepository<Organization, Long> {
    Optional<Organization> findByIdAndStatus(Long id, int status);
}
