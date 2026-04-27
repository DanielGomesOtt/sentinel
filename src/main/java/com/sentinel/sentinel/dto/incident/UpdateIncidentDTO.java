package com.sentinel.sentinel.dto.incident;

import com.sentinel.sentinel.enums.IncidentStatus;
import com.sentinel.sentinel.enums.Severity;
import jakarta.validation.constraints.NotNull;

public record UpdateIncidentDTO(
        @NotNull
        Long incidentId,
        String title,
        String description,
        Severity severity,
        String serviceName,
        IncidentStatus incidentStatus
) {
    public UpdateIncidentDTO(Long incidentId,
                             String title,
                             String description,
                             Severity severity,
                             String serviceName) {
        this(incidentId, title, description, severity, serviceName, null);
    }
}
