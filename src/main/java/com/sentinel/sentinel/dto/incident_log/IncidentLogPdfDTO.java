package com.sentinel.sentinel.dto.incident_log;

import com.sentinel.sentinel.enums.IncidentLogLevel;
import com.sentinel.sentinel.models.IncidentLog;

import java.time.Instant;

public record IncidentLogPdfDTO(
        Long id,
        Long incidentId,
        IncidentLogLevel level,
        String message,
        String stack_trace,
        String service_name,
        Instant createdAt
) {

    public IncidentLogPdfDTO(IncidentLog incidentLog) {
        this(incidentLog.getId(), incidentLog.getIncidentId().getId(), incidentLog.getLevel(), incidentLog.getMessage(),
                incidentLog.getStackTrace(), incidentLog.getServiceName(), incidentLog.getCreatedAt());
    }
}
