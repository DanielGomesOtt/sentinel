package com.sentinel.sentinel.dto.incident;

import com.sentinel.sentinel.models.Incident;

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
    public CreatedIncidentDTO(Incident createdIncident) {
        this(createdIncident.getId(), createdIncident.getTitle(), createdIncident.getDescription(),
                createdIncident.getSeverity().name(), createdIncident.getIncidentStatus().name(),
                createdIncident.getServiceName(), createdIncident.getSlaDeadline().toString(),
                createdIncident.getCreatedBy().getId());
    }
}
