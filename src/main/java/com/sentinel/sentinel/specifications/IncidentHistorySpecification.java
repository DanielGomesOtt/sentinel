package com.sentinel.sentinel.specifications;

import com.sentinel.sentinel.enums.IncidentStatus;
import com.sentinel.sentinel.models.IncidentHistory;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;

public class IncidentHistorySpecification {

    public static Specification<IncidentHistory> userId(Long userId) {
        return (root, query, criteriaBuilder) -> {
            if (userId == null) return null;

            return criteriaBuilder.equal(root.get("performedBy").get("id"), userId);
        };
    }

    public static Specification<IncidentHistory> OrganizationId(Long organizationId) {
        return (root, query, criteriaBuilder) -> {
            if (organizationId == null) return null;

            return criteriaBuilder.equal(root.get("incidentId").get("createdByOrganization").get("id"), organizationId);
        };
    }

    public static Specification<IncidentHistory> incidentId(Long incidentId) {
        return (root, query, criteriaBuilder) -> {
            if (incidentId == null) return null;

            return criteriaBuilder.equal(root.get("incidentId").get("id"), incidentId);
        };
    }

    public static Specification<IncidentHistory> newStatus(IncidentStatus newStatus) {
        return (root, query, criteriaBuilder) -> {
            if (newStatus == null) return null;

            return criteriaBuilder.equal(root.get("newStatus"), newStatus.name());
        };
    }

    public static Specification<IncidentHistory> previousStatus(IncidentStatus previousStatus) {
        return (root, query, criteriaBuilder) -> {
            if (previousStatus == null) return null;

            return criteriaBuilder.equal(root.get("previousStatus"), previousStatus.name());
        };
    }

    public static Specification<IncidentHistory> action(String action) {
        return (root, query, criteriaBuilder) -> {
            if (action == null) return null;

            return criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("action")),
                    "%" + action.toLowerCase() + "%"
            );
        };
    }

    public static Specification<IncidentHistory> from(Instant from) {
        return (root, query, criteriaBuilder) -> {
            if (from == null) return null;

            return criteriaBuilder.greaterThanOrEqualTo(
                    root.get("createdAt"),
                    from
            );
        };
    }

    public static Specification<IncidentHistory> to(Instant to) {
        return (root, query, criteriaBuilder) -> {
            if (to == null) return null;

            return criteriaBuilder.lessThanOrEqualTo(
                    root.get("createdAt"),
                    to
            );
        };
    }
}