package com.sentinel.sentinel.repositories;

import com.sentinel.sentinel.enums.IncidentStatus;
import com.sentinel.sentinel.enums.Severity;
import com.sentinel.sentinel.models.Incident;
import com.sentinel.sentinel.models.Organization;
import com.sentinel.sentinel.models.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;

public interface IncidentRepository extends JpaRepository<Incident, Long>, JpaSpecificationExecutor<Incident> {
    Optional<Incident> findByIdAndCreatedByOrganization(Long incidentId, Organization organization);

    Optional<Incident> findByIdAndCreatedByOrganizationAndCreatedBy(Long incidentId, Organization organization, Users user);

    @Query(value = """
       SELECT DISTINCT i.* FROM incident i
       WHERE i.organization_id = :organizationId
       AND (:userId IS NULL OR i.created_by = :userId)
       AND (:title IS NULL OR i.title LIKE '%' || :title || '%')
       AND (:description IS NULL OR i.description LIKE '%' || :description || '%')
       AND (:severityEnum IS NULL OR i.severity = :severityEnum)
       AND (:statusEnum IS NULL OR i.status = :statusEnum)
       AND (:serviceName IS NULL OR i.service_name = :serviceName)
       AND (:slaDeadlineInstant IS NULL OR i.sla_deadline = :slaDeadlineInstant)
       AND (:slaViolate IS NULL OR i.sla_violated = :slaViolate)
       """,
            countQuery = """
       SELECT COUNT(*) FROM incident i
       WHERE i.organization_id = :organizationId
       AND (:userId IS NULL OR i.created_by = :userId)
       AND (:title IS NULL OR i.title LIKE '%' || :title || '%')
       AND (:description IS NULL OR i.description LIKE '%' || :description || '%')
       AND (:severityEnum IS NULL OR i.severity = :severityEnum)
       AND (:statusEnum IS NULL OR i.status = :statusEnum)
       AND (:serviceName IS NULL OR i.service_name = :serviceName)
       AND (:slaDeadlineInstant IS NULL OR i.sla_deadline = :slaDeadlineInstant)
       AND (:slaViolate IS NULL OR i.sla_violated = :slaViolate)
       """,
            nativeQuery = true)
    Page<Incident> findAll(
            @Param("title") String title,
            @Param("description") String description,
            @Param("severityEnum") Severity severityEnum,
            @Param("statusEnum") IncidentStatus statusEnum,
            @Param("serviceName") String serviceName,
            @Param("slaDeadlineInstant") Instant slaDeadlineInstant,
            @Param("slaViolate") Boolean slaViolate,
            @Param("organizationId") Long organizationId,
            @Param("userId") Long userId,
            Pageable pagination
    );
}
