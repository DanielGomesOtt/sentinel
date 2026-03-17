package com.sentinel.sentinel.specifications;

import com.sentinel.sentinel.enums.IncidentStatus;
import com.sentinel.sentinel.models.Incident;
import org.springframework.data.jpa.domain.Specification;
import com.sentinel.sentinel.enums.Severity;

import java.time.Instant;

public class IncidentSpecification {

    public static Specification<Incident> organizationId(Long organizationId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("createdByOrganization").get("id"), organizationId);
    }

    public static Specification<Incident> userId(Long userId) {
        return (root, query, criteriaBuilder) -> {
            if (userId == null) return null;

            return criteriaBuilder.equal(root.get("createdBy").get("id"), userId);
        };
    }

    public static Specification<Incident> title(String title) {
        return (root, query, criteriaBuilder) -> {
            if (title == null) return null;

            return criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("title")),
                    "%" + title.toLowerCase() + "%"
            );
        };
    }

    public static Specification<Incident> description(String description) {
        return (root, query, criteriaBuilder) -> {
            if (description == null) return null;

            return criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("description")),
                    "%" + description.toLowerCase() + "%"
            );
        };
    }

    public static Specification<Incident> severity(Severity severity) {
        return (root, query, criteriaBuilder) -> {
            if (severity == null) return null;

            return criteriaBuilder.equal(root.get("severity"), severity);
        };
    }

    public static Specification<Incident> status(IncidentStatus status) {
        return (root, query, criteriaBuilder) -> {
            if (status == null) return null;

            return criteriaBuilder.equal(root.get("status"), status);
        };
    }

    public static Specification<Incident> serviceName(String serviceName) {
        return (root, query, criteriaBuilder) -> {
            if (serviceName == null) return null;

            return criteriaBuilder.equal(root.get("serviceName"), serviceName);
        };
    }

    public static Specification<Incident> slaDeadline(Instant slaDeadline) {
        return (root, query, criteriaBuilder) -> {
            if (slaDeadline == null) return null;

            return criteriaBuilder.equal(root.get("slaDeadline"), slaDeadline);
        };
    }

    public static Specification<Incident> slaViolated(Boolean slaViolated) {
        return (root, query, criteriaBuilder) -> {
            if (slaViolated == null) return null;

            return criteriaBuilder.equal(root.get("slaViolate"), slaViolated);
        };
    }
}