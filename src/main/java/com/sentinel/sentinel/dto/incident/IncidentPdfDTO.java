package com.sentinel.sentinel.dto.incident;

import com.sentinel.sentinel.models.Incident;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public record IncidentPdfDTO(Long id,
                             String title,
                             String description,
                             String severity,
                             String status,
                             String service,
                             String user,
                             String integration,
                             String created_at,
                             String deadline) {


    public IncidentPdfDTO(Incident incident) {
        this(incident.getId(), incident.getTitle(), incident.getDescription(),
                incident.getSeverity().name(), incident.getIncidentStatus().name(),
                incident.getServiceName(), (incident.getCreatedBy() != null ?
                        incident.getCreatedBy().getName() : ""),
                (incident.getCreatedBySystemIntegration() != null ?
                        incident.getCreatedBySystemIntegration().getName() :""),
                DateTimeFormatter
                        .ofPattern("yyyy-MM-dd HH:mm:ss")
                        .withZone(ZoneId.systemDefault())
                        .format(incident.getCreatedAt()),
                DateTimeFormatter
                        .ofPattern("yyyy-MM-dd HH:mm:ss")
                        .withZone(ZoneId.systemDefault())
                        .format(incident.getSlaDeadline()));
    }
}
