package com.sentinel.sentinel.repositories;

import com.sentinel.sentinel.enums.IncidentStatus;
import com.sentinel.sentinel.models.Incident;
import com.sentinel.sentinel.models.Organization;
import com.sentinel.sentinel.models.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface IncidentRepository extends JpaRepository<Incident, Long>, JpaSpecificationExecutor<Incident> {
    Optional<Incident> findByIdAndCreatedByOrganization(Long incidentId, Organization organization);

    Optional<Incident> findByIdAndCreatedByOrganizationAndCreatedBy(Long incidentId, Organization organization, Users user);

    Optional<List<Incident>> findBySlaViolateFalseAndSlaDeadlineBeforeAndIncidentStatusNot(
            Instant now,
            IncidentStatus status
    );
}
