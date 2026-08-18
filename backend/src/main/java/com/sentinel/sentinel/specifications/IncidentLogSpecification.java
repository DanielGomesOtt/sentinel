package com.sentinel.sentinel.specifications;

import com.sentinel.sentinel.enums.IncidentLogLevel;
import com.sentinel.sentinel.models.IncidentLog;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;

public class IncidentLogSpecification {

    public static Specification<IncidentLog> userId(Long userId) {
        return (root, query, criteriaBuilder) -> {
            if (userId == null) return null;

            return criteriaBuilder.equal(root.get("incidentId").get("created_by").get("id"), userId);
        };
    }

    public static Specification<IncidentLog> OrganizationId(Long organizationId) {
        return (root, query, criteriaBuilder) -> {
            if (organizationId == null) return null;

            return criteriaBuilder.equal(root.get("incidentId").get("createdByOrganization").get("id"), organizationId);
        };
    }

    public static Specification<IncidentLog> incidentId(Long incidentId) {
        return (root, query, criteriaBuilder) -> {
            if (incidentId == null) return null;

            return criteriaBuilder.equal(root.get("incidentId").get("id"), incidentId);
        };
    }

    public static Specification<IncidentLog> incidentLogLevel(IncidentLogLevel incidentLogLevel) {
        return (root, query, criteriaBuilder) -> {
            if (incidentLogLevel == null) return null;

            return criteriaBuilder.equal(root.get("level"), incidentLogLevel.name());
        };
    }

    public static Specification<IncidentLog> message(String message) {
        return (root, query, criteriaBuilder) -> {
            if (message == null) return null;

            return criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("message")),
                    "%" + message.toLowerCase() + "%"
            );
        };
    }

    public static Specification<IncidentLog> serviceName(String serviceName) {
        return (root, query, criteriaBuilder) -> {
            if (serviceName == null) return null;

            return criteriaBuilder.equal(root.get("serviceName"), serviceName);
        };
    }

    public static Specification<IncidentLog> to(Instant to) {
        return (root, query, criteriaBuilder) -> {
            if (to == null) return null;

            return criteriaBuilder.lessThanOrEqualTo(
                    root.get("createdAt"),
                    to
            );
        };
    }

    public static Specification<IncidentLog> from(Instant from) {
        return (root, query, criteriaBuilder) -> {
            if (from == null) return null;

            return criteriaBuilder.greaterThanOrEqualTo(
                    root.get("createdAt"),
                    from
            );
        };
    }

}
