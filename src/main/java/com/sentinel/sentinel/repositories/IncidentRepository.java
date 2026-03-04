package com.sentinel.sentinel.repositories;

import com.sentinel.sentinel.models.Incident;
import com.sentinel.sentinel.models.Organization;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IncidentRepository extends JpaRepository<Incident, Long> {
    Optional<Incident> findByIdAndCreatedByOrganization(Long incidentId, Organization organization);
}
