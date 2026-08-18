package com.sentinel.sentinel.dto.incident;

import com.sentinel.sentinel.models.Incident;

public record CreatedIncidentBySystemIntegrationDTO(
        Long id,
        String title,
        String description,
        String severity,
        String status,
        String serviceName,
        String slaDeadline,
        Long createdBySystemIntegration
) {

    public CreatedIncidentBySystemIntegrationDTO(Incident createdIncident) {
        this(createdIncident.getId(), createdIncident.getTitle(), createdIncident.getDescription(),
                createdIncident.getSeverity().name(), createdIncident.getIncidentStatus().name(),
                createdIncident.getServiceName(), createdIncident.getSlaDeadline().toString(),
                createdIncident.getCreatedBySystemIntegration().getId());
    }

}
