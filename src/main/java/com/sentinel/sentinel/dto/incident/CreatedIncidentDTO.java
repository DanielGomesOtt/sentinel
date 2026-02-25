package com.sentinel.sentinel.dto.incident;

public record CreatedIncidentDTO(
        Long id,
        String title,
        String description,
        String severity,
        String status,
        String serviceName,
        String slaDeadline,
        Long createdBy
) {
}
